package com.friertech.ordersystemproject;

import java.awt.*;
import java.io.*;
import java.lang.annotation.ElementType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import java.io.FileOutputStream;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.layout.Region;
import org.json.JSONObject;


import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.filechooser.FileSystemView;

import static java.lang.System.out;
import static javax.swing.SwingConstants.RIGHT;
import static javax.swing.SwingConstants.TOP;

public class PDFController {

    /** Prints to a pdf file */
    void createPdfFile(MongoCollection<org.bson.Document> col, String user, Parent root) throws BadElementException, IOException {
        // Performing a read operation on the collection.
        FindIterable<org.bson.Document> fi = col.find();
        MongoCursor<org.bson.Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                JSONObject obj = new JSONObject(cursor.next().toJson());
                String name = obj.getString("name");
                if(name.equals(user)){
                    Path currentRelativePath = Paths.get("");
                    String s = currentRelativePath.toAbsolutePath().toString();
                    Document document = new Document();

                    Image img = Image.getInstance(s + "\\src\\main\\resources\\com\\friertech\\ordersystemproject\\logo.png");
                    float x = (PageSize.A4.getWidth() - img.getScaledWidth()) / 2+100;
                    float y = (PageSize.A4.getHeight() - img.getScaledHeight()) - 100;
                    try
                    {
                        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"\\Nordjysk-ByggeKontrol Order BK-"+obj.getString("id")+".pdf"));
                        document.open();
                        img.setAbsolutePosition(x, y);
                        document.add(img);
                        Paragraph nameHeader = new Paragraph(user, FontFactory.getFont(FontFactory.TIMES_ROMAN,20, Font.BOLD, BaseColor.BLACK));
                        document.add(nameHeader);
                        Paragraph idHeader = new Paragraph("BK-"+obj.getString("id"), FontFactory.getFont(FontFactory.TIMES_ROMAN,15, Font.BOLD, BaseColor.BLACK));
                        document.add(idHeader);
                        document.add(new Paragraph("\n"));
                        document.add(new Paragraph("\n"));
                        Paragraph date = new Paragraph("Date: "+obj.getString("date"), FontFactory.getFont(FontFactory.TIMES_ROMAN,10, Font.NORMAL, BaseColor.BLACK));
                        document.add(date);
                        Paragraph order = new Paragraph("Order #: "+obj.getString("id"), FontFactory.getFont(FontFactory.TIMES_ROMAN,10, Font.NORMAL, BaseColor.BLACK));
                        document.add(order);
                        Paragraph user_ = new Paragraph(obj.getString("name"), FontFactory.getFont(FontFactory.TIMES_ROMAN,10, Font.NORMAL, BaseColor.BLACK));
                        document.add(user_);
                        Paragraph mail = new Paragraph(obj.getString("mail"), FontFactory.getFont(FontFactory.TIMES_ROMAN,10, Font.NORMAL, BaseColor.BLACK));
                        document.add(mail);
                        Paragraph number = new Paragraph(obj.getString("number"), FontFactory.getFont(FontFactory.TIMES_ROMAN,10, Font.NORMAL, BaseColor.BLACK));
                        document.add(number);
                        Paragraph status = new Paragraph("Status: "+obj.getString("status"), FontFactory.getFont(FontFactory.TIMES_ROMAN,10, Font.NORMAL, BaseColor.BLACK));
                        document.add(status);
                        document.add(new Paragraph("\n"));
                        Font fontQouteItems = new Font(BaseFont.createFont(), 12);
                        PdfContentByte canvas = writer.getDirectContent();

                        // Item Number
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(obj.getString("date"), fontQouteItems), 60, 450, 0);

                        // Estimated Qty
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("Order #: " +obj.getString("id"), fontQouteItems), 143, 450, 0);

                        // Item Description
                        ColumnText ct = new ColumnText(canvas); // Uses a simple column box to provide proper text wrapping
                        ct.setSimpleColumn(new Rectangle(193, 070, 390, 450));
                        ct.setText(new Phrase(obj.getString("description"), fontQouteItems));
                        ct.go();

                        document.close();
                        writer.close();


                        // open dialog
                        Alert alert = new Alert(Alert.AlertType.NONE, "Sucessfully created PDF file ("+FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"\\Nordjysk-ByggeKontrol Order BK-"+obj.getString("id")+".pdf"+")", ButtonType.OK);
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        alert.show();

                        // open document on computer
                        OrderPopupScreenController odsc = new OrderPopupScreenController();
                        if (Desktop.isDesktopSupported()) {
                            try {
                                File myFile = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"\\Nordjysk-ByggeKontrol Order BK-"+obj.getString("id")+".pdf");
                                Desktop.getDesktop().open(myFile);
                            } catch (IOException ex) {
                                // no application registered for PDFs
                            }
                        }
                    } catch (DocumentException e)
                    {
                        e.printStackTrace();
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            cursor.close();
        }
    }
}
