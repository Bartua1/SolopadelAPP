package com.example.myfirstapp;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("products")
public class Product {
    @Id
    private String id;

    private String name;
    private double price;
    private String type;
    private int iva;
    private String url;


    public Product() {}

    public Product(String name, double price, String type, int iva, String url) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.iva = iva;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", iva='" + iva + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
