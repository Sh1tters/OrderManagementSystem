package com.friertech.ordersystemproject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import java.io.FileOutputStream;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Tab;


import static java.lang.System.out;
import static javax.swing.SwingConstants.RIGHT;
import static javax.swing.SwingConstants.TOP;

public class PDFController {

    /** Prints to a pdf file */
    void createPdfFile() throws BadElementException, IOException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        Document document = new Document();

        Image img = Image.getInstance(s + "\\src\\main\\resources\\com\\friertech\\ordersystemproject\\logo.png");
        float x = (PageSize.A4.getWidth() - img.getScaledWidth()) / 2;
        float y = (PageSize.A4.getHeight() - img.getScaledHeight()) - 100;
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Nordjysk-ByggeKontrol Order BK-1.pdf"));
            document.open();
            img.setAbsolutePosition(x, y);
            document.add(img);
            Paragraph nameHeader = new Paragraph("INSERT NAME HERE", FontFactory.getFont(FontFactory.TIMES_ROMAN,18, Font.BOLD, BaseColor.BLACK));
            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
