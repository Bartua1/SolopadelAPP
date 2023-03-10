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
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.notNull;
import static org.bson.codecs.BsonValueCodecProvider.getClassForBsonType;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

abstract class AbstractByteBufBsonDocument extends BsonDocument {
    private static final long serialVersionUID = 1L;
    private static final CodecRegistry REGISTRY = fromProviders(new BsonValueCodecProvider());

    @Override
    public void clear() {
        throw new UnsupportedOperationException("ByteBufBsonDocument instances are immutable");
    }

    @Override
    public BsonValue put(final String key, final BsonValue value) {
        throw new UnsupportedOperationException("ByteBufBsonDocument instances are immutable");
    }

    @Override
    public BsonDocument append(final String key, final BsonValue value) {
        throw new UnsupportedOperationException("ByteBufBsonDocument instances are immutable");
    }

    @Override
    public void putAll(final Map<? extends String, ? extends BsonValue> m) {
        throw new UnsupportedOperationException("ByteBufBsonDocument instances are immutable");
    }

    @Override
    public BsonValue remove(final Object key) {
        throw new UnsupportedOperationException("ByteBufBsonDocument instances are immutable");
    }

    @Override
    public boolean isEmpty() {
        return assertNotNull(findInDocument(new Finder<Boolean>() {
            @Override
            public Boolean find(final BsonReader bsonReader) {
                return false;
            }

            @Override
            public Boolean notFound() {
                return true;
            }
        }));
    }

    @Override
    public int size() {
        return assertNotNull(findInDocument(new Finder<Integer>() {
            private int size;

            @Override
            @Nullable
            public Integer find(final BsonReader bsonReader) {
                size++;
                bsonReader.readName();
                bsonReader.skipValue();
                return null;
            }

            @Override
            public Integer notFound() {
                return size;
            }
        }));
    }

    @Override
    public Set<Entry<String, BsonValue>> entrySet() {
        return toBaseBsonDocument().entrySet();
    }

    @Override
    public Collection<BsonValue> values() {
        return toBaseBsonDocument().values();
    }

    @Override
    public Set<String> keySet() {
        return toBaseBsonDocument().keySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }

        Boolean containsKey = findInDocument(new Finder<Boolean>() {
            @Override
            public Boolean find(final BsonReader bsonReader) {
                if (bsonReader.readName().equals(key)) {
                    return true;
                }
                bsonReader.skipValue();
                return null;
            }

            @Override
            public Boolean notFound() {
                return false;
            }
        });
        return containsKey != null ? containsKey : false;
    }

    @Override
    public boolean containsValue(final Object value) {
        Boolean containsValue = findInDocument(new Finder<Boolean>() {
            @Override
            public Boolean find(final BsonReader bsonReader) {
                bsonReader.skipName();
                if (deserializeBsonValue(bsonReader).equals(value)) {
                    return true;
                }
                return null;
            }

            @Override
            public Boolean notFound() {
                return false;
            }
        });
        return containsValue != null ? containsValue : false;
    }

    @Nullable
    @Override
    public BsonValue get(final Object key) {
        notNull("key", key);
        return findInDocument(new Finder<BsonValue>() {
            @Override
            public BsonValue find(final BsonReader bsonReader) {
                if (bsonReader.readName().equals(key)) {
                    return deserializeBsonValue(bsonReader);
                }
                bsonReader.skipValue();
                return null;
            }

            @Nullable
            @Override
            public BsonValue notFound() {
                return null;
            }
        });
    }

    /**
     * Gets the first key in this document.
     *
     * @return the first key in this document
     * @throws java.util.NoSuchElementException if the document is empty
     */
    public String getFirstKey() {
        return assertNotNull(findInDocument(new Finder<String>() {
            @Override
            public String find(final BsonReader bsonReader) {
                return bsonReader.readName();
            }

            @Override
            public String notFound() {
                throw new NoSuchElementException();
            }
        }));
    }

    interface Finder<T> {
        @Nullable
        T find(BsonReader bsonReader);
        @Nullable
        T notFound();
    }

    @Nullable abstract <T> T findInDocument(Finder<T> finder);

    //AbstractBsonReader getBsonReader();

    abstract BsonDocument toBaseBsonDocument();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return toBaseBsonDocument().equals(o);
    }

    @Override
    public int hashCode() {
        return toBaseBsonDocument().hashCode();
    }

    private BsonValue deserializeBsonValue(final BsonReader bsonReader) {
        return REGISTRY.get(getClassForBsonType(bsonReader.getCurrentBsonType())).decode(bsonReader, DecoderContext.builder().build());
    }

    // see https://docs.oracle.com/javase/6/docs/platform/serialization/spec/output.html
    Object writeReplace() {
        return toBaseBsonDocument();
    }

}
