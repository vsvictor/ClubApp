package com.aod.clubapp;

import android.app.Application;
import android.util.Log;

import com.aod.clubapp.utils.AppUtils;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class ClubApplication extends Application {
	private static final String TAG = "ClubApplication";
	
	private static ClubApplication instance;
	private boolean isDebuggable = false;
	private FbSessionStatusCallback fdSessionStatusListener;
	GoogleCloudMessaging gcm;
	String regid;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		isDebuggable = AppUtils.checkIsDebuggableAppFlag(this, "com.aod.clubapp");
		fdSessionStatusListener = new FbSessionStatusCallback();
		//Picasso.with(this).setDebugging(true);
		int code;
		if((code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) != ConnectionResult.SUCCESS){
			Log.e(TAG, "GooglePlayServicesUtil.isGooglePlayServicesAvailable return fail");
		} else {
			 gcm = GoogleCloudMessaging.getInstance(this);
//	            regid = getRegistrationId(this);
//
//	            if (regid.isEmpty()) {
//	                registerInBackground();
//	            }			
		}
	}
	
	public static ClubApplication getInstance() {
		return instance;
	}

	public static boolean isDebuggable() {
		return instance.isDebuggable;
	}
	
	
    private class FbSessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	if (session.isOpened()) {
        		Log.d(TAG, "FD session status in FbSessionStatusCallback: open");
        		fetchUserInfo(session);
        	} else {
        		Log.d(TAG, "FD session status in FbSessionStatusCallback: not open");
        	}
        }
    }
    //todo: remove, it is just a test
    private void fetchUserInfo(Session currentSession) {
    	final Session  savedSession = currentSession;
        if (currentSession != null && currentSession.isOpened()) {
            Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser me, Response response) {
                    if (response.getRequest().getSession() == savedSession) {
                        Log.d(TAG, " FB user info: fname " + me.getFirstName() + ", username" + me.getUsername());
                    }
                }
            });
            request.executeAsync();
        }
    }    
    
	public static Session.StatusCallback getFdSessionStatusListener() {
		return instance.fdSessionStatusListener;
	}
	
}
