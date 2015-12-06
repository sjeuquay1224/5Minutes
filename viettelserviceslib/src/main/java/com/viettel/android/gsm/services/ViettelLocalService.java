package com.viettel.android.gsm.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.viettel.android.gsm.ViettelClient;
import com.viettel.android.gsm.ViettelClient.ViettelClientStatus;
import com.viettel.android.gsm.utils.ViettelUtils;

public final class ViettelLocalService extends Service {
	// Binder given to clients
	private final IBinder mBinder = new ViettelBinder();

	// Viettelclient for service
	private ViettelClient viettelClient;

	//flag check exit app to home screen
	private boolean isExitApp = false;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// destroy resources
		if (isExitApp && viettelClient != null) {
			viettelClient.onDestroy();
			isExitApp = false;
			viettelClient = null;
		}
	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class ViettelBinder extends Binder {
		// Return this instance of LocalService so clients can call public methods
		public ViettelLocalService getService() {
			return ViettelLocalService.this;
		}

		public boolean isAlive() {
			return isAlive();
		}
	}

	// step 1: initialize for ViettelClient.
	public void initialize(ViettelClient.OnConnectionCallbacks callbacks) {
		if (viettelClient == null) {
			Context context = getApplicationContext();
			context = (context == null) ? getBaseContext() : context;
			viettelClient = new ViettelClient(context, callbacks);
		}
	}

	// step 2: initialize publisherId and appId for ViettelClient.
	public void setViettelId(String publisherId, String appId) {
		ensureViettelClient();
		if (ViettelUtils.isEmpty(publisherId)) {
			throw new NullPointerException("publisherId is null.");
		}
		if (ViettelUtils.isEmpty(appId)) {
			throw new NullPointerException("appId is null.");
		}
		
		viettelClient.setViettelId(publisherId, appId);
	}

	// step 3: start ViettelClient.
	public void startClient() {
		ensureViettelClient();
		viettelClient.connect();
	}
	
	public ViettelClient getViettelClient() {
		return viettelClient;
	}
	
	public void setExitApp(boolean isExitApp) {
		this.isExitApp = isExitApp;
	}

	public void setReadRequestTimeout(long readRequestTimeout) {
		ensureViettelClient();
		if (readRequestTimeout < 0) {
			throw new ExceptionInInitializerError("readRequestTimeout < 0");
		}
		viettelClient.setReadRequestTimeout(readRequestTimeout);
	}

	public void setTestDevice(boolean testDevice) {
		ensureViettelClient();
		viettelClient.setTestDevice(testDevice);
	}
	
	public String getMsisdn() {
		return viettelClient != null ? viettelClient.getMsisdn() : null;
	}

	public boolean isConnected(){
		return viettelClient != null ? viettelClient.isConnected() : false;
	}
	
	public boolean isConnecting(){
		return viettelClient != null ? viettelClient.isConnecting() : false;
	}
	
	public boolean isDisconnect(){
		return viettelClient != null ? viettelClient.isDisconnect() : false;
	}
	
	public ViettelClientStatus getClientStatus(){
		return viettelClient != null ? viettelClient.getStatus() : null;
	}
	
	// ensure class ViettelClient
	private void ensureViettelClient() {
		if (viettelClient == null) {
			throw new NullPointerException("viettelClient is null.");
		}
	}

}
