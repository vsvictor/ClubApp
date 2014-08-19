package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

/**
 * Implementing SignIn command
 *
 * @author Anatoliy Odukha <aodukha@gmail.com>
 */
public class SignIn implements IApiCommand {

	private String userName;
	private String password;
	private AuraDataSession session;
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "auth/signin";
	public SignIn(String userName, String password, AuraDataSession session) {
		super();
		this.userName = userName;
		this.password = password;
		this.session = session;
	}

	@Override
	public void run() {
		Log.d("AURA_API", "executiong LogIn");
		String postObj = "{ \"login\": \""  + userName + "\", \"password\":\""+ password+ "\" }";		
		session.resetCredentials();
		try {
			Log.d("AURA_API", "SignIn request url: " + apiPath);
			Log.d("AURA_API", "SignIn request dara: " + postObj);
			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postObj);
			Log.d("AURA_API", "SignIn response: " + obj.toString());
			
			long id = obj.getLong("id");
			boolean admin = false;
			if(obj.has("admin")) {
				admin = obj.getBoolean("admin");
			}
			String tooken = obj.getString("auth_token");
			session.setAuraCredentials(userName, password, tooken, id, admin);
			
		} catch (IOException e) {
			Log.d("AURA_API", "executiong LogIn IOException", e);
			e.printStackTrace();
		} catch (JSONException e) {
			Log.d("AURA_API", "executiong LogIn JSONException", e);
			e.printStackTrace();
		}
	}

	private void broadcastLoginResult(Context cntx, boolean success) {
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(cntx);
		brdMannager.sendBroadcast(new Intent(AuraDataSession.ACTION_LOGIN_RESULT).putExtra(AuraDataSession.OPERATION_STATUS, success ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL));
	}
	
	@Override
	public void onSuccesExecution(Context cntx) {
		broadcastLoginResult(cntx, session.isLoggedIn());
		
	}

	@Override
	public void onFailExecution(Context cntx) {
		broadcastLoginResult(cntx, false);
	}
	
	@Override
	public boolean isFailed() {
		return false;
	}
}
