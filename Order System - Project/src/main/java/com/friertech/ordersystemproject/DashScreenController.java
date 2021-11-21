package com.friertech.ordersystemproject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
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
    @FXML
    private CheckMenuItem colordisplayer;

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
                "Sort by oldest",
                "Sort by pending orders",
                "Sort by confirmed orders",
                "Sort by archived orders",
                "Sort by ignored orders"
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

        table.setOnMouseClicked((
                MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                out.println(table.getSelectionModel().getSelectedItem().getName());
            }
        });
        table.setRowFactory(tv -> new TableRow<OrderModel>() {
            @Override
            public void updateItem(OrderModel item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getStatus().equals("CONFIRMED")) {
                    setStyle("-fx-background-color: green;");
                } else if (item.getStatus().equals("IGNORED")){
                    setStyle("-fx-background-color: tomato;");
                } else if(item.getStatus().equals("ARCHIVED")){
                    setStyle("-fx-background-color: grey;");
                } else if(item.getStatus().equals("PENDING")){
                    setStyle("-fx-background-color: lightgrey;");
                } else {
                    setStyle("");
                }
            }
        });

    }

    @FXML void onColorDisplayerChange(){

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

    @FXML
    public void onPendingClick() throws ParseException {
        combobox.setValue("Sort by pending orders");
        updateTable();
    }
    @FXML
    public void onConfirmedClick() throws ParseException {
        combobox.setValue("Sort by confirmed orders");
        updateTable();
    }
    @FXML
    public void onIgnoredClick() throws ParseException {
        combobox.setValue("Sort by ignored orders");
        updateTable();
    }
    @FXML
    public void onArchivedClick() throws ParseException {
        combobox.setValue("Sort by archived orders");
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
        // delete all tableview items
        for(int i = 0; i < table.getItems().size(); i++){
            table.getItems().clear();
        }
        if(filter.equals("Sort by newest")){
            // Fetch documents from mongodb
            getAllNewestOrders(dsh.collection);

        } else if(filter.equals("Sort by oldest")){
            // Fetch documents from mongodb
            getAllOldestOrders(dsh.collection);
        } else if(filter.equals("Sort by pending orders")){
            // Fetch documents from mongodb
            getAllPendingOrders(dsh.collection);
        }else if(filter.equals("Sort by confirmed orders")){
            // Fetch documents from mongodb
            getAllConfirmedOrders(dsh.collection);
        }else if(filter.equals("Sort by archived orders")){
            // Fetch documents from mongodb
            getAllArchivedOrders(dsh.collection);
        }else if(filter.equals("Sort by ignored orders")){
            // Fetch documents from mongodb
            getAllIgnoredOrders(dsh.collection);
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
    public void getAllConfirmedOrders(MongoCollection<Document> col) {
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("status").equals("CONFIRMED")){
                    OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));
                    table.getItems().add(newOrder);
                }
            }
        } finally {
            cursor.close();
        }
    }

    // Method to fetch all documents from the mongo collection.
    public void getAllIgnoredOrders(MongoCollection<Document> col) {
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("status").equals("IGNORED")){
                    OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));
                    table.getItems().add(newOrder);
                }
            }
        } finally {
            cursor.close();
        }
    }

    // Method to fetch all documents from the mongo collection.
    public void getAllArchivedOrders(MongoCollection<Document> col) {
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("status").equals("ARCHIVED")){
                    OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));
                    table.getItems().add(newOrder);
                }
            }
        } finally {
            cursor.close();
        }
    }

    // Method to fetch all documents from the mongo collection.
    public void getAllPendingOrders(MongoCollection<Document> col) {
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("status").equals("PENDING")){
                    OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));
                    table.getItems().add(newOrder);
                }
            }
        } finally {
            cursor.close();
        }
    }

    // Method to fetch all documents from the mongo collection.
    public void getAllOldestOrders(MongoCollection<Document> col) {
        // In order to reverse the iterate, we will append each json to an array list string and there for reverse that list string and take each json out from there

        // For ArrayList
        List<OrderModel> list = new ArrayList<>();

        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                OrderModel newOrder = new OrderModel(obj.getString("name"), "BK-"+obj.getString("id"), obj.getString("date"), obj.getString("mail"), obj.getString("status"), obj.getString("description"));

                // adding the elements
                list.add(newOrder);
            }
        } finally {
            cursor.close();
        }
        // print list in Reverse using for loop
        for (int i = list.size() - 1; i >= 0; i--)
        {
            // access elements by their index (position)
            table.getItems().add(list.get(i));
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
