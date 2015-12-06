package com.directions.sample.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.sample.R;
import com.directions.sample.model.RoundImage;
import com.directions.sample.model.SoundManager;
import com.github.clans.fab.FloatingActionButton;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by RON on 10/15/2015.
 */
public class ProfileFragment extends Fragment {
    private static int RESULT_LOAD_IMG = 1;
    List<Item> itemList;
    BlueListApplication blApplication;
    ArrayAdapter<Item> lvArrayAdapter;
    byte[] imageAsBytes;
    byte[] byteArray;
    ImageView imageView, address, cm;
    String strBase64, imgDecodableString, hinhanh3;
    public static final String CLASS_NAME = "ProfileFragment";
    int stt;
    TextView ten, mail, tien, socm, socm1, diachi, diachi1, phone, pass, name;
    CollapsingToolbarLayout collapsingToolbar;
    private SoundManager mSoundManager;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getActivity().getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        blApplication = (BlueListApplication) getActivity().getApplication();
        itemList = blApplication.getItemList();
        View layout = inflater.inflate(R.layout.profile, container, false);
        stt = getArguments().getInt("stt");


        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar2);
        //collapsingToolbar.setTitle("Xin Chào");

        imageView = (ImageView) layout.findViewById(R.id.img_avatar);
        ten = (TextView) layout.findViewById(R.id.txt_hoten_customer);
        mail = (TextView) layout.findViewById(R.id.txt_mailcustomer);
        tien = (TextView) layout.findViewById(R.id.txt_tien);
        socm = (TextView) layout.findViewById(R.id.txt_socmnd);
        diachi = (TextView) layout.findViewById(R.id.txt_diachi);
        socm1 = (TextView) layout.findViewById(R.id.cmnd);
        diachi1 = (TextView) layout.findViewById(R.id.diachi);
        phone = (TextView) layout.findViewById(R.id.txt_phone);
        pass = (TextView) layout.findViewById(R.id.txt_pass);
        name = (TextView) layout.findViewById(R.id.txt_name);
        address = (ImageView) layout.findViewById(R.id.imv_address);
        cm = (ImageView) layout.findViewById(R.id.imv_cmnd);

        name.setText(itemList.get(stt).getNameuser());
        ten.setText(itemList.get(stt).getName());
        mail.setText(itemList.get(stt).getMail());
        phone.setText(itemList.get(stt).getPhone());
        pass.setText(itemList.get(stt).getPass());
        tien.setText(itemList.get(stt).getMoney() + " VNĐ");
        if (itemList.get(stt).getQuyen().equalsIgnoreCase("CUSTOMER")) {
            diachi.setVisibility(View.INVISIBLE);
            socm.setVisibility(View.INVISIBLE);

        }
        socm.setText(itemList.get(stt).getCmnd());
        diachi.setText(itemList.get(stt).getAddress());

        if (itemList.get(stt).getImage3().equals("0")) {
            Toast.makeText(getActivity().getApplicationContext(), "Chưa có ảnh đại diện", Toast.LENGTH_LONG);
        } else {
            imageAsBytes = Base64.decode(itemList.get(stt).getImage3().getBytes(), Base64.DEFAULT);
            //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            imageView.setImageDrawable(new RoundImage(b));
            //collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.white));
            /*Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));*/

        }


        fab = (FloatingActionButton) layout.findViewById(R.id.fab_option);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet(v);
            }
        });
        return layout;
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == getActivity().RESULT_OK
                    && null != data) {
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
                imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));


                //chuyển ẩnh thành chuỗi

                Bitmap Image = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
                strBase64 = Base64.encodeToString(byteArray, 0);

                hinhanh3 = strBase64;

                //byteArray = Base64.decode(strBase64, Base64.DEFAULT);
                //Bitmap decodedByte = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //imageView.setImageBitmap(decodedByte);


                Item item = itemList.get(stt);

                item.setImage3(strBase64);
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
                            getActivity().setResult(BlueListApplication.EDIT_ACTIVITY_RC, returnIntent);
                            getActivity().finish();
                        }
                        return null;
                    }

                }, Task.UI_THREAD_EXECUTOR);
                Toast.makeText(getActivity().getApplicationContext(), "Thành Công", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Bạn chưa chọn ảnh",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Ðã có lỗi xảy ra", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        name.setText(itemList.get(stt).getNameuser());
        if (itemList.get(stt).getImage3().equals("0")) {
            Toast.makeText(getActivity().getApplicationContext(), "Chưa có ảnh đại diện", Toast.LENGTH_LONG);
        } else {
            imageAsBytes = Base64.decode(itemList.get(stt).getImage3().getBytes(), Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            imageView.setImageDrawable(new RoundImage(b));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void openBottomSheet(View v) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_sheet, null);
        TextView payment = (TextView) view.findViewById(R.id.txt_payment);
        TextView photo = (TextView) view.findViewById(R.id.txt_upanh);
        TextView edit = (TextView) view.findViewById(R.id.txt_edit);

        final Dialog mBottomSheetDialog = new Dialog(getActivity(),
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setTitle("Tùy chọn");
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        payment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                Intent i = new Intent(getActivity(), ActivityRechargeCard.class);
                i.putExtra("sothutu", stt);
                startActivity(i);
                mBottomSheetDialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent i = new Intent(getActivity(), UpdateActivity.class);
                i.putExtra("stt", stt);
                startActivity(i);
                mBottomSheetDialog.dismiss();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

                mBottomSheetDialog.dismiss();
            }
        });

    }
}
