package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.Albums;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoadInterview  extends BaseGetApiCommand implements IApiCommand {
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "albums?";
	//private static final String apiPath = AuraDataSession.BASE_URL + "albums/interview?";
	//http://fixapp-clubs.herokuapp.com/api/v1/albums?auth_token=3925a11e32d9a8157f432038b6e85e45&interview=1&page=1
	
	private String query;
	private ArrayList<Album> loadedData;
	private int page;
	
	public LoadInterview(String authTooken, String query, int page, boolean followedUsers) {
		super(authTooken);
		this.query = query;
		this.page = page;
	}
	
	public LoadInterview(String authTooken, String query, int page) {
		super(authTooken);
		this.query = query;
		this.page = page;
	}
	
	
	@Override
	public void run() {
		
		loadedData = new ArrayList<Album>();

		try {				
			String gq;
			
			gq = appendAuthToUrl(getApiPath());
			gq = addParameterToUrl(gq, "interview", "1");
			gq = addParameterToUrl(gq, "page", Integer.toString(page));
			

			if(!TextUtils.isEmpty(query)) {
				gq = addParameterToUrl(gq, "q", query);				
			}
			Log.d(TAG, "Load interview query: " + gq);
			String qRes = doGet(gq);
			if( TextUtils.isEmpty(qRes)) {
				//break;
				return;
			} else {
				Log.d(TAG, "LoadAlbums interview loaded: " + qRes);
				Gson gson = getGson();
				Albums albums =  gson.fromJson(qRes, Albums.class);
				if(albums != null && albums.getAlbums().length > 0 ) {
					for(Album alb : albums.getAlbums()) {
						loadedData.add(alb);
					}
				} else {
					//all available albums loaded
					//break;
					return;
				}
			}					
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
			setCommandFailed(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadAlbums.run() failed by exception", e );
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadAlbums.run() failed by exception", e );
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

	public ArrayList<Album> getLoadedData() {
		return loadedData;
	}

	@Override
	public String getApiPath() {
		return apiPath;
	}
}
