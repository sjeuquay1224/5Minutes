package com.directions.sample;

/**
 * Created by RON on 9/21/2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.directions.sample.activity.ActivityRechargeCard;
import com.directions.sample.adapter.RatingAdapter;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.RatingItem;
import com.directions.sample.model.RoundImage;
import com.directions.sample.model.SoundManager;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.Item;
import com.ibm.bluelist.Ranting;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class ActivityComment extends AppCompatActivity
        implements RatingBar.OnRatingBarChangeListener,
        View.OnClickListener,
        View.OnTouchListener {

    final Context context = this;
    private RatingBar rb, rb2;
    List<Item> itemList;
    List<Ranting> rantings;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    byte[] imageAsBytes;
    Button lienhe, report;
    String tien, l, start, destination, sokm;
    TextView ratingText, name;
    private int numStars, count;
    EditText tieude, mota;
    private Toolbar toolbar;
    int stt, tam, sothutu = 0;
    RecyclerView recyclerView;
    public static final String CLASS_NAME = "ActivityComment";
    AlertDialogManager alert = new AlertDialogManager();
    CollapsingToolbarLayout collapsingToolbar;
    ImageView imageView;
    float diem = 0.0f;
    int luot = 0;
    TextView tv, tv1, danhgia;
    private SoundManager mSoundManager;
    Vibrator rung;
    int luotblock = 0;
    RadioButton radioButton, radioButton2, radioButton3, radioButton4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();

        rantings = blApplication.getRating();
        // Get the view from activity_profile_driver.xml
        setContentView(R.layout.activity_profile_driver);

        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mSoundManager.addSound(2, R.raw.warring);
        mSoundManager.addSound(3, R.raw.success);

        Bundle extras = getIntent().getExtras();
        l = extras.getString("drivername");
        tien = extras.getString("sotien");
        start = extras.getString("start");
        destination = extras.getString("destination");
        stt = extras.getInt("stt");
        sokm = extras.getString("km");


        // listItems();

        // listItemRating();

        recyclerView = (RecyclerView) findViewById(R.id.drawerListRating);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        imageView = (ImageView) findViewById(R.id.img_content);
        rb = (RatingBar) this.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) rb.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        tv = (TextView) findViewById(R.id.txt_songuoi);
        tv1 = (TextView) findViewById(R.id.txt_diem);
        danhgia = (TextView) findViewById(R.id.txt_danhgia);
        lienhe = (Button) findViewById(R.id.btn_lienhe);
        report = (Button) findViewById(R.id.btn_report);

        name = (TextView) findViewById(R.id.txt_name1);

        danhgia.setVisibility(View.INVISIBLE);

        recyclerView.setAdapter(new RatingAdapter(hienthi()));


        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getName().equals(l)) {
                sothutu = i;
                if (!itemList.get(i).getImage3().trim().equals("0")) {
                    imageAsBytes = Base64.decode(itemList.get(i).getImage3().getBytes(), Base64.DEFAULT);
                    Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageDrawable(new RoundImage(b));
                }


            }

        }
        name.setText(itemList.get(sothutu).getNameuser());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                onBackPressed();
            }
        });

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        lienhe.setOnClickListener(this);
        report.setOnClickListener(this);
        rb.setOnTouchListener(this);


    }

    private ArrayList<RatingItem> hienthi() {
        ArrayList<RatingItem> palettes = new ArrayList<>();
        luot = 0;
        for (int i = 0; i < rantings.size(); i++) {
            if (rantings.get(i).getNAME().equals(l)) {
                luot++;
                diem = diem + Float.parseFloat(rantings.get(i).getRANTINGNUMBER());
                palettes.add(new RatingItem(rantings.get(i).getUSER(), rantings.get(i).getTITLE(), rantings.get(i).getCONTENT(), Float.parseFloat(rantings.get(i).getRANTINGNUMBER().trim())));
            }
        }
        tv.setText(luot + " Lượt");
        if (luot != 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            tv1.setText("" + df.format((diem / luot)));
            rb.setRating(diem / luot);
            //Toast.makeText(getApplicationContext(),df.format((diem / luot)),Toast.LENGTH_LONG).show();
        } else {

            danhgia.setVisibility(View.VISIBLE);
            rb.setRating(0);
            tv1.setText("0");
        }


        return palettes;
    }

    @Override
    public void onClick(final View v) {
        //xử lý ratingbar
        if (v.getId() == R.id.btn_lienhe) {

            if (itemList.get(stt).getQuyen().equalsIgnoreCase("CUSTOMER")) {
                if (tien != null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    // set title
                    alertDialogBuilder.setTitle("Thực Hiện Chuyến Đi");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Bạn muốn chuyến đi với giá " + tien + " VNĐ")
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            context);
                                    mSoundManager.playSound(2);
                                    rung.vibrate(500);
                                    // set title
                                    alertDialogBuilder.setTitle("Thông báo");

                                    // set dialog message
                                    alertDialogBuilder
                                            .setIcon(R.drawable.icon_fail)
                                            .setMessage("Bạn hãy chọn hình thức thanh toán")
                                            .setCancelable(false)
                                            .setPositiveButton("Tài khoản", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    // if this button is clicked, close
                                                    // current activity
                                                    mSoundManager.playSound(1);
                                                    if (Float.parseFloat(itemList.get(stt).getMoney().trim()) < Float.parseFloat(tien.trim())) {
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                                context);
                                                        mSoundManager.playSound(2);
                                                        rung.vibrate(500);
                                                        // set title
                                                        alertDialogBuilder.setTitle("Thông báo");

                                                        // set dialog message
                                                        alertDialogBuilder
                                                                .setIcon(R.drawable.icon_fail)
                                                                .setMessage("Số tiền trong tài khoản của quí khách không đủ để thực hiện chuyến đi này. Vui lòng nạp thẻ !")
                                                                .setCancelable(false)
                                                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        // if this button is clicked, close
                                                                        // current activity
                                                                        mSoundManager.playSound(1);
                                                                        Intent i = new Intent(ActivityComment.this, ActivityRechargeCard.class);
                                                                        i.putExtra("sothutu", stt);
                                                                        startActivity(i);
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

                                                    } else {
                                                        rung.vibrate(500);
                                                        mSoundManager.playSound(3);
                                                        AlertDialog alertDialog = new AlertDialog.Builder(ActivityComment.this).create();
                                                        alertDialog.setTitle("Thông báo");
                                                        alertDialog.setIcon(R.drawable.icon_success);
                                                        alertDialog.setMessage("Yêu cầu của bạn đã được gửi đến " + itemList.get(sothutu).getName() + "\nVui lòng đợi trong giây lát...");
                                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thoát",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        guithongtinnchodriver();
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                        alertDialog.show();
                                                        dialog.dismiss();


                                                    }

                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("Tiền mặt", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // if this button is clicked, just close
                                                    // the dialog box and do nothing
                                                    alert.showAlertDialog(ActivityComment.this, "Thông báo", "Tính năng đang được cập nhật", false);
                                                    dialog.cancel();
                                                }
                                            });

                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // show it
                                    alertDialog.show();
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
                } else {
                    rung.vibrate(500);
                    mSoundManager.playSound(2);
                    AlertDialog alertDialog = new AlertDialog.Builder(ActivityComment.this).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setIcon(R.drawable.icon_fail);
                    alertDialog.setMessage("Bạn chưa chọn tuyến đường sẽ di chuyển! Vui lòng chọn lại");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Quay lại",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            } else if (itemList.get(stt).getQuyen().equalsIgnoreCase("DRIVER")) {
                if (itemList.get(stt).getName().equals(itemList.get(sothutu).getName())) {
                    mSoundManager.playSound(1);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);

                    // set title
                    alertDialogBuilder.setTitle("Cảnh báo");

                    // set dialog message
                    alertDialogBuilder
                            .setIcon(R.drawable.icon_fail)
                            .setMessage("Bạn bạn không thể chuyển nhượng cho chính mình!")
                            .setCancelable(false)
                            .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    mSoundManager.playSound(1);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);

                    // set title
                    alertDialogBuilder.setTitle("Chuyển Nhượng");

                    // set dialog message
                    alertDialogBuilder
                            .setIcon(R.drawable.icon_fail)
                            .setMessage("Bạn chắc chắn muốn chuyển nhượng lại chuyến đi này!")
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    chuyennhuong();
                                    rung.vibrate(500);
                                    mSoundManager.playSound(3);
                                    AlertDialog alertDialog = new AlertDialog.Builder(ActivityComment.this).create();
                                    alertDialog.setTitle("Thông báo");
                                    alertDialog.setIcon(R.drawable.icon_success);
                                    alertDialog.setMessage("Yêu cầu của bạn đã được gửi đến " + itemList.get(sothutu).getName().equals(l));
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thoát",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    xoathongbao();
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
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
            }
        }
        if (v.getId() == R.id.btn_report) {
            if (itemList.get(stt).getQuyen().equalsIgnoreCase("DRIVER")) {
                if (itemList.get(stt).getName().equals(itemList.get(sothutu).getName())) {
                    mSoundManager.playSound(1);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);

                    // set title
                    alertDialogBuilder.setTitle("Cảnh báo");

                    // set dialog message
                    alertDialogBuilder
                            .setIcon(R.drawable.icon_fail)
                            .setMessage("Bạn bạn không thể tố cáo chính mình!")
                            .setCancelable(false)
                            .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }else
                    layoutreport();
            }else {
                layoutreport();
            }
        }
    }
public void layoutreport(){
    mSoundManager.playSound(1);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            context);
    LayoutInflater inflater = getLayoutInflater();
    View rootView = inflater.inflate(R.layout.report_layout, null);
    radioButton = (RadioButton) rootView.findViewById(R.id.radioButton);
    radioButton2 = (RadioButton) rootView.findViewById(R.id.radioButton2);
    radioButton3 = (RadioButton) rootView.findViewById(R.id.radioButton3);
    radioButton4 = (RadioButton) rootView.findViewById(R.id.radioButton4);

    alertDialogBuilder
            .setMessage("Vấn đề là gì?*")
            .setIcon(R.drawable.icon_fail)
            .setTitle("Báo cáo!!")
            .setCancelable(false)
            .setView(rootView)
            .setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mSoundManager.playSound(1);
                    report();

                    AlertDialog alertDialog = new AlertDialog.Builder(ActivityComment.this).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage(getString(R.string.thongbao));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                    dialog.dismiss();
                }
            })
            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
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
    public void report() {
        Item item = itemList.get(sothutu);
        luotblock++;
        String block = Integer.toString(luotblock);
        item.setBlockUser(block);

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
    }

    public void createItemRating(String title, String content, int i) {


        Ranting item = new Ranting();
        if (!l.equals("") && !title.equals("") && !content.equals("")) {
            item.setNAME(l);
            item.setUSER(itemList.get(stt).getName());
            item.setTITLE(title);
            item.setCONTENT(content);
            item.setRANTINGNUMBER("" + i);
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
                        listItemRating();
                    }
                    return null;
                }
            });
        }
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
                        //chay();
                        sortItemsRating(rantings);
                        lvArrayAdapter.notifyDataSetChanged();
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

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (ratingBar.getId() == R.id.ratingBar2) {
            numStars = (int) rating;
            switch (numStars) {
                case 0:
                    ratingText.setText("Rất Ghét");
                    count = 0;
                    break;
                case 1:
                    ratingText.setText("Ghét");
                    count = 1;
                    break;
                case 2:
                    ratingText.setText("Không thích");
                    count = 2;
                    break;
                case 3:
                    ratingText.setText("OK");
                    count = 3;
                    break;
                case 4:
                    ratingText.setText("Thích");
                    count = 4;
                    break;
                case 5:
                    ratingText.setText("Rất thích");
                    count = 5;
                    break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mSoundManager.playSound(1);
            // TODO perform your action here
            boolean kiemtra = false;
            for (int i = 0; i < rantings.size(); i++) {
                if (rantings.get(i).getNAME().equals(l))
                    if (rantings.get(i).getUSER().equals(itemList.get(stt).getName())) {
                        tam = i;
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        LayoutInflater inflater = getLayoutInflater();
                        View rootView = inflater.inflate(R.layout.comment, null);
                        ratingText = (TextView) rootView.findViewById(R.id.tvranting);
                        tieude = (EditText) rootView.findViewById(R.id.edt_title);
                        tieude.setHintTextColor(getResources().getColor(R.color.primary));
                        mota = (EditText) rootView.findViewById(R.id.edit_description);
                        mota.setHintTextColor(getResources().getColor(R.color.primary));
                        rb2 = (RatingBar) rootView.findViewById(R.id.ratingBar2);
                        LayerDrawable stars = (LayerDrawable) rb2.getProgressDrawable();
                        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                        rb2.setOnRatingBarChangeListener(this);
                        // set dialog message
                        alertDialogBuilder
                                .setTitle("Chỉnh Sửa Đánh Giá")
                                .setCancelable(false)
                                .setView(rootView)
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        rung.vibrate(500);
                                        mSoundManager.playSound(3);
                                        Ranting item1 = rantings.get(tam);

                                        item1.setTITLE(tieude.getText().toString());
                                        item1.setCONTENT(mota.getText().toString());
                                        item1.setRANTINGNUMBER("" + count);
                                        /**
                                         * IBMObjectResult is used to handle the response from the server after
                                         * either creating or saving an object.
                                         *
                                         * onResult is called if the object was successfully saved.
                                         * onError is called if an error occurred saving the object.
                                         */
                                        item1.save().continueWith(new Continuation<IBMDataObject, Void>() {

                                            @Override
                                            public Void then(Task<IBMDataObject> task) throws Exception {
                                                if (task.isCancelled()) {
                                                    Log.e(CLASS_NAME, "Exception : " + task.toString() + " was cancelled.");
                                                } else if (task.isFaulted()) {
                                                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                                } else {
                                                    Intent returnIntent = new Intent();
                                                    setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                                                    //finish();
                                                }
                                                return null;
                                            }

                                        }, Task.UI_THREAD_EXECUTOR);
                                        recyclerView.setAdapter(new RatingAdapter(hienthi()));
                                        //Adapter.notifyDataSetChanged();

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
                        kiemtra = true;
                    }
            }
            if (!kiemtra) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                LayoutInflater inflater = getLayoutInflater();
                View rootView = inflater.inflate(R.layout.comment, null);
                ratingText = (TextView) rootView.findViewById(R.id.tvranting);
                tieude = (EditText) rootView.findViewById(R.id.edt_title);
                tieude.setHintTextColor(getResources().getColor(R.color.primary));
                mota = (EditText) rootView.findViewById(R.id.edit_description);
                mota.setHintTextColor(getResources().getColor(R.color.primary));
                rb2 = (RatingBar) rootView.findViewById(R.id.ratingBar2);
                LayerDrawable stars = (LayerDrawable) rb2.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                rb2.setOnRatingBarChangeListener(this);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Đánh Giá")
                        .setCancelable(false)
                        .setView(rootView)
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                rung.vibrate(500);
                                mSoundManager.playSound(3);
                                createItemRating(tieude.getText().toString(), mota.getText().toString(), count);
                                recyclerView.setAdapter(new RatingAdapter(hienthi()));

                                //Adapter.notifyDataSetChanged();

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
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listItemRating();

    }

    public void guithongtinnchodriver() {
        Item item = itemList.get(sothutu);
        item.setNamecustmer(itemList.get(stt).getName());
        item.setMessage(tien);
        item.setStart(start);
        item.setDestination(destination);
        item.setKm(sokm);
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
    }

    public void chuyennhuong() {
        Item item = itemList.get(sothutu);

        item.setMessage(itemList.get(stt).getMoney2());
        item.setStart(itemList.get(stt).getStart());
        item.setDestination(itemList.get(stt).getDestination());
        item.setNamecustmer(itemList.get(stt).getNamecustmer());
        item.setKm(itemList.get(stt).getKm());
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
    }

    public void xoathongbao() {
        Item item1 = itemList.get(stt);
        Float sotien = Float.parseFloat(itemList.get(stt).getMoney().trim()) - Float.parseFloat(itemList.get(stt).getMoney2().trim());
        String str = Float.toString(sotien);
        item1.setMoney(str);
        item1.setMoney2("0");
        item1.setStart("0");
        item1.setDestination("0");
        item1.setNamecustmer("0");
        item1.setKm("0");
        /**
         * IBMObjectResult is used to handle the response from the server after
         * either creating or saving an object.
         *
         * onResult is called if the object was successfully saved.
         * onError is called if an error occurred saving the object.
         */
        item1.save().continueWith(new Continuation<IBMDataObject, Void>() {

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
}