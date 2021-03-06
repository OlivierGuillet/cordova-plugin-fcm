package com.gae.scaffolder.plugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

public class FCMPlugin extends CordovaPlugin {
 
	private static final String TAG = "FCMPlugin";
	
	public static CordovaWebView gWebView;
	public static String notificationCallBack = "FCMPlugin.onNotificationReceived";
	public static String tokenRefreshCallBack = "FCMPlugin.onTokenRefreshReceived";
	public static Boolean notificationCallBackReady = false;

    
	public FCMPlugin() {}
	
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		gWebView = webView;
		Log.d(TAG, "==> FCMPlugin initialize");
		FirebaseMessaging.getInstance().subscribeToTopic("android");
		FirebaseMessaging.getInstance().subscribeToTopic("all");
	}

    
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

		Log.d(TAG,"==> FCMPlugin execute: "+ action);
		
		try{
			// READY //
			if (action.equals("ready")) {
				//
				callbackContext.success();
			}
			// GET TOKEN //
			else if (action.equals("getToken")) {
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						try{
							String token = FirebaseInstanceId.getInstance().getToken();
							callbackContext.success( FirebaseInstanceId.getInstance().getToken() );
							Log.d(TAG,"\tToken: "+ token);
						}catch(Exception e){
							Log.d(TAG,"\tError retrieving token");
						}
					}
				});
			}
			// NOTIFICATION CALLBACK REGISTER //
			else if (action.equals("registerNotification")) {
				notificationCallBackReady = true;
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
                        FCMPlugin.this.sendStoredNotification();
					}
				});
			}
			// UN/SUBSCRIBE TOPICS //
			else if (action.equals("subscribeToTopic")) {
				cordova.getThreadPool().execute(new Runnable() {
					public void run() {
						try{
							FirebaseMessaging.getInstance().subscribeToTopic( args.getString(0) );
							callbackContext.success();
						}catch(Exception e){
							callbackContext.error(e.getMessage());
						}
					}
				});
			}
			else if (action.equals("unsubscribeFromTopic")) {
				cordova.getThreadPool().execute(new Runnable() {
					public void run() {
						try{
							FirebaseMessaging.getInstance().unsubscribeFromTopic( args.getString(0) );
							callbackContext.success();
						}catch(Exception e){
							callbackContext.error(e.getMessage());
						}
					}
				});
			}
			else{
				callbackContext.error("Method not found");
				return false;
			}
		}catch(Exception e){
			Log.d(TAG, "ERROR: onPluginAction: " + e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
		}
		
		//cordova.getThreadPool().execute(new Runnable() {
		//	public void run() {
		//	  //
		//	}
		//});
		
		//cordova.getActivity().runOnUiThread(new Runnable() {
        //    public void run() {
        //      //
        //    }
        //});
		return true;
	}
	
    // Send Stored notification
    public void sendStoredNotification() {
        Log.d(TAG, "==> FCMPlugin. check stored notifications");
        // get current notification counter
        if (notificationCallBackReady && gWebView != null) {
            Log.d(TAG, "==> FCMPlugin. : View is ready");
            int current = NotificationsPreferencesStorage.getNotificationCount(gWebView.getContext());
            if (current > 0) {
                Log.d(TAG, "==> FCMPlugin nb Notifications stored: " + String.valueOf(current));
                for (int i = 0; i < current; i++) {
                    // Get Notification content by id
                    String notifJson = NotificationsPreferencesStorage.getNotification(gWebView.getContext(), i);
                    if (notifJson != null) {
                        String callBack = "javascript:" + notificationCallBack + "(" + notifJson + ")";
                        Log.d(TAG, "\tSent PUSH to view: " + callBack);
                        //TODO: sendJavascript is deprecated ??
                        gWebView.sendJavascript(callBack);
                    }
                    Log.d(TAG, "==> FCMPlugin. : remove notification N°" + String.valueOf(i));
                    NotificationsPreferencesStorage.removeNotification(gWebView.getContext(), i);
                }
                Log.d(TAG, "==> FCMPlugin. : End push, reset counter");
                NotificationsPreferencesStorage.setNotificationCount(gWebView.getContext(), 0);
            } else {
                Log.d(TAG, "==> FCMPlugin. :no stored notifications");
            }
        } else {
            Log.d(TAG, "==> FCMPlugin. : View not ready. Can send Stored NOTIFICATION ");
        }
    }
	public static boolean sendPushPayload(Map<String, Object> payload, Context context) {
		Log.d(TAG, "==> FCMPlugin sendPushPayload");
		Log.d(TAG, "\tnotificationCallBackReady: " + notificationCallBackReady);
		Log.d(TAG, "\tgWebView: " + gWebView);
        JSONObject jo = null;
	    try {
		    jo = new JSONObject();
			for (String key : payload.keySet()) {
			    jo.put(key, payload.get(key));
				Log.d(TAG, "\tpayload: " + key + " => " + payload.get(key));
            }
			String callBack = "javascript:" + notificationCallBack + "(" + jo.toString() + ")";
			if(notificationCallBackReady && gWebView != null){
				Log.d(TAG, "\tSent PUSH to view: " + callBack);
				gWebView.sendJavascript(callBack);
                return true;
			}else {
                Log.d(TAG, "\tView not ready. SAVED NOTIFICATION: " + jo.toString());
                // Store notificaiton
                NotificationsPreferencesStorage.addNotification(context, jo.toString());
                int count = NotificationsPreferencesStorage.getNotificationCount(context);
                Log.d(TAG, "==> FCMPlugin. : Notifications stored =" + String.valueOf(count));
                return false;			}
		} catch (Exception e) {
			Log.d(TAG, "\tERROR sendPushToView. SAVED NOTIFICATION: " + e.getMessage());
            if (jo != null) {
                NotificationsPreferencesStorage.addNotification(context, jo.toString());
            }
            return false;
        }
	}

    
	public static void sendTokenRefresh(String token) {
		Log.d(TAG, "==> FCMPlugin sendRefreshToken");
	  try {
			String callBack = "javascript:" + tokenRefreshCallBack + "('" + token + "')";
			gWebView.sendJavascript(callBack);
		} catch (Exception e) {
			Log.d(TAG, "\tERROR sendRefreshToken: " + e.getMessage());
		}
	}

    
    @Override
	public void onDestroy() {
		gWebView = null;
		notificationCallBackReady = false;
	}
} 
