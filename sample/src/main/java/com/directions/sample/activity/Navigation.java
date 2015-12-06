package com.directions.sample.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.directions.sample.Map;
import com.directions.sample.R;
import com.directions.sample.SessionManager;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.SoundManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;


public class Navigation extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = Navigation.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    public int profile;
    List<Item> itemList;
    BlueListApplication blApplication;
    String name, mail;
    public static final String CLASS_NAME = "Navigation";
    byte[] imageAsBytes;
    // Alert Dialog Manager
    //AlertDialogManager alert = new AlertDialogManager();
    private SoundManager mSoundManager;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();
        setContentView(R.layout.slidingmenu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
        Bundle extras = getIntent().getExtras();
        name = extras.getString("ten");
        mail = extras.getString("mail");
        for (int i = 1; i < itemList.size(); i++)
            if (name.trim().equals(itemList.get(i).getName()) && mail.trim().equals(itemList.get(i).getMail()))
                profile = i;

        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        drawerFragment.setusername(itemList.get(profile).getNameuser(), mail);


        if (!itemList.get(profile).getImage3().equals("0")) {
            imageAsBytes = Base64.decode(itemList.get(profile).getImage3().getBytes(), Base64.DEFAULT);
            //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            drawerFragment.setimage(imageAsBytes);
        }


        // display the first navigation drawer view on app launch
        displayView(0);
        mAdView = (AdView) findViewById(R.id.ad_view);
        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("stt", profile);
        bundle.putString("ten", itemList.get(profile).getName());

        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                mSoundManager.playSound(1);
                fragment = new Map();
                title = getString(R.string.title_home);
                break;
            case 1:
                mSoundManager.playSound(1);
                fragment = new ProfileFragment();
                title = getString(R.string.title_profile);
                break;
            case 2:
                mSoundManager.playSound(1);
                fragment = new HistoryTripFragment();
                title = getString(R.string.title_historytrip);
                break;
            case 3:
                mSoundManager.playSound(1);
                fragment = new HistoryCardFragment();
                title = getString(R.string.title_historycrad);
                break;
            case 4:
                mSoundManager.playSound(1);
                fragment = new AboutFragment();
                title = getString(R.string.title_about);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for2Click();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            for2Click();
            return true;
        }
        //return false;
        return super.onKeyDown(keyCode, event);
    }

    private static Boolean isExit = false;
    private void for2Click() {

        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // Ready to quit
            Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // Cancel to exit
                }
            }, 2000); // 2 seconds

        } else {
           finish();
        }
    }
    public void onResume() {
        super.onResume();
        drawerFragment.setusername(itemList.get(profile).getNameuser(), mail);
        if (itemList.get(profile).getImage3().equals("0")) {
            Toast.makeText(getApplicationContext(), "Chưa có ảnh đại diện", Toast.LENGTH_LONG);
        } else {
            imageAsBytes = Base64.decode(itemList.get(profile).getImage3().getBytes(), Base64.DEFAULT);
            drawerFragment.setimage(imageAsBytes);

        }
        if (mAdView != null) {
            mAdView.resume();
        }
    }
    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();

    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}