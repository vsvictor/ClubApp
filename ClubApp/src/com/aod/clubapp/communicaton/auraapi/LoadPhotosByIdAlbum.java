package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.Albums;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Photos;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Implement command to load all albums 
 *
 * @author Anatoliy Odukha <aodukha@gmail.com>
 */
public class LoadPhotosByIdAlbum extends BaseGetApiCommand implements IApiCommand {
	
	private static final String apiPath = AuraDataSession.BASE_URL + "albums/";
	
	private long idAlbum;
	private ArrayList<Photo> res;
	public LoadPhotosByIdAlbum(String authTooken, long idAlbum) {
		super(authTooken);
		this.idAlbum = idAlbum;
	}
	
	@Override
	public void run() {
		try {
				//String gq = appendAuthToUrl(apiPath);
				//gq = addParameterToUrl(gq, "id", Long.toString(this.idAlbum));
				String gq = apiPath+String.valueOf(this.idAlbum)+"/";
				gq = appendAuthToUrl(gq);
				String qRes = doGet(gq);
				if( TextUtils.isEmpty(qRes)) {
					//break;
					return;
				} else {
					Log.d(TAG, "LoadAlbums loaded: " + qRes);
					Gson gson = getGson();
					Albums albums =  gson.fromJson(qRes, Albums.class);
					if(albums != null && albums.getAlbums().length > 0 ) {
						for(Album alb : albums.getAlbums()) {
							//loadedData.add(alb);
							Photo[] ph = alb.getPhotos();
							for(Photo photo: ph)
							res.add(photo);
						}
					} else {
						//all available albums loaded
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

	public ArrayList<Photo> getLoadedData() {
		return res;
	}

	@Override
	public String getApiPath() {
		return apiPath;
	}

}
