package com.directions.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.sample.PlaceAutoCompleteAdapter;
import com.directions.sample.R;
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
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by RON on 11/6/2015.
 */
public class UpdateActivity extends AppCompatActivity implements
        View.OnClickListener, TextView.OnEditorActionListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private Toolbar toolbar;
    int l;
    BlueListApplication blApplication;
    List<Item> itemList;
    public static final String CLASS_NAME = "UpdateActivity";
    private static final String LOG_TAG = "UpdateActivity";
    Button capnhat, huy;
    EditText pass, phone, mail, name;
    AutoCompleteTextView mAutocompleteTextView;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(16.0429623, 108.1489704), new LatLng(16.0429623, 108.1489704));

    String ViDo, KinhDo, kinhdovodo;
    LatLng check;
    Pattern p;
    private SoundManager mSoundManager;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    Vibrator rung;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Get application context, item list. */
        blApplication = (BlueListApplication) getApplicationContext();
        itemList = blApplication.getItemList();
        setContentView(R.layout.update);

        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mSoundManager.addSound(2, R.raw.success);
        mSoundManager.addSound(3, R.raw.warring);

        Bundle extras = getIntent().getExtras();
        l = extras.getInt("stt");
        p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();
        name = (EditText) findViewById(R.id.edt_ten_update);
        pass = (EditText) findViewById(R.id.edt_pass_update);
        phone = (EditText) findViewById(R.id.edt_phone_update);
        mail = (EditText) findViewById(R.id.edt_mail_update);

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView_update);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        TextView address = (TextView) findViewById(R.id.txt_address);

        if (itemList.get(l).getQuyen().equalsIgnoreCase("CUSTOMER")) {
            mAutocompleteTextView.setVisibility(View.INVISIBLE);
            address.setVisibility(View.INVISIBLE);
        }

        name.setText(itemList.get(l).getNameuser());
        pass.setText(itemList.get(l).getPass());
        phone.setText(itemList.get(l).getPhone());
        mail.setText(itemList.get(l).getMail());
        mAutocompleteTextView.setText(itemList.get(l).getAddress());

       /* check= new LatLng(Double.parseDouble(itemList.get(l).getVido()), Double.parseDouble(itemList.get(l).getKinhDo()));
        ViDo=itemList.get(l).getVido();
        KinhDo=itemList.get(l).getKinhDo();*/
        capnhat = (Button) findViewById(R.id.btn_update);
        huy = (Button) findViewById(R.id.btn_cancel);
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
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

        capnhat.setOnClickListener(this);
        huy.setOnClickListener(this);


        name.setOnEditorActionListener(this);
        pass.setOnEditorActionListener(this);
        phone.setOnEditorActionListener(this);
        mail.setOnEditorActionListener(this);
        mAutocompleteTextView.setOnEditorActionListener(this);
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
            //Toast.makeText(getApplicationContext(),ViDo+KinhDo,Toast.LENGTH_LONG).show();

            if (attributions != null) {
                //mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            mSoundManager.playSound(1);
            onBackPressed();
        }
        if (v.getId() == R.id.btn_update) {
            String pas = pass.getText().toString();
            String mai = mail.getText().toString();
            String pho = phone.getText().toString();
            Matcher m1 = p.matcher(pas);
            boolean b1 = m1.find();
            boolean login = true;
            for (int i = 0; i < itemList.size(); i++) {
                if (i == l) {
                    if (mail.getText().toString().equalsIgnoreCase(itemList.get(i).getMail()))
                        login = true;
                } else if (mail.getText().toString().equalsIgnoreCase(itemList.get(i).getMail())) {
                    Toast.makeText(getApplicationContext(), "Email đã tồn tại ", Toast.LENGTH_LONG).show();
                    rung.vibrate(500);
                    mSoundManager.playSound(3);
                    login = false;
                }
            }
            if (login) {
                try {
                    if (pass.getText().toString().length() <= 6) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Mật khẩu phải trên 6 ký tự", false);
                    } else if (b1) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Mật khẩu không được chứa ký tự đặc biệt", false);
                    } else if (mai.isEmpty()) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Mail không được để trống", false);
                    } else if (!isValidEmail(mai)) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Địa chỉ Mail không hợp lệ", false);
                    } else if (pho.isEmpty()) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Số điện thoại không được để trống", false);
                    } else if (pho.length() < 10 || pho.length() > 11) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Số điện thoại không hợp lệ", false);
                    } else if (!isValidPhone(pho)) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Số điện thoại không hợp lệ", false);
                    } else if (itemList.get(l).getQuyen().equalsIgnoreCase("DRIVER")) {
                        if (check == null) {
                            if (mAutocompleteTextView.getText().length() > 0) {
                                mSoundManager.playSound(3);
                                rung.vibrate(500);
                                mAutocompleteTextView.setError("Chọn địa điểm trong danh sách.");
                            } else {
                                mSoundManager.playSound(3);
                                rung.vibrate(500);
                                alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Hãy điền vào địa điểm địa nơi sống", false);
                            }
                        } else
                            Update(v);

                    } else
                        Update(v);

                } catch (Exception e) {
                    Log.e(CLASS_NAME, "Exception : " + e.getMessage());
                }
            }

        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String pas = pass.getText().toString();
            String mai = mail.getText().toString();
            String pho = phone.getText().toString();
            Matcher m1 = p.matcher(pas);
            boolean b1 = m1.find();
            boolean login = true;
            for (int i = 0; i < itemList.size(); i++) {
                if (i == l) {
                    if (mail.getText().toString().equalsIgnoreCase(itemList.get(i).getMail()))
                        login = true;
                } else if (mail.getText().toString().equalsIgnoreCase(itemList.get(i).getMail())) {
                    Toast.makeText(getApplicationContext(), "Email đã tồn tại ", Toast.LENGTH_LONG).show();
                    rung.vibrate(500);
                    mSoundManager.playSound(3);
                    login = false;
                }
            }
            if (login) {
                try {
                    if (pass.getText().toString().length() <= 6) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Mật khẩu phải trên 6 ký tự", false);
                        //Toast.makeText(getApplicationContext(), "Mật khẩu phải trên 6 ký tự", Toast.LENGTH_LONG).show();
                    } else if (b1) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Mật khẩu không được chứa ký tự đặc biệt", false);
                        //Toast.makeText(getApplicationContext(), "Mật khẩu không được chứa ký tự đặc biệt", Toast.LENGTH_LONG).show();
                    } else if (mai.isEmpty()) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Mail không được để trống", false);
                        //Toast.makeText(getApplicationContext(), "Mail không được để trống", Toast.LENGTH_LONG).show();
                    } else if (!isValidEmail(mai)) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Địa chỉ Mail không hợp lệ", false);
                        // Toast.makeText(getApplicationContext(), "Địa chỉ Mail không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (pho.isEmpty()) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Số điện thoại không được để trống", false);
                        // Toast.makeText(getApplicationContext(), "Số điện thoại không được để trống", Toast.LENGTH_LONG).show();
                    } else if (pho.length() < 10 || pho.length() > 11) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Số điện thoại không hợp lệ", false);
                        //Toast.makeText(getApplicationContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (!isValidPhone(pho)) {
                        mSoundManager.playSound(3);
                        rung.vibrate(500);
                        alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Số điện thoại không hợp lệ", false);
                        //Toast.makeText(getApplicationContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_LONG).show();
                    } else if (itemList.get(l).getQuyen().equalsIgnoreCase("DRIVER")) {
                        if (check == null) {
                            if (mAutocompleteTextView.getText().length() > 0) {
                                mSoundManager.playSound(3);
                                rung.vibrate(500);
                                mAutocompleteTextView.setError("Chọn địa điểm trong danh sách.");
                            } else {
                                mSoundManager.playSound(3);
                                rung.vibrate(500);
                                alert.showAlertDialog(UpdateActivity.this, "Cảnh báo!", "Hãy điền vào địa điểm địa nơi sống", false);
                                //Toast.makeText(getApplicationContext(), "Hãy điền vào địa điểm địa nơi sống.", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Update(v);


                    } else
                        Update(v);

                } catch (Exception e) {
                    Log.e(CLASS_NAME, "Exception : " + e.getMessage());
                }
            }
            return true;
        }
        return false;
    }

    public void Update(View v) {
        Item item = itemList.get(l);
        EditText ten = (EditText) findViewById(R.id.edt_ten_update);
        String textten = ten.getText().toString();

        EditText pass = (EditText) findViewById(R.id.edt_pass_update);
        String textpass = pass.getText().toString();
        EditText phone = (EditText) findViewById(R.id.edt_phone_update);
        String textphone = phone.getText().toString();
        EditText mail = (EditText) findViewById(R.id.edt_mail_update);
        String textmail = mail.getText().toString();
        AutoCompleteTextView mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView_update);
        String textaddress = mAutocompleteTextView.getText().toString();

        item.setVido(ViDo);
        item.setKinhDo(KinhDo);
        item.setPass(textpass);
        item.setPhone(textphone);
        item.setAddress(textaddress);
        item.setMail(textmail);
        item.setNameuser(textten);
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
        listItems();
        alert.showAlertDialog(UpdateActivity.this, "Thành Công!", "Bạn đã chỉnh sửa thông tin thành công", true);
        mSoundManager.playSound(2);
        rung.vibrate(500);

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
                        itemList.clear();
                        for (IBMDataObject item : objects) {
                            itemList.add((Item) item);
                        }
                        sortItems(itemList);
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {

        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }
}
