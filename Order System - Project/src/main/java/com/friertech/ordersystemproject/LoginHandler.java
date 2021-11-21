package com.friertech.ordersystemproject;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Handler;

import static com.mongodb.client.model.Filters.eq;
import static java.lang.System.out;

public class LoginHandler {
    public static ConnectionString connectionString = new ConnectionString("mongodb+srv://User:SZRNozvZpJAQxjhe@cluster0.yjmz2.mongodb.net/Nordjysk-Byggekontrol?retryWrites=true&w=majority");
    public static MongoClient mongoClient = MongoClients.create(connectionString);
    public static MongoDatabase database = mongoClient.getDatabase("Nordjysk-ByggeKontrol");
    public static MongoCollection collection = database.getCollection("Login");

    String[] abc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "!", "@", "#", "£", "$", "&", "¤", "?", "/", "\\"};
    String[] splitText;
    int Key = 18, endofAbc = 0;
    String data = "";

    boolean requestIDConfirmation(String id) {
        Object user = collection.find(eq("user", id)).first();

        if(user == null){
            return false;
        } else {
            /** Convert java object to json object */
            String jsonInString = new Gson().toJson(user);

            JSONObject jsonData = new JSONObject(jsonInString);

            /** Get value from key */
            String user_ = jsonData.getString("user");


            if (Objects.equals(id, user_)) {
                return true;
            } else {
                return false;
            }
        }
    }

    boolean requestPasswordConfirmation(String password) {
        String encryptedPassword = "";
        splitText = password.split("");
        for (int a = 0; a < splitText.length; a++) {
            for (int i = 0; i < abc.length; i++) {

                if (splitText[a].equalsIgnoreCase(abc[i])) {
                    if (i + Key >= abc.length) {
                        int index = Math.abs(abc.length - Key - i);
                        encryptedPassword = encryptedPassword + abc[index];
                        endofAbc = 0;
                    } else if (endofAbc != 1) {
                        encryptedPassword = encryptedPassword + abc[i + Key];
                    }
                }
            }
            if (splitText[a].equalsIgnoreCase(" ")) {
                encryptedPassword = encryptedPassword + " ";
            }
        }

        Object passw = collection.find(eq("pass", encryptedPassword)).first();

        if (passw == null) {
            return false;
        } else {
            /** Convert java object to json object */
            String jsonInString = new Gson().toJson(passw);

            JSONObject jsonData = new JSONObject(jsonInString);

            /** Get value from key */
            String pass = jsonData.getString("pass");

            /** Password already encrypted, so were just updating it. */
            password = encryptedPassword;

            if (Objects.equals(pass, password)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
