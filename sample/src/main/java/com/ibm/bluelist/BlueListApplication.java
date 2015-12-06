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
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.core.http.IBMHttpResponse;
import com.ibm.mobile.services.data.IBMData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bolts.Continuation;
import bolts.Task;

public final class BlueListApplication extends Application {
	private static final String APP_ID = "applicationID";
	private static final String APP_SECRET = "applicationSecret";
	private static final String APP_ROUTE = "applicationRoute";
	private static final String PROPS_FILE = "bluelist.properties";
	public static final int EDIT_ACTIVITY_RC = 1;
	private static final String CLASS_NAME = BlueListApplication.class
			.getSimpleName();
	List<Item> itemList;
	List<HistoryTrip> historyTrips;
	List<Ranting> rantings;
	List<HistoryCard> historyCards;
	Properties props;
	IBMCloudCode cloudCodeService;
	public BlueListApplication() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity,
										  Bundle savedInstanceState) {
				Log.d(CLASS_NAME,
						"Activity created: " + activity.getLocalClassName());
			}

			@Override
			public void onActivityStarted(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity started: " + activity.getLocalClassName());
			}

			@Override
			public void onActivityResumed(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity resumed: " + activity.getLocalClassName());
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity,
													Bundle outState) {
				Log.d(CLASS_NAME,
						"Activity saved instance state: "
								+ activity.getLocalClassName());
			}

			@Override
			public void onActivityPaused(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity paused: " + activity.getLocalClassName());
			}

			@Override
			public void onActivityStopped(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity stopped: " + activity.getLocalClassName());
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity destroyed: " + activity.getLocalClassName());
			}
		});
	}

	@Override
	public void onCreate() {
		super.onCreate();
		itemList = new ArrayList<Item>();
		historyTrips=new ArrayList<HistoryTrip>();
		rantings=new ArrayList<Ranting>();
		historyCards=new ArrayList<HistoryCard>();
		// Read from properties file.
		props = new Properties();
		Context context = getApplicationContext();
		try {
			AssetManager assetManager = context.getAssets();
			props.load(assetManager.open(PROPS_FILE));
			Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
		} catch (FileNotFoundException e) {
			Log.e(CLASS_NAME, "The bluelist.properties file was not found.", e);
		} catch (IOException e) {
			Log.e(CLASS_NAME,
					"The bluelist.properties file could not be read properly.", e);
		}
		// Initialize the IBM core backend-as-a-service.
		IBMBluemix.initialize(this, props.getProperty(APP_ID), props.getProperty(APP_SECRET), props.getProperty(APP_ROUTE));
		// Initialize the IBM Data Service.
		IBMData.initializeService();
		cloudCodeService = IBMCloudCode.initializeService();
		// Register the Item Specialization.
		Item.registerSpecialization(Item.class);
		HistoryTrip.registerSpecialization(HistoryTrip.class);
		Ranting.registerSpecialization(Ranting.class);
		HistoryCard.registerSpecialization(HistoryCard.class);
		/*cloudCodeService.get("https://mobile.ng.bluemix.net/dataview/v1/apps/cdbf1d65-8f6c-4261-9628-b9fd9c7577fa/items").continueWith(new Continuation<IBMHttpResponse, Void>() {

			@Override
			public Void then(Task<IBMHttpResponse> task) throws Exception {
				if (task.isFaulted()) {
					// error handling code here
					Toast.makeText(getApplicationContext(), "Thất bại!", Toast.LENGTH_LONG).show();
				} else {
					IBMHttpResponse response = task.getResult();
					if(response.getHttpResponseCode() == 200) {
						// take action on success
						Toast.makeText(getApplicationContext(), "Thành Công!", Toast.LENGTH_LONG).show();
					}
				}
				return null;
			}
		});*/
	}


	/**
	 * returns the itemList, an array of Item objects.
	 *
	 * @return itemList
	 */
	public List<Item> getItemList() {
		return itemList;
	}
	public List<HistoryTrip> getHistoryTrips() {
		return historyTrips;
	}


	public List<Ranting> getRating() {
		return rantings;
	}
	public List<HistoryCard> getHistoryCards(){
		return historyCards;
	}
}