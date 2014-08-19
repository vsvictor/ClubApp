package com.aod.clubapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Photo.PhotoUserTag;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.aod.clubapp.ui.UserTagsLayout;


public class UserMarksViewActivity extends AuraBaseActivity {
	public static final String TAG = "UserMarksViewActivity";
	
	private ImageView photoView;
	private long albumId;
	private long photoId;
	private int albumCollection = ALBUM_COLLECTION_MAIN;
	private Album album;
	private Photo photo;

	private UserTagsLayout tagsLayout;
	private RelativeLayout tagsContainer;
	
	private ArrayList<PhotoUserTag> addedTags;
	private float lastTapPos = 0;
	private TagslayoutInfo tagsInfo;
	
	
	public static void showMarks(long albumId, int albumCollectionId, long photoId, Activity cntx) {
		Log.d(TAG, "UserMarksViewActivity album id= " + Long.toString(albumId) + " photo id=" + photoId);
		Intent intent = new Intent(cntx, UserMarksViewActivity.class)
			.putExtra(PARAM_ALBUM_ID, albumId)
			.putExtra(PARAM_PHOTO_ID, photoId)
			.putExtra(PARAM_ALBUMS_COLLECTION, albumCollectionId);
    	cntx.startActivityForResult(intent, 0);
	}
	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		fillContent();
	}

	@Override
	public void onDataServiceDisConnected() {
	}

	private Album findAlbumInCollection(List<Album> collect, long albId){
		Album result = null;
		for(Album alb : collect){
			if(alb.getId() == albId){
				result = alb;
			}
		}		
		return result;	
	}
	
	
	private boolean initializationDone = false;
	private void fillContent(){
		if(getDataSession() == null)
			return;
		
		if(initializationDone) {
			return;
		}
		
		if(albumCollection == ALBUM_COLLECTION_MAIN){
			//album = getDataSession().getAlbumById(albumId);
			if(album == null){
				album = getDataSession().getAlbumsFriendsForId(albumId);
			}
		}else if(albumCollection == ALBUM_COLLECTION_FRIENDS) {
			album = getDataSession().getAlbumsFriendsForId(albumId);
			if(album == null){
				album = getDataSession().getAlbumById(albumId);
			}
		} else if (albumCollection == ALBUM_COLLECTION_PLACE) {
			album = findAlbumInCollection(getDataSession().getAlbumsForLastPlace(), albumId);
			if(album == null){
				album = getDataSession().getAlbumById(albumId);
			}
			if(album == null){
				album = getDataSession().getAlbumsFriendsForId(albumId);
			}
		}
		
		if(album == null) {
			Log.d(TAG, "fillContent() albumid=" + Long.toString(albumId));
			//album = getDataSession().getAlbumById(albumId);
			album = MainActivity.currAlbum;
		}
		photo = album.getPhotoById(photoId);
		if(photo != null){		
			//Picasso.with(this).load(photo.getImageMediumUrl()).into(photoView);
			AuraBaseActivity.loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);			
			
			fillPhotoTags();
			initializationDone = true;
		}
	}
	
	private void fillPhotoTags(){
		if(photo == null)
			return;
		LayoutInflater inflater = getLayoutInflater();
		if(tagsLayout != null) {
			tagsLayout.removeAllViews();//clear
			
		}
		if(tagsContainer != null){
			tagsContainer.removeAllViews();//clear
			tagsInfo.clean();
		}
		for(PhotoUserTag tag : photo.getUserTags()) {
			addTagOnScreen(tag, inflater);
		}
		for(PhotoUserTag tag : addedTags) {
			addTagOnScreen(tag, inflater);
		}
		
		findViewById(android.R.id.content).invalidate();
	}

	private void addTagOnScreen(PhotoUserTag tag, LayoutInflater inflater) {
		if(tagsLayout != null) {
			View label = inflater.inflate(R.layout.user_mark, null, false);
			label.setTag(Float.valueOf(tag.getX()));
			TextView tv = (TextView) label.findViewById(R.id.tv_user);
			tv.setText("@" + tag.getLogin());
			label.setTag(tag.getUserId());
			label.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(v.getTag() != null){
						long uid = ((Long)v.getTag()).longValue();
						ViewUserProfileActivity.showViewUserPrifileActivity(uid, UserMarksViewActivity.this);
					}else {
						Log.w(TAG, "addTagOnScreen.OnClickListener view have no tag");
					}					
				}
			});
			tagsLayout.addView(label);
		}
		
		if(tagsContainer != null){
			View label = inflater.inflate(R.layout.one_user_mark, null, false);
			label.setTag(Float.valueOf(tag.getX()));

			
			TextView tv = (TextView) label.findViewById(R.id.tv_user);
			tv.setText("@" + tag.getLogin());

			tagsContainer.addView(label,tagsInfo.addTag(tag, label));
			tagsContainer.forceLayout();
			tagsContainer.invalidate();

			label.setTag(R.id.tag_user_id, tag.getUserId());
			
			label.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					long uid = (Long)v.getTag(R.id.tag_user_id);
					if(uid > 0){
						ViewUserProfileActivity.showViewUserPrifileActivity(uid, UserMarksViewActivity.this);
					}
				}
			});			

		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mark_on_photo);
		albumId = getIntent().getLongExtra(PARAM_ALBUM_ID, -1);
		photoId = getIntent().getLongExtra(PARAM_PHOTO_ID, -1);
		albumCollection = getIntent().getIntExtra(PARAM_ALBUMS_COLLECTION, ALBUM_COLLECTION_MAIN);
		photoView = (ImageView)findViewById(R.id.photo_view);

		//tagsLayout = (UserTagsLayout)findViewById(R.id.user_tags);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		tagsInfo = new TagslayoutInfo(size.x);		
		tagsContainer = (RelativeLayout)findViewById(R.id.umarks_container);
		
		photoView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				Log.d(TAG, "OnTouchListener: x= " + ev.getRawX() + " y=" + ev.getRawY());
				lastTapPos = ev.getRawX() / photoView.getWidth();
				Intent intent = new Intent(UserMarksViewActivity.this, SelectFriendActivity.class);
				intent.putExtra(SelectFriendActivity.PARAM_REM_USERS, userTagedAsString());
				startActivityForResult(intent, 0);
				return false;
			}
		});
		addedTags = new ArrayList<PhotoUserTag>();
		fillContent();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK)
			return;
			
		long selectedId = data.getLongExtra(PARAM_USER_ID, -1);
		Log.d(TAG, "onActivityResult " + selectedId);
		if(selectedId > 0) {
			String friendLogin = null;
			try{
				friendLogin = getDataSession().friendLoginFromId(selectedId);
			} catch(Exception ex){
				friendLogin = "";
			}
			PhotoUserTag tag = null;
			if(!TextUtils.isEmpty(friendLogin )){
				tag = new PhotoUserTag(-1, lastTapPos, selectedId, friendLogin);							
				
			}else {
				Profile prof = getDataSession().getMyProfile();
				if(selectedId == prof.getId()) {
					tag = new PhotoUserTag(-1, lastTapPos, selectedId, prof.getLogin());
				}
			}
			if(tag != null){
				addedTags.add(tag);
				fillPhotoTags();
			}
		}
	}
	
	public void onOkClick(View v) {
		Log.d(TAG, "onOkClick");
		for(PhotoUserTag tag : addedTags){
			getLocalService().markUserOnPhoto(tag.getUserId(), photoId, tag.getX());
		}
		setResult(RESULT_OK);
		finish();
	}
	
	public void onCancelClick(View v) {
		Log.d(TAG, "onCancelClick");
		//ignoring tags in addedTags and just finish
		setResult(RESULT_CANCELED);
		finish();
	}
	
    private static class Trio {
    	int x;
    	int width;
    	int level;
    	
    	public Trio(int x, int w, int l) {
    		this.x = x;
    		this.width = w;
    		this.level = 0;
    	}
    	public void setLevel(int l) {
    		level = l;
    	}
    	public boolean isOverlaps(Trio obj){
    		if(level != obj.level) {
    			return false;
    		}
    		//выбираем отрезок, который левей, и если Х правого отрезка оказывается 
    		//всередине выбраного - отрезки пересекаются
    		if(obj.x >= x) {
    			return obj.x <= (x + width);
    		}
    		return x <= (obj.x + obj.width); 
    	}
    }
    
    String userTagedAsString(){
    	String emptyRes = "";
    	if(photo != null){
    		StringBuilder builder =  new StringBuilder();
    		PhotoUserTag[] tgs = photo.getUserTags();
    		if(tgs != null && tgs.length > 0 ){
    			for(PhotoUserTag tg : tgs){
    				builder.append(tg.getUserId()).append(" ");
    			}
    			return builder.toString();
    		}
    	}
    	return emptyRes;
    }
    
	public class TagslayoutInfo {

		private ArrayList<Trio> prosesedElem;
		private int parentWidth;
		
		public TagslayoutInfo(int widht){
			parentWidth = widht;
			prosesedElem = new ArrayList<Trio>();
		}
		
		public RelativeLayout.LayoutParams addTag(PhotoUserTag photoTag, View target) {
			target.measure(480, 200);
			int verticalOfset =target.getMeasuredHeight();
			int tvWidth = target.getMeasuredWidth();

			
			int itemX = (Math.round(parentWidth * photoTag.getX()) - tvWidth/ 2);
			itemX = itemX < 0 ? 0 : itemX;
			Trio position = new Trio(itemX, tvWidth, 0);

levelFound: for (int level = 0; level < 4; level++) {
				position.setLevel(level);
				boolean freeSpaceFound = true;
				for (Trio tr : prosesedElem) {
					if (tr.isOverlaps(position)) {
						freeSpaceFound = false;
						break;
					}
				}
				if (freeSpaceFound) {
					break levelFound;
				}
			}

			int yTop = (int) (position.level * Math.round(verticalOfset * 1.5));
			prosesedElem.add(position);
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(tvWidth, verticalOfset);
			params.leftMargin = itemX;
			params.topMargin = yTop;			

			return params;
		}
		
		public void clean(){
			prosesedElem.clear();
		}
	}
	
}
