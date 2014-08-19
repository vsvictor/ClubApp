package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

public class AddUserTagCommand extends BaseGetApiCommand  implements IApiCommand {
	//http://fixapp-clubs.herokuapp.com/api/v1/photo_user_tags
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "photo_user_tags";	
	
	long userId;
	long photoId;
	float x;
	
	public AddUserTagCommand(String authTooken, long userId, long photoId, float x) {
		super(authTooken);
		this.userId = userId;
		this.photoId = photoId;
		this.x = x;
		
		
	}
	
	@Override
	public void run() {
		try {
			//auth_token=6ac2381bb43a553c6253a2a1067261cc&photo_user_tag[photo_id]=48&photo_user_tag[user_id]=5&photo_user_tag[x]=0.70625
			String postData = "auth_token=" + getAuthTooken()  
					+ "&photo_user_tag[photo_id]=" + Long.toString(photoId)
					+ "&photo_user_tag[user_id]=" +  Long.toString(userId)
					+ "&photo_user_tag[x]=" + Float.toString(x);

			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postData, false);
			Log.d("TAG", "AddUserTagCommand.run result:" + obj.toString());			
		} catch (JSONException e) {
			Log.e(TAG, "AddUserTagCommand.run()  failed ", e);
			setAuthFailture(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "AddUserTagCommand.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "AddUserTagCommand.run() failed by exception", e);
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

