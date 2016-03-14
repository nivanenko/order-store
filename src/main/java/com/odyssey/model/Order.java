package com.odyssey.model;

import java.util.ArrayList;

public class Order {

    private int depID;
    private String depZip;
    private String depState;
    private String depCity;

    private int delID;
    private String delZip;
    private String delState;
    private String delCity;

    private ArrayList<Integer> itemID = new ArrayList<>();
    private ArrayList<Double> itemWeight = new ArrayList<>();
    private ArrayList<Double> itemVol = new ArrayList<>();
    private ArrayList<Boolean> itemHazBool = new ArrayList<>();
    private ArrayList<Integer> itemHazInt = new ArrayList<>();
    private ArrayList<String> itemProd = new ArrayList<>();

    public int getDepID() {
        return depID;
    }

    public void setDepID(int depID) {
        this.depID = depID;
    }

    public String getDepZip() {
        return depZip;
    }

    public void setDepZip(String depZip) {
        this.depZip = depZip;
    }

    public String getDepState() {
        return depState;
    }

    public void setDepState(String depState) {
        this.depState = depState;
    }

    public String getDepCity() {
        return depCity;
    }

    public void setDepCity(String depCity) {
        this.depCity = depCity;
    }

    public int getDelID() {
        return delID;
    }

    public void setDelID(int delID) {
        this.delID = delID;
    }

    public String getDelZip() {
        return delZip;
    }

    public void setDelZip(String delZip) {
        this.delZip = delZip;
    }

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState;
    }

    public String getDelCity() {
        return delCity;
    }

    public void setDelCity(String delCity) {
        this.delCity = delCity;
    }

    public ArrayList<Integer> getItemID() {
        return itemID;
    }

    public ArrayList<Double> getItemWeight() {
        return itemWeight;
    }

    public ArrayList<Double> getItemVol() {
        return itemVol;
    }

    public ArrayList<Boolean> getItemHazBool() {
        return itemHazBool;
    }

    public ArrayList<Integer> getItemHazInt() {
        return itemHazInt;
    }

    public ArrayList<String> getItemProd() {
        return itemProd;
    }
}