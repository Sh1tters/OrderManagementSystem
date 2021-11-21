package com.friertech.ordersystemproject;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;

import static java.lang.System.out;

public class DashScreenHandler {
    public ConnectionString connectionString = new ConnectionString("mongodb+srv://User:SZRNozvZpJAQxjhe@cluster0.yjmz2.mongodb.net/Nordjysk-Byggekontrol?retryWrites=true&w=majority");
    public MongoClient mongoClient = MongoClients.create(connectionString);
    public MongoDatabase database = mongoClient.getDatabase("Nordjysk-ByggeKontrol");
    public MongoCollection collection = database.getCollection("Orders");



}
