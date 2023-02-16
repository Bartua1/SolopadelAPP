package com.example.myfirstapp;

import dev.morphia.annotations.Entity;

@Entity("clientproducts")
public class Clientproduct {
    private String id;
    private String client;
    private String product;
    private int quantity;
    private int iva;
    private double price;

    public Clientproduct(String client, String product, int quantity, int iva, double price) {
        this.client = client;
        this.product = product;
        this.quantity = quantity;
        this.iva = iva;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Clientproduct{" +
                "id='" + id + '\'' +
                ", client='" + client + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity + '\'' +
                ", iva=" + iva + '\'' +
                ", price=" + price +
                '}';
    }
}
