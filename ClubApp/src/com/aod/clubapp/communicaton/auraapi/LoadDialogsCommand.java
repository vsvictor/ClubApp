package com.aod.clubapp.communicaton.auraapi;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Dialogs;
import com.aod.clubapp.communicaton.datamodel.UserActivities;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoadDialogsCommand  extends BaseGetApiCommand  implements IApiCommand {

	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "dialogs?"; 
	
	private AuraDataSession session;
	
	public LoadDialogsCommand(AuraDataSession session) {
		super(session.getAuthTooken());
		this.session = session;
	}

	@Override
	public String getApiPath() {
		return apiPath;
	}

	@Override
	public void run() {
		try {
			String gq = appendAuthToUrl(getApiPath());
			String qRes = doGet(gq);
			if( TextUtils.isEmpty(qRes)) {
				//failed
				Log.d(TAG, "LoadDialogsCommand received empty result");
			} else {
				Log.d(TAG, "LoadDialogsCommand loaded: " + qRes);
				Gson gson = getGson();
				Dialogs dialogs  =  gson.fromJson(qRes, Dialogs.class);				
				session.setDialogs(dialogs);
				
				setCommandFailed(false);
				setAuthFailture(false);				
			}
		} catch (ServerUnAuthorizedException e) {
			setCommandFailed(true);
			setAuthFailture(true);
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
			setCommandFailed(true);
		}catch (MalformedURLException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadDialogsCommand.run() failed by exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			setCommandFailed(true);
			Log.e(TAG, "LoadDialogsCommand.run() failed by exception", e);
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
		return isCommandFailed();
	}

}
