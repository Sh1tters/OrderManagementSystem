package com.friertech.ordersystemproject;

public class DevicesModel {
    private String printername;
    private String orentations;

    public DevicesModel(String printername) {
        this.printername = printername;
    }

    public String getPrintername() {
        return printername;
    }

    public void setPrintername(String printername) {
        this.printername = printername;
    }

}
