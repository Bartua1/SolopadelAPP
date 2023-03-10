/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.reactivestreams.client;

import com.mongodb.client.AbstractCrudTest;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.CommandListener;
import com.mongodb.reactivestreams.client.syncadapter.SyncMongoClient;
import org.bson.BsonArray;
import org.bson.BsonDocument;

import static com.mongodb.client.Fixture.getMongoClientSettingsBuilder;
import static com.mongodb.reactivestreams.client.syncadapter.ContextHelper.CONTEXT_PROVIDER;
import static com.mongodb.reactivestreams.client.syncadapter.ContextHelper.assertContextPassedThrough;

public class CrudTest extends AbstractCrudTest {

    private MongoClient mongoClient;

    public CrudTest(final String filename, final String description, final String databaseName, final String collectionName,
                    final BsonArray data, final BsonDocument definition, final boolean skipTest) {
        super(filename, description, databaseName, collectionName, data, definition, skipTest);
    }

    @Override
    protected void createMongoClient(final CommandListener commandListener) {
        mongoClient = new SyncMongoClient(
                MongoClients.create(getMongoClientSettingsBuilder()
                        .addCommandListener(commandListener)
                        .contextProvider(CONTEXT_PROVIDER)
                        .build()));
    }

    @Override
    protected MongoDatabase getDatabase(final String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }

    @Override
    public void shouldPassAllOutcomes() {
        super.shouldPassAllOutcomes();
        assertContextPassedThrough();
    }

    @Override
    public void cleanUp() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

}
