package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Place;
import com.aod.clubapp.communicaton.datamodel.PlacesList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoadPlacesCommand extends BaseGetApiCommand implements IApiCommand {
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "places?";
		
	private long categoryId;
	private AuraDataSession session;
	private ArrayList<Place> loadedData;
	
	public LoadPlacesCommand(AuraDataSession session, long categoryId) {
		super(session.getAuthTooken());
		this.session = session;
		this.categoryId = categoryId;
	}

	@Override
	public void run() {
		String gq = appendAuthToUrl(getApiPath());				
		gq = addParameterToUrl(gq, "place_category_id", Long.toString(categoryId));
		try {
			String qRes = doGet(gq);			
			if( TextUtils.isEmpty(qRes)) {
				//failed
				Log.d(TAG, "LoadPlaces received empty result");
			} else {
				Log.d(TAG, "LoadPlaces loaded: " + qRes);
				Gson gson = getGson();
				PlacesList places  =  gson.fromJson(qRes, PlacesList.class);				
				session.setPlaces(categoryId ,places);				
				setCommandFailed(false);
				setAuthFailture(false);
			}			
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
			setAuthFailture(true);
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
			setCommandFailed(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadPlaces.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadPlaces.run() failed by exception", e);
			e.printStackTrace();
		}		
	}

	@Override
	public void onSuccesExecution(Context cntx) {
		Log.d(TAG, "LoadPlacesCommand onSuccesExecution categoryId=" + categoryId);
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(session.getContext());
		brdMannager.sendBroadcast(
				new Intent(AuraDataSession.ACTION_LOAD_PLACES_RESULT)
					.putExtra(AuraDataSession.OPERATION_STATUS, (!isCommandFailed()) ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL)
					.putExtra(AuraDataSession.PLACE_CATEGORY_ID, categoryId)
				);
		
	}

	@Override
	public void onFailExecution(Context cntx) {
		Log.d(TAG, "LoadPlacesCommand onFailExecution categoryId=" + categoryId);
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

}
