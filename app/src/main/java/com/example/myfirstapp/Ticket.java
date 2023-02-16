package com.example.myfirstapp;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("tickets")
public class Ticket {
    private String id;
    private String client;
    private Date date;
    private List<Clientproduct> clientproducts;
    private double total;

    public Ticket(String client, Date date, List<Clientproduct> clientproducts, double total) {
        this.client = client;
        this.date = date;
        this.clientproducts = clientproducts;
        this.total = total;
    }

    public Ticket(JSONObject jsonObject){
        try {
            this.client = jsonObject.getString("client");
            this.date = TransformDate(jsonObject.getString("date"));
            List<Clientproduct> cp = new ArrayList<>();
            for (int i = 0; i < jsonObject.getJSONArray("clientproducts").length(); i++) {
                String product = jsonObject.getJSONArray("clientproducts").getJSONObject(i).getString("product");
                int quantity = jsonObject.getJSONArray("clientproducts").getJSONObject(i).getInt("quantity");
                int iva = jsonObject.getJSONArray("clientproducts").getJSONObject(i).getInt("iva");
                double price = jsonObject.getJSONArray("clientproducts").getJSONObject(i).getDouble("price");
                String client = jsonObject.getJSONArray("clientproducts").getJSONObject(i).getString("client");
                Clientproduct c = new Clientproduct(client, product, quantity, iva, price);
                c.setId(jsonObject.getJSONArray("clientproducts").getJSONObject(i).getString("_id"));
                cp.add(c);
            }
            this.clientproducts = cp;
            this.total = jsonObject.getDouble("total");
        } catch (Exception e) {
            this.client = "";
            this.date = new Date();
            this.clientproducts = new ArrayList<>();
            this.total = 0;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date TransformDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(date.split(".")[0]);
        } catch (Exception e) {
            return new Date();
        }
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Clientproduct> getClientproducts() {
        return clientproducts;
    }

    public void setClientproduct(List<Clientproduct> clientproducts) {
        this.clientproducts = clientproducts;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
