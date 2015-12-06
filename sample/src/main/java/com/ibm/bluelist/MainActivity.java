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
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.directions.sample.R;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends Activity implements OnEditorActionListener{

	List<Item> itemList;
	BlueListApplication blApplication;
	ArrayAdapter<Item> lvArrayAdapter;
	ActionMode mActionMode = null;
	int listItemPosition;
	public static final String CLASS_NAME="MainActivity";

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
		setContentView(R.layout.bluemix_activity_main);
		
		/* Use application class to maintain global state. */
		blApplication = (BlueListApplication) getApplication();
		itemList = blApplication.getItemList();
		
		/* Set up the array adapter for items list view. */
		ListView itemsLV = (ListView)findViewById(R.id.itemsList);
		lvArrayAdapter = new ArrayAdapter<Item>(this, R.layout.list_item_1, itemList);
		itemsLV.setAdapter(lvArrayAdapter);
		
		/* Refresh the list. */
		listItems(); 

		/* Set long click listener. */
		itemsLV.setOnItemLongClickListener(new OnItemLongClickListener() {
			/* Called when the user long clicks on the textview in the list. */
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position,
										   long rowId) {
				listItemPosition = position;
				if (mActionMode != null) {
					return false;
				}
		        /* Start the contextual action bar using the ActionMode.Callback. */
				mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
				return true;
			}
		});
		EditText pass = (EditText) findViewById(R.id.edt_pass);
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		/* Set key listener for edittext (done key to accept item to list). */
		itemToAdd.setOnEditorActionListener(this);
		pass.setOnEditorActionListener(this);
		itemToAdd.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					createItem(v);
					return true;
				}
				return false;
			}
		});

		/* Set key listener for edittext (done key to accept item to list). */
		pass.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_DONE){
					createItem(v);
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Removes text on click of x button.
	 *
	 * @param  v the edittext view.
	 */
	public void clearText(View v) {
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		EditText pass = (EditText) findViewById(R.id.edt_pass);
		itemToAdd.setText("");
		pass.setText("");
	}
	
	/**
	 * Refreshes itemList from data service.
	 * 
	 * An IBMQuery is used to find all the list items.
	 */
	public void listItems() {
		try {
			IBMQuery<Item> query = IBMQuery.queryForClass(Item.class);
			// Query all the Item objects from the server.
			query.find().continueWith(new Continuation<List<Item>, Void>() {

				@Override
				public Void then(Task<List<Item>> task) throws Exception {
                    final List<Item> objects = task.getResult();
                     // Log if the find was cancelled.
                    if (task.isCancelled()){
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
                        for(IBMDataObject item:objects) {
                            itemList.add((Item) item);
                        }
                        sortItems(itemList);
                        lvArrayAdapter.notifyDataSetChanged();
					}
					return null;
				}
			},Task.UI_THREAD_EXECUTOR);
			
		}  catch (IBMDataException error) {
			Log.e(CLASS_NAME, "Exception : " + error.getMessage());
		}
	}
	
	/**
	 * On return from other activity, check result code to determine behavior.
	 */
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		switch (resultCode)
		{
		/* If an edit has been made, notify that the data set has changed. */
		case BlueListApplication.EDIT_ACTIVITY_RC:
			sortItems(itemList);
			lvArrayAdapter.notifyDataSetChanged();
    		break;
		}
    }
	
	/**
	 * Called on done and will add item to list.
	 *
	 * @param  v edittext View to get item from.
	 * @throws IBMDataException 
	 */
	public void createItem(View v) {
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);

		String toAdd = itemToAdd.getText().toString();

		EditText pass = (EditText) findViewById(R.id.edt_pass);
		String topass = pass.getText().toString();

		Item item = new Item();
		if (!toAdd.equals("")&&!topass.equals("")) {
			item.setName(toAdd);
			item.setPass(topass);

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
			itemToAdd.setText("");
			pass.setText("");
		}

	}
	
	/**
	 * Will delete an item from the list.
	 *
	 * @param  //Item item to be deleted
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
	 * @param //List<Item> theList
	 */
	private void sortItems(List<Item> theList) {
		// Sort collection by case insensitive alphabetical order.
		Collections.sort(theList, new Comparator<Item>() {
			public int compare(Item lhs,
					Item rhs) {
				String lhsName = lhs.getName()+lhs.getPass();
				String rhsName = rhs.getName()+lhs.getPass();
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
		 * @param /ActionMode mode and MenuItem item clicked
		 */
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	Item lItem = itemList.get(listItemPosition);
	    	/* Switch dependent on which action item was clicked. */
	    	switch (item.getItemId()) {
	    		/* On edit, get all info needed & send to new, edit activity. */
	            case R.id.action_edit:
	            	updateItem(lItem.getName());
					updateItem(lItem.getPass());
	                mode.finish(); /* Action picked, so close the CAB. */
	                return true;
	            /* On delete, remove list item & update. */
	            case R.id.action_delete:
					deleteItem(lItem);
	                mode.finish(); /* Action picked, so close the CAB. */
	            default:
	                return false;
	        }
	    }

	    /* Called on exit of action mode. */
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.getId()==R.id.edt_pass ||v.getId()==R.id.itemToAdd)
		if(actionId==EditorInfo.IME_ACTION_DONE){
			createItem(v);
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		finish();
	}
}
