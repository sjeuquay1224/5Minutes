package com.directions.sample.activity;

import android.app.AlertDialog;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.sample.R;

import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.SoundManager;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.HistoryCard;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;
import com.viettel.android.gsm.ViettelClient;
import com.viettel.android.gsm.ViettelError;
import com.viettel.android.gsm.VtResponseCode;
import com.viettel.android.gsm.charging.ChargingGateWayApi;
import com.viettel.android.gsm.charging.PaymentInfo;
import com.viettel.android.gsm.charging.TopupInfo;
import com.viettel.android.gsm.listener.ViettelOnResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by RON on 10/15/2015.
 */
public class NapTheActivity extends AppCompatActivity implements View.OnClickListener, ViettelClient.OnConnectionCallbacks {
    Button nap, rut,Charging;
    private Toolbar toolbar;
    List<Item> itemList;
    List<HistoryCard> historyCards;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    int sothutu;
    protected ViettelClient viettelClient;
    String PublisherID = "122245", AppID = "12608";
    public static final String CLASS_NAME = "NapTheActivity";
    private SoundManager mSoundManager;
    AlertDialogManager alert = new AlertDialogManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();
        historyCards=blApplication.getHistoryCards();
        setContentView(R.layout.napthe);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mSoundManager.addSound(2, R.raw.success);
        mSoundManager.addSound(3, R.raw.warring);


		//khởi tạo Viettel Api
        viettelClient = new ViettelClient(this, this);
        viettelClient.setViettelId(PublisherID, AppID);
        viettelClient.connect();

        Bundle extras = getIntent().getExtras();
        sothutu = extras.getInt("sothutu");


        //Toast.makeText(getApplicationContext(), itemList.get(sothutu).getName().toString(), Toast.LENGTH_LONG).show();
        Charging= (Button) findViewById(R.id.btn_Charging);
        Charging.setOnClickListener(this);
        nap = (Button) findViewById(R.id.btn_nap);
        nap.setOnClickListener(this);
        rut = (Button) findViewById(R.id.btn_rut);
        rut.setOnClickListener(this);

