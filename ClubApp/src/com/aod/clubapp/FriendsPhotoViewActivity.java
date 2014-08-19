package com.aod.clubapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Comment;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Photo.PhotoUserTag;
import com.viewpagerindicator.CirclePageIndicator;

public class FriendsPhotoViewActivity extends BasePhotoViewActivity {
	public static final String TAG = "PlaceViewActivity";

	boolean showFriendsAlbum = true;
	private ViewPager pager;
	private AlbumPagerAdapter adapter;
	private CirclePageIndicator indicator;
	
	private Map<Long, List<Comment>> albumComments = new HashMap<Long, List<Comment>>();
	
	private boolean initializationDone = false;
	
	public static final String PARAM_FRIENDS_ALBUM = "param_album_PARAM_FRIENDS_ALBUM";
	
	protected void onCommentsReceived(boolean failed){
		if(!failed){
			if(readLastComments() == photoId) {
				updatePhotoInfo();
			}
		}
	}
	
	public static void showFriendPhotos(long albumId,  Activity cntx) {
		Log.d(TAG, "showFriendPhotod album id= " + Long.toString(albumId));
		Intent intent = new Intent(cntx, FriendsPhotoViewActivity.class)
			.putExtra(PARAM_ALBUM_ID, albumId)
			.putExtra(PARAM_FRIENDS_ALBUM, true);
    	cntx.startActivity(intent);
	}
	
	public static void showInterviewPhotos(long albumId,  Activity cntx) {
		Log.d(TAG, "showFriendPhotod album id= " + Long.toString(albumId));
		Intent intent = new Intent(cntx, FriendsPhotoViewActivity.class)
			.putExtra(PARAM_ALBUM_ID, albumId)
			.putExtra(PARAM_FRIENDS_ALBUM, false);
    	cntx.startActivity(intent);
	}
	
	@Override
	public void onDataServiceConnected() {
		Log.d(TAG, "onDataServiceConnected");
		super.onDataServiceConnected();
		fillContent();
	}

    @Override
	protected void setupContentView() {
    	setContentView(R.layout.activity_view_friends_photo);		
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		pager = (ViewPager)findViewById(R.id.pager);
		indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		showFriendsAlbum = getIntent().getBooleanExtra(PARAM_FRIENDS_ALBUM, true);
		((TextView) findViewById(R.id.tv_marked_users_msg)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onMarkClick");
				UserMarksViewActivity.showMarks((int)albumId, ALBUM_COLLECTION_FRIENDS, photoId, FriendsPhotoViewActivity.this);
				
			}
		});		
		
		fillContent();
	}

	private void fillContent() {
		if(getDataSession() == null)
			return;
		if(initializationDone)
			return;
		if(pager != null) {
			Log.d(TAG, "fillContent() albumid=" + Long.toString(albumId));
			if(showFriendsAlbum ) {
				album = getDataSession().getAlbumsFriendsForId(albumId);
			} else {
				album = getDataSession().getAlbumById(albumId);				
			}
			if(album != null && album.isInterview()){
				//hide check me button for interview
				findViewById(R.id.container_marks_btn).setVisibility(View.GONE);
			}
			updateActionBarTitle(album);
			adapter = new AlbumPagerAdapter(getSupportFragmentManager(), album);
			pager.setAdapter(adapter);
	        indicator.setViewPager(pager);
	        
	        //We set this on the indicator, NOT the pager
	        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            	Log.d(TAG, "view pager new position is " + position);
	            	if(album != null) {
	            		Photo pht = album.getPhotoByPosition(position);
	            		if(pht == null) {
	            			Log.e(TAG, "inconsistend data, album does not have photo as position " + position);

	            		} else {
	            			photoId = pht.getId();
	            			Log.d(TAG, "selected photo " + photoId);
	            			updatePhotoInfo();
	            		}
	            	}
	            }

	            @Override
	            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	            }

	            @Override
	            public void onPageScrollStateChanged(int state) {
	            }
	        });
	        //select first photo in view pager
	        indicator.setCurrentItem(0);
	        Photo pht = album.getPhotoByPosition(0);
	        if(pht != null) {
    			photoId = pht.getId();
	        }
	        updatePhotoInfo();
	        initializationDone = true;
		}
	}

	private void updatePhotoInfo(){
		Log.d(TAG, "updating view for photoid =" + photoId);
		photo = album.getPhotoById(photoId);
		if(photo != null) {			
			updateLikesCount(photo);
			List<Comment> comments = albumComments.get(photoId);
			if(comments == null){
				Log.d(TAG, "Not loaded yet comments for photo " + photoId);
				//load absent comments
				getLocalService().loadCommentsForPhoto(photoId);
				hideComments();
			} else { 
				updateCommentsInfo(comments);				
			}			
			updateMarkedUsersLabels(photo);
		}
	}


	
	private long readLastComments() {
		Pair<Long, List<Comment>> pair = getDataSession().getLastComments();		
		if(album.getPhotoById(pair.first)!= null ) {
			albumComments.put(pair.first, pair.second);			
			return pair.first;
		}
		return pair.first;
	}

	public void onLikeClick(View v){
		Log.d(TAG, "onLikeClick");
		if(photoId > 0 && album != null) {
			Photo target = album.getPhotoById(photoId);
			processLikeClick(target);			
		}
	}
	
	public void onMarkClick(View v){
		Log.d(TAG, "onMarkClick");
		UserMarksViewActivity.showMarks((int)albumId, ALBUM_COLLECTION_FRIENDS, photoId, this);
	}
	
	public void onAddCommentsClick(View v){
		Log.d(TAG, "onAddCommentsClick");
		if(photoId > 0) {
			CommentsListActivity.showCommentsActivity(albumId, photoId, this);
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		initializationDone = false;
		fillContent();
	}
}
