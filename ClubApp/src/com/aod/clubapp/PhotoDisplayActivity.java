package com.aod.clubapp;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Comment;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.util.SystemUiHider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


/**
 * Photo view screen
 * 
 * @author Anatoliy Odukha <aodukha@gmail.com>
 *
 */
public class PhotoDisplayActivity extends BasePhotoViewActivity {
	
	public static final String TAG = "PhotoDisplay";
	
	protected ImageView photoView;
	
	
	private boolean initializationDone = false;

	protected void onCommentsReceived(boolean failed){
		if(!failed){
			initCommentsAndLikes();			
		}
	}
	public static void showPhoto(long albumId, long photoId, Activity cntx) {
		Log.d(TAG, "showPhoto album id= " + Long.toString(albumId) + " photo id " + Long.toString(photoId));
		Intent intent = new Intent(cntx, PhotoDisplayActivity.class).putExtra(PARAM_ALBUM_ID, albumId);
		intent.putExtra(PARAM_PHOTO_ID, photoId);
    	cntx.startActivity(intent);
	}
	
    @Override
	protected void setupContentView() {
    	setContentView(R.layout.activity_view_photo);
		
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		photoView = (ImageView) findViewById(R.id.photo_view);
		
		((TextView) findViewById(R.id.tv_marked_users_msg)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				UserMarksViewActivity.showMarks((int)albumId, ALBUM_COLLECTION_MAIN, photoId, PhotoDisplayActivity.this);				
			}
		});		

		fillContent();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initCommentsAndLikes();
	};

	@Override
	public void onDataServiceConnected() {
		Log.d(TAG, "onDataServiceConnected");
		super.onDataServiceConnected();
		fillContent();		
	}

	@Override
	public void onDataServiceDisConnected() {
		Log.d(TAG, "onDataServiceDisConnected");		
	}	
	
	private void fillContent() {
		if(getDataSession() == null)
			return;
		
		initCommentsAndLikes();
		
		if(initializationDone)
			return;
		Log.d(TAG, "fillContent() albumid=" + Long.toString(albumId));
		//album = getDataSession().getAlbumById(albumId);
		album = MainActivity.currAlbum;
		updateActionBarTitle(album);
		if(album == null || album.getPhotosCount() < 1) {
			Log.i(TAG, "Nothing to show in album, album not found or empty, id=" + Long.toString(albumId));
			return;
		}
		photo = album.getPhotoById(photoId);
		photo.getImageMediumUrl();
		updateMarkedUsersLabels(photo);
		if(photo != null && !initializationDone) {
			getLocalService().loadCommentsForPhoto(photoId);
			if (!TextUtils.isEmpty(photo.getImageMediumUrl())) {				
				loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
				initializationDone = true;
			}
		}
	}
	
	
	private void initCommentsAndLikes() {
		if(getDataSession() == null)
			return;
		updateLikesCount(photo);
		Pair<Long, List<Comment>> pair = getDataSession().getLastComments();
		
		if(pair != null && pair.first == photoId) {
			updateCommentsInfo(pair.second);
		} else {
			hideComments();
		}
	
	}

	public void onLikeClick(View v){
		Log.d(TAG, "onLikeClick");
		processLikeClick(photo);
	}
	
	public void onMarkClick(View v){
		Log.d(TAG, "onMarkClick");
		UserMarksViewActivity.showMarks((int)albumId, ALBUM_COLLECTION_MAIN, photoId, this);
	}
	
	public void onAddCommentsClick(View v){
		Log.d(TAG, "onAddCommentsClick");
		CommentsListActivity.showCommentsActivity(albumId, photoId, this);		
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		initializationDone = false;
		fillContent();
	}
	
//	protected void doAfterVisChanged() {	
//		if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {				
//			loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
//		}
//		
//	}
}
