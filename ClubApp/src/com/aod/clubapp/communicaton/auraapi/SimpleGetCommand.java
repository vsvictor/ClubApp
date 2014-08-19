package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;

public class SimpleGetCommand  extends BaseGetApiCommand  implements IApiCommand {
	 
	public static final String TAG = "SimpleGetCommand";
	private AuraDataSession session;
	long requestId;
	String url;
	String result;
	
	public SimpleGetCommand(AuraDataSession session, long requestId, String url) {
		super(session.getAuthTooken());
		this.session = session;
		this.requestId = requestId;
		this.url = url;
	}

	@Override
	public void run() {
		try {
			Log.d(TAG, "SimpleGetCommand executing: " + url);
			result = doGet(url);
			Log.d(TAG, "SimpleGetCommand executing: " + url + " result: " + result);
			setCommandFailed(false);
			setAuthFailture(false);				
			
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
			setAuthFailture(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "SimpleGetCommand.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "SimpleGetCommand.run() failed by exception", e);
			e.printStackTrace();
		}		
	}

	@Override
	public void onSuccesExecution(Context cntx) {
		session.putResponse(requestId, result);
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(cntx);
		brdMannager.sendBroadcast(
				new Intent(AuraDataSession.ACTION_HTTP_GET_RESULT)
					.putExtra(AuraDataSession.OPERATION_STATUS, AuraDataSession.STATUS_OK )
					.putExtra(AuraDataSession.SIMPLE_GET_ID, requestId)
					.putExtra(AuraDataSession.SIMPLE_GET_RESPONSE, result));
	}

	@Override
	public void onFailExecution(Context cntx) {
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(cntx);
		brdMannager.sendBroadcast(
				new Intent(AuraDataSession.ACTION_HTTP_GET_RESULT)
					.putExtra(AuraDataSession.OPERATION_STATUS, AuraDataSession.STATUS_FAIL )
					.putExtra(AuraDataSession.SIMPLE_GET_ID, requestId));
	}

	@Override
	public boolean isFailed() {
		return isCommandFailed();
	}
	
	@Override
	public String getApiPath() {
		Log.w(TAG, "SimpleGetCommand.getApiPath() should be never called");
		return null;
	}
}
