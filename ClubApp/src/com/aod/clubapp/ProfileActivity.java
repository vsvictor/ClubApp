package com.aod.clubapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Profile;


public class ProfileActivity extends AuraBaseActivity {

	public static final String TAG = "ProfileActivity";
	ImageView photoView;
	TextView likesCount;
	TextView commentsCount;
	TextView photoCount;
	TextView followersCount;
	TextView followCount;
	TextView unreadMsgCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);
		
		photoView = (ImageView )findViewById(R.id.photo_view);		
		likesCount = (TextView)findViewById(R.id.tv_likes_count);
		commentsCount = (TextView)findViewById(R.id.tv_comments_count);
		photoCount = (TextView)findViewById(R.id.tv_photo_count);
		followersCount = (TextView)findViewById(R.id.tv_followers_count);
		followCount = (TextView)findViewById(R.id.tv_follow_count);
		unreadMsgCount = (TextView)findViewById(R.id.tv_unread_msg_count);

		View.OnClickListener newsClicked = new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ProfileActivity.this, NewsActivity.class));
			}
		};
		
		View.OnClickListener messagesClicked = new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ProfileActivity.this, DialogsActivity.class));
			}
		};
		
		findViewById(R.id.news_container).setOnClickListener(newsClicked);
		photoCount.setOnClickListener(newsClicked);
		likesCount.setOnClickListener(newsClicked);
		findViewById(R.id.messages_container).setOnClickListener(messagesClicked);
		
		findViewById(R.id.followers_container).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				SubscriptionManagmentActivity.showSubscriptionManagment(SubscriptionManagmentActivity.MODE_SHOW_FOLLOWERS, ProfileActivity.this);
				
			}
		});
		
		findViewById(R.id.followings_container).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				SubscriptionManagmentActivity.showSubscriptionManagment(SubscriptionManagmentActivity.MODE_SHOW_FOLLOWED, ProfileActivity.this);				
			}
		});
		
	}
	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		getLocalService().loadNews();//TODO: find better way for reload?
		getLocalService().loadDialogs();
		if(getDataSession().getMyProfile() == null){
			getLocalService().reloadProfile();
		}
		fillContent();
	}

	@Override
	public void onDataServiceDisConnected() {
	}

	public void onEditProfileClick(View view) {
		startActivityForResult(new Intent(this, EditProfileActivity.class), 105);		
	}
	
	public void onEditSettomgsClick(View view) {
		
	}
	
	
	private void fillContent() {
		if(getDataSession() == null)
			return;
		Profile profile = getDataSession().getMyProfile();
		if(profile == null)
			return;
		
		if(getActionBar() != null) {
			getActionBar().setTitle("@" + profile.getLogin());
		}
				
		likesCount.setText(Integer.toString(profile.getLikesCount()));
		commentsCount.setText(Integer.toString(profile.getCommentsCount()));
		photoCount.setText(Integer.toString(profile.getPhotosTaggedInCount()));
		followersCount.setText(Integer.toString(profile.getFollowersCount()));
		followCount.setText(Integer.toString(profile.getFollowedUsersCount()));
		unreadMsgCount.setText(Integer.toString(profile.getUnreadMessagesCount()));
		if(profile.getUnreadMessagesCount() == 0){
			unreadMsgCount.setBackgroundColor(Color.TRANSPARENT);
		}
		
		Photo photo = profile.getProfilePicture();
		if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
			loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
		}
		
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {    	
    	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {    		
    	case R.id.menu_settings:			
    		Log.d(TAG, "menu_settings");
    		startActivity(new Intent(this, EditSettingsActivity.class));
    		return true;
    	case R.id.menu_edit_profile:
    		startActivityForResult(new Intent(this, EditProfileActivity.class), 105);
    		Log.d(TAG, "menu_edit");
    		break;
    	case android.R.id.home:    		
    		onBackPressed();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Profile profile = getDataSession().getMyProfile();
    	if(getActionBar() != null && profile != null) {
			getActionBar().setTitle("@" + profile.getLogin());
		}    	
    }
}
