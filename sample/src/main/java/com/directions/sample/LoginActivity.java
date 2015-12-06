package com.directions.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.sample.activity.Navigation;
import com.directions.sample.adapter.ConnectionDetector;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.SoundManager;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.HistoryCard;
import com.ibm.bluelist.HistoryTrip;
import com.ibm.bluelist.Item;
import com.ibm.bluelist.Ranting;
import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.http.IBMHttpResponse;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by RON on 9/21/2015.
 */
public class LoginActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {

    List<Item> itemList;
    BlueListApplication blApplication;
    List<HistoryTrip> historyTrips;
    List<Ranting> rantings;


    Button dangnhap, dangky;
    EditText name, pass, input1, input2;
    String acc, mai;
    ImageView logo3, logo2;
    TextView quenpass;

    Context context = this;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    public static final String CLASS_NAME = "LoginActivity";
    // Connection detector class

    boolean login = true;
    Pattern p;
    String l1, l2;
    // Session Manager Class
    SessionManager session;
    Vibrator rung;
    private SoundManager mSoundManager;
    private TextInputLayout inputLayoutName, inputLayoutPass, inputLayoutName2, inputLayoutEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setupBluelist();
        setContentView(R.layout.activity_login);


        // Session Manager
        session = new SessionManager(getApplicationContext());
        session.isLoggedIn();

