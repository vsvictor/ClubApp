package com.aod.clubapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Photos;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SelectPhotoActivity extends AuraBaseActivity {

	private static final String TAG = "SelectPhotoActivity";
	private static final String URLBASE =  AuraDataSession.BASE_URL_OLD + "photos?auth_token=%s&user_id=%d";
	
	long userId = -1;
	private long requestId = -1;
	GridView gridview;
	Photo[] photos;
	String title;
	
	public static void showUsersPhotos(long userId, String title, Activity cntx, int requestCode) {
		Log.d(TAG, "showUsersPhotos  id= " + Long.toString(userId));
		Intent intent = new Intent(cntx, SelectPhotoActivity.class)
			.putExtra(PARAM_USER_ID, userId)
			.putExtra(PARAM_TITLE, title);
    	cntx.startActivityForResult(intent, requestCode);
	}

	
	public void requestUpdate(){
		String urlQery = String.format(URLBASE, getDataSession().getAuthTooken(), userId);
		requestId = System.currentTimeMillis();
		getLocalService().executeSimpleGet(requestId, urlQery);
	}
	
	@Override
	public void onSimpleGet(long requestId, boolean success) {
		try {
			if(requestId == this.requestId){
				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
				Gson gson = BaseGetApiCommand.getGson();		
				Photos pls  =  gson.fromJson(qRes, Photos.class);
				requestId = -1;
				if(pls.getPhotos() != null && pls.getPhotos().length > 0){
					photos = pls.getPhotos();
					fillContent();
				} else {
					//set empty vew
				}
			}
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_content);

		Log.d(TAG, "OnCreate");

		userId = getIntent().getLongExtra(PARAM_USER_ID, -1);
		title = getIntent().getStringExtra(PARAM_TITLE);
	    gridview = (GridView) findViewById(R.id.gridView);

	    if(!TextUtils.isEmpty(title))
			getActionBar().setTitle(title);
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            long photoId = (Long)v.getTag();
	            Log.d(TAG, "Clicked photo with id=" + photoId);
	            if(photoId > 0){
	            	Intent intent = new Intent();		        	
		        	intent.putExtra(PARAM_PHOTO_ID, photoId);
		        	setResult(RESULT_OK, intent);
	            }else {
	            	setResult(RESULT_CANCELED);
	            }
	            finish();
	        }
	    });		
				
		fillContent();
	}
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		Log.d(TAG, "onDataServiceConnected");		
		fillContent();
		
	}
	@Override
	public void onDataServiceDisConnected() {
		//Nothing todo
		Log.d(TAG, "onDataServiceDisConnected");
		
	}

	private void fillContent(){
		if(getDataSession() == null)
			return;
		if(photos == null || photos.length == 0){
			requestUpdate();
			return;
		}
    	gridview.setAdapter(new ImageListAdapter(this, photos));
	}	


}
