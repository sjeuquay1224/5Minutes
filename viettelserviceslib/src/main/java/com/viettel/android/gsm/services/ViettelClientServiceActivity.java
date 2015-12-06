package com.viettel.android.gsm.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;

import com.viettel.android.gsm.ViettelClient;
import com.viettel.android.gsm.ViettelClient.OnConnectionCallbacks;
import com.viettel.android.gsm.ViettelError;
import com.viettel.android.gsm.utils.AndroidUtils;

/**
 * required add information below to AndroidManifest.xml
 * 
 * <pre>
 * &lt;service android:name="com.viettel.android.gsm.services.ViettelLocalService"
 * 	android:enabled="true" /&gt;
 * </pre>
 * 
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
 */
public abstract class ViettelClientServiceActivity extends AbstractViettelActivity {

	/**
	 * ViettelLocalService
	 */
	private ViettelLocalService mService;

	/**
	 * mBound
	 */
	private boolean mBound = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Validate
		mClassExitApp = classExitApp();
		checkViettelLocalService();
		ensureClassExitApp();
		
		//Bind to ViettelLocalService
		if(!mBound){
			Intent intent = new Intent(this, ViettelLocalService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindClientService(mClassExitApp);
	}

	@Override
	public void onConnectFail(ViettelError arg0) {
		unbindClientService(getClass());
		if (customCallbacks != null) {
			customCallbacks.onConnectFail(arg0);
		}
		//TODO something.
	}

	@Override
	public void onConnected() {
		if (customCallbacks != null) {
			customCallbacks.onConnected();
		}
		//TODO something.
	}
	
	@Override
	public OnConnectionCallbacks onConnectionCallbacks() {
		return null;
	}
	
	@Override
	public ViettelClient getViettelClient() {
		return (mBound && mService != null) ? mService.getViettelClient() : null;
	}
	
	/**
	 * get ViettelLocalService
	 * 
	 * @return ViettelLocalService if ViettelLocalService is inBound, Otherwise
	 *         return null.
	 */
	public ViettelLocalService getViettelLocalService(){
		return mBound ? mService : null;
	}
	
	@Override
	public void startClient(OnConnectionCallbacks callbacks) {
		InitializeParams params = initParams();
		if(params == null){
			throw new NullPointerException("InitializeParams is null.");
		}
		if(mBound && equalsMainClass(mClassExitApp)){
			customCallbacks = (callbacks != null) ? callbacks : onConnectionCallbacks();
			mService.initialize((callbacks != null) ? callbacks : ViettelClientServiceActivity.this);
			mService.setViettelId(params.getPublisherId(), params.getAppId());
			mService.setTestDevice(params.isTestDevice());
			if(params.getReadRequestTimeout() > 0){
				mService.setReadRequestTimeout(params.getReadRequestTimeout());
			}
			mService.startClient();
		}
	}
	
	@Override
	public void startClient() {
		startClient(null);
	}
	
	//Unbind ViettelLocalService from the service.
	//Note: Call method when exit the application or you dont want to use ViettelClient (SDK of Viettel).
	private void unbindClientService(Class<?> activityExit){
		if(activityExit == null){
			throw new NullPointerException("activityExit");
		}
		boolean isMainClass = equalsMainClass(activityExit);
		if (mBound && mService != null && mConnection != null) {
			mService.setExitApp(isMainClass);
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	//Check Exsist Services ViettelLocalService in file AndroidManifest.xml
	private void checkViettelLocalService() {
    	try {
			AndroidUtils.isExsistServices(this, ViettelLocalService.class);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(TAG + "--> Class ViettelLocalService not found in file AndroidManifest.xml", e);
		}
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			// We've bound to ViettelLocalService, cast the IBinder and get
			// LocalService instance
			ViettelLocalService.ViettelBinder b = (ViettelLocalService.ViettelBinder) binder;
			mService = b.getService();
			mBound = (mService != null);
			
			//call method startClient() connect to server if autoConnectServer() return true.
			//you can call method startClient() at other times and autoConnectServer() return false.
			if(autoConnectServer()){
				startClient();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			mBound = false;
		}
	};

}
