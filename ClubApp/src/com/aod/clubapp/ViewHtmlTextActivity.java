package com.aod.clubapp;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

public class ViewHtmlTextActivity extends Activity {
	
	public static final String TAG = "ViewHtmlTextActivity";
	public static final String PARAM_TITLE = "param.TITLE";
	public static final String PARAM_URL = "param.URL";
	
	public static void showPlacesListActivity(String title, String url, Activity cntx) {
		Log.d(TAG, "ViewHtmlTextActivity url= " + url);
		Intent intent = new Intent(cntx, ViewHtmlTextActivity.class)
			.putExtra(PARAM_TITLE, title)
			.putExtra(PARAM_URL, url);
    	cntx.startActivity(intent);    	
	}
	
	public static void showLicenseAgreement(Activity cntx) {
		showPlacesListActivity(cntx.getString(R.string.title_license) , "file:///android_asset/license_agreement.html", cntx); 
	}
	
	public static void showConfiudentialPolicy(Activity cntx) {
		showPlacesListActivity(cntx.getString(R.string.title_policy) , "file:///android_asset/conf_policy.html", cntx); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "eb2c8c8e");
		setContentView(R.layout.activity_html_text_view);
		WebView webView = (WebView) findViewById(R.id.web_view);
		webView.loadUrl("file:///android_asset/license_agreement.html");
		
		String title = getIntent().getStringExtra(PARAM_TITLE);
		String url = getIntent().getStringExtra(PARAM_URL);
		if(getActionBar() != null && !TextUtils.isEmpty(title))
			getActionBar().setTitle(title);
		if(getActionBar() != null) {
    		getActionBar().setDisplayHomeAsUpEnabled(true);
    	}
		webView.loadUrl(url);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {    		
    	case android.R.id.home:    		
    		onBackPressed();
    		return true;
		}
	    return super.onOptionsItemSelected(item);
	}	
}
