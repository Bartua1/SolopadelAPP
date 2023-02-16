package com.example.myfirstapp;

import dev.morphia.Morphia;
import dev.morphia.Datastore;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientURI;

public class MongoDBConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private final String DATABASE_NAME = "Solopadel";
    private static Datastore datastore;

    public MongoDBConnection(String host, int port){
        try {
            MongoClientURI connectionString = new MongoClientURI("mongodb://"+host+":"+port+"/");
            mongoClient = MongoClients.create(String.valueOf(connectionString));
            database = mongoClient.getDatabase(DATABASE_NAME);
            datastore = Morphia.createDatastore(mongoClient, DATABASE_NAME);
            System.out.println("Connected to MongoDB successfully");
        }catch(Exception e){
            System.out.println("Failed to connect to MongoDB");
            e.printStackTrace();
        }
    }

    public MongoClient getMongoClient() {
        return (MongoClient) mongoClient;
    }

    public static Datastore getDatastore(String host, int port) {
        MongoDBConnection mongoDBConnection = new MongoDBConnection(host, port);
        return mongoDBConnection.getDatastore();
    }

    Datastore getDatastore() {
        return datastore;
    }

    public void close(){
        mongoClient.close();
    }
}
