package com.odyssey.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    private int depZip;
    private String depState;
    private String depCity;

    private int delZip;
    private String delState;
    private String delCity;

    private ArrayList<Integer> itemID = new ArrayList<>();
    private ArrayList<Double> itemWeight = new ArrayList<>();
    private ArrayList<Double> itemVol = new ArrayList<>();
    private ArrayList<Boolean> itemHaz = new ArrayList<>();
    private ArrayList<String> itemProd = new ArrayList<>();

    public int getDepZip() {
        return depZip;
    }

    public void setDepZip(int depZip) {
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

    public int getDelZip() {
        return delZip;
    }

    public void setDelZip(int delZip) {
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

    public void setItemID(ArrayList<Integer> itemID) {
        this.itemID = itemID;
    }

    public ArrayList<Double> getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(ArrayList<Double> itemWeight) {
        this.itemWeight = itemWeight;
    }

    public ArrayList<Double> getItemVol() {
        return itemVol;
    }

    public void setItemVol(ArrayList<Double> itemVol) {
        this.itemVol = itemVol;
    }

    public ArrayList<Boolean> getItemHaz() {
        return itemHaz;
    }

    public void setItemHaz(ArrayList<Boolean> itemHaz) {
        this.itemHaz = itemHaz;
    }

    public ArrayList<String> getItemProd() {
        return itemProd;
    }

    public void setItemProd(ArrayList<String> itemProd) {
        this.itemProd = itemProd;
    }
}
