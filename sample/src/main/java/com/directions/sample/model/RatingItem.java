package com.directions.sample.model;

import android.graphics.Bitmap;

/**
 * Created by RON on 11/9/2015.
 */
public class RatingItem {
    private String ten;
    private String title;
    private String content;
    private float so;


    public RatingItem(String ten, String title, String content,float so) {
        this.ten = ten;
        this.title = title;
        this.content = content;
        this.so=so;


    }
    public String getTen() {
        return ten;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public float getSo() {
        return so;
    }
}
