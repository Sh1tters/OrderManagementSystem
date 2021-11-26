package com.friertech.ordersystemproject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.bson.Document;
import org.json.JSONObject;

import static java.lang.System.out;
import java.io.FileInputStream;
import java.io.IOException;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.Copies;
import javax.print.event.*;
import javax.swing.filechooser.FileSystemView;

public class PrintingService {
    void main(MongoCollection<Document> col, String user, Parent root) throws PrintException,
            IOException{
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                try{
                    JSONObject obj = new JSONObject(cursor.next().toJson());
                    if(obj.getString("name").equals(user)){
                        PrintService ps=PrintServiceLookup.lookupDefaultPrintService();
                        DocPrintJob job=ps.createPrintJob();
                        job.addPrintJobListener(new PrintJobAdapter() {
                            public void printDataTransferCompleted(PrintJobEvent event){
                                out.println("data transfer complete");
                            }
                            public void printJobNoMoreEvents(PrintJobEvent event){
                                out.println("received no more events");
                            }
                        });
                        FileInputStream fis=new FileInputStream(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"\\Nordjysk-ByggeKontrol Order BK-"+obj.getString("id")+".pdf");
                        Doc doc=new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
                        // Doc doc=new SimpleDoc(fis, DocFlavor.INPUT_STREAM.JPEG, null);
                        PrintRequestAttributeSet attrib=new HashPrintRequestAttributeSet();
                        attrib.add(new Copies(1));
                        job.print(doc, attrib);
                    }
                } catch(Exception e){
                    // open dialog
                    Alert alert = new Alert(Alert.AlertType.NONE, ""+e, ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.show();
                }
            }
        } finally {
            cursor.close();
        }
    }
}