        //listItems();
        khoitao();
        //gọi hiệu ứng
        Animation();
        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.warring);
        mSoundManager.addSound(2, R.raw.click);
        mSoundManager.addSound(3, R.raw.success);

    }

    public void setupBluelist() {
        /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();
        historyTrips = blApplication.getHistoryTrips();
        rantings = blApplication.getRating();


    }


    public void khoitao() {
        dangnhap = (Button) findViewById(R.id.btn_dangnhap);
        dangky = (Button) findViewById(R.id.btn_dangky);
        quenpass = (TextView) findViewById(R.id.txt_quenpass);

        name = (EditText) findViewById(R.id.edt_name);


        pass = (EditText) findViewById(R.id.edt_pass);
        logo3 = (ImageView) findViewById(R.id.imv_logo3);
        logo2 = (ImageView) findViewById(R.id.imv_logo2);


        name.setOnEditorActionListener(this);
        pass.setOnEditorActionListener(this);

        //gọi button đăng nhập
        dangnhap.setOnClickListener(this);
        //gọi button đăng ký
        dangky.setOnClickListener(this);
        quenpass.setOnClickListener(this);

        p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name1);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.input_layout_pass1);


        name.addTextChangedListener(new MyTextWatcher(name));
        pass.addTextChangedListener(new MyTextWatcher(pass));
    }

    //xử lý button
    @Override
    public void onClick(View v) {
        //xử lý button đăng nhập
        if (v.getId() == R.id.btn_dangnhap) {
            if (!validateName()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            if (!validatePassword()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return;
            }
            loginAccount();

        }
        //xử lý button đăng ký
        if (v.getId() == R.id.btn_dangky) {
            mSoundManager.playSound(2);
            Intent i = new Intent(this, ActivityDangKy.class);

            startActivity(i);
        }
        if (v.getId() == R.id.txt_quenpass) {
            mSoundManager.playSound(2);
            quenMatKhau();
        }
    }

    public void loginAccount() {
        for (int i = 0; i < itemList.size(); i++) {
            if (name.getText().toString().equalsIgnoreCase(itemList.get(i).getName())
                    && pass.getText().toString().equalsIgnoreCase(itemList.get(i).getPass())
                    ) {
                if (Integer.parseInt(itemList.get(i).getBlockUser()) > 5) {
                    alert.showAlertDialog(LoginActivity.this, "Đăng Nhập Thất Bại!", "Tài khoản của bạn đã bị khóa", false);
                } else if (itemList.get(i).getQuyen().equalsIgnoreCase("ADMIN")) {
                    session.createLoginSession(itemList.get(i).getName(), itemList.get(i).getMail());
                    mSoundManager.playSound(3);
                    rung.vibrate(500);
                    Intent intent = new Intent(this, AdminActivity.class);
                    startActivity(intent);

                    login = false;
                } else {
                    session.createLoginSession(itemList.get(i).getName(), itemList.get(i).getMail());
                    //alert.showAlertDialog(LoginActivity.this, "Đăng Nhập Thành Công!", "Xin chào\t" + itemList.get(i).getName(), true);
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công!\nXin chào\t" + itemList.get(i).getName(), Toast.LENGTH_LONG).show();
                    mSoundManager.playSound(3);
                    rung.vibrate(500);
                    Intent intent = new Intent(this, Navigation.class);
                    intent.putExtra("ten", itemList.get(i).getName());
                    intent.putExtra("mail", itemList.get(i).getMail());
                    startActivity(intent);


                    login = false;
                }
            }
        }
        if (login) {
            mSoundManager.playSound(1);
            rung.vibrate(500);
            alert.showAlertDialog(LoginActivity.this, "Đăng Nhập Thất Bại!", "Tài khoản hoặc mật khẩu không hợp lệ !Vui lòng đăng nhập lại", false);
        }
    }

    public void Animation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(5000);
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        logo2.startAnimation(rotateAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(5000);
        alphaAnimation.setRepeatCount(RotateAnimation.INFINITE);
        logo3.startAnimation(alphaAnimation);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (!validateName()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            if (!validatePassword()) {
                mSoundManager.playSound(1);
                rung.vibrate(500);
                return false;
            }
            loginAccount();
            return true;
        }
        return false;
    }

    public void quenMatKhau() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        LayoutInflater inflater = getLayoutInflater();
        View rootView = inflater.inflate(R.layout.quenpass, null);
        inputLayoutName2 = (TextInputLayout) rootView.findViewById(R.id.input_layout_name2);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_mail2);


        input1 = (EditText) rootView.findViewById(R.id.edt_qptk);
        input2 = (EditText) rootView.findViewById(R.id.edt_qpm);

        input1.addTextChangedListener(new MyTextWatcher(input1));
        input2.addTextChangedListener(new MyTextWatcher(input2));

        alertDialogBuilder.setTitle("Lấy lại mật khẩu!")
                .setView(rootView)
                .setMessage("Nhập tên tài khoản và mail để lấy lại mật khẩu")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

                        quenMK();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.icon)
                .show();

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void quenMK() {
        l1 = input1.getText().toString();
        l2 = input2.getText().toString();
        for (int i = 0; i < itemList.size(); i++) {
            if ((l1.equals(itemList.get(i).getName())) && (l2.equals(itemList.get(i).getMail()))) {
                mSoundManager.playSound(3);
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Mật khẩu của bạn là: " + itemList.get(i).getPass());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                mSoundManager.playSound(1);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilder.setTitle("Thông báo");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Tài khoản hoặc Mail không tồn tại!")
                        .setCancelable(false)
                        .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                rung.vibrate(500);
                                dialog.dismiss();

                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

        }


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
                case R.id.edt_name:
                    validateName();
                    break;
                case R.id.edt_pass:
                    validatePassword();
                    break;
                case R.id.edt_qptk:
                    validateQuenName();
                    break;
                case R.id.edt_qpm:
                    validateEmail();
                    break;

            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateQuenName() {

        String acc = input1.getText().toString();
        Matcher m = p.matcher(acc);
        boolean b = m.find();
        if (b) {
            inputLayoutName2.setError("Tài khoản không được chưa ký tự đặc biệt");
            requestFocus(input1);
            return false;
        } else if (input1.getText().toString().length() > 25) {
            inputLayoutName2.setError("Tài khoản phải dưới 25 ký tự ");
            requestFocus(input1);
            return false;
        } else if (input1.getText().toString().trim().isEmpty() || input1.getText().toString().length() <= 6) {
            inputLayoutName2.setError("Tài khoản phải trên 6 ký tự ");
            requestFocus(input1);
            return false;
        } else {

            inputLayoutName2.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        String acc = name.getText().toString();
        Matcher m = p.matcher(acc);
        boolean b = m.find();
        if (b) {
            inputLayoutName.setError("Tài khoản không được chưa ký tự đặc biệt");
            requestFocus(name);
            return false;
        } else if (name.getText().toString().length() > 25) {
            inputLayoutName.setError("Tài khoản phải dưới 25 ký tự ");
            requestFocus(name);
            return false;
        } else if (name.getText().toString().trim().isEmpty() || name.getText().toString().length() <= 6) {
            inputLayoutName.setError("Tài khoản phải trên 6 ký tự ");
            requestFocus(name);
            return false;
        } else {

            inputLayoutName.setErrorEnabled(false);
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
        String email = input2.getText().toString().trim();
        if (email.isEmpty()) {
            inputLayoutEmail.setError("Địa chỉ email không được để trống");
            requestFocus(input2);
            return false;
        } else if (!isValidEmail(email)) {
            inputLayoutEmail.setError("Địa chỉ email không hợp lệ");
            requestFocus(input2);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {

        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}