package com.directions.sample.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.sample.LoginActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by RON on 10/5/2015.
 */
public class DriverLoginFragment extends Fragment implements TextView.OnEditorActionListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener {
    private static int RESULT_LOAD_IMG = 1, RESULT_LOAD_IMG2 = 2;
    String imgDecodableString;
    List<Item> itemList;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    byte[] byteArray;
    String strBase64, hinhanh = "", hinhanh2 = "", ViDo, KinhDo, kinhdovodo;
    ImageButton imgView, imgView2;
    public static final String CLASS_NAME = "DriverLoginFragment";
    private static final String LOG_TAG = "DriverLoginFragment";
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    EditText pass, account, mail, socmnd, phone;
    Pattern p;
    boolean kiemtra1 = false, kiemtra2 = false;
    LatLng check;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    Vibrator rung;
    private SoundManager mSoundManager;
    private TextInputLayout inputLayoutName, inputLayoutPass,
            inputLayoutEmail, inputLayoutPhone, inputLayoutPersonalID, inputLayoutAddress, inputLayoutImage;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blApplication = (BlueListApplication) getActivity().getApplication();
        itemList = blApplication.getItemList();

        rung = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this).build();
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getActivity().getBaseContext());
        mSoundManager.addSound(1, R.raw.warring);
        mSoundManager.addSound(2, R.raw.success);
        mSoundManager.addSound(3, R.raw.click);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_driver_login, container, false);

        p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);

        //khai báo và khởi tạo AutocompleteTextView
        mAutocompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id
                .autoCompleteTextView);

        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name_driver);
        inputLayoutPass = (TextInputLayout) rootView.findViewById(R.id.input_layout_pass_driver);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_mail_driver);
        inputLayoutPhone = (TextInputLayout) rootView.findViewById(R.id.input_layout_phone_driver);

        inputLayoutPersonalID = (TextInputLayout) rootView.findViewById(R.id.input_layout_cmnd_driver);
        inputLayoutAddress = (TextInputLayout) rootView.findViewById(R.id.input_layout_address_driver);
        //khai báo biến
        Button dangky = (Button) rootView.findViewById(R.id.btn_dangky_driver);
        Button huy = (Button) rootView.findViewById(R.id.btn_cancel_driver);
        pass = (EditText) rootView.findViewById(R.id.edt_pass_driver);
        account = (EditText) rootView.findViewById(R.id.edt_acc_driver);
        mail = (EditText) rootView.findViewById(R.id.edt_mail_driver);
        socmnd = (EditText) rootView.findViewById(R.id.edt_cmnd);
        phone = (EditText) rootView.findViewById(R.id.edt_phone_driver);
        imgView = (ImageButton) rootView.findViewById(R.id.imb_cmnd);
        imgView2 = (ImageButton) rootView.findViewById(R.id.imb_banglai);

        account.addTextChangedListener(new MyTextWatcher(account));
        pass.addTextChangedListener(new MyTextWatcher(pass));
        mail.addTextChangedListener(new MyTextWatcher(mail));
        phone.addTextChangedListener(new MyTextWatcher(phone));
        socmnd.addTextChangedListener(new MyTextWatcher(socmnd));
        mAutocompleteTextView.addTextChangedListener(new MyTextWatcher(mAutocompleteTextView));


        /* Set key listener for edittext (done key to accept item to list). */
        account.setOnEditorActionListener(this);
        pass.setOnEditorActionListener(this);
        mail.setOnEditorActionListener(this);
        socmnd.setOnEditorActionListener(this);
        phone.setOnEditorActionListener(this);
        mAutocompleteTextView.setOnEditorActionListener(this);


        //khởi tạo sự kiện
        imgView.setOnClickListener(this);
        imgView2.setOnClickListener(this);
        huy.setOnClickListener(this);
        dangky.setOnClickListener(this);


        return rootView;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == getActivity().RESULT_OK
                    && null != data) {
                clickanh(data, imgView);
                kiemtra1 = true;
            } else if (requestCode == RESULT_LOAD_IMG2 && resultCode == getActivity().RESULT_OK
                    && null != data) {
                clickanh(data, imgView2);
                kiemtra2 = true;
            } else {
                alert.showAlertDialog(getActivity(), "Cảnh báo", "Bạn chưa chọn ảnh", false);

            }
        } catch (Exception e) {
            alert.showAlertDialog(getActivity(), "Cảnh báo", "Ðã có lỗi xảy ra", false);
        }

    }

    public void clickanh(Intent data, ImageView i) {

        // Get the Image from data

        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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
                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (!validateName()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validateEmail()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validatePassword()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validatePhone()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validatePersonalID()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validateAddress()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validateImage()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            createItem1(v);
            mSoundManager.playSound(2);
            rung.vibrate(500);
            return true;
        }
        return false;
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

        Toast.makeText(getActivity().getApplicationContext(), "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imb_cmnd) {
            mSoundManager.playSound(3);
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        }
        if (v.getId() == R.id.imb_banglai) {
            mSoundManager.playSound(3);
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent2 = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent2, RESULT_LOAD_IMG2);
        }
        if (v.getId() == R.id.btn_dangky_driver) {
            if (!validateName()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validateEmail()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validatePassword()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validatePhone()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validatePersonalID()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validateAddress()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validateImage()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            createItem1(v);
            mSoundManager.playSound(2);
            rung.vibrate(500);
        }
        if (v.getId() == R.id.btn_cancel_driver) {
            mSoundManager.playSound(3);
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent);
        }

    }

    public void createItem1(View v) {
        EditText account = (EditText) getActivity().findViewById(R.id.edt_acc_driver);
        String toacc = account.getText().toString();

        EditText pass = (EditText) getActivity().findViewById(R.id.edt_pass_driver);
        String topass = pass.getText().toString();

        EditText mail = (EditText) getActivity().findViewById(R.id.edt_mail_driver);
        String tomail = mail.getText().toString();

        EditText cmnd = (EditText) getActivity().findViewById(R.id.edt_cmnd);
        String tocmnd = cmnd.getText().toString();

        EditText phone = (EditText) getActivity().findViewById(R.id.edt_phone_driver);
        String tophone = phone.getText().toString();

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteTextView);
        String tocomplete = autoCompleteTextView.getText().toString();
        Item item = new Item();
        if (!toacc.equals("") && !topass.equals("") && !tomail.equals("") && !tophone.equals("") && !tocmnd.equals("") && !tocomplete.equals("")) {
            item.setName(toacc);
            item.setPass(topass);
            item.setMail(tomail);
            item.setPhone(tophone);
            item.setQuyen("DRIVER");
            item.setCmnd(tocmnd);
            item.setImage(hinhanh);
            item.setImage2(hinhanh2);
            item.setImage3("0");
            item.setVido(ViDo);
            item.setKinhDo(KinhDo);
            item.setMoney("0");
            item.setAddress(tocomplete);
            item.setMoney2("0");
            item.setStart("0");
            item.setDestination("0");
            item.setMessage("0");
            item.setNamecustmer("0");
            item.setDone("0");
            item.setPRICE("9000");
            item.setBlockUser("0");
            item.setNameuser("0");
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
                        listItems();
                    }
                    return null;
                }

            });

            // Set text field back to empty after item is added.
            account.setText("");
            pass.setText("");
            mail.setText("");
            phone.setText("");
            imgView.setImageResource(android.R.color.transparent);
            imgView2.setImageResource(android.R.color.transparent);
            mAutocompleteTextView.setText(null);
            socmnd.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

            // set title
            alertDialogBuilder.setTitle("Thành Công!");

            // set dialog message
            alertDialogBuilder
                    .setIcon(R.drawable.icon_success)
                    .setMessage("Chúc mừng bạn đã đăng ký thành công. Quay về trang đăng nhập !")
                    .setCancelable(false)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
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
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {

        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edt_acc_driver:
                    validateName();
                    break;
                case R.id.edt_pass_driver:
                    validatePassword();
                    break;
                case R.id.edt_mail_driver:
                    validateEmail();
                    break;
                case R.id.edt_phone_driver:
                    validatePhone();
                    break;
                case R.id.edt_cmnd:
                    validatePersonalID();
                    break;
                case R.id.autoCompleteTextView:
                    validateAddress();
                    break;

            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateName() {
        String acc = account.getText().toString();
        Matcher m = p.matcher(acc);
        boolean b = m.find();

        if (b) {
            inputLayoutName.setError("Tài khoản không được chưa ký tự đặc biệt");
            requestFocus(account);
            return false;
        } else if (account.getText().toString().trim().isEmpty() || account.getText().toString().length() <= 6) {
            inputLayoutName.setError("Tài khoản phải trên 6 ký tự ");
            requestFocus(account);
            return false;
        } else if (account.getText().toString().length() > 25) {
            inputLayoutName.setError("Tài khoản phải dưới 25 ký tự ");
            requestFocus(account);
            return false;
        } else {
            for (int i = 0; i < itemList.size(); i++) {
                if (account.getText().toString().equalsIgnoreCase(itemList.get(i).getName())) {
                    inputLayoutName.setError("Tài khoản đã tồn tại");
                    requestFocus(account);
                    return false;
                } else {
                    inputLayoutName.setErrorEnabled(false);
                }
            }
        }
        return true;
    }

    private boolean validatePassword() {
        String pas = pass.getText().toString();
        Matcher m = p.matcher(pas);
        boolean b = m.find();
        if (b) {
            inputLayoutPass.setError("Mật khẩu không được chưa ký tự đặc biệt");
            requestFocus(pass);
            return false;
        } else if (pass.getText().toString().trim().isEmpty() || pass.getText().toString().length() <= 6) {
            inputLayoutPass.setError("Mật khẩu phải trên 6 ký tự (Bao gồm số và chữ)");
            requestFocus(pass);
            return false;
        } else if (pass.getText().toString().length() > 15) {
            inputLayoutPass.setError("Mật khẩu phải dưới 15 ký tự (Bao gồm số và chữ)");
            requestFocus(pass);
            return false;
        } else
            inputLayoutPass.setErrorEnabled(false);

        return true;
    }

    private boolean validateEmail() {
        String email = mail.getText().toString().trim();
        for (int i = 0; i < itemList.size(); i++)
            if (mail.getText().toString().equalsIgnoreCase(itemList.get(i).getMail())) {
                inputLayoutEmail.setError("Địa chỉ email đã tồn tại");
                requestFocus(mail);
                return false;
            }
        if (email.isEmpty()) {
            inputLayoutEmail.setError("Địa chỉ email không được để trống");
            requestFocus(mail);
            return false;
        } else if (!isValidEmail(email)) {
            inputLayoutEmail.setError("Địa chỉ email không hợp lệ");
            requestFocus(mail);
            return false;
        } else {

            inputLayoutEmail.setErrorEnabled(false);
        }


        return true;
    }

    private boolean validatePhone() {
        String pho = phone.getText().toString().trim();
        if (pho.isEmpty()) {
            inputLayoutPhone.setError("Số điện thoại không được để trống");
            requestFocus(phone);
            return false;
        } else if (!isValidPhone(pho) || pho.length() < 10 || pho.length() > 11) {
            inputLayoutPhone.setError("Số điện thoại không hợp lệ");
            requestFocus(phone);
            return false;
        } else

            inputLayoutPhone.setErrorEnabled(false);

        return true;
    }

    private boolean validatePersonalID() {
        String pho = socmnd.getText().toString().trim();
        if (pho.isEmpty()) {
            inputLayoutPersonalID.setError("Số chứng minh nhân dân không được để trống");
            requestFocus(socmnd);
            return false;
        } else if (!isValidPhone(pho) || pho.length() != 9) {
            inputLayoutPersonalID.setError("Số chứng minh nhân dân không hợp lệ");
            requestFocus(socmnd);
            return false;
        } else
            inputLayoutPersonalID.setErrorEnabled(false);

        return true;
    }

    private boolean validateAddress() {

        if (check == null) {
            if (mAutocompleteTextView.getText().length() > 0) {
                inputLayoutAddress.setError("Chọn địa điểm trong danh sách.");
                mAutocompleteTextView.setError("Chọn địa điểm trong danh sách.");
                requestFocus(mAutocompleteTextView);
                return false;

            } else {
                inputLayoutAddress.setError("Hãy điền vào địa điểm địa nơi sống");
                requestFocus(mAutocompleteTextView);
                return false;
            }
        } else
            inputLayoutAddress.setErrorEnabled(false);
        return true;
    }

    private boolean validateImage() {
        if (!kiemtra1 || !kiemtra2) {
            alert.showAlertDialog(getActivity(), "Cảnh báo", "Bạn chưa chọn ảnh CMND hoặc bằng lái xe", false);
            return false;
        }
        return true;
    }

}