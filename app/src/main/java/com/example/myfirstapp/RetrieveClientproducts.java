package com.example.myfirstapp;

import android.os.AsyncTask;

import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RetrieveClientproducts extends AsyncTask<Void, Void, List<Clientproduct>> {

    private String host = "192.168.1.149";
    private int port = 27017;
    private MainActivity mainActivity;

    public RetrieveClientproducts(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected List<Clientproduct> doInBackground(Void... voids) {
        List<Clientproduct> clientproducts = new ArrayList<>();
        try {
            MongoClientURI connectionString = new MongoClientURI("mongodb://192.168.1.149:27017/");
            System.out.println("Cum: " + String.valueOf(connectionString));
            MongoClient mongoClient = MongoClients.create(String.valueOf(connectionString));
            MongoDatabase database = mongoClient.getDatabase("Solopadel");

            MongoCollection<Document> collection = database.getCollection("clientproducts");
            FindIterable<Document> documents = collection.find();

            for (Document document : documents) {
                String id = document.getObjectId("_id").toHexString();
                String client = document.getString("client");
                String product = document.getString("product");
                int quantity = document.getInteger("quantity");
                int iva = document.getInteger("iva");
                double price;
                try {
                    price = document.getDouble("price");
                } catch (Exception e) {
                    price = Double.valueOf(document.getInteger("price"));
                }

                Clientproduct cp = new Clientproduct(client, product, quantity, iva, price);
                cp.setId(id);
                clientproducts.add(cp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientproducts;
    }

    @Override
    protected void onPostExecute(List<Clientproduct> clientproducts) {
        super.onPostExecute(clientproducts);
        mainActivity.updateClientproducts(clientproducts);
    }
}
