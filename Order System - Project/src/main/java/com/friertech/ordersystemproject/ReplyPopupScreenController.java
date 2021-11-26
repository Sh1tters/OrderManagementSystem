package com.friertech.ordersystemproject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.bson.Document;
import org.json.JSONObject;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ReplyPopupScreenController implements Initializable {
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private TextField message;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }




    void updateFields(MongoCollection<Document> col, String user){
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("name").equals(user)){
                   // from.setText("info@nordjysk-byggekontrol.dk");
                    //to.setText(obj.getString("mail"));
                }
            }
        } finally {
            cursor.close();
        }
    }
}
