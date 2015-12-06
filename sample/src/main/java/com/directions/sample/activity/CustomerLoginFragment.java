package com.directions.sample.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.sample.LoginActivity;
import com.directions.sample.R;
import com.directions.sample.model.AlertDialogManager;
import com.directions.sample.model.SoundManager;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.Item;
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
 * Created by RON on 10/5/2015.
 */
public class CustomerLoginFragment extends Fragment implements TextView.OnEditorActionListener, OnClickListener {
    List<Item> itemList;
    BlueListApplication blApplication;
    private SoundManager mSoundManager;
    public static final String CLASS_NAME = "CustomerLoginFragment";
    EditText pass, account, mail, phone;
    Pattern p, p2;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    Vibrator rung;
    private TextInputLayout inputLayoutName, inputLayoutPass, inputLayoutEmail, inputLayoutPhone;

    public CustomerLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rung = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        blApplication = (BlueListApplication) getActivity().getApplication();
        itemList = blApplication.getItemList();
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getActivity().getBaseContext());
        mSoundManager.addSound(1, R.raw.warring);
        mSoundManager.addSound(2, R.raw.success);
        mSoundManager.addSound(3, R.raw.click);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_dangky_customer, container, false);

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        inputLayoutPass = (TextInputLayout) rootView.findViewById(R.id.input_layout_pass);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_mail);
        inputLayoutPhone = (TextInputLayout) rootView.findViewById(R.id.input_layout_phone);

        p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
        p2 = Pattern.compile("[^A-Za-z0-9@. ]", Pattern.CASE_INSENSITIVE);

        pass = (EditText) rootView.findViewById(R.id.edt_pass);
        account = (EditText) rootView.findViewById(R.id.edt_acc);
        mail = (EditText) rootView.findViewById(R.id.edt_mail_customer);
        phone = (EditText) rootView.findViewById(R.id.edt_phone_customer);


        account.addTextChangedListener(new MyTextWatcher(account));
        pass.addTextChangedListener(new MyTextWatcher(pass));
        mail.addTextChangedListener(new MyTextWatcher(mail));
        phone.addTextChangedListener(new MyTextWatcher(phone));

        /* Set key listener for edittext (done key to accept item to list). */
        account.setOnEditorActionListener(this);
        pass.setOnEditorActionListener(this);
        mail.setOnEditorActionListener(this);
        phone.setOnEditorActionListener(this);
        Button dangky = (Button) rootView.findViewById(R.id.btn_dangky);
        dangky.setOnClickListener(this);
        Button huy = (Button) rootView.findViewById(R.id.btn_cancel_customer);
        huy.setOnClickListener(this);

        return rootView;
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
                        //lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    public void createItem(View v) {
        EditText account = (EditText) getActivity().findViewById(R.id.edt_acc);
        String toacc = account.getText().toString();

        EditText pass = (EditText) getActivity().findViewById(R.id.edt_pass);
        String topass = pass.getText().toString();


        EditText mail = (EditText) getActivity().findViewById(R.id.edt_mail_customer);
        String tomail = mail.getText().toString();

        EditText phone = (EditText) getActivity().findViewById(R.id.edt_phone_customer);
        String tophone = phone.getText().toString();
        Item item = new Item();
        if (!toacc.equals("") && !topass.equals("") && !tomail.equals("") && !tophone.equals("")) {
            item.setName(toacc);
            item.setPass(topass);
            item.setMail(tomail);
            item.setPhone(tophone);
            item.setQuyen("CUSTOMER");
            item.setImage3("0");
            item.setMoney("0");
            item.setCmnd("0");
            item.setImage("0");
            item.setImage2("0");
            item.setImage3("0");
            item.setVido("0");
            item.setKinhDo("0");
            item.setAddress("0");
            item.setMoney2("0");
            item.setMessage("0");
            item.setStart("0");
            item.setDestination("0");
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

            // set title
            alertDialogBuilder.setTitle("Thành Công!");

            // set dialog message
            alertDialogBuilder
                    .setIcon(R.drawable.icon_success)
                    .setMessage("Chúc mừng bạn đã đăng ký thành công. Quay về trang đăng nhập")
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
            createItem(v);
            mSoundManager.playSound(2);
            rung.vibrate(500);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_dangky) {
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
            createItem(v);
            mSoundManager.playSound(2);
            rung.vibrate(500);

        }
        if (v.getId() == R.id.btn_cancel_customer) {
            mSoundManager.playSound(3);
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent);
        }
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
                case R.id.edt_acc:
                    validateName();
                    break;
                case R.id.edt_pass:
                    validatePassword();
                    break;
                case R.id.edt_mail_customer:
                    validateEmail();
                    break;
                case R.id.edt_phone_customer:
                    validatePhone();
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
        if (account.getText().toString().trim().isEmpty() || account.getText().toString().length() <= 6) {
            inputLayoutName.setError("Tài khoản phải trên 6 ký tự ");
            requestFocus(account);
            return false;
        } else if (account.getText().toString().length() > 25) {
            inputLayoutName.setError("Tài khoản phải dưới 25 ký tự ");
            requestFocus(account);
            return false;
        } else if (b) {
            inputLayoutName.setError("Tài khoản không được chưa ký tự đặc biệt");
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
        if (pass.getText().toString().trim().isEmpty() || pass.getText().toString().length() <= 6) {
            inputLayoutPass.setError("Mật khẩu phải trên 6 ký tự (Bao gồm số và chữ)");
            requestFocus(pass);
            return false;
        } else if (pass.getText().toString().length() > 15) {
            inputLayoutPass.setError("Mật khẩu phải dưới 15 ký tự (Bao gồm số và chữ)");
            requestFocus(pass);
            return false;
        } else if (b) {
            inputLayoutPass.setError("Mật khẩu không được chưa ký tự đặc biệt");
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

}