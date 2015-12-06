package com.directions.sample.model;

/**
 * Created by RON on 11/9/2015.
 */
public class HistoryCardItem {
    private String seri;
    private String gia;
    private String date;
    private String loai;
    private String id;


    public HistoryCardItem(String seri, String gia, String date,String loai,String id) {
        this.seri = seri;
        this.gia = gia;
        this.date = date;
        this.loai=loai;
        this.id=id;


    }
    public String getSeri() {
        return seri;
    }

    public String getGia() {
        return gia;
    }

    public String getDate() {
        return date;
    }

    public String getLoai() {
        return loai;
    }

    public String getId() {
        return id;
    }
}
