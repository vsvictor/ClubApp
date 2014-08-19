package com.aod.clubapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aod.clubapp.communicaton.AuraDataService;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.Preferences;
import com.facebook.*;
import com.facebook.model.*;

/**
 * Implementing login screen view, here is all logic for entering username/password 
 * and passing them to service
 * 
 *  @author Anatoliy Odukha <aodukha@gmail.com>
 */
public class EnterAppScreen extends BaseScreen {
	
	private static final String TAG = "EnterAppScreen";  
	
	private LoginActivity parent;
	
	private EditText pwdEdit;
	private EditText loginEdit;
	private Button enterBtn;
	private ImageView fbEnter;
	private ImageView instagramEnter;
	private TextView regMe;
	private TextView restorePwd;
	
	
	EnterAppScreen(View view, LoginActivity parent) {
		super(view);
		this.parent = parent;
		pwdEdit = (EditText)bindToView(R.id.edPassword, null);
		loginEdit = (EditText)bindToView(R.id.edLogin, null);
		enterBtn = (Button)bindToView(R.id.btnEnter, this);
		
		
		String lastName = Preferences.getLastUsedUsername(parent);
		if(!TextUtils.isEmpty(lastName)){
			loginEdit.setText(lastName);
		}
		
		fbEnter = (ImageView)bindToView(R.id.img_fb_enter, this);
		instagramEnter = (ImageView)bindToView(R.id.img_instagram_enter, this);
		
		regMe = (TextView)bindToView(R.id.goto_reg, this);
		restorePwd = (TextView)bindToView(R.id.goto_restore_pwd, this);
	}
	
	public void onEnterClicked(View v){
		AuraDataSession.clearState(parent);
		Preferences.setResetBadge(parent, true);
		//validate name+pwd fields
		String uname = loginEdit.getText().toString();
		String pwd = pwdEdit.getText().toString();
		if(TextUtils.isEmpty(uname) || TextUtils.isEmpty(pwd)) {
			Toast.makeText(view.getContext(), R.string.fill_name_and_pwd, Toast.LENGTH_SHORT).show();
			return;
		}		
		parent.getDataService().initiateLogin(uname, pwd);
		parent.swithToProgressView();
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "OnClick");
		if(v == enterBtn) {
			onEnterClicked(v);
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
		if(v == regMe) {
			Log.d(TAG, "Ask for registration");
			onRegUser(v);
			return;
		}
		if(v == restorePwd) {
			Log.d(TAG, "Ask to restore pwd");
			onRestorePwd(v);
			return;
		}
		
	}
	
	public void onEnterWithFB(View v){
		// start Facebook Login
		  Session.openActiveSession(parent, true, ClubApplication.getFdSessionStatusListener());		  
	}
	
	public void onEnterWithInstagram(View v){
		//todo
	}
	
	public void onRegUser(View v){
		parent.swithToRegistrationView();
	}
	
	public void onRestorePwd(View v){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				parent);
 
			// set title
			alertDialogBuilder.setTitle(R.string.title_dlg_parol_restore);
 
			// set dialog message
			alertDialogBuilder
				.setMessage(R.string.dlg_parol_restore_msg)
				.setCancelable(false)
				.setPositiveButton(R.string.btn_approve, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {

					}
				  }); 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create(); 
				// show it
				alertDialog.show();
	}
	
	public void HideSoftKeyboard(){		
		((InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
		
	public void setLogoVisible(boolean state){
		View v = parent.findViewById(R.id.image);
		if(v != null) {
			if(state && v.getVisibility() !=View.VISIBLE){
				v.setVisibility(View.VISIBLE);	 
			} if(!state && v.getVisibility() != View.GONE){
				v.setVisibility(View.GONE);
			}
		}
	}
}
