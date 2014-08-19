package com.aod.clubapp;

import java.net.URLEncoder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Profile;

public class EditSettingsActivity extends AuraBaseActivity {
	public static final String TAG = "EditSettingsActivity"; 
	
	private AQuery aq;
	Switch chbxTagsPrivate;
	Switch chbxMsgPrivate;
	Switch chbxLikes;
	Switch chbxComments;
	Switch chbxTags;
	Switch chbxMessages;
	Switch chbxContacts;
	
	TextView tvAgreemnt;
	TextView tvPolicy;
	TextView tvContactUs;
	TextView tvExit;
	boolean updateOnExit = false;
	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		fillContent();

	}

	@Override
	public void onDataServiceDisConnected() {
	}
	CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton v, boolean isChecked) {
			if(getDataSession() == null) return;
			Profile profile = getDataSession().getMyProfile();
			if(profile == null) return;
			updateOnExit = true;
			if(v == (CompoundButton)chbxLikes){
				profile.getSettings().setPushSettingsLikes(isChecked);
			}
			if(v == (CompoundButton)chbxComments){
				profile.getSettings().setPushSettingsComments(isChecked);
			}
			if(v == (CompoundButton)chbxTags){
				profile.getSettings().setPushSettingsTags(isChecked);
			}
			if(v == (CompoundButton)chbxMessages){
				profile.getSettings().setPushSettingsMessages(isChecked);
			}
			if(v == (CompoundButton)chbxContacts){
				profile.getSettings().setPushSettingsContacts(isChecked);
			}
		}
	}; 
	
	
	View.OnClickListener clickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(v == tvAgreemnt){
				ViewHtmlTextActivity.showLicenseAgreement(EditSettingsActivity.this);				
			} else if(v== tvPolicy){
				ViewHtmlTextActivity.showConfiudentialPolicy(EditSettingsActivity.this);
			}else if(v == tvContactUs) {
				// ACTION_SENDTO filters for email apps (discard bluetooth and others)
				String uriText =
				    "mailto:youremail@gmail.com" + 
				    "?subject=" + URLEncoder.encode("Clubs android applicataion") + 
				    "&body=" + URLEncoder.encode("Hello app developers,");

				Uri uri = Uri.parse(uriText);

				Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
				sendIntent.setData(uri);
				startActivity(Intent.createChooser(sendIntent, getString(R.string.msg_send_email))); 
	
			} else if(v == tvExit) {
				showExitAlert();
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		if(getActionBar() != null) {
			getActionBar().setTitle(getString(R.string.title_settings));
		}
		
		aq = new AQuery(this);
		
		chbxTagsPrivate = (Switch)findViewById(R.id.cb_tags_private);
		chbxTagsPrivate.setTextOn(getString(R.string.yes));
		chbxTagsPrivate.setTextOff(getString(R.string.no));
		
		chbxMsgPrivate = (Switch)findViewById(R.id.cb_messages_private);
		chbxMsgPrivate.setTextOn(getString(R.string.yes));
		chbxMsgPrivate.setTextOff(getString(R.string.no));
		
		chbxLikes = (Switch)findViewById(R.id.cb_likes);
		chbxLikes.setTextOn(getString(R.string.yes));
		chbxLikes.setTextOff(getString(R.string.no));
		
		chbxComments = (Switch)findViewById(R.id.cb_comments);
		chbxComments.setTextOn(getString(R.string.yes));
		chbxComments.setTextOff(getString(R.string.no));
		
		chbxTags = (Switch)findViewById(R.id.cb_tags);
		chbxTags.setTextOn(getString(R.string.yes));
		chbxTags.setTextOff(getString(R.string.no));				
				
		chbxMessages = (Switch)findViewById(R.id.cb_messages);
		chbxMessages.setTextOn(getString(R.string.yes));
		chbxMessages.setTextOff(getString(R.string.no));
		
		chbxContacts = (Switch)findViewById(R.id.cb_contacts);
		chbxContacts.setTextOn(getString(R.string.yes));
		chbxContacts.setTextOff(getString(R.string.no));
		
		
		chbxLikes.setOnCheckedChangeListener(checkBoxListener);
		chbxComments.setOnCheckedChangeListener(checkBoxListener);
		chbxTags.setOnCheckedChangeListener(checkBoxListener);
		chbxMessages.setOnCheckedChangeListener(checkBoxListener);
		chbxContacts.setOnCheckedChangeListener(checkBoxListener);

		tvAgreemnt = (TextView)findViewById(R.id.tv_user_agreement);
		tvPolicy = (TextView)findViewById(R.id.tv_policy);
		tvContactUs = (TextView)findViewById(R.id.tv_contace_us);
		tvExit = (TextView)findViewById(R.id.tv_exit);
		
		tvAgreemnt.setOnClickListener(clickListener);
		tvPolicy.setOnClickListener(clickListener);
		tvContactUs.setOnClickListener(clickListener);
		tvExit.setOnClickListener(clickListener);
		
		
		
	}
	
	private void fillContent() {
		if(getDataSession() == null)
			return;
		Profile profile = getDataSession().getMyProfile();
		if(profile == null)
			return;
		
		chbxLikes.setChecked(profile.getSettings().isPushSettingsLikes());
		chbxComments.setChecked(profile.getSettings().isPushSettingsComments());
		chbxTags.setChecked(profile.getSettings().isPushSettingsTags());
		chbxMessages.setChecked(profile.getSettings().isPushSettingsMessages());
		chbxContacts.setChecked(profile.getSettings().isPushSettingsContacts());
		
		aq.id(R.id.cb_tags_private).checked(profile.getSettings().isTags_private());
		aq.id(R.id.cb_messages_private).checked(profile.getSettings().isMessagesPrivate());
		
		ImageView photoView = (ImageView )findViewById(R.id.photo_view);
		Photo photo = profile.getProfilePicture();
		if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
			loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(updateOnExit){
			Log.d(TAG, "uploading changes in profile settings");
		}
	}
}
