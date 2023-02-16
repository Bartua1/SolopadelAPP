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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RetrieveTickets extends AsyncTask<Void, Void, List<Ticket>> {

    private String host = "192.168.1.149";
    private int port = 27017;
    private MainActivity mainActivity;

    public RetrieveTickets(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected List<Ticket> doInBackground(Void... voids) {
        MongoClientURI connectionString = new MongoClientURI("mongodb://"+host+":"+port+"/");
        MongoClient mongoClient = MongoClients.create(String.valueOf(connectionString));
        MongoDatabase database = mongoClient.getDatabase("Solopadel");
        List<Ticket> tickets = new ArrayList<>();

        try {
            MongoCollection<Document> collection = database.getCollection("tickets");
            FindIterable<Document> documents = collection.find();

            for (Document document : documents) {
                Ticket t = new Ticket(new JSONObject(document.toJson()));
                tickets.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tickets;
    }

    @Override
    protected void onPostExecute(List<Ticket> tickets) {
        super.onPostExecute(tickets);
        mainActivity.updateTickets(tickets);
    }
}
