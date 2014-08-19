package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;

public class SignUp  implements IApiCommand {

	private String userName;
	private String email;
	private String password;
	private String tooken;
	private String resultMsg;
	private String resultErrorsMsg;
	private AuraDataSession session;
	
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "auth/signup";
	
	public SignUp(String userName, String email, String password, AuraDataSession session) {
		super();
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.session = session;
	}

	@Override
	public void run() {
		Log.d("AURA_API", "executiong SignUp");
		String postObj = "{ \"login\": \""  + userName+ "\",  \"email\": \"" + email + "\", \"password\":\""+ password+ "\" }";
		
		
		session.resetCredentials();
		try {
			Log.d("AURA_API", "SignUp request url: " + apiPath);
			Log.d("AURA_API", "SignUp request dara: " + postObj);
			JSONObject obj =  HttpClient.SendHttpPost(apiPath, postObj);
			Log.d("AURA_API", "SignUp response: " + obj.toString());
			
			long id = -1;
			boolean admin = false;

			if(obj.has("message")){
				resultMsg = obj.getString("message");
			}
			
			if(obj.has("errors")){
				resultErrorsMsg = obj.getString("errors");
				if(!TextUtils.isEmpty(resultErrorsMsg)){
					int begin = resultErrorsMsg.indexOf("[\"");
					int end  = resultErrorsMsg.indexOf("\"]");
					if(begin == 0 && end > 4){
						resultErrorsMsg = resultErrorsMsg.substring(2, end );
					}					
				}
						
			}
			
			if(obj.has("id")){
				id = obj.getLong("id");
			}
			if(obj.has("admin")){
				admin = obj.getBoolean("admin");
			}
			if(obj.has("auth_token")){
				tooken = obj.getString("auth_token");
			}
			
			
			session.setUserName(userName);
			if(!TextUtils.isEmpty(tooken)){
				session.setAuraCredentials(userName, password, tooken, id, admin);
			}
			
		} catch (IOException e) {
			Log.d("AURA_API", "executiong LogIn IOException", e);
			e.printStackTrace();
		} catch (JSONException e) {
			Log.d("AURA_API", "executiong LogIn JSONException", e);
			e.printStackTrace();
		}
	}

	private void broadcastLoginResult(Context cntx, boolean success, String msg) {
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(cntx);
		Intent intent = new Intent(AuraDataSession.ACTION_LOGIN_RESULT)
			.putExtra(AuraDataSession.OPERATION_STATUS, success ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL);
		if(!TextUtils.isEmpty(resultMsg)){
			intent.putExtra(AuraDataSession.MESSAGE, msg);
		}		
		brdMannager.sendBroadcast(intent);
	}
	
	@Override
	public void onSuccesExecution(Context cntx) {
		broadcastLoginResult(cntx, session.isLoggedIn(), null);
		
	}

	@Override
	public void onFailExecution(Context cntx) {
		broadcastLoginResult(cntx, false, resultErrorsMsg);
	}

	@Override
	public boolean isFailed() {
		return TextUtils.isEmpty(tooken);
	}

}
