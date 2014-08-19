package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;

public class RemoveChatMessage  extends BaseGetApiCommand implements IApiCommand {
	public static final String TAG = "RemoveChatMessage"; 
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "messages/";
	private long msgId;
	public RemoveChatMessage(String authTooken, long msgId) {
		super(authTooken);
		this.msgId = msgId;
 
	}
	@Override
	public void run() {
		try {
			String gq = getApiPath();
			String qRes = doDelete(gq);
			Log.d(TAG, "RemoveChatMessage  request result is:" + qRes);
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
			setAuthFailture(true);					
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "RemoveChatMessage.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "RemoveChatMessage.run() failed by exception", e);
			e.printStackTrace();
		}				
	}
	
	@Override
	public void onSuccesExecution(Context cntx) {
	}
	@Override
	public void onFailExecution(Context cntx) {
	}
	@Override
	public boolean isFailed() {
		return false;
	}
	@Override
	public String getApiPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(apiPath)
			.append(msgId)
			.append("?auth_token=")
			.append(getAuthTooken());		
		return sb.toString();
	}

	
}