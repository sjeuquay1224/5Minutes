/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.bluelist;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.directions.sample.LoginActivity;
import com.directions.sample.R;
import com.directions.sample.activity.PlaceArrayAdapter;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.SoundManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ibm.mobile.services.data.IBMDataObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;

public class EditActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener {

    String originalItem;
    int location;
    BlueListApplication blApplication;
    List<Item> itemList;
    private Toolbar toolbar;
    public static final String CLASS_NAME = "EditActivity";
    private static int RESULT_LOAD_IMG = 1, RESULT_LOAD_IMG2 = 2;
    String imgDecodableString;

    ArrayAdapter<Item> lvArrayAdapter;
    byte[] byteArray;
    String strBase64, hinhanh = "", hinhanh2 = "", ViDo, KinhDo, kinhdovodo;
    ImageButton imgView, imgView2;
    private static final String LOG_TAG = "EditActivity";
    AutoCompleteTextView mAutocompleteTextView;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(16.0429623, 108.1489704), new LatLng(16.0429623, 108.1489704));

    EditText pass, mail, socmnd, phone, report;
    Pattern p;
    boolean kiemtra = false, test = true;
    LatLng check;
    byte[] imageAsBytes, imageAsBytes1;
    private SoundManager mSoundManager;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    @Override
    /**
     * onCreate called when edit activity is created.
     *
     * Sets up the application, sets listeners, and gets intent info from calling activity.
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Get application context, item list. */
        blApplication = (BlueListApplication) getApplicationContext();
        itemList = blApplication.getItemList();
        setContentView(R.layout.bluemix_activity_edit);


        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mSoundManager.addSound(2, R.raw.success);
        mSoundManager.addSound(3, R.raw.warring);
        /* Information required to edit item. */
        Intent intent = getIntent();
        originalItem = intent.getStringExtra("ItemText");
        location = intent.getIntExtra("ItemLocation", 0);


        Button huy = (Button) findViewById(R.id.btn_cancel);
        Button dongy = (Button) findViewById(R.id.btn_accept);
        p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
        TextView acc = (TextView) findViewById(R.id.txt_acc);
        TextView anh1 = (TextView) findViewById(R.id.txt_anh1);
        TextView anh2 = (TextView) findViewById(R.id.txt_anh2);
        TextView cm = (TextView) findViewById(R.id.txt_cmnd);
        TextView add = (TextView) findViewById(R.id.txt_diachi);
        EditText money = (EditText) findViewById(R.id.edt_tien);
        TextView tvreport = (TextView) findViewById(R.id.txt_report);
        imgView = (ImageButton) findViewById(R.id.imb_cmnd1);
        imgView2 = (ImageButton) findViewById(R.id.imb_banglai1);
        report = (EditText) findViewById(R.id.edt_report);
        pass = (EditText) findViewById(R.id.edt_pass);
        mail = (EditText) findViewById(R.id.edt_mail);
        socmnd = (EditText) findViewById(R.id.edt_cmnd1);
        phone = (EditText) findViewById(R.id.edt_phone);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView_Edit);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);


        if (itemList.get(location).getQuyen().equals("CUSTOMER")) {
            socmnd.setVisibility(View.INVISIBLE);
            imgView.setVisibility(View.INVISIBLE);
            imgView2.setVisibility(View.INVISIBLE);
            report.setVisibility(View.INVISIBLE);
            tvreport.setVisibility(View.INVISIBLE);
            mAutocompleteTextView.setVisibility(View.INVISIBLE);
            add.setVisibility(View.INVISIBLE);
            anh1.setVisibility(View.INVISIBLE);
            anh2.setVisibility(View.INVISIBLE);
            cm.setVisibility(View.INVISIBLE);
            add.setVisibility(View.INVISIBLE);
            test = false;
        }


        acc.setText(itemList.get(location).getName());
        pass.setText(itemList.get(location).getPass());
        phone.setText(itemList.get(location).getPhone());
        mail.setText(itemList.get(location).getMail());
        money.setText(itemList.get(location).getMoney());
        report.setText(itemList.get(location).getBlockUser());
        if (test) {
            imageAsBytes = Base64.decode(itemList.get(location).getImage().getBytes(), Base64.DEFAULT);
            imageAsBytes1 = Base64.decode(itemList.get(location).getImage2().getBytes(), Base64.DEFAULT);
            imgView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            imgView2.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes1, 0, imageAsBytes1.length));
            imgView.setOnClickListener(this);
            imgView2.setOnClickListener(this);
            socmnd.setText(itemList.get(location).getCmnd());
            mAutocompleteTextView.setText(itemList.get(location).getAddress());
        }


        dongy.setOnClickListener(this);
        huy.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chỉnh Sửa Thông Tin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                onBackPressed();
            }
        });

    }

    /**
     * On completion of edit, edit itemList, return to main activity with edit return code.
     *
     * @param //View v
     */
    public void finishedEdit(View v) {
        Item item = itemList.get(location);

        EditText money = (EditText) findViewById(R.id.edt_tien);
        String tomoney = money.getText().toString();
        EditText pass = (EditText) findViewById(R.id.edt_pass);
        String topass = pass.getText().toString();

        EditText mail = (EditText) findViewById(R.id.edt_mail);
        String tomail = mail.getText().toString();

        EditText cmnd = (EditText) findViewById(R.id.edt_cmnd1);
        String tocmnd = cmnd.getText().toString();

        EditText phone = (EditText) findViewById(R.id.edt_phone);
        String tophone = phone.getText().toString();

        EditText report = (EditText) findViewById(R.id.edt_report);
        String toreport = report.getText().toString();

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_Edit);
        String tocomplete = autoCompleteTextView.getText().toString();
        item.setPass(topass);
        item.setMoney(tomoney);
        item.setPhone(tophone);
        item.setMail(tomail);
        item.setCmnd(tocmnd);
        item.setImage(hinhanh);
        item.setImage2(hinhanh2);
        item.setAddress(tocomplete);
        item.setVido(ViDo);
        item.setKinhDo(KinhDo);
        item.setBlockUser(toreport);
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
        Toast.makeText(getApplicationContext(), "Thành Công", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getApplicationContext(), "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imb_cmnd1) {
            mSoundManager.playSound(1);
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        }
        if (v.getId() == R.id.imb_banglai1) {
            mSoundManager.playSound(1);
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent2 = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent2, RESULT_LOAD_IMG2);
        }
        if (v.getId() == R.id.btn_cancel) {
            mSoundManager.playSound(1);
            onBackPressed();
        }
        if (v.getId() == R.id.btn_accept) {
            String pas = pass.getText().toString();
            String cmnd = socmnd.getText().toString();
            String mai = mail.getText().toString();
            String pho = phone.getText().toString();

            Matcher m1 = p.matcher(pas);


            boolean b1 = m1.find();


            boolean login = true;
            /*for (int i = 0; i < itemList.size(); i++) {
                if (mail.getText().toString().equalsIgnoreCase(itemList.get(i).getMail().toString())) {
                    Toast.makeText(getApplicationContext(), "Email đã tồn tại ", Toast.LENGTH_LONG).show();
                    mSoundManager.playSound(3);
                    login = false;
                }
            }*/
            if (login) {
                try {
                    if (pass.getText().toString().length() <= 6) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Mật khẩu phải trên 6 ký tự", Toast.LENGTH_LONG).show();
                    } else if (b1) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Mật khẩu không được chứa ký tự đặc biệt", Toast.LENGTH_LONG).show();
                    } else if (mai.isEmpty()) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Mail không được để trống", Toast.LENGTH_LONG).show();
                    } else if (!isValidEmail(mai)) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Địa chỉ Mail không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (pho.isEmpty()) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Số điện thoại không được để trống", Toast.LENGTH_LONG).show();
                    } else if (pho.length() < 10 || pho.length() > 11) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (!isValidPhone(pho)) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (cmnd.isEmpty()) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Số Chứng Minh không được để trống", Toast.LENGTH_LONG).show();
                    } else if (cmnd.length() != 9) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Số Chứng Minh không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (!isValidPhone(cmnd)) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Số Chứng Minh không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (check == null) {
                        if (mAutocompleteTextView.getText().length() > 0) {
                            mAutocompleteTextView.setError("Chọn địa điểm trong danh sách.");

                        } else {
                            mSoundManager.playSound(3);
                            Toast.makeText(getApplicationContext(), "Hãy điền vào địa điểm địa nơi sống.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!kiemtra) {
                        mSoundManager.playSound(3);
                        Toast.makeText(getApplicationContext(), "Bạn chưa chọn ảnh", Toast.LENGTH_LONG).show();
                    } else {
                        mSoundManager.playSound(2);
                        finishedEdit(v);
                    }
                } catch (Exception e) {
                    Log.e(CLASS_NAME, "Exception : " + e.getMessage());
                }
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            check = place.getLatLng();
            CharSequence attributions = places.getAttributions();


            //mAttTextView.setText(Html.fromHtml(place.getLatLng() + ""));
            kinhdovodo = Html.fromHtml(place.getLatLng() + "").toString();
            StringTokenizer tokens = new StringTokenizer(kinhdovodo, "(,)");
            String first = tokens.nextToken();// this will contain "LaLng"
            ViDo = tokens.nextToken();// Vĩ độ
            KinhDo = tokens.nextToken();// Kinh Ðộ


            if (attributions != null) {
                //mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                clickanh(data, imgView);
            } else if (requestCode == RESULT_LOAD_IMG2 && resultCode == RESULT_OK
                    && null != data) {
                clickanh(data, imgView2);
            } else {
                alert.showAlertDialog(EditActivity.this, "Cảnh báo", "Bạn chưa chọn ảnh", false);

            }
        } catch (Exception e) {
            alert.showAlertDialog(EditActivity.this, "Cảnh báo", "Ðã có lỗi xảy ra", false);

        }

    }

    public void clickanh(Intent data, ImageView i) {

        // Get the Image from data

        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imgDecodableString = cursor.getString(columnIndex);
        cursor.close();


        // chuyển chuỗi thành ảnh
        //imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

        //chuyển ẩnh thành chuỗi

        Bitmap Image = BitmapFactory.decodeFile(imgDecodableString);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byteArray = stream.toByteArray();
        strBase64 = Base64.encodeToString(byteArray, 0);

        hinhanh2 = strBase64;

        byteArray = Base64.decode(strBase64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        i.setImageBitmap(decodedByte);
        kiemtra = true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {

        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }
}