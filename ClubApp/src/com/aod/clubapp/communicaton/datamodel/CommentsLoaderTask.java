package com.aod.clubapp.communicaton.datamodel;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

import com.aod.clubapp.communicaton.auraapi.LoadComments;
import com.aod.clubapp.communicaton.auraapi.ServerUnAuthorizedException;

public class CommentsLoaderTask  extends AsyncTask<Pair<AuraDataSession, Long>, Void, Void> {
	private static final String TAG = "CommentsLoaderTask";
	
	String authTooken;
	boolean failed = false;
	AuraDataSession session;
	long photoId;
	Comments comments;
	
	public CommentsLoaderTask(String authTooken) {
		super();
		this.authTooken = authTooken;
	}

	@Override
	protected Void doInBackground(Pair<AuraDataSession, Long>... params) {
		session = params[0].first;
		photoId = params[0].second;
		Log.d(TAG, "CommentsLoaderTask.doInBackground photoId" + photoId);
		LoadComments cmd = new LoadComments(photoId, session.getAuthTooken());
		cmd.run();
		if(cmd.isAuthFailed()) {
			if(session.reloginAfterAuthError()) {
				Log.i(TAG, "Auth error comes from server");
				cmd.run();
				failed = cmd.isFailed();
			}
		}
		if(!failed) {
			Comments cmt = cmd.getComments();
			session.setLastLoadedComments(photoId, cmt);
			
		}
		return null;
	}
	
//	private boolean executeQuery() {
//		LoadComments cmd = new LoadComments(photoId, session.getAuthTooken());
//		cmd.run();
//		return cmd.isFailed();
//	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d(TAG, "CommentsLoaderTask.doInBackground onPostExecute" + photoId);
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(session.getContext());
		brdMannager.sendBroadcast(
				new Intent(AuraDataSession.ACTION_LOAD_COMMENTS_RESULT)
					.putExtra(AuraDataSession.OPERATION_STATUS, (!failed) ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL)
					.putExtra(AuraDataSession.PHOTO_ID, photoId)
				);
	}
}
