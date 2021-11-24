module com.friertech.ordersystemproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires json;
    requires java.desktop;
    requires itextpdf;

    opens com.friertech.ordersystemproject to javafx.fxml;
    exports com.friertech.ordersystemproject;
}