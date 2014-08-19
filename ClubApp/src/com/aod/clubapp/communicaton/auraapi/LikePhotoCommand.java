package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

public class LikePhotoCommand  extends BaseGetApiCommand implements IApiCommand {
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "likes";
	
	private long photoId = -1;
	private boolean failed = false;
	private boolean removeLike = false;
	public LikePhotoCommand(String authTooken, long photoId) {
		super(authTooken);
		this.removeLike = false;
		this.photoId= photoId;
	}

	public LikePhotoCommand(String authTooken, long photoId, boolean removeLike) {
		super(authTooken);
		this.removeLike = removeLike;
		this.photoId= photoId; 
	}

	@Override
	public void run() {
		if(!removeLike) {
			likePhoto();
		} else {
			removeLike();
		}
	}

	private void likePhoto() {
		try {
			String postData = "auth_token=" + getAuthTooken() + "&like[photo_id]=" + Long.toString(photoId);
			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postData,false);
			Log.d("TAG", "LikePhotoCommand.run result:" + obj.toString());
		} catch (JSONException e) {
			Log.e(TAG, "LikePhotoCommand.run()  failed ", e);
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

	private void removeLike() {
		try {
			String gq = appendAuthToUrl(getApiPath());
			gq = addParameterToUrl(gq, "photo_id", Long.toString(photoId));
			String qRes = doDelete(gq);
			Log.d(TAG, "removeLike request result for photo "+ photoId +" is:" + qRes);
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
		return failed;
	}

	@Override
	public String getApiPath() {
		return apiPath;
	}
}
