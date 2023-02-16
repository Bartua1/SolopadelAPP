package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

public class Elements {
    public List<Product> products;
    public List<Client> clients;
    public List<Clientproduct> test;
    public List<Ticket> tickets;

    public Elements(List<Product> products, List<Client> clients, List<Clientproduct> test, List<Ticket> tickets) {
        this.products = products;
        this.clients = clients;
        this.test = test;
        this.tickets = tickets;
    }

    public Elements(){
        this.products = new ArrayList<Product>();
        this.clients = new ArrayList<Client>();
        this.test = new ArrayList<Clientproduct>();
        this.tickets = new ArrayList<Ticket>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Clientproduct> getClientProducts() {
        return test;
    }

    public void setClientProducts(List<Clientproduct> test) {
        this.test = test;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
