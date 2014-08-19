package com.aod.clubapp.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class AppUtils {

	public static boolean checkIsDebuggableAppFlag(Context cntx, String packageName) {
		// Add code to print out the key hash
		try {
			PackageInfo info = cntx.getPackageManager().getPackageInfo(packageName, 0);
			return (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;			
		} catch (NameNotFoundException e) {
		}
		return false;
	}
	
	public static String prepareDataStr(String str){
		if(TextUtils.isEmpty(str)){
			return "";
		}
		return str;
	}
	
	public static String prepareBooleanStr(boolean val){
		return val ? "0" : "1";
	}
	
}
