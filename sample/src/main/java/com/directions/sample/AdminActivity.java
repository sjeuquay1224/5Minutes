package com.directions.sample;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.directions.sample.model.SoundManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.EditActivity;
import com.ibm.bluelist.HistoryCard;
import com.ibm.bluelist.HistoryTrip;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

public class AdminActivity extends AppCompatActivity {
    private Toolbar toolbar;
    ArrayAdapter<Item> lvArrayAdapter;
    List<Item> itemList;
    BlueListApplication blApplication;
    List<HistoryCard> historyCards;
    List<HistoryTrip> historyTrips;
    // lvArrayAdapter;
    ArrayAdapter<Item> lvArrayAdapter1;
    ActionMode mActionMode = null;
    int listItemPosition;
    public static final String CLASS_NAME = "AdminActivity";
    final Context context = this;
    EditText money, money2;
    Item lItem;
    ListView itemsLV;
    private SoundManager mSoundManager;
    FloatingActionMenu menuLabelsRight;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private Handler mUiHandler = new Handler();
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    // Session Manager Class
    SessionManager session;
    Vibrator rung;

    @Override
    /**
     * onCreate called when main activity is created.
     *
     * Sets up the itemList, application, and sets listeners.
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();
        historyCards = blApplication.getHistoryCards();
        historyTrips = blApplication.getHistoryTrips();
        setContentView(R.layout.activity_admin);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        rung = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        mSoundManager.addSound(2, R.raw.success);
        mSoundManager.addSound(3, R.raw.warring);

        /* Refresh the list. */
        listItems();


        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.actv_admin);
        actv.setHintTextColor(getResources().getColor(R.color.white));
        lvArrayAdapter1 = new ArrayAdapter<Item>(this, R.layout.list_item_1, itemList);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(lvArrayAdapter1);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.BLUE);

        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) { //do your work here
                    // When user changed the Text
                    lvArrayAdapter.getFilter().filter(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        menuLabelsRight = (FloatingActionMenu) findViewById(R.id.menu);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);
        fab4.setOnClickListener(clickListener);
        fab5.setOnClickListener(clickListener);

        menus.add(menuLabelsRight);
        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }


		/* Set long click listener. */
        itemsLV.setOnItemLongClickListener(new OnItemLongClickListener() {
            /* Called when the user long clicks on the textview in the list. */
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position,
                                           long rowId) {
                listItemPosition = position;
                toolbar.setVisibility(View.INVISIBLE);
                mSoundManager.playSound(1);
                /*if (mActionMode != null) {
                    return false;
                }*/
                /* Start the contextual action bar using the ActionMode.Callback. */
                mActionMode = AdminActivity.this.startActionMode(mActionModeCallback);
                return true;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản Lý Tài Khoản");


    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Use application class to maintain global state. */
        blApplication = (BlueListApplication) getApplication();
        itemList = blApplication.getItemList();
    }

    /**
     * Removes text on click of x button.
     *
     * @param v the edittext view.
     */
    public void clearText(View v) {
        EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
        itemToAdd.setText("");
    }

    /**
     * Refreshes itemList from data service.
     * <p/>
     * An IBMQuery is used to find all the list items.
     */
    public void listItems() {
        try {

             /* Set up the array adapter for items list view. */
            itemsLV = (ListView) findViewById(R.id.itemsList);
            lvArrayAdapter = new ArrayAdapter<Item>(this, R.layout.list_item_1, itemList);
            itemsLV.setAdapter(lvArrayAdapter);
            //lvArrayAdapter = new CustomListAdapter(this, listname, listmail);
            //itemsLV.setAdapter(lvArrayAdapter);
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

    /**
     * On return from other activity, check result code to determine behavior.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        /* If an edit has been made, notify that the data set has changed. */
            case BlueListApplication.EDIT_ACTIVITY_RC:
                sortItems(itemList);
                lvArrayAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * Will delete an item from the list.
     *
     * @param //Item item to be deleted
     */
    public void deleteItem(Item item) {
        itemList.remove(listItemPosition);

        // This will attempt to delete the item on the server.
        item.delete().continueWith(new Continuation<IBMDataObject, Void>() {

            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                // Log if the delete was cancelled.
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                }

                // Log error message, if the delete task fails.
                else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                }

                // If the result succeeds, reload the list.
                else {
                    lvArrayAdapter.notifyDataSetChanged();
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

        lvArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Will call new activity for editing item on list.
     *
     * @parm String name - name of the item.
     */
    public void updateItem(String name) {
        Intent editIntent = new Intent(getBaseContext(), EditActivity.class);
        editIntent.putExtra("ItemText", name);
        editIntent.putExtra("ItemLocation", listItemPosition);
        startActivityForResult(editIntent, BlueListApplication.EDIT_ACTIVITY_RC);
    }

    /**
     * Sort a list of Items.
     *
     * @param //List<Item> theList
     */
    private void sortItems(List<Item> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<Item>() {
            public int compare(Item lhs,
                               Item rhs) {
                String lhsName = lhs.getName();
                String rhsName = rhs.getName();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            /* Inflate a menu resource with context menu items. */
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.editaction, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         * Called when user clicks on contextual action bar menu item.
         *
         * Determined which item was clicked, and then determine behavior appropriately.
         *
         * @param //ActionMode mode and MenuItem item clicked
         */
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            lItem = itemList.get(listItemPosition);
            /* Switch dependent on which action item was clicked. */
            switch (item.getItemId()) {
                /* On edit, get all info needed & send to new, edit activity. */
                case R.id.action_edit:
                    mSoundManager.playSound(1);
                    updateItem(lItem.getPass());
                    mode.finish(); /* Action picked, so close the CAB. */
                    toolbar.setVisibility(View.VISIBLE);
                    return true;
                /* On delete, remove list item & update. */
                case R.id.action_delete:
                    mSoundManager.playSound(1);
                    toolbar.setVisibility(View.VISIBLE);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set title
                    alertDialogBuilder.setTitle("Cảnh Báo!!");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Bạn chắc chắn muốn xóa tài khoản!")
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //AdminActivity.this.finish();
                                    deleteItem(lItem);
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
                    mode.finish(); /* Action picked, so close the CAB. */
                default:
                    return false;
            }
        }

        /* Called on exit of action mode. */
        public void onDestroyActionMode(ActionMode mode) {
            toolbar.setVisibility(View.VISIBLE);
            mActionMode = null;
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = "";

            switch (v.getId()) {
                case R.id.fab1:
                    mSoundManager.playSound(1);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    LayoutInflater inflater = getLayoutInflater();
                    View rootView = inflater.inflate(R.layout.khuyenmai, null);
                    money = (EditText) rootView.findViewById(R.id.edt_km);

                    // set title
                    alertDialogBuilder.setTitle("Khuyến Mãi!");

                    // set dialog message
                    alertDialogBuilder
                            .setView(rootView)
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    boolean check = true;
                                    String tomoney = money.getText().toString();
                                    if (tomoney.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "Không được rỗng", Toast.LENGTH_LONG).show();
                                        mSoundManager.playSound(3);
                                        check = false;
                                    }
                                    if (check) {
                                        Float a = Float.parseFloat(tomoney);
                                        for (int i = 1; i < itemList.size(); i++) {
                                            Item item = itemList.get(i);
                                            Float sotien = Float.parseFloat(itemList.get(i).getMoney()) + a;
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
                                        }
                                        Toast.makeText(getApplicationContext(), "Thành Công", Toast.LENGTH_LONG).show();
                                        mSoundManager.playSound(2);
                                    }
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
                    text = fab1.getLabelText();
                    break;
                case R.id.fab2:
                    mSoundManager.playSound(1);
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(
                            context);

                    // set title
                    alertDialogBuilder1.setTitle("Tạo Tài Khoản Mới");

                    // set dialog message
                    alertDialogBuilder1
                            .setMessage("Chọn Đồng ý để tạo mới tài khoản")
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(AdminActivity.this, ActivityRegister.class);
                                    startActivity(i);
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
                    AlertDialog alertDialog1 = alertDialogBuilder1.create();

                    // show it
                    alertDialog1.show();
                    break;
                case R.id.fab3:
                    mSoundManager.playSound(1);
                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
                            context);
                    LayoutInflater inflater2 = getLayoutInflater();
                    View rootView2 = inflater2.inflate(R.layout.layout_doigia, null);
                    money2 = (EditText) rootView2.findViewById(R.id.edt_doigia);

                    // set title
                    alertDialogBuilder2.setTitle("Thay Đổi Giá!");

                    // set dialog message
                    alertDialogBuilder2
                            .setView(rootView2)
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    boolean check = true;
                                    String tomoney = money2.getText().toString();
                                    if (tomoney.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "Không được rỗng", Toast.LENGTH_LONG).show();
                                        mSoundManager.playSound(3);
                                        check = false;
                                    }
                                    if (check) {
                                        Float a = Float.parseFloat(tomoney);
                                        for (int i = 1; i < itemList.size(); i++) {
                                            Item item = itemList.get(i);
                                            String str = Float.toString(a);
                                            item.setPRICE(str);
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
                                        Toast.makeText(getApplicationContext(), "Thành Công", Toast.LENGTH_LONG).show();
                                    }
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
                    AlertDialog alertDialog2 = alertDialogBuilder2.create();

                    // show it
                    alertDialog2.show();
                    break;
                case R.id.fab4:
                    uudai();
                    break;
                case R.id.fab5:
                      /* Use application class to maintain global state. */
                    blApplication = (BlueListApplication) getApplication();
                    itemList = blApplication.getItemList();
                    listItems();
                    break;

            }


        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for2Click();        //Double click the exit function call
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Thoát Tài Khoản");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Bạn chắc chắn muốn thoát tài khoản!")
                    .setCancelable(false)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Clear the session data
                            // This will clear all session data and
                            // redirect user to LoginActivity
                            session.logoutUser();
                            rung.vibrate(500);
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
    public void uudai(){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    // set title
    alertDialogBuilder.setTitle("Danh sách ưu đãi");

    // set dialog message
    alertDialogBuilder
            .setMessage("Tính năng này đang được cập nhật!")
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
}


}