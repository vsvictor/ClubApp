package com.aod.clubapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

/**
 * Utilities for accessing app date in shared preferences
 * 
 * @author Anatoliy Odukha <aodukha@gmail.com>
 *
 * Application keeps in shared preferences: last used login credentials
 */
public class Preferences {
	private static final String TAG = "Preferences";
	public static final String PREF_STORAGE_NAME = "com.aod.aura_club_app";
	
	private static final String KEY_USER_ID = "aura_user_id"; //keep last used user name
	private static final String KEY_USER_NAME = "user_name"; //keep last used user name
	private static final String KEY_USER_PWD = "user_pwd"; // keep last used password
	private static final String KEY_SESSION_TOOKEN = "session_tooken";
	private static final String KEY_LAST_AUTH_TIME = "auth_time"; // keep last used password
	private static final String KEY_RESET_BADGE = "reset_badge";
	
	private static final int TOOKEN_EXPIRATION_TIME = 365 * (24 * 60 * (60 * 1000));//in milliseconds, 1 year
	
	public static class UserInfo {
		String name;
		String password;
		String sessionTooken;
		long userId;
		public UserInfo(long userId, String name, String password, String sessionTooken) {
			this.name = name;
			this.password = password;
			this.sessionTooken = sessionTooken;
			this.userId = userId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getSessionTooken() {
			return sessionTooken;
		}
		public void setSessionTooken(String sessionTooken) {
			this.sessionTooken = sessionTooken;
		}
		public long getUserId() {
			return userId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}
	}

	public static SharedPreferences getAppPreferences(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		return prefs;
		
	}
	
	public static Pair<String,String> getLastUsedCredentials(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		String uname = prefs.getString(KEY_USER_NAME, null);
		String pwd = prefs.getString(KEY_USER_PWD, null);
		return Pair.create(uname, pwd);
	}
	
	public static void storeCredentials(Context ctx, long userId, String userName, String pwd, String tooken){
		if(TextUtils.isEmpty(userName)){
			Log.w(TAG, "storeCredentials unexpected value for username, it cannot be empty");
			return;
		}
		
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putLong(KEY_USER_ID, userId);
		editor.putString(KEY_USER_NAME, userName);
		editor.putLong(KEY_LAST_AUTH_TIME, System.currentTimeMillis());
		if(TextUtils.isEmpty(pwd)) {
			editor.remove(KEY_USER_PWD);
		} else {
			editor.putString(KEY_USER_PWD, pwd);
		}
		if(TextUtils.isEmpty(tooken)) {
			editor.remove(KEY_SESSION_TOOKEN);
		} else {
			editor.putString(KEY_SESSION_TOOKEN, tooken);
		}
		editor.commit();		
	}
		
	public static String getLastUsedUsername(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		return prefs.getString(KEY_USER_NAME, null);
	}
	
	public static String getLastUsedTooken(Context ctx) {		
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		long lastTime = prefs.getLong(KEY_LAST_AUTH_TIME, 0);
		if((System.currentTimeMillis() - lastTime) < TOOKEN_EXPIRATION_TIME) {
			return prefs.getString(KEY_SESSION_TOOKEN, null);
		}
		return null;
	}
	
	public static long getLastUserId(Context ctx) {		
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		long uid = prefs.getLong(KEY_USER_ID, -1);
		return uid;
	}
	
	public static Preferences.UserInfo getUserInfo(Context ctx) {
		Pair<String, String> user = Preferences.getLastUsedCredentials(ctx);
		UserInfo result = new UserInfo(getLastUserId(ctx), user.first, user.second, getLastUsedTooken(ctx));
		return result;
	}
	
	public static boolean getResetBadge(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);		
		return prefs.getBoolean(KEY_RESET_BADGE, true);
	}
	
	public static void setResetBadge(Context ctx, boolean val) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_STORAGE_NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(KEY_RESET_BADGE, val);
		editor.commit();
	}
	
}
