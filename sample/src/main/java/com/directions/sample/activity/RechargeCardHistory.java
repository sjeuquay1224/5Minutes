package com.directions.sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.directions.sample.R;

/**
 * Created by RON on 10/15/2015.
 */
public class RechargeCardHistory extends AppCompatActivity {
    int sotien;
    private Toolbar toolbar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lichsugiaodich);
       /* Bundle extras = getIntent().getExtras();
        sotien = extras.getInt("sotien");
        */
        toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
