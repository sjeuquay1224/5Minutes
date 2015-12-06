package com.directions.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.directions.sample.model.SoundManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by RON on 11/11/2015.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    Button logout;
    // Session Manager Class
    SessionManager session;
    Vibrator rung;
    private SoundManager mSoundManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Session class instance
        session = new SessionManager(getApplicationContext());


        logout = (Button) findViewById(R.id.btn_logout);
        logout.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cài đặt");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_logout) {
            mSoundManager.playSound(1);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle("Đăng Xuất!");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Bạn muốn đăng xuất ngay bây giờ?")
                    .setCancelable(false)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // Clear the session data
                            // This will clear all session data and
                            // redirect user to LoginActivity
                            session.logoutUser();
                            rung.vibrate(500);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(R.drawable.icon_fail)
                    .show();

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}
