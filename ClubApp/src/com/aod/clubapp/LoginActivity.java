package com.aod.clubapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.aod.clubapp.communicaton.AuraDataService;
import com.aod.clubapp.communicaton.IAuraOperations;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.utils.Preferences;
import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.facebook.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * First applicator screen, may show:
 *  1. Enter screen(enter name/pwd or enter with social network)
 *  2. Registration view
 *  3. If there are some saved credentials, it will login directly and navigate to progress view 
 *  3. Loading progress: initiate data update sequence and show progress while downloading/updating albums
 *  
 *   @author Anatoliy Odukha <aodukha@gmail.com>
 */
public class LoginActivity extends Activity {
	
	private static final String TAG = "EnterAppScreen";
	
	private static final int LOGIN_VIEW_POS = 0;
	private static final int PROGRESS_VIEW_POS = 1;
	private static final int REGISTRATION_VIEW_POS = 2;
	
	private ViewFlipper switcher;
	private Animation slideLeft;
	private Animation slideRight;
	private EnterAppScreen enterScr;
	private RegistrationScreen regmeScr;
	private View loginView;
	private View auroProgress;
	private View regmeView;
	private IAuraOperations dataService;
	
	private ServiceConnection svcConn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			dataService = (IAuraOperations) binder;
			try {
				if (dataService.isLoggedIn()) {
					afterSuccessLogin();
				}
			} catch (Throwable t) {
				Log.e(TAG, "Exception in call to registerAccount()", t);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			dataService = null;
		}
	};
	
	private BroadcastReceiver loginStatusReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "catched broadcast AuraDataSession.ACTION_LOGIN_OK");
			int status = intent.getIntExtra(AuraDataSession.OPERATION_STATUS, AuraDataSession.STATUS_FAIL);
			
			if (status == AuraDataSession.STATUS_FAIL) {										
				Log.i(TAG, "login failed, returning back to login view");
				String errMsg = intent.getStringExtra(AuraDataSession.MESSAGE);
				swithToLoginView();
				showBadLoginMsg(errMsg);
			} else {
				afterSuccessLogin();
			}			
		}
	};
	
	private void afterSuccessLogin() {
		Log.i(TAG, "login ok. ready for work, auth token " + dataService.getAuthTooken());
		dataService.reloadProfile();
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		if(Preferences.getResetBadge(this)){
			resetBadgeRequest();
			Preferences.setResetBadge(this, false);
		}
		//startActivity(new Intent(LoginActivity.this, AnnouncementListActivity.class));
		LoginActivity.this.finish();
	}
	
	public IAuraOperations getDataService() {
		return dataService;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "eb2c8c8e");
		setContentView(R.layout.activity_login);
		switcher = (ViewFlipper) findViewById(R.id.switcher);
		slideLeft = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		slideRight = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		switcher.setInAnimation(slideLeft);
		switcher.setOutAnimation(slideRight);
		prepareViews();

		if(ClubApplication.isDebuggable()) {
			// Add code to print out the key hash
			try {
				PackageInfo info = getPackageManager().getPackageInfo(
						"com.aod.clubapp", PackageManager.GET_SIGNATURES);
	
				for (Signature signature : info.signatures) {
					MessageDigest md = MessageDigest.getInstance("SHA");
					md.update(signature.toByteArray());
					String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
					Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
				}
			} catch (NameNotFoundException e) {
	
			} catch (NoSuchAlgorithmException e) {
	
			}
		}
		final View activityRootView = findViewById(R.id.login_root);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
		        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
		        	Log.d(TAG, "onscreen keyboard detected:on");
		        	enterScr.setLogoVisible(false);
		        } else {
		        	Log.d(TAG, "onscreen keyboard detected:off");
		        	enterScr.setLogoVisible(true);
		        }
		     }
		});		
		
	}

    @Override
    protected void onStart() {
    	super.onStart();
    	bindService(new Intent(this, AuraDataService.class), svcConn, BIND_AUTO_CREATE);
    	LocalBroadcastManager.getInstance(this).registerReceiver(loginStatusReceiver, new IntentFilter(AuraDataSession.ACTION_LOGIN_RESULT));
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	unbindService(svcConn);
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(loginStatusReceiver);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();    	
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.d(TAG, "onConfigurationChanged");
    }    

    private void prepareViews() {
    	//adding login view at position 0
    	loginView = ViewFlipper.inflate(this, R.layout.login_view, null);    	
    	enterScr = new EnterAppScreen(loginView, this);    	
    	switcher.addView(loginView, LOGIN_VIEW_POS);
    	//adding progress view at position 1 
    	auroProgress = ViewFlipper.inflate(this, R.layout.aura_progress, null);
    	ImageView img = (ImageView)auroProgress.findViewById(R.id.img_aprogress);
    	switcher.addView(auroProgress, PROGRESS_VIEW_POS);     	  
    	AnimationDrawable frameAnimation = (AnimationDrawable) img.getDrawable();
		frameAnimation.start();
		//adding login view at position 0
    	regmeView = ViewFlipper.inflate(this, R.layout.regme_view, null);    	
    	regmeScr = new RegistrationScreen(regmeView, this);    	
    	switcher.addView(regmeView, REGISTRATION_VIEW_POS);

    	//show Login by default
		switcher.setDisplayedChild(LOGIN_VIEW_POS);
    }
    
    public void swithToLoginView(){
    	Log.d(TAG, "switching to login view");
    	switcher.setInAnimation(slideLeft);
        switcher.setOutAnimation(slideRight);
    	switcher.setDisplayedChild(LOGIN_VIEW_POS);
    	
    	loginView.requestFocus();
    }
    
    public void swithToProgressView(){
    	Log.d(TAG, "switching to progress view");
    	switcher.setInAnimation(slideRight);
        switcher.setOutAnimation(slideLeft);
    	switcher.setDisplayedChild(PROGRESS_VIEW_POS);
    	enterScr.HideSoftKeyboard();
    }
    
    public void swithToRegistrationView(){
    	Log.d(TAG, "switching to registration view");
    	switcher.setInAnimation(slideRight);
        switcher.setOutAnimation(slideLeft);
        switcher.setDisplayedChild(REGISTRATION_VIEW_POS);
    	regmeView.requestFocus();
    }
    
    @Override
    public void onBackPressed() {
    	if(switcher.getDisplayedChild() == PROGRESS_VIEW_POS) {
    		swithToLoginView();
    		return;
    	}
    	if(switcher.getDisplayedChild() == REGISTRATION_VIEW_POS) {
    		swithToLoginView();
    		return;
    	}
    	super.onBackPressed();
    }
        
    private void showBadLoginMsg(String errMsg){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
			String errorMsg;
			// set title
			if(TextUtils.isEmpty(errMsg)){
				alertDialogBuilder.setTitle(R.string.title_dlg_parol_error);
				errorMsg = getString(R.string.dlg_parol_error_msg);
			}else{
				errorMsg = errMsg;
				alertDialogBuilder.setTitle(R.string.title_dlg_title_error);
			}
 
			// set dialog message
			alertDialogBuilder
				.setMessage(errorMsg)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {

					}
				  }); 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create(); 
				// show it
				alertDialog.show();    	
    }
    
    private void showRestorePwd(View v){
    	//todo, no server support for this
    }
    
    private void resetBadgeRequest(){
    	long mark = System.currentTimeMillis();
		String url = AuraDataSession.BASE_URL_OLD + "profile/reset_badge";
		String data = "auth_token=" + dataService.getAuthTooken();
		dataService.executeSimplePost(mark, url, data);
    }
    
}
