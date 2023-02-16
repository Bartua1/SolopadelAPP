package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    Button myButton;
    TextView myText;
    Log log;
    private String[] texts = {"Cum", "Sex", "Gay", "Tonto", "Hello World!"};
    private List<Product> products;
    private List<Client> clients;
    private List<Clientproduct> clientproducts;
    private List<Ticket> tickets;

    public void updateClients(List<Client> clients){
        this.clients = clients;
    }

    public void updateClientproducts(List<Clientproduct> clientproducts){
        this.clientproducts = clientproducts;
    }

    public void updateProducts(List<Product> products) {
        this.products = products;
        // update UI or use products in other parts of the code
    }

    public void updateTickets(List<Ticket> tickets){
        this.tickets = tickets;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton = (Button) findViewById(R.id.myButton);
        myButton.setText("Click me!");
        myText = findViewById(R.id.textView);
        new RetrieveProducts(this).execute();
        new RetrieveClients(this).execute();
        new RetrieveClientproducts(this).execute();
        new RetrieveTickets(this).execute();
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tickets.size()!=0) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(tickets.size());
                    while (myText.getText().equals(tickets.get(randomIndex))) {
                        randomIndex = random.nextInt(tickets.size());
                    }
                    myText.setText(tickets.get(randomIndex).getClient());
                }
                else {
                    myText.setText("No hay productos");
                }
            }
        });
    }

    public void miEvento(View view){
        Toast.makeText(this, "Hola Mundo", Toast.LENGTH_LONG).show();
        log.w("Rafa", "Mi mensage de debug");
    }


}