        if (itemList.get(sothutu).getQuyen().equalsIgnoreCase("CUSTOMER"))
            rut.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thanh toán");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (v.getId() == R.id.btn_nap) {
            mSoundManager.playSound(1);
            if (viettelClient != null) {
                ChargingGateWayApi.topupCardApp(viettelClient, new ViettelOnResponse<TopupInfo>() {
                    @Override
                    public void onResult(final TopupInfo topupInfo, final int vtCode) {

                        if (vtCode == VtResponseCode.VT_RESULT_OK && topupInfo != null) {
                            mSoundManager.playSound(2);
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                            String strDate = sdf.format(c.getTime());
                            createItemHistoryCard(topupInfo.getCardSerial(),topupInfo.getAmount(),strDate,topupInfo.getTransactionId());

                            Item item = itemList.get(sothutu);
                            Float sotien = Float.parseFloat(itemList.get(sothutu).getMoney().trim()) + Float.parseFloat(topupInfo.getAmount());
                            String str = Float.toString(sotien);
                            item.setMoney(str);
                            /**
                             * IBMObjectResult is used to handle the response from the server after
                             * either creating or saving an object.
                             *
                             * onResult is called if the object was successfully saved.
                             * onError is called if an error occurred saving the object.
                             */
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
                            mSoundManager.playSound(2);
                            alert.showAlertDialog(NapTheActivity.this, "Thành Công", "Chúc mừng bạn đã nạp thẻ thành công",true);
                            //Toast.makeText(getApplicationContext(), "Thành Công " + topupInfo.getAmount(), Toast.LENGTH_LONG).show();
                        } else {
                            mSoundManager.playSound(3);
                            alert.showAlertDialog(NapTheActivity.this, "Thất bại", "Giao dịch đã bị hủy", false);
                            //Toast.makeText(NapTheActivity.this, "Thất Bại" + "\ntopupInfo: " + (topupInfo != null ? topupInfo.toString() : "") + "\nvtCode: " + vtCode, Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }else
                    alert.showAlertDialog(getApplicationContext(),"Cảnh báo","Mất kết nối",false);
        }
        if (v.getId() == R.id.btn_rut) {
            mSoundManager.playSound(1);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    NapTheActivity.this);
            // set title
            alertDialogBuilder.setTitle("Thông báo!");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Bạn muốn rút tiền trong tài khoản?")
                    .setCancelable(false)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        if (v.getId() == R.id.btn_Charging){
            mSoundManager.playSound(1);
            String pack="capstonecmu";
            String phonenumber=itemList.get(sothutu).getPhone();
            if (viettelClient != null) {
                ChargingGateWayApi.processCharging(viettelClient, phonenumber, pack, new ViettelOnResponse<PaymentInfo>() {
                    @Override
                    public void onResult(PaymentInfo paymentInfo, int vtCode) {

                        if (vtCode == VtResponseCode.VT_RESULT_OK && paymentInfo != null) {
                            mSoundManager.playSound(2);
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                            String strDate = sdf.format(c.getTime());
                            createItemHistoryCard(paymentInfo.getMsisdn(), paymentInfo.getAmount(), strDate, paymentInfo.getTransactionId());

                            Item item = itemList.get(sothutu);
                            Float sotien = Float.parseFloat(itemList.get(sothutu).getMoney().trim()) + Float.parseFloat(paymentInfo.getAmount());
                            String str = Float.toString(sotien);
                            item.setMoney(str);
                            /**
                             * IBMObjectResult is used to handle the response from the server after
                             * either creating or saving an object.
                             *
                             * onResult is called if the object was successfully saved.
                             * onError is called if an error occurred saving the object.
                             */
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
                            mSoundManager.playSound(2);
                            alert.showAlertDialog(NapTheActivity.this, "Thành công", "Chúc mừng bạn đã nạp thẻ thành công", true);
                            //Toast.makeText(getApplicationContext(), "Thành Công " + topupInfo.getAmount(), Toast.LENGTH_LONG).show();
                        } else {
                            mSoundManager.playSound(3);
                            alert.showAlertDialog(NapTheActivity.this, "Thất bại", "Giao dịch đã bị hủy", false);
                            //Toast.makeText(NapTheActivity.this, "Thất Bại" + "\ntopupInfo: " + (topupInfo != null ? topupInfo.toString() : "") + "\nvtCode: " + vtCode, Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }else
                alert.showAlertDialog(getApplicationContext(),"Cảnh báo","Mất kết nối",false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viettelClient != null) {
            viettelClient.onDestroy();
        }

    }

    @Override
    public void onConnected() {
        final LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView1);
        rootView.removeAllViews();
        TextView textView = new TextView(NapTheActivity.this);
        textView.setText("Connected");
        rootView.setGravity(Gravity.TOP);
        rootView.addView(textView);
        rootView.removeAllViews();
    }

    @Override
    public void onConnectFail(ViettelError viettelError) {
        final LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView1);
        rootView.removeAllViews();
        TextView textView = new TextView(NapTheActivity.this);
        textView.setText(viettelError.toString());
        rootView.setGravity(Gravity.TOP);
        rootView.addView(textView);
    }
    public void createItemHistoryCard(String seri, String gia, String date,String id) {


        HistoryCard item = new HistoryCard();
        if (!seri.equals("")&&!gia.equals("")&&!date.equals("")&&!id.equals("")) {
            item.setName(itemList.get(sothutu).getName());
            item.setSeri(seri);
            item.setMenhgia(gia);
            item.setDatetime(date);
            item.setType("VIETTEL");
            item.setMagd(id);

            // Use the IBMDataObject to create and persist the Item object.
            item.save().continueWith(new Continuation<IBMDataObject, Void>() {

                @Override
                public Void then(Task<IBMDataObject> task) throws Exception {
                    // Log if the save was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the save task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        listItemsHistoryCard();
                    }
                    return null;
                }

            });

        }

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
                        lvArrayAdapter.notifyDataSetChanged();
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
}
