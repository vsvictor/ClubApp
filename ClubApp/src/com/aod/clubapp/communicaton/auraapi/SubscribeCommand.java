package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

public class SubscribeCommand  extends BaseGetApiCommand  implements IApiCommand {

	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "followings?";	
	
	private long userId;
	private boolean unsubcribe = false;
	private AuraDataSession session;
	
	public SubscribeCommand(long userId, boolean unsubcribe, AuraDataSession session) {
		super(session.getAuthTooken());
		this.session = session;
		this.userId = userId;
		this.unsubcribe = unsubcribe;
	}
	
	@Override
	public void run() {
		if(!unsubcribe){
			doSubscribe();
		} else {
			doUnsubscribe();
		}
		//refresh friends list
		LoadFollowedUsersCommand command = new LoadFollowedUsersCommand(session.getCurrentUserId(), session);
		command.run();
	}

	private void doSubscribe(){
		try {
			String postData = "auth_token=" + getAuthTooken()  
					+ "&user_id=" +  Long.toString(userId);

			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postData, false);
			Log.d("TAG", "doSubscribe() result:" + obj.toString());			
		} catch (JSONException e) {
			Log.e(TAG, "doSubscribe()  failed ", e);
			setAuthFailture(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "doSubscribe() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "doSubscribe() failed by exception", e);
			e.printStackTrace();
		}
	}
	
	private void doUnsubscribe(){
		try {
			String gq = appendAuthToUrl(getApiPath());
			gq = addParameterToUrl(gq, "user_id", Long.toString(userId));
			String qRes = doDelete(gq);
			Log.d(TAG, "doUnsubscribe request result for userId "+ userId +" is:" + qRes);
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
			setAuthFailture(true);					
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "LikePhotoCommand.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "LikePhotoCommand.run() failed by exception", e);
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



