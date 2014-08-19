package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

public class PostRegIdCommand extends BaseGetApiCommand   implements IApiCommand {
	//http://fixapp-clubs.herokuapp.com/api/v1/profile/add_device_token

	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "profile/add_device_token";	
	private static final String bodyFormat = "{  \"device_token\" : \"%s\", \"kind\" : \"android\" }" ;		
	String regId;
	
	public PostRegIdCommand(String authTooken, String regId) {
		super(authTooken);
		this.regId = regId;

	}
	
	@Override
	public void run() {
		try {
			String postData = String.format(bodyFormat, regId);
			JSONObject obj =  HttpClient.SendHttpPost(apiPath + "?auth_token=" + getAuthTooken(), postData, true);
			Log.d("TAG", "PostRegIdCommand.run result:" + obj.toString());			
		} catch (JSONException e) {
			Log.e(TAG, "PostRegIdCommand.run()  failed ", e);
			setAuthFailture(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "PostRegIdCommand.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "PostRegIdCommand.run() failed by exception", e);
			e.printStackTrace();
		}		
	}

	@Override
	public void onSuccesExecution(Context cntx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailExecution(Context cntx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public String getApiPath() {
		return apiPath;
	}

}


