package com.friertech.ordersystemproject;

import com.itextpdf.text.BadElementException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
// File Name SendHTMLEmail.java


public class DashScreenController implements Initializable {
    DashScreenHandler dsh = new DashScreenHandler();
    int confirmed = 0, ignored = 0, pending = 0, archived = 0;
    String filter = "";
    String currentUser;

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
    @FXML
    private CheckMenuItem darkmode;
    @FXML
    private Button vieworder;
    @FXML
    private Button ignore;
    @FXML
    private Button confirm;
    @FXML
    private Button archive;


    public TableView<OrderModel> table;
    public TableColumn<OrderModel, Object> id;
    public TableColumn<OrderModel, Object> date;
    public TableColumn<OrderModel, Object> name;
    public TableColumn<OrderModel, Object> mail;
    public TableColumn<OrderModel, Object> description;
    public TableColumn<OrderModel, Object> status;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(combobox != null){
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
                    vieworder.setDisable(false);
                    ignore.setDisable(false);
                    confirm.setDisable(false);
                    archive.setDisable(false);
                }
            });

            table.setRowFactory(tv -> new TableRow<OrderModel>() {
                @Override
                public void updateItem(OrderModel item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if(colordisplayer.isSelected()) {
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
                    } else {
                        setStyle("");
                    }
                }
            });
        }

    }

    /** If view order button has been clicked */
    @FXML void onViewOrderClick() throws IOException {
        OrderPopupScreenController opsc = new OrderPopupScreenController();
        Stage popupwindow= new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("OrderPopupScreen.fxml")));
        opsc.getSelectedItemData(dsh.collection, table.getSelectionModel().getSelectedItem().getName(), root);
        //update our printer tableview
        updateDescriptionField(root);
        Scene popup = new Scene(root);
        popupwindow.setResizable(false);
        popupwindow.setTitle("Order Information Window (" + table.getSelectionModel().getSelectedItem().getId() + ")");
        popupwindow.setScene(popup);
        popupwindow.showAndWait();

    }

    /** Updates description text field in Order Information window */
    void updateDescriptionField(Parent root){
        // set content of text area
        TextArea descriptionHeader = (TextArea) root.lookup("#descriptionField");
        descriptionHeader.setText(table.getSelectionModel().getSelectedItem().getDescription());
    }

    /** Changes to darkmode/lightmode */
    @FXML void onDarkModeChange(){
        //darkmode
        Scene scene = (Scene) table.getScene();
        if(darkmode.isSelected()){
            scene.getRoot().setStyle("-fx-base:black");
        } else {
            scene.getRoot().setStyle("");
        }
    }

    /** Changes the tableview row colors */
    @FXML void onColorDisplayerChange(){
        table.setRowFactory(tv -> new TableRow<OrderModel>() {
            @Override
            public void updateItem(OrderModel item, boolean empty) {
                super.updateItem(item, empty) ;
                if(colordisplayer.isSelected()) {
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
                } else {
                    setStyle("");
                }
            }
        });
    }

    /** If user typed in search bar */
    @FXML
    public void onKeyTyped(KeyEvent event) {
        updateTableById(searchField.getText().toString());
    }

    /** If combonox has been clicked */
    @FXML
    public void onComboboxClick() throws ParseException {
        // Update new filter method on tableview
        updateTable();
    }

    /** If menu pending button has been clicked */
    @FXML
    public void onPendingClick() throws ParseException {
        combobox.setValue("Sort by pending orders");
        updateTable();
    }
    /** If menu confirmed button has been clicked */
    @FXML
    public void onConfirmedClick() throws ParseException {
        combobox.setValue("Sort by confirmed orders");
        updateTable();
    }
    /** If menu ignored button has been clicked */
    @FXML
    public void onIgnoredClick() throws ParseException {
        combobox.setValue("Sort by ignored orders");
        updateTable();
    }
    /** If menu archived button has been clicked */
    @FXML
    public void onArchivedClick() throws ParseException {
        combobox.setValue("Sort by archived orders");
        updateTable();
    }

    /** Updates the table by id in tableview */
    void updateTableById(String id){
        // delete all tableview items
        for(int i = 0; i < table.getItems().size(); i++){
            table.getItems().clear();
        }
        getAllSearchOrders(dsh.collection, id);
    }

    /** Updates the table in tableview */
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

    /** Updates the menu items in our menubar */
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

    /** Gets all confirmed orders */
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

    /** Gets all ignored orders */
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

    /** Gets all archived orders */
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

    /** Gets all pending orders */
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

    /** Gets all oldest orders */
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

    /** Gets all newest orders */
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

    /** Gets all orders from the search */
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



    /** On ignore button click event */
    @FXML
    public void onIgnoreClicked() throws ParseException {
        updateOrderStatus(dsh.collection,"IGNORED", table.getSelectionModel().getSelectedItem().getName());
        updateTable();
    }

    /** On confirm button click event */
    @FXML
    public void onConfirmClicked() throws ParseException {
        updateOrderStatus(dsh.collection,"CONFIRMED", table.getSelectionModel().getSelectedItem().getName());
        updateTable();
    }

    /** On archive button click event */
    @FXML
    public void onArchiveClicked() throws ParseException {
        updateOrderStatus(dsh.collection,"ARCHIVED", table.getSelectionModel().getSelectedItem().getName());
        updateTable();
    }


    /** Updates status of order */
    public void updateOrderStatus(MongoCollection<Document> col, String status, String user){
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("name").equals(user)){
                    col.updateOne(Filters.eq("name", user), Updates.set("status", status));
                }

            }
        } finally {
            cursor.close();
        }
    }


}
