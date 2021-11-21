package com.friertech.ordersystemproject;

import javafx.scene.control.Button;

public class OrderModel {
    private String name;
    private String mail;
    private String id;
    private String date;
    private String desc;
    private String status;


    public OrderModel(String name, String id, String date, String mail, String status, String desc){
        this.name = name;
        this.mail = mail;
        this.id = id;
        this.date = date;
        this.desc = desc;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return desc;
    }

    public void setBudget(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
