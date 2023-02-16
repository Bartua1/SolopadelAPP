package com.example.myfirstapp;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("clients")
public class Client {
    @Id
    private String id;
    private String name;


    public Client(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'';
    }
}
