package com.aod.clubapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Photos;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.aod.clubapp.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AuraBaseActivity {
	
	public static final String TAG = "EditProfileActivity"; 
	public static final int REQUEST_PHOTO = 112;
	
	private long loadPhotoReqId = 0;
	private AQuery aq;
	
	private ImageView photoView;
	private EditText editName;
	private EditText editLogin;
	private EditText editEmail;
	private EditText editPhone;
	
	private Profile editedProfile;
	
	TextView likesCount;
	TextView commentsCount;
	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		fillContent();

	}

	@Override
	public void onDataServiceDisConnected() {
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		aq = new AQuery(this); 
		if(getActionBar() != null) {
			getActionBar().setTitle(getString(R.string.title_edit_profile));
		}
		
		photoView = (ImageView )findViewById(R.id.photo_view);
		editName = (EditText)findViewById(R.id.edit_name);
		editLogin = (EditText)findViewById(R.id.edit_login);
		editEmail = (EditText)findViewById(R.id.edit_email);
		editPhone = (EditText)findViewById(R.id.edit_phone);
		fillContent();	
	}
	
	
	private void fillContent() {
		if(getDataSession() == null)
			return;
		if(getDataSession().getMyProfile() == null)
			return;
		editedProfile = getDataSession().getMyProfile();
		
		aq.id(R.id.cb_tags_private).checked(editedProfile.getSettings().isTags_private());
		aq.id(R.id.cb_messages_private).checked(editedProfile.getSettings().isMessagesPrivate());		

		aq.id(R.id.btn_save).clicked(this, "buttonSaveClicked");
		aq.id(R.id.btn_change_pwd).clicked(this, "buttonChangePwdClicked");
		
		aq.id(R.id.edit_name).text(editedProfile.getName());		
		aq.id(R.id.edit_login).text(editedProfile.getLogin());
		aq.id(R.id.edit_email).text(editedProfile.getEmail());
		aq.id(R.id.edit_phone).text(editedProfile.getPhone());
		
		if(getDataSession().getMyProfile() != null){
			aq.id(R.id.tv_likes_count).text(Integer.toString(getDataSession().getMyProfile().getLikesCount()));
			aq.id(R.id.tv_comments_count).text(Integer.toString(getDataSession().getMyProfile().getCommentsCount()));
		}
		aq.id(R.id.news_container).clicked(this, "newsClicked");
		
		Photo photo = editedProfile.getProfilePicture();
		if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
			loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
		}
		
	}

	public void buttonSaveClicked(View view){	
		Log.d(TAG, "buttonSaveClicked");
		String url = AuraDataSession.BASE_URL_OLD + "profile";
		String frm = "auth_token=%s&profile[name]=%s&profile[login]=%s&profile[email]=%s&profile[phone]=%s";
		editedProfile.setName(editName.getText().toString());
		editedProfile.setLogin(editLogin.getText().toString());
		editedProfile.setEmail(editEmail.getText().toString());
		editedProfile.setPhone(editPhone.getText().toString());
		
		String data = String.format(frm, 
				getDataSession().getAuthTooken(),
				AppUtils.prepareDataStr(editedProfile.getName()),
				AppUtils.prepareDataStr(editedProfile.getLogin()),
				AppUtils.prepareDataStr(editedProfile.getEmail()),
				AppUtils.prepareDataStr(editedProfile.getPhone())
			);
		getLocalService().executeSimplePut(System.currentTimeMillis(), url, data);
		Toast.makeText(this, R.string.msg_changes_saved, Toast.LENGTH_LONG).show();
		setResult(RESULT_OK);
	}
	
	
	public void newsClicked(View view){
		startActivity(new Intent(this, NewsActivity.class));
	}
	private void putNewProfilePhoto(int photoId){
		String dataFrmt = "auth_token=%s&profile[profile_picture_id]=%d";		   
		String url =  AuraDataSession.BASE_URL + "profile";
		String dat = String.format(dataFrmt, getDataSession().getAuthTooken(), photoId);
		getLocalService().executeSimplePut(System.currentTimeMillis(), url, dat);		
	}
	
	public void buttonChangePwdClicked(View view){
		Log.d(TAG, "buttonChangePwdClicked");
		LayoutInflater inflater = getLayoutInflater();
		final View dialoglayout = inflater.inflate(R.layout.change_password_view, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(dialoglayout);
		builder.setTitle(R.string.title_change_pwd);
		builder.setPositiveButton(R.string.ru_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // FIRE ZE MISSILES!            	
            	String editPwd =  "auth_token=%s&new_password=%s&old_password=%s";
            	String oldPwd = ((EditText)dialoglayout.findViewById(R.id.ed_old_password)).getText().toString();
            	String newPwd = ((EditText)dialoglayout.findViewById(R.id.ed_new_password)).getText().toString();
            	String dat = String.format(editPwd, getDataSession().getAuthTooken(), newPwd, oldPwd);            	
            	String url =  AuraDataSession.BASE_URL + "profile/edit_password";            	
            	getLocalService().executeSimplePut(System.currentTimeMillis(), url, dat);
            	Toast tst = Toast.makeText(EditProfileActivity.this, R.string.msg_pwd_changes, Toast.LENGTH_LONG);
            	tst.show();
            	getDataSession().setPassword(newPwd);
            }
        })
        .setNegativeButton(R.string.ru_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
		
		builder.show();
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK)
			return;
		if(data != null){
			int photoId = (int) data.getLongExtra(PARAM_PHOTO_ID, -1);
			if(photoId != -1){
				putNewProfilePhoto(photoId);
				loadPhotoReqId = requestLoadOnePhoto(photoId);
			}
		}
	}
	
	@Override
	public void onSimpleGet(long requestId, boolean success) {
		try {
			if(requestId == loadPhotoReqId){
				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
				Gson gson = BaseGetApiCommand.getGson();		
				Photos pls  =  gson.fromJson(qRes, Photos.class);
				requestId = -1;
				Photo photo;
				if(pls != null && pls.getPhotos() != null && pls.getPhotos().length > 0){
					Photo arr[] = pls.getPhotos(); 
					photo =  arr[0];
					if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {
						Picasso.with(this).cancelRequest(photoView);
						loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
					}
				} else {
					Log.w(TAG, "Failed to load profile picture");
				}
			}
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
		}
	}
	
	
	public void onSelectPhoto(View v){
		Profile me = getDataSession().getMyProfile();
		SelectPhotoActivity
			.showUsersPhotos(me.getId(), me.getLogin(), this, REQUEST_PHOTO);
	}
}
