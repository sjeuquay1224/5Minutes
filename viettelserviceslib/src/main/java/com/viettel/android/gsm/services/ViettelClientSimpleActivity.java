package com.viettel.android.gsm.services;

import android.os.Bundle;

import com.viettel.android.gsm.ViettelClient;
import com.viettel.android.gsm.ViettelClient.OnConnectionCallbacks;
import com.viettel.android.gsm.ViettelError;

/**
 * 
 * Use ViettelClientSimpleActivity with no service in my app.
 * You can custom listener {@link #onConnectionCallbacks} for connection callback use @Override method
 * {@link #onConnectionCallbacks()}. see more an example of onConnectionCallbacks():
 * 
 * <pre class="prettyprint">
 * //@Override
 * public OnConnectionCallbacks onConnectionCallbacks() {
 * 	return new OnConnectionCallbacks() {
 * 		//@Override
 *		public void onConnected() {
 * 			//TODO Something...
 * 		}
 *			
 *		//@Override
 * 		public void onConnectFail(ViettelError messageErrors) {
 * 			//TODO Something...
 * 		}
 *	};
 * }
 * </pre>
 *
 */
public abstract class ViettelClientSimpleActivity extends AbstractViettelActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Validate
		mClassExitApp = classExitApp();
		ensureClassExitApp();

		//call method startClient() connect to server if autoConnectServer() return true.
		//you can call method startClient() at other times and autoConnectServer() return false.
		if(autoConnectServer()){
			startClient();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroy(mClassExitApp);
	}

	@Override
	public void onConnectFail(ViettelError arg0) {
		onDestroy(getClass());
		if (customCallbacks != null) {
			customCallbacks.onConnectFail(arg0);
		}
		// TODO something.
	}

	@Override
	public void onConnected() {
		if (customCallbacks != null) {
			customCallbacks.onConnected();
		}
		// TODO something.
	}

	@Override
	public OnConnectionCallbacks onConnectionCallbacks() {
		return null;
	}
	
	@Override
	public ViettelClient getViettelClient() {
		return ViettelLocalSimple.getInstances().getViettelClient();
	}
	
	@Override
	public void startClient() {
		startClient(null);
	}
	
	@Override
	public void startClient(OnConnectionCallbacks callbacks) {
		InitializeParams params = initParams();
		if(params == null){
			throw new NullPointerException("InitializeParams is null.");
		}
		if(equalsMainClass(mClassExitApp)){
			customCallbacks = (callbacks != null) ? callbacks : onConnectionCallbacks();
			ViettelLocalSimple.getInstances().initialize(ViettelClientSimpleActivity.this, (callbacks != null) ? callbacks : ViettelClientSimpleActivity.this);
			ViettelLocalSimple.getInstances().setViettelId(params.getPublisherId(), params.getAppId());
			ViettelLocalSimple.getInstances().setTestDevice(params.isTestDevice());
			if(params.getReadRequestTimeout() > 0){
				ViettelLocalSimple.getInstances().setReadRequestTimeout(params.getReadRequestTimeout());
			}
			ViettelLocalSimple.getInstances().startClient();
		}
	}
	

	// destroy ViettelClient if current Activity is mClassExitApp. 
	//Note: Call method when exit the application or you dont want to use ViettelClient (SDK of Viettel).
	private void onDestroy(Class<?> activityExit) {
		if (activityExit == null) {
			throw new NullPointerException("activityExit");
		}
		if(equalsMainClass(activityExit)){
			ViettelLocalSimple.getInstances().onDestroy();
		}

	}

}
