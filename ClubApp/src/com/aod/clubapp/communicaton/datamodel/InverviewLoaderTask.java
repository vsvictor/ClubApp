package com.aod.clubapp.communicaton.datamodel;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aod.clubapp.communicaton.auraapi.LoadInterview;
import com.aod.clubapp.communicaton.auraapi.ServerUnAuthorizedException;

public class InverviewLoaderTask   extends AsyncTask<AuraDataSession, Void, Void> {
	private static final String TAG = "InverviewLoaderTask";
	
	String authTooken;
	String query;
	boolean failed = false;
	int page;
	AuraDataSession session;
	
	public InverviewLoaderTask(String authTooken, String query, int page) {
		super();
		this.query = query;
		this.authTooken = authTooken;

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
		LoadInterview command;

		command = new LoadInterview(authTooken, query, page);
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

			LoadedAlbumsKeeper interviews = session.getInterviews();
			interviews.addAlbumsNextPage(command.getLoadedData(), page);
			
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(session.getContext());
		int albumListType = AuraDataSession.ALBUMS_LIST_TYPE_INTERVIEW;
			
		brdMannager.sendBroadcast(
				new Intent(AuraDataSession.ACTION_LOAD_ALBUMS_RESULT)
					.putExtra(AuraDataSession.OPERATION_STATUS, (!failed) ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL)
					.putExtra(AuraDataSession.PLACE_ID, (long)-1)
					.putExtra(AuraDataSession.ALBUMS_LIST_TYPE, albumListType));
	}
}
