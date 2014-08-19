package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Comments;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoadComments extends BaseGetApiCommand implements IApiCommand {
	//http://fixapp-clubs.herokuapp.com/api/v1/comments?auth_token=20e94902cb9683c82a68ca06a246118d&photo_id=34
	
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "comments?";
	
	private String query;
	private long photoId;
	Comments comments; 
	
	public LoadComments(long photoId, String authTooken) {
		super(authTooken);
		this.photoId = photoId;
	}

	@Override
	public void run() {
		try {
			String gq = appendAuthToUrl(getApiPath());
			gq = addParameterToUrl(gq, "photo_id", Long.toString(photoId));
			String qRes = doGet(gq);

			if( TextUtils.isEmpty(qRes)) {
				//failed
			} else {
				Log.d(TAG, "LoadComments loaded: " + qRes);
				Gson gson = getGson();
				comments =  gson.fromJson(qRes, Comments.class);
				setCommandFailed(false);
				setAuthFailture(false);
			}
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
			setAuthFailture(true);
		}catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
			setCommandFailed(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadComments.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadComments.run() failed by exception", e);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getApiPath() {
		return apiPath;
	}

	public Comments getComments() {
		return comments;
	}
}
