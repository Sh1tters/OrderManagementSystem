package com.friertech.ordersystemproject;

import java.io.FileOutputStream;

import com.itextpdf.text.BadElementException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.bson.Document;
import org.json.JSONObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class OrderPopupScreenController implements Initializable {
    @FXML
    public TableView<DevicesModel> deviceTable;
    @FXML
    public TableColumn<DevicesModel, Object> printers;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        printers.setCellValueFactory(new PropertyValueFactory<>("printers"));

        updatePrinterTable();
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
                    dateText.setText("Date: "+obj.getString("date"));

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


    /** If ok button has been clicked in Order Information window */
    @FXML void onOkButtonClicked() throws IOException {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    /** If reply button has been clicked in Order Information window */
    @FXML void onReplyButtonClicked(){

    }

    /** If print radio button has been clicked in Order Information window */
    @FXML
    void onPrintClick(){
        PDF.setSelected(false);
        Print.setSelected(true);
        printButton.setDisable(true);
        availableText.setVisible(true);
        deviceTable.setDisable(false);

        // able to click print button when printer machine selected

    }

    /** If pdf radio button has been clicked in Order Information window */
    @FXML void onPDFClick(){
        PDF.setSelected(true);
        Print.setSelected(false);
        printButton.setDisable(false);
        availableText.setVisible(false);
        deviceTable.setDisable(true);
    }

    /** If print button has been clicked in Order Information window */
    @FXML void onPrintButtonClick() throws BadElementException, IOException {
        if(PDF.isSelected()){
            PDFController pc = new PDFController();
            pc.createPdfFile();
        }
    }

    /** Sends mail */
    void sendMail(String to, String from, String host){
        // user auth

    }

    /** Updates our printer table view */
    void updatePrinterTable(){
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            DevicesModel newDevice = new DevicesModel(printer.getName());
            deviceTable.getItems().add(newDevice);
        }
    }
}
