package com.aod.clubapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Comment;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class CommentsListActivity  extends AuraBaseActivity {
	
	public static final String TAG = "CommentsListActivity";

	private ImageView photoView;
	private ListView listView;
	private Handler handler;
	private long albumId = -1;
	private long photoId = -1;
	private Album album;
	private Photo photo;
	private List<Comment> comments;
	
	private boolean initializationDone = false;
	
	public static void showCommentsActivity(long albumId, long photoId, Activity cntx) {
		Log.d(TAG, "showCommentsActivity album id= " + Long.toString(albumId) + " photo id " + Long.toString(photoId));
		Intent intent = new Intent(cntx, CommentsListActivity.class).putExtra(PARAM_ALBUM_ID, albumId);
		intent.putExtra(PARAM_PHOTO_ID, photoId);
    	cntx.startActivity(intent);
	}

	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		fillContent();	
	}

	@Override
	public void onDataServiceDisConnected() {
		
	}
	
	private BroadcastReceiver commentsReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "catched broadcast AuraDataSession.ACTION_LOAD_COMMENTS_RESULT");
			int status = intent.getIntExtra(AuraDataSession.OPERATION_STATUS, AuraDataSession.STATUS_FAIL);
			
			if (status == AuraDataSession.STATUS_FAIL) {										
				Log.i(TAG, "commentsReceiver operation failed");
				return;
			}
			long id = intent.getLongExtra(AuraDataSession.PHOTO_ID, -1);
			if(photoId > 0 && id == photoId) {
				reloadCommentsList();
			}
		}
	};
	
    @Override
    protected void onStart() {
    	super.onStart();
    	LocalBroadcastManager.getInstance(this).registerReceiver(commentsReceiver, new IntentFilter(AuraDataSession.ACTION_LOAD_COMMENTS_RESULT));
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(commentsReceiver);
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_comments);
		
		handler = new Handler();
		
		albumId = getIntent().getLongExtra(PARAM_ALBUM_ID, -1);
		photoId = getIntent().getLongExtra(PARAM_PHOTO_ID, -1);
		listView = (ListView) findViewById(R.id.comments_list);
		photoView = (ImageView) findViewById(R.id.photo_view);
		//set actionbar title
		updateActionBarTitle(album);
		fillContent();
	}	

	private void fillContent() {
		if(getDataSession() == null)
			return;
		if(!initializationDone) {
			
			Log.d(TAG, "fillContent() albumid=" + Long.toString(albumId));
			album = getDataSession().getAlbumById(albumId);
			updateActionBarTitle(album);
					
			if(album == null || album.getPhotosCount() < 1) {
				Log.i(TAG, "Nothing to show in album, album not found or empty, id=" + Long.toString(albumId));
				return;
			}
			photo = album.getPhotoById(photoId);
			photo.getImageMediumUrl();
			if(photo != null) {
				if (!TextUtils.isEmpty(photo.getImageMediumUrl())) {
					loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
				}							
			}
			initializationDone = true;
		}
		reloadCommentsList();
	}	
	
	private void reloadCommentsList() {
		if(getDataSession() == null)
			return;
		if(!readLastComments(photoId)) {
			getLocalService().loadCommentsForPhoto(photoId);
		}
		if(comments != null && comments.size() > 0) {
			Comment[] arr = comments.toArray(new Comment[comments.size()]);
			Arrays.sort(arr);
			CommentsListAdapter adapter = new CommentsListAdapter(this, arr);
			listView.setAdapter(adapter);
			listView.setSelection(comments.size() -1);
		} else {
			listView.setAdapter(null);
		}
	}
	
	private boolean readLastComments(long id) {
		Pair<Long, List<Comment>> pair = getDataSession().getLastComments();
		if(pair.first == id) {
			comments = pair.second;
			return true;
		}
		return false;
	}
	
	private void forceContentUpdate() {
		handler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				getLocalService().loadCommentsForPhoto(photoId);
			}
		}, 500);
	}
	
	public void onClickSend(View view){
		Log.d(TAG, "Send btn clicked");	
		EditText editor = (EditText) findViewById(R.id.my_comment);
		String txt = editor.getText().toString();
		if(!TextUtils.isEmpty(txt)) {
			getLocalService().postCommentForPhoto(photoId, txt);
		}
		((EditText) findViewById(R.id.my_comment)).setText("");
		forceContentUpdate();		 
	}
	
}
