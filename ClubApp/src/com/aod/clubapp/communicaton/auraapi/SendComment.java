package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.CommentsLoaderTask;
import com.aod.clubapp.utils.HttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

public class SendComment extends BaseGetApiCommand  implements IApiCommand {
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "comments?";
	
	private long photoId;
	private String commentBody;
	AuraDataSession session;
	
	public SendComment(AuraDataSession session, long photoId, String commentBody) {
		super(session.getAuthTooken());
		this.photoId = photoId;
		this.commentBody = commentBody;
		this.session = session;
	}
	
	@Override
	public void run() {
		try {
			String postData = "auth_token=" + getAuthTooken() + "&comment[body]=" + URLEncoder.encode(commentBody) + "&comment[photo_id]=" + Long.toString(photoId);
			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postData, false);
			Log.d("TAG", "SendComment.run result:" + obj.toString());			
		} catch (JSONException e) {
			Log.e(TAG, "SendComment.run()  failed ", e);
			setAuthFailture(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "SendComment.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "SendComment.run() failed by exception", e);
			e.printStackTrace();
		}		
	}

	@Override
	public void onSuccesExecution(Context cntx) {
		AsyncTask<Pair<AuraDataSession, Long>, Void, Void> task = new CommentsLoaderTask(session.getAuthTooken()).execute( Pair.create(session, photoId));		
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
