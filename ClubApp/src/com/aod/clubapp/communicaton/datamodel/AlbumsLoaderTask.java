package com.aod.clubapp.communicaton.datamodel;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aod.clubapp.communicaton.AuraDataService;
import com.aod.clubapp.communicaton.auraapi.IApiCommand;
import com.aod.clubapp.communicaton.auraapi.LoadAlbums;
import com.aod.clubapp.communicaton.auraapi.ServerUnAuthorizedException;
import com.aod.clubapp.communicaton.auraapi.SignIn;

/**
 * Implements separated task to run query for loading albums for Анонсы page 
 * 
 * @author Anatoliy Odukha <aodukha@gmail.com>
 *
 */
public class AlbumsLoaderTask  extends AsyncTask<AuraDataSession, Void, Void> {
	private static final String TAG = "AlbumsLoaderTask";
	
	String authTooken;
	String query;
	boolean failed = false;
	long placeId = -1;
	int page;
	AuraDataSession session;
	boolean followedUsers = false;
	
	public AlbumsLoaderTask(String authTooken, String query, int page) {
		super();
		this.query = query;
		this.authTooken = authTooken;
		this.placeId = -1;
		this.followedUsers = false;
		this.page = page;
	}
	
	public AlbumsLoaderTask(String authTooken, String query, int page, boolean followedUsers) {
		super();
		this.query = query;
		this.authTooken = authTooken;
		this.placeId = -1;
		this.followedUsers = followedUsers;
		this.page = page;
	}
	
	public AlbumsLoaderTask(String authTooken, String query, int page, long placeId) {
		super();
		this.query = query;
		this.authTooken = authTooken;
		this.placeId = placeId;
		this.followedUsers = false;
		this.page = page;
	}
	

	@Override
	protected Void doInBackground(AuraDataSession... params) {
		session = params[0];
		try {
			failed = !executeLoadAlbums();
		} catch (ServerUnAuthorizedException e) {
			Log.i(TAG, "Auth error comes from server");
			if(session.reloginAfterAuthError()) {
				failed = !executeLoadAlbumsNoThrow();
			}
		}
		return null;
	}
	
	private boolean executeLoadAlbums() throws ServerUnAuthorizedException {
		return executeLoadAlbums(false);
	}

	private boolean executeLoadAlbumsNoThrow() {
		try {
			return executeLoadAlbums(true);
		} catch (ServerUnAuthorizedException e) {
			Log.e(TAG, "Unexpected exceptin in executeLoadAlbumsNoThrow", e);
		}
		Log.e(TAG, "Should never execute this string in executeLoadAlbumsNoThrow");
		return false;
	}

	private boolean executeLoadAlbums(boolean noThrow) throws ServerUnAuthorizedException {
		//prepare query
		LoadAlbums command;
		if(placeId >= 0){
			command = new LoadAlbums(authTooken, query, page, placeId);
		} else if(followedUsers) {
			command = new LoadAlbums(authTooken, query, page, followedUsers);
		}else {
			command = new LoadAlbums(authTooken, query, page);
		}
		command.run();
		if(command.isAuthFailed()) {
			if(!noThrow) {
				throw new ServerUnAuthorizedException();
			}
			return false;
		}
		if(command.isCommandFailed()) {
			return false;
		}
		else {
			if(placeId >= 0){
				session.setAlbumsForLastPlace(command.getLoadedData());
			} if(followedUsers) {
				session.setAlbumsForFollowed(command.getLoadedData());
			}
			else {
				//session.setAlbums(command.getLoadedData());
				session.addAlbumsNextPage(command.getLoadedData(), page);
			}
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(session.getContext());
		int albumListType = AuraDataSession.ALBUMS_LIST_TYPE_DEFAULT;
		if(followedUsers) {
			albumListType = AuraDataSession.ALBUMS_LIST_TYPE_FOLLOWED_USERS;
		} else if(placeId > 0) {
			albumListType = AuraDataSession.ALBUMS_LIST_TYPE_PLACES;
		}
			
		brdMannager.sendBroadcast(
				new Intent(AuraDataSession.ACTION_LOAD_ALBUMS_RESULT)
					.putExtra(AuraDataSession.OPERATION_STATUS, (!failed) ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL)
					.putExtra(AuraDataSession.PLACE_ID, placeId)
					.putExtra(AuraDataSession.ALBUMS_LIST_TYPE, albumListType));
	}
}
