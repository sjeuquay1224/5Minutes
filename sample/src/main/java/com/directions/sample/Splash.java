package com.directions.sample;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.directions.sample.activity.Navigation;
import com.directions.sample.adapter.ConnectionDetector;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.GifMovieView;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.HistoryCard;
import com.ibm.bluelist.HistoryTrip;
import com.ibm.bluelist.Item;
import com.ibm.bluelist.Ranting;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;


/**
 * Created by RON on 9/22/2015.
 */
public class Splash extends Activity {

    // Session Manager Class
    SessionManager session;

    List<Item> itemList;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    List<HistoryTrip> historyTrips;
    List<Ranting> rantings;
    List<HistoryCard> historyCards;
    MediaPlayer song;
    public static final String CLASS_NAME = "Splash";

    // Connection detector class
    private ConnectionDetector cd;
    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBluelist();
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Toast.makeText(getApplicationContext(),"Loading...",Toast.LENGTH_LONG).show();
        song = MediaPlayer.create(this, R.raw.splash_sound);


        final GifMovieView gif1 = (GifMovieView) findViewById(R.id.gif1);
        gif1.setMovieResource(R.drawable.gif);
        checkinternet();

        listItems();
        listItemsHistoryTrip();
        listItemRating();
        listItemsHistoryCard();

        // Session class instance
        session = new SessionManager(getApplicationContext());


    }

    public void checkinternet() {
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Thông báo!");
            alertDialog.setMessage("Không có kết nối mạng. Khởi động lại");
            alertDialog.setIcon(R.drawable.icon_fail);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Đồng ý",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else song.start();

    }

    public void setupBluelist() {
        /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();
        historyTrips = blApplication.getHistoryTrips();
        rantings = blApplication.getRating();
        historyCards = blApplication.getHistoryCards();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        song.stop();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            song.stop();
            finish();
            return true;
        }
        //return false;
        return super.onKeyDown(keyCode, event);
    }

    public void listItems() {
        try {
            IBMQuery<Item> query = IBMQuery.queryForClass(Item.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<Item>, Void>() {

                @Override
                public Void then(Task<List<Item>> task) throws Exception {
                    final List<Item> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        //Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                        thongbaoloi();
                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        //Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                        thongbaoloi();
                    }


                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        itemList.clear();
                        for (IBMDataObject item : objects) {
                            itemList.add((Item) item);
                        }
                        chay();
                        sortItems(itemList);
                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItems(List<Item> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<Item>() {
            public int compare(Item lhs,
                               Item rhs) {
                String lhsName = lhs.getName() + lhs.getPass();
                String rhsName = rhs.getName() + lhs.getPass();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    public void listItemsHistoryTrip() {
        try {
            IBMQuery<HistoryTrip> query = IBMQuery.queryForClass(HistoryTrip.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<HistoryTrip>, Void>() {

                @Override
                public Void then(Task<List<HistoryTrip>> task) throws Exception {
                    final List<HistoryTrip> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");

                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }
                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        historyTrips.clear();
                        for (IBMDataObject item : objects) {
                            historyTrips.add((HistoryTrip) item);
                        }
                        // Start long running operation in a background thread
                        sortItemsHistoryTrip(historyTrips);
                        //lvArrayAdapter.notifyDataSetChanged();

                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);


        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItemsHistoryTrip(List<HistoryTrip> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<HistoryTrip>() {
            public int compare(HistoryTrip lhs,
                               HistoryTrip rhs) {
                String lhsName = lhs.getNAME();
                String rhsName = rhs.getNAME();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    public void listItemRating() {
        try {
            IBMQuery<Ranting> query = IBMQuery.queryForClass(Ranting.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<Ranting>, Void>() {

                @Override
                public Void then(Task<List<Ranting>> task) throws Exception {
                    final List<Ranting> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }


                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        rantings.clear();
                        for (IBMDataObject item : objects) {
                            rantings.add((Ranting) item);
                        }
                        sortItemsRating(rantings);
                        //lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItemsRating(List<Ranting> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<Ranting>() {
            public int compare(Ranting lhs,
                               Ranting rhs) {
                String lhsName = lhs.getNAME();
                String rhsName = rhs.getNAME();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    public void listItemsHistoryCard() {
        try {
            IBMQuery<HistoryCard> query = IBMQuery.queryForClass(HistoryCard.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<HistoryCard>, Void>() {

                @Override
                public Void then(Task<List<HistoryCard>> task) throws Exception {
                    final List<HistoryCard> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }


                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        historyCards.clear();
                        for (IBMDataObject item : objects) {
                            historyCards.add((HistoryCard) item);
                        }
                        sortItemsHistoryCard(historyCards);

                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItemsHistoryCard(List<HistoryCard> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<HistoryCard>() {
            public int compare(HistoryCard lhs,
                               HistoryCard rhs) {
                String lhsName = lhs.getName();
                String rhsName = rhs.getName();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    public void chay() {
        if (itemList.size() != 0 || historyTrips.size() != 0 || rantings.size() != 0 || historyCards.size() != 0) {
           // finishedEdit();
            if (session.isLoggedIn()) {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();
                // name
                String name = user.get(SessionManager.KEY_NAME);
                // email
                String email = user.get(SessionManager.KEY_EMAIL);

                if (name.equals("admin") && email.equals("admin@gmail.com")) {
                    Intent intent = new Intent(Splash.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Splash.this, Navigation.class);

                    intent.putExtra("ten", name);
                    intent.putExtra("mail", email);
                    startActivity(intent);
                }



                /*Thread background = new Thread(new Runnable() {
                    public void run() {
                        try {
                            while (itemList.size() == 0||historyTrips.size()==0||rantings.size()==0||historyCards.size()==0) {
                                Thread.sleep(100);
                            }


                        } catch (Throwable t) {
                            // just end the background thread
                        }
                    }
                });


                background.start();*/
            } else {
                session.checkLogin();

            }
        }
    }
    public void thongbaoloi() {
        AlertDialog alertDialog = new AlertDialog.Builder(Splash.this).create();
        alertDialog.setTitle("Thông báo!");
        alertDialog.setMessage("Quá trình tải dữ liệu đã xảy ra lỗi.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thử lại",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //setupBluelist();
                        listItems();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /*public void finishedEdit() {
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            item.setNameuser("Nguyễn Văn Hải");

            item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                @Override
                public Void then(Task<IBMDataObject> task) throws Exception {
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                    } else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    } else {
                        Intent returnIntent = new Intent();
                        setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                        finish();
                    }
                    return null;
                }

            }, Task.UI_THREAD_EXECUTOR);
        }
        Toast.makeText(getApplicationContext(), "Thành Công", Toast.LENGTH_LONG).show();
    }*/

}
