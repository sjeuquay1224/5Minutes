package com.viettel.android.gsm.services;

import com.viettel.android.gsm.ViettelClient;
import com.viettel.android.gsm.ViettelClient.OnConnectionCallbacks;

import android.app.Activity;

abstract class AbstractViettelActivity extends Activity implements OnConnectionCallbacks{

	/**
	 * IMPORTANT!!!! set class for activity exit application. Change class with
	 * Activity main process for your app. use method {@link #classExitApp()}.
	 */
	protected Class<?> mClassExitApp = null;

	/**
	 * Custom callback for ViettelClient in class {@link ViettelLocalService} or {@link ViettelLocalSimple}.
	 */
	protected OnConnectionCallbacks customCallbacks;

	/**
	 * TAG for log
	 */
	protected String TAG = AbstractViettelActivity.class.getSimpleName();
	
	/**
	 * set custom callback for ViettelClient in class {@link ViettelLocalService} or {@link ViettelLocalSimple}.
	 * @param customCallbacks
	 */
	public abstract OnConnectionCallbacks onConnectionCallbacks();
	
	/**
	 * required set main class. Use only in case of escape the application to
	 * home screen (recommended) or you dont want to use ViettelClient (ViettelAPI SDK of Viettel).
	 * <p>
	 * <strong>Warning: </strong>must not return null; it may either return a
	 * non-null value or throw an exception.
	 * </p>
	 * 
	 * @return Class<?> return not null; it may either return a non-null value
	 *         or throw an exception.
	 * 
	 * @throws NullPointerException
	 *             if <code>classExitApp()</code> return null.
	 */
	public abstract Class<?> classExitApp();

	/**
	 * required initialize parameters for class {@link ViettelClient}. Use
	 * connect App to ViettelAPIServer. parameter required: publisherId and appId, 
	 * parameter optional: readRequestTimeout and testDevice.
	 * <p>
	 * <strong>Warning: </strong> must not return null; it may either return a
	 * non-null value or throw an exception.
	 * </p>
	 * 
	 * @return InitializeParams return not null; it may either return a non-null
	 *         value or throw an exception.
	 * 
	 * @throws NullPointerException
	 *             if <code>getInitParams()</code> return null.
	 */
	public abstract InitializeParams initParams();

	/**
	 * set flag auto or manual connect to server.
	 * <p>
	 * <li> if method autoConnectServer() return true then when start the application 
	 * will auto connect to server use method {@link #startClient()}.
	 * <li> if method autoConnectServer() return false then when start the application 
	 * will not auto connect to server use method {@link #startClient()} and
	 * you must call method manual use {@link #startClient()} or {@link #startClient(OnConnectionCallbacks)}.
	 * </p>
	 * @return true if auto connect, otherwise manual connect to server.
	 */
	public abstract boolean autoConnectServer();
	
	/**
	 * Initialize ViettelClient in {@link ViettelLocalService} or {@link ViettelLocalSimple}. 
	 * <li>If Connected and call method {@link ViettelClientServiceActivity#getViettelLocalService()} 
	 * or {@link getViettelClient()} will return object ViettelClient not null, Otherwise return null.
	 * <li>If Connected and call method {@link getViettelClient()} will return object ViettelClient not null,
	 * Otherwise return null.
	 * @param OnConnectionCallbacks 
	 */
	public abstract void startClient(OnConnectionCallbacks callbacks);
	
	/**
	 * Initialize ViettelClient in {@link ViettelLocalService} or {@link ViettelLocalSimple}. 
	 * <li>If Connected and call method {@link ViettelClientServiceActivity#getViettelLocalService()} 
	 * or {@link #getViettelClient()} will return object ViettelClient not null, Otherwise return null.
	 * <li>If Connected and call method {@link #getViettelClient()} will return object ViettelClient not null,
	 * Otherwise return null.
	 * @param OnConnectionCallbacks 
	 */
	public abstract void startClient();
	
	/**
	 * get ViettelClient from {@link ViettelLocalService} or
	 * {@link ViettelLocalSimple}. if ViettelLocalService is inBound or
	 * {@link ViettelLocalSimple} is available return object ViettelClient not
	 * null, Otherwise return null.
	 */
	public abstract ViettelClient getViettelClient();
	
	/**
	 * ensure mClassExitApp != null ?
	 */
	protected void ensureClassExitApp() {
		if (this.mClassExitApp == null) {
			throw new NullPointerException("please setter mClassExitApp!!!");
		}
	}

	/**
	 * Check class for current activity are equals class for MainClass Activity?
	 * 
	 * @param activityExit
	 *            class activity for main process
	 * @return true if equals, otherwise return false
	 */
	protected boolean equalsMainClass(Class<?> activityExit) {
		boolean mEquals = false;
		if (activityExit != null && getClass() != null) {
			mEquals = activityExit.getSimpleName().equalsIgnoreCase(getClass().getSimpleName());
		}
		return mEquals;
	}

	public class InitializeParams {

		private String publisherId; // PublisherId
		private String appId; // AppId
		private boolean testDevice; // default is false
		private long readRequestTimeout; // default is 0

		// SETTERS----------------------------------
		public void setViettelId(String publisherId, String appId) {
			this.publisherId = publisherId;
			this.appId = appId;
		}

		public void setReadTimeout(long readRequestTimeout) {
			if (readRequestTimeout < 0) {
				throw new ExceptionInInitializerError("readRequestTimeout < 0");
			}
			this.readRequestTimeout = readRequestTimeout;
		}

		public void setTestDevice(boolean testDevice) {
			this.testDevice = testDevice;
		}

		// GETTERS----------------------------------
		protected String getPublisherId() {
			return publisherId;
		}

		protected String getAppId() {
			return appId;
		}

		protected long getReadRequestTimeout() {
			return readRequestTimeout;
		}

		protected boolean isTestDevice() {
			return testDevice;
		}

	}
}
