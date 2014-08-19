package com.aod.clubapp;

//import com.facebook.Session;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistrationScreen extends BaseScreen {
	private static final String TAG = "RegistrationScreen";  
	
	private LoginActivity parent;
	
	private EditText loginEd;
	private EditText emailEdit;

	private EditText pwdEdit;
	private EditText pwd2Edit;
	
	private Button regmeBtn;
	
	private ImageView fbEnter;
	private ImageView instagramEnter;
	
	RegistrationScreen(View view, LoginActivity parent) {
		super(view);
		this.parent = parent;
		
		loginEd = (EditText)bindToView(R.id.edLogin, null);
		emailEdit = (EditText)bindToView(R.id.edEmail, null);
		
		pwdEdit = (EditText)bindToView(R.id.edPassword, null);
		pwd2Edit = (EditText)bindToView(R.id.edPasswordRepeat, null);
		regmeBtn = (Button)bindToView(R.id.btnEnter, this);
		
		fbEnter = (ImageView)bindToView(R.id.img_fb_enter, this);
		instagramEnter = (ImageView)bindToView(R.id.img_instagram_enter, this);
		
		bindToView(R.id.container_agreement, new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				ViewHtmlTextActivity.showLicenseAgreement(RegistrationScreen.this.parent);				
			}
		});
	}
	
	private boolean validateInput() {
		if( TextUtils.isEmpty(loginEd.getText().toString()) ||
			TextUtils.isEmpty(emailEdit.getText().toString()) ||
			TextUtils.isEmpty(pwdEdit.getText().toString()) ||
			TextUtils.isEmpty(pwd2Edit.getText().toString())) {
			Toast.makeText(parent, 
					parent.getString(R.string.warning_fill_all_required_fields), 
					Toast.LENGTH_SHORT).show();			
			return false;
		}
		
		if(pwdEdit.getText().toString().compareTo(pwdEdit.getText().toString()) != 0) {
			Toast.makeText(parent, 
					parent.getString(R.string.warning_different_pwd_and_pwd2), 
					Toast.LENGTH_SHORT).show();			
			return false;
		}		
		return true;
	}

	public void onRegClicked(View v) {
		//initiate registration process
		Log.d(TAG, "Initiate registrationj process");
		if(validateInput()) {
			parent.getDataService().initiateRegistration(loginEd.getText().toString(), emailEdit.getText().toString(), pwdEdit.getText().toString());
			parent.swithToProgressView();
		}
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "OnClick");
		if(v == regmeBtn) {
			onRegClicked(v);
			return;
		}
		if(v == instagramEnter) {
			Log.d(TAG, "Enter with Instagram");
			onEnterWithInstagram(v);
			return;
		}
		if(v == fbEnter) {
			Log.d(TAG, "Enter with FB");
			onEnterWithFB(v);			
			return;
		}
		
	}

	public void onEnterWithFB(View v){
		// start Facebook Login
		  //Session.openActiveSession(parent, true, ClubApplication.getFdSessionStatusListener());
		fillEmailFromContacts();
	}
	
	public void onEnterWithInstagram(View v){
		fillEmailFromContacts();
	}

	private void fillEmailFromContacts(){
		//Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(parent).getAccounts();
		for (Account account : accounts) {
		    if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
		        String possibleEmail = account.name;
		        if(!TextUtils.isEmpty(possibleEmail)){
		        	emailEdit.setText(possibleEmail);
		        	break;
		        }
		    }
		}		
	}
	
}
