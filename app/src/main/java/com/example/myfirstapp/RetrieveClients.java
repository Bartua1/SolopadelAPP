package com.example.myfirstapp;

import android.os.AsyncTask;

import com.example.myfirstapp.Product;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RetrieveClients extends AsyncTask<Void, Void, List<Client>> {

    private String host = "192.168.1.149";
    private int port = 27017;
    private MainActivity mainActivity;

    public RetrieveClients(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected List<Client> doInBackground(Void... voids) {
        List<Client> clients = new ArrayList<>();
        try {
            MongoClientURI connectionString = new MongoClientURI("mongodb://192.168.1.149:27017/");
            System.out.println("Cum: " + String.valueOf(connectionString));
            MongoClient mongoClient = MongoClients.create(String.valueOf(connectionString));
            MongoDatabase database = mongoClient.getDatabase("Solopadel");


            try {
                MongoCollection<Document> collection = database.getCollection("clients");
                FindIterable<Document> documents = collection.find();

                for (Document document : documents) {
                    String id = document.getObjectId("_id").toHexString();
                    String name = document.getString("name");

                    Client c = new Client(name);
                    c.setId(id);
                    clients.add(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clients;
        } catch (Exception e) {
            return clients;
        }
    }

    @Override
    protected void onPostExecute(List<Client> clients) {
        super.onPostExecute(clients);
        mainActivity.updateClients(clients);
    }
}
