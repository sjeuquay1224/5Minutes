package com.viettel.android.gsm.services;

import android.content.Context;

import com.viettel.android.gsm.ViettelClient;
import com.viettel.android.gsm.ViettelClient.ViettelClientStatus;
import com.viettel.android.gsm.utils.ViettelUtils;

public class ViettelLocalSimple {

	private static ViettelLocalSimple sInstances = null;
	private ViettelClient viettelClient;

	public synchronized static ViettelLocalSimple getInstances() {
		if (sInstances == null) {
			sInstances = new ViettelLocalSimple();
		}
		return sInstances;
	}

	// step 1: initialize for ViettelClient.
	public void initialize(Context context, ViettelClient.OnConnectionCallbacks callbacks) {
		if (viettelClient == null) {
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

	public void onDestroy() {
		if (viettelClient != null) {
			viettelClient.onDestroy();
			viettelClient = null;
		}
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

	public boolean isConnected() {
		return viettelClient != null ? viettelClient.isConnected() : false;
	}

	public boolean isConnecting() {
		return viettelClient != null ? viettelClient.isConnecting() : false;
	}

	public boolean isDisconnect() {
		return viettelClient != null ? viettelClient.isDisconnect() : false;
	}

	public ViettelClientStatus getClientStatus() {
		return viettelClient != null ? viettelClient.getStatus() : null;
	}

	// ensure class ViettelClient
	private void ensureViettelClient() {
		if (viettelClient == null) {
			throw new NullPointerException("viettelClient is null.");
		}
	}
}
