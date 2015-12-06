package com.directions.sample.model;

/**
 * Created by RON on 11/8/2015.
 */public class HistoryTripItem {
    private String diemdon;
    private String diemden;
    private String giatien;

    public HistoryTripItem(String diemdon, String diemden, String giatien) {
        this.diemdon = diemdon;
        this.diemden = diemden;
        this.giatien = giatien;

    }
    public String getDiemden() {
        return diemden;
    }

    public String getDiemdon() {
        return diemdon;
    }

    public String getGiatien() {
        return giatien;
    }
}