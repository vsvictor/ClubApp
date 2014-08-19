package com.aod.clubapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ViewUserProfileActivity extends AuraBaseActivity {
	public static final String TAG = "ViewUserProfileActivity ";
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "users/%d?auth_token=%s";
	private static final String apiPathAcceptFriendReq = AuraDataSession.BASE_URL + "following_requests/%d/accept";
	private static final String apiPathRejectFriendReq = AuraDataSession.BASE_URL + "following_requests/%d/reject";
	private static final String apiPathSubscribe = AuraDataSession.BASE_URL + "users/%d?auth_token=%s";
	
	private long userId;
	private User userInfo;	
	private long requestId = -1;
	
	
	private ImageView photoView;
	//private TextView likesCount;
	private TextView photoCount;
	private TextView followersCount;
	private TextView followCount;
	
	private AQuery aq;
	
	public static void showViewUserPrifileActivity(long userId, Activity cntx) {
		Log.d(TAG, "showViewUserPrifileActivity  userId= " + userId);
		Intent intent = new Intent(cntx, ViewUserProfileActivity.class)
			.putExtra(PARAM_USER_ID, userId);
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

	@Override
	public void onSimpleGet(long requestId, boolean success) {
		if(requestId < 0 || requestId != this.requestId)
			return;
		try{
			String qRes = getDataSession().getSimpleHttpResponse(requestId);		
			Gson gson = BaseGetApiCommand.getGson();		
			userInfo  =  gson.fromJson(qRes, User.class);
			if(userInfo != null){
				fillContent();
			}
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
		}
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_user);
		
		aq = new AQuery(this);
		
		userId = getIntent().getLongExtra(PARAM_USER_ID, -1);
		photoView = (ImageView )findViewById(R.id.photo_view);
		//likesCount = (TextView)findViewById(R.id.tv_likes_count);
		photoCount = (TextView)findViewById(R.id.tv_photo_count);
		followersCount = (TextView)findViewById(R.id.tv_followers_count);
		followCount = (TextView)findViewById(R.id.tv_follow_count);
		showAcceptSubscripRequest(false);
		
		((ImageView)findViewById(R.id.img_allow)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				long tempId = System.currentTimeMillis();
				String url = String.format(apiPathAcceptFriendReq, userId, getDataSession().getAuthTooken()); 
				getLocalService().executeSimplePut(tempId , url.toString(), "auth_token=" + getDataSession().getAuthTooken());
				userInfo.setInFollowingRequest(false);
				showAcceptSubscripRequest(false);
			}
		});
		
		((ImageView)findViewById(R.id.img_prohibit)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				long tempId = System.currentTimeMillis();
				String url = String.format(apiPathAcceptFriendReq, userId, getDataSession().getAuthTooken()); 
				getLocalService().executeSimplePut(tempId, url.toString(),  "auth_token=" + getDataSession().getAuthTooken());
				userInfo.setInFollowingRequest(false);
				showAcceptSubscripRequest(false);
			}
		});
	}
	
	
	
	private void fillContent(){
		if(getDataSession() == null) {
			return;
		}
		if(getDataSession() != null && requestId < 0) {
			requestUpdate();
			return;
		}
		
		if(getActionBar() != null) {
			getActionBar().setTitle("@" + userInfo.getLogin());
		}
		photoCount.setText(Long.toString(userInfo.getPhotosTaggedInCount()));
		followersCount.setText(Long.toString(userInfo.getFollowersCount()));
		followCount.setText(Long.toString(userInfo.getFollowedUsersCount()));
		updateSubscribeButtonTitle();
		Photo photo = userInfo.getProfilePicture();
		if(userInfo.isInFollowingRequest()) {
			showAcceptSubscripRequest(true);
		} else {
			showAcceptSubscripRequest(false);
		}
		if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
			loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
		}
	}
	
	private void requestUpdate() {
		if(requestId > 0) {
			getDataSession().removeSimpleHttpResponse(requestId);
		}
		requestId = System.currentTimeMillis();
		String url = String.format(apiPath, userId, getDataSession().getAuthTooken()); 
		getLocalService().executeSimpleGet(requestId, url.toString());
	}

	public void onSubscribeClick(View v){
		if(userInfo == null)
			return;
		if(userInfo.isFollowing()){
			getLocalService().unsubscribeFrom(userId);
			userInfo.setFollowing(false);
		} else {
			getLocalService().subscribeFor(userId);
			userInfo.setFollowing(true);
		}
		updateSubscribeButtonTitle();
	}
	
	private void updateSubscribeButtonTitle(){
		if(userInfo == null)
			return;
		if(userInfo.isFollowing()){
			((Button)findViewById(R.id.btn_subscribe)).setText(getString(R.string.btn_unsubcribe));
		} else {
			((Button)findViewById(R.id.btn_subscribe)).setText(getString(R.string.btn_subcribe));
		}
	}
	
	public void onSendMessageClick(View v){
		if(userInfo == null)
			return;		
		DialogViewActivity.showNewDialogViewActivity(userId, this);		
	}

	private void showAcceptSubscripRequest(boolean showIt) {
		findViewById(R.id.ask_container).setVisibility(showIt ? View.VISIBLE : View.GONE);
	}
}
