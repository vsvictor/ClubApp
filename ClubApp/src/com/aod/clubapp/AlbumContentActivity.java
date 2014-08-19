package com.aod.clubapp;

import com.aod.clubapp.communicaton.datamodel.Album;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

public class AlbumContentActivity extends AuraBaseActivity {
	private static final String TAG = "AlbumContentActivity";

	private long albumId = -1;
	private Album album;
	GridView gridview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_content);

		Log.d(TAG, "OnCreate");

		albumId = getIntent().getLongExtra(PARAM_ALBUM_ID, -1);
		
	    gridview = (GridView) findViewById(R.id.gridView);

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            long photoId = (Long)v.getTag();
	            Log.d(TAG, "Clicked photo with id=" + photoId);
	            if(photoId > 0)
	            	PhotoDisplayActivity.showPhoto(albumId, photoId, AlbumContentActivity.this);	            
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
		Log.d(TAG, "fillContent() albumid=" + Long.toString(albumId));
		//album = getDataSession().getAlbumById(albumId);
		album = MainActivity.currAlbum;
		if(album != null) {
			String title = album.getName();
			if(TextUtils.isEmpty(title))
				title = album.getPlaceName();
			if(!TextUtils.isEmpty(title))
				getActionBar().setTitle(title);
		}
		if(album == null || album.getPhotosCount() < 1) {
			Log.i(TAG, "Nothing to show in album, album not found or empty, id=" + Long.toString(albumId));
			return;
		}
    	gridview.setAdapter(new ImageListAdapter(this, album));
	}
	
}
