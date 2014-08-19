package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

public class PostDialogMessageCommand extends BaseGetApiCommand  implements IApiCommand {
	//http://fixapp-clubs.herokuapp.com/api/v1/messages

	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "messages";	
	
	long dialogId;
	String msgBody;
	
	public PostDialogMessageCommand(String authTooken, long dialogId, String msgBody) {
		super(authTooken);
		this.dialogId = dialogId;
		this.msgBody = msgBody;

	}
	
	@Override
	public void run() {
		try {
			String postData = "auth_token=" + getAuthTooken()  
					+ "&message[body]=" + URLEncoder.encode(msgBody)
					+ "&message[dialog_id]=" +  Long.toString(dialogId);

			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postData, false);
			Log.d("TAG", "postDialogMessage.run result:" + obj.toString());			
		} catch (JSONException e) {
			Log.e(TAG, "postDialogMessage.run()  failed ", e);
			setAuthFailture(true);
		} catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "postDialogMessage.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "postDialogMessage.run() failed by exception", e);
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


