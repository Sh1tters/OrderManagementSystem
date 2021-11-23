package com.friertech.ordersystemproject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
    private Button okButton;
    @FXML
    private Button replyButton;
    @FXML
    private RadioButton PDF;
    @FXML
    private RadioButton Print;
    @FXML
    private Button printButton;
    @FXML
    private Text availableText;
    @FXML
    private TableView deviceList;


    public TableView<DevicesModel> deviceTable;
    public TableColumn<DevicesModel, Object> printers;

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
        Stage popupwindow= new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("OrderPopupScreen.fxml")));
        getSelectedItemData(dsh.collection, table.getSelectionModel().getSelectedItem().getName(), root);
        //update our printer tableview
        printers.setCellValueFactory(new PropertyValueFactory<>("printers"));
        updatePrinterTable();
        updateDescriptionField(root);
        Scene popup = new Scene(root);
        popupwindow.setResizable(false);
        popupwindow.setTitle("Order Information Window (" + table.getSelectionModel().getSelectedItem().getId() + ")");
        popupwindow.setScene(popup);
        popupwindow.showAndWait();

    }

    /** Gets the current selected item data and sets the Order Information text to the right data */
    public void getSelectedItemData(MongoCollection<Document> col, String user, Parent root) {
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                if(obj.getString("name").equals(user)){
                    Label nameHeader = (Label) root.lookup("#nameHeader");
                    nameHeader.setText(obj.getString("name"));

                    Label orderHeader = (Label) root.lookup("#orderHeader");
                    orderHeader.setText("Order #: BK-" + obj.getString("id"));

                    Label dateText = (Label) root.lookup("#dateText");
                    dateText.setText(obj.getString("date"));

                    Label numberText = (Label) root.lookup("#numberText");
                    numberText.setText(obj.getString("number"));

                    Label mailText = (Label) root.lookup("#mailText");
                    mailText.setText(obj.getString("mail"));

                    Label nameText = (Label) root.lookup("#nameText");
                    nameText.setText(obj.getString("name"));

                    Label orderText = (Label) root.lookup("#orderText");
                    orderText.setText("Order #: BK-" + obj.getString("id"));

                    Label statusText = (Label) root.lookup("#statusText");
                    statusText.setText("Status: " + obj.getString("status"));

                    Label descriptionHeader = (Label) root.lookup("#descriptionHeader");
                    descriptionHeader.setText("Order description (BK-" + obj.getString("id") + ")");
                }
            }
        } finally {
            cursor.close();
        }
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

    /** If ok button has been clicked in Order Information window */
    @FXML void onOkButtonClicked() throws IOException {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    /** If reply button has been clicked in Order Information window */
    @FXML void onReplyButtonClicked(){

    }

    /** If print radio button has been clicked in Order Information window */
    @FXML void onPrintClick(){
        PDF.setSelected(false);
        Print.setSelected(true);
        printButton.setDisable(true);
        availableText.setVisible(true);

        // able to click print button when printer machine selected

    }

    /** If pdf radio button has been clicked in Order Information window */
    @FXML void onPDFClick(){
        PDF.setSelected(true);
        Print.setSelected(false);
        printButton.setDisable(false);
        availableText.setVisible(false);
    }

    /** If print button has been clicked in Order Information window */
    @FXML void onPrintButtonClick(){

    }

    /** Sends mail */
    void sendMail(String to, String from, String host){
        // user auth

    }

    /** Updates description text field in Order Information window */
    void updateDescriptionField(Parent root){
        // set content of text area
        TextArea descriptionHeader = (TextArea) root.lookup("#descriptionField");
        descriptionHeader.setText(table.getSelectionModel().getSelectedItem().getDescription());
    }

    /** Updates our printer table view */
    void updatePrinterTable(){
        DevicesModel newDevice = new DevicesModel("bruh");
        deviceTable.getItems().add(newDevice);
    }


}
