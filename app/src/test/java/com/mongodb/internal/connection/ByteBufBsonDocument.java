/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.internal.connection;

import com.mongodb.lang.Nullable;
import org.bson.BsonBinaryReader;
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.ByteBuf;
import org.bson.ByteBufNIO;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.io.ByteBufferBsonInput;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

class ByteBufBsonDocument extends AbstractByteBufBsonDocument {
    private static final long serialVersionUID = 2L;

    private final transient ByteBuf byteBuf;

    static List<ByteBufBsonDocument> createList(final ResponseBuffers responseBuffers) {
        int numDocuments = responseBuffers.getReplyHeader().getNumberReturned();
        ByteBuf documentsBuffer = responseBuffers.getBodyByteBuffer();
        documentsBuffer.order(ByteOrder.LITTLE_ENDIAN);
        List<ByteBufBsonDocument> documents = new ArrayList<>(numDocuments);
        while (documents.size() < numDocuments) {
            int documentSizeInBytes = documentsBuffer.getInt();
            documentsBuffer.position(documentsBuffer.position() - 4);
            ByteBuf documentBuffer = documentsBuffer.duplicate();
            documentBuffer.limit(documentBuffer.position() + documentSizeInBytes);
            documents.add(new ByteBufBsonDocument(new ByteBufNIO(documentBuffer.asNIO())));
            documentBuffer.release();
            documentsBuffer.position(documentsBuffer.position() + documentSizeInBytes);
        }
        return documents;
    }

    static List<ByteBufBsonDocument> createList(final ByteBufferBsonOutput bsonOutput, final int startPosition) {
        List<ByteBuf> duplicateByteBuffers = bsonOutput.getByteBuffers();
        CompositeByteBuf outputByteBuf = new CompositeByteBuf(duplicateByteBuffers);
        outputByteBuf.position(startPosition);
        List<ByteBufBsonDocument> documents = new ArrayList<>();
        int curDocumentStartPosition = startPosition;
        while (outputByteBuf.hasRemaining()) {
            int documentSizeInBytes = outputByteBuf.getInt();
            ByteBuf slice = outputByteBuf.duplicate();
            slice.position(curDocumentStartPosition);
            slice.limit(curDocumentStartPosition + documentSizeInBytes);
            documents.add(new ByteBufBsonDocument(slice));
            curDocumentStartPosition += documentSizeInBytes;
            outputByteBuf.position(outputByteBuf.position() + documentSizeInBytes - 4);
        }
        for (ByteBuf byteBuffer : duplicateByteBuffers) {
            byteBuffer.release();
        }
        return documents;
    }

    static ByteBufBsonDocument createOne(final ByteBufferBsonOutput bsonOutput, final int startPosition) {
        List<ByteBuf> duplicateByteBuffers = bsonOutput.getByteBuffers();
        CompositeByteBuf outputByteBuf = new CompositeByteBuf(duplicateByteBuffers);
        outputByteBuf.position(startPosition);
        int documentSizeInBytes = outputByteBuf.getInt();
        ByteBuf slice = outputByteBuf.duplicate();
        slice.position(startPosition);
        slice.limit(startPosition + documentSizeInBytes);
        for (ByteBuf byteBuffer : duplicateByteBuffers) {
            byteBuffer.release();
        }
        return new ByteBufBsonDocument(slice);
    }

    @Override
    public String toJson() {
        return toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build());
    }

    @Override
    public String toJson(final JsonWriterSettings settings) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter, settings);
        ByteBuf duplicate = byteBuf.duplicate();
        try (BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(duplicate))) {
            jsonWriter.pipe(reader);
            return stringWriter.toString();
        } finally {
            duplicate.release();
        }
    }

    @Override
    public BsonReader asBsonReader() {
        return new BsonBinaryReader(new ByteBufferBsonInput(byteBuf.duplicate()));
    }

    @Nullable
    <T> T findInDocument(final Finder<T> finder) {
        ByteBuf duplicateByteBuf = byteBuf.duplicate();
        try (BsonBinaryReader bsonReader = new BsonBinaryReader(new ByteBufferBsonInput(byteBuf.duplicate()))) {
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                T found = finder.find(bsonReader);
                if (found != null) {
                    return found;
                }
            }
            bsonReader.readEndDocument();
        } finally {
            duplicateByteBuf.release();
        }

        return finder.notFound();
    }

    @Override
    public BsonDocument clone() {
        byte[] clonedBytes = new byte[byteBuf.remaining()];
        byteBuf.get(byteBuf.position(), clonedBytes);
        return new RawBsonDocument(clonedBytes);
    }

    int getSizeInBytes() {
        return byteBuf.getInt(byteBuf.position());
    }

    BsonDocument toBaseBsonDocument() {
        ByteBuf duplicateByteBuf = byteBuf.duplicate();
        try (BsonBinaryReader bsonReader = new BsonBinaryReader(new ByteBufferBsonInput(duplicateByteBuf))) {
            return new BsonDocumentCodec().decode(bsonReader, DecoderContext.builder().build());
        } finally {
            duplicateByteBuf.release();
        }
    }

    ByteBufBsonDocument(final ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    // see https://docs.oracle.com/javase/6/docs/platform/serialization/spec/input.html
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
}
