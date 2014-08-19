package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.HttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Base class for all API commands that works via GET
 *
 * @author Anatoliy Odukha <aodukha@gmail.com>
 */
public abstract class BaseGetApiCommand {
	
	public static final String TAG = "GetApiCommand";
	
	private String authTooken;
	private boolean commandFailed = false;
	private boolean authFailture = false; // true if server return Auth Error
	
	public abstract String getApiPath(); 
	
	public BaseGetApiCommand(String authTooken) {
		this.authTooken = authTooken;
	}

	public String getAuthTooken() {
		return authTooken;
	}

	public String addParameterToUrl(String url, String paramName, String value) {
		if(TextUtils.isEmpty(url) || TextUtils.isEmpty(paramName)){
			return url;
		}
		
		if(!url.endsWith("?") && !url.endsWith("&")) {
			if(url.contains("?")) {
				url = url + "&";				
			} else {
				url = url + "?";
			}
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(paramName,value));
		String paramString = URLEncodedUtils.format(params, "utf-8");
	    url += paramString;
	    return url;		
	}
	
	String appendAuthToUrl(String url) {
		return addParameterToUrl(url, "auth_token", authTooken);
	}
	
	public static Gson getGson() {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
		return gson;
	}
		
	public String doGet(String url) throws MalformedURLException, IOException, ServerUnAuthorizedException {
		Log.d(TAG, "executing GET: " + url);
		String qRes = "";
		try{
			qRes = HttpClient.sendHttpGet(url);
			if(checkUnAuthorizedMessage(qRes)) {
				authFailture = true;
				throw new ServerUnAuthorizedException("Server returned Unauthorized for: " + url);
			}
		} catch(HttpHostConnectException ex){
			Log.i(TAG, "connection exception");
		}
		return qRes;
		
	}

	public String doDelete(String url) throws MalformedURLException, IOException, ServerUnAuthorizedException {
		Log.d(TAG, "executing GET: " + url);	
		String qRes = HttpClient.SendHttpDelete(url);
		if(checkUnAuthorizedMessage(qRes)) {
			authFailture = true;
			throw new ServerUnAuthorizedException("Server returned Unauthorized for: " + url);
		}
		
		return qRes;
		
	}	
	
	public String doPut(String url, String content, String contentType)  throws MalformedURLException, IOException{
		String qRes = "";
		try{
			qRes = HttpClient.sendSimpleHttpPut(url, content, contentType);
		} catch (HttpHostConnectException ex){
			Log.i(TAG, "connection exception");
			
		}
		return qRes; 
	}
	
	void broadcastResult(Context cntx,String action, boolean success) {
		LocalBroadcastManager brdMannager = LocalBroadcastManager.getInstance(cntx);
		brdMannager.sendBroadcast(new Intent(action).putExtra(AuraDataSession.OPERATION_STATUS, success ? AuraDataSession.STATUS_OK : AuraDataSession.STATUS_FAIL));
	}

	public boolean isCommandFailed() {
		return commandFailed;
	}

	public void setCommandFailed(boolean commandFailed) {
		this.commandFailed = commandFailed;
	}
	
	/**
	 * @param jsonString json string received from server
	 * @return  true if server response contains "message":"Unauthorized"
	 */
	public boolean checkUnAuthorizedMessage(String jsonString) {
		if(TextUtils.isEmpty(jsonString))
			return false;
		return jsonString.contains("\"message\":\"Unauthorized\"");
	}

	public  boolean isAuthFailed() {
		return authFailture;
	}
	
	public void setAuthFailture(boolean val){
		authFailture = val;
	}
}
