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
import java.util.List;

public class RetrieveProducts extends AsyncTask<Void, Void, List<Product>> {

    private String host = "192.168.1.149";
    private int port = 27017;
    private MainActivity mainActivity;

    public RetrieveProducts(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected List<Product> doInBackground(Void... voids) {
        MongoClientURI connectionString = new MongoClientURI("mongodb://"+host+":"+port+"/");
        MongoClient mongoClient = MongoClients.create(String.valueOf(connectionString));
        MongoDatabase database = mongoClient.getDatabase("Solopadel");
        MongoCollection<Document> collection = database.getCollection("products");

        FindIterable<Document> documents = collection.find();
        List<Product> products = new ArrayList<>();

        for (Document document : documents) {
            String id = document.getObjectId("_id").toHexString();
            String name = document.getString("name");
            double price;
            try {
                price = document.getDouble("price");
            } catch (Exception e) {
                price = Double.valueOf(document.getInteger("price"));
            }
            String type = document.getString("type");
            int iva = document.getInteger("iva");
            String url = document.getString("url");

            Product p = new Product(name, price, type, iva, url);
            p.setId(id);
            products.add(p);
        }

        return products;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
        super.onPostExecute(products);
        mainActivity.updateProducts(products);
    }
}
