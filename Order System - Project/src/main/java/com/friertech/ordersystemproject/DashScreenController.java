package com.friertech.ordersystemproject;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static java.lang.System.out;

public class DashScreenController implements Initializable {
    DashScreenHandler dsh = new DashScreenHandler();
    int confirmed = 0, ignored = 0, pending = 0, archived = 0;
    String filter = "";

    @FXML
    private ComboBox<String> combobox;
    @FXML
    private MenuItem pendingmenu;
    @FXML
    private MenuItem confirmedmenu;
    @FXML
    private MenuItem ignoredmenu;
    @FXML
    private MenuItem archivedmenu;
    @FXML
    private TextField searchField;

    public TableView<OrderModel> table;

    public TableColumn<OrderModel, Object> id;
    public TableColumn<OrderModel, Object> date;
    public TableColumn<OrderModel, Object> name;
    public TableColumn<OrderModel, Object> mail;
    public TableColumn<OrderModel, Object> description;
    public TableColumn<OrderModel, Object> status;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        combobox.getItems().addAll(
                "Sort by newest",
                "Sort by oldest"
        );

        // Update table
        try {
            updateTable();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // add item to tableview
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        mail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Update menu item names
        updateMenuItems();



    }

    @FXML
    public void onKeyTyped(KeyEvent event) {
        updateTableById(searchField.getText().toString());
    }

    @FXML
    public void onComboboxClick() throws ParseException {
        // Update new filter method on tableview
        updateTable();
    }

    void updateTableById(String id){
        // delete all tableview items
        for(int i = 0; i < table.getItems().size(); i++){
            table.getItems().clear();
        }
        getAllSearchOrders(dsh.collection, id);
    }

    void updateTable() throws ParseException {
        filter = combobox.getSelectionModel().getSelectedItem();
        if(filter == null) filter = "Sort by newest";


        if(filter.equals("Sort by newest")){
            // delete all tableview items
            for(int i = 0; i < table.getItems().size(); i++){
                table.getItems().clear();
            }
            // Fetch documents from mongodb
            getAllNewestOrders(dsh.collection);

        } else if(filter.equals("Sort by oldest")){
            // delete all tableview items
            for(int i = 0; i < table.getItems().size(); i++){
                table.getItems().clear();
            }
            // Fetch documents from mongodb
            getAllOldestOrders(dsh.collection);
        }
    }

    void updateMenuItems(){
        for(int i = 0; i < table.getItems().size(); i++){
            String status = table.getItems().get(i).getStatus();

            // check each status
            if(Objects.equals(status, "PENDING")) pending++;
            if(Objects.equals(status, "CONFIRMED")) confirmed++;
            if(Objects.equals(status, "IGNORED")) ignored++;
            if(Objects.equals(status, "ARCHIVED")) archived++;
        }
        pendingmenu.setText("Pending ("+pending+")");
        confirmedmenu.setText("Confirmed ("+confirmed+")");
        ignoredmenu.setText("Ignored ("+ignored+")");
        archivedmenu.setText("Archived ("+archived+")");
    }

    // Method to fetch all documents from the mongo collection.
    public void getAllOldestOrders(MongoCollection<Document> col) {

        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    // Method to fetch all documents from the mongo collection.
    public void getAllNewestOrders(MongoCollection<Document> col) {
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));
                table.getItems().add(newOrder);

            }
        } finally {
            cursor.close();
        }
    }

    // Method to fetch all documents with a search id from the mongo collection.
    public void getAllSearchOrders(MongoCollection<Document> col, String id) {
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                String bk = "BK-"+obj.getString("id");
                if(id.contains(obj.getString("id")) || bk.startsWith(id)){
                    OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));
                    table.getItems().add(newOrder);
                }
            }
        } finally {
            cursor.close();
        }
    }


}
