package com.aod.clubapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.aod.clubapp.communicaton.AuraDataService;
import com.aod.clubapp.communicaton.IAuraOperations;
import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import com.bugsense.trace.BugSenseHandler;

/**
 * keeps common functionality in one place:
 *  1. routines for initializing and accessing data service
 *  
 *  
 *  
 * @author Anatoliy Odukha <aodukha@gmail.com>
 *
 */
public abstract class AuraBaseActivity extends FragmentActivity implements OnQueryTextListener{
	private static final String TAG = "AuraBaseActivity";
	
	public static final String PARAM_TITLE = "param.TITLE";
	public static final String PARAM_ALBUM_ID = "param_album_ID";
	public static final String PARAM_PHOTO_ID = "param_photo_ID";
	public static final String PARAM_CATEGORY_ID = "PlaceCategorisListActivity.PARAM_CATEGORY";
	public static final String PARAM_PLACE_ID = "Place.PARAM_PLACE_ID";
	public static final String PARAM_USER_ID = "param.PARAM_USER_ID";
	public static final String PARAM_DIALOG_ID = "param.PARAM_DIALOG_ID";
	public static final String PARAM_ALBUMS_COLLECTION = "param.PARAM_ALBUMS_COLLECTION";
	public static final String PARAM_SEARCH_QUERY = "param.PARAM_SEARCH_QUERY";
	public static final String PARAM_URL = "param.PARAM_URL";
	
	public static final int ALBUM_COLLECTION_MAIN = 1;
	public static final int ALBUM_COLLECTION_FRIENDS = 2;
	public static final int ALBUM_COLLECTION_PLACE = 3;
	
	private IAuraOperations dataService;	
	protected SearchView searchView; 
	
	private ServiceConnection svcConn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			dataService = (IAuraOperations) binder;

			try {
				if (!dataService.isLoggedIn()) {
					startActivity(new Intent(AuraBaseActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					finish();
				}
				onDataServiceConnected();				
			} catch (Throwable t) {
				Log.e(TAG, "Exception in call to onServiceConnected()", t);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			dataService = null;
			onDataServiceDisConnected();
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "catched broadcast AuraDataSession.ACTION_HTTP_GET_RESULT");
			int status = intent.getIntExtra(AuraDataSession.OPERATION_STATUS, AuraDataSession.STATUS_FAIL);						
			long id = intent.getLongExtra(AuraDataSession.SIMPLE_GET_ID, -1);
			onSimpleGet(id, status != AuraDataSession.STATUS_FAIL);			
		}
	};
	
	private BroadcastReceiver closeAllReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Received close all event, finishing");
			finish();
		}
	};
	
	public void onDataServiceConnected(){
		if(getDataSession() != null){
			if(!getDataSession().isLoggedIn()){
				finish();
			}
		}
	}
	
	public abstract void onDataServiceDisConnected();
	
	public AuraDataSession getDataSession() {
		if(dataService == null)
			return null;
		return dataService.getDataSession();
	}
	
	public IAuraOperations getLocalService() {
		return dataService;
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	bindService(new Intent(this, AuraDataService.class), svcConn, BIND_AUTO_CREATE);
    	LocalBroadcastManager
		.getInstance(this)
		.registerReceiver(receiver, new IntentFilter(AuraDataSession.ACTION_HTTP_GET_RESULT));
    	
    	LocalBroadcastManager
		.getInstance(this)
		.registerReceiver(closeAllReceiver, new IntentFilter(AuraDataSession.ACTION_CLOSE_ALL_SCREENS));
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	unbindService(svcConn);
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(closeAllReceiver);
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	BugSenseHandler.initAndStartSession(this, "eb2c8c8e");
    	if(getActionBar() != null) {
    		getActionBar().setDisplayHomeAsUpEnabled(true);
    	}
    }
    
    public void onSimpleGet(long requestId, boolean success){
    	//Do nothing, override it in consumer
    }
    
    /**
     * Show album title(or place if title absent) as Actionbar title
     * 
     * @param album
     */
    protected void updateActionBarTitle(Album album){
    	if(album == null)
    		return;
		//set actionbar title
		String title = album.getName();
		if(TextUtils.isEmpty(title))
			title = album.getPlaceName();
		if(!TextUtils.isEmpty(title) && getActionBar() != null)
			getActionBar().setTitle(title);
    }
    
    protected void updateActionBarTitle(String val){
    	if(val == null)
    		return;
		//set actionbar title		
    	getActionBar().setTitle(val);
    }
    
	public void updateLikesCound(TextView tv, long likesCount, boolean isLiked) {
		Drawable left = isLiked ? 
					getResources().getDrawable(R.drawable.heart_liked) :
						getResources().getDrawable(R.drawable.heart_unliked);
		tv.setCompoundDrawablesWithIntrinsicBounds (left, null, null, null);
		tv.setText(Long.toString(likesCount));
	}
	
	public static void loadFullScreenImage(String url, ImageView photoView, int width, int height, Activity activity) {
		final int imgViewW = width;
		final int imgViewH = height;
		Log.d(TAG, String.format("loadFullScreenImage: %1$d x %2$d url:%3$s ", width, height, url));
		Picasso.with(activity)
			.load(url)
			.transform(new Transformation() {						
				@Override
				public Bitmap transform(Bitmap source) {
					
					Bitmap result;
					int fixedWidth = source.getWidth();  
				    int fixedHeight = Math.round(fixedWidth  * ((float)imgViewH/(float)imgViewW));
				    if(source.getHeight() < fixedHeight){
				    	//недостаточно пикселей по высоте, увеличиваем высоту битмапа для сохранения пропорций
				    	result = Bitmap.createBitmap(fixedWidth, fixedHeight,  Bitmap.Config.ARGB_8888);
				    	result.eraseColor(Color.argb(0,0,0,0));
				        Canvas canvas = new Canvas(result);
				        
				        int paddingTop, paddingBottom;
				        paddingTop = (fixedHeight - source.getHeight())/2;
				        paddingBottom = fixedHeight - paddingTop - source.getHeight();
				        
				        canvas.drawBitmap(source, 
				        		new Rect(0, 0, source.getWidth(), source.getHeight()), 
				        		new Rect(0, paddingTop, source.getWidth(), paddingTop + source.getHeight()), 
				        		null);
				    }else {
				    	//обрезаем низ
				    	result = Bitmap.createBitmap(source, 0, 0, fixedWidth, fixedHeight);
				    }
				    if (result != source) {
				    	source.recycle();
				    }
				    return result;
				    
				}
				
				@Override
				public String key() {
					return "fitWithIntoImageViewCutDown()";
		
				}
			})
			.into(photoView);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(getDataSession() != null){
			if(!getDataSession().isLoggedIn()){
				finish();
			}
		}
	}
	
	@SuppressLint("NewApi")
	public static void loadFullScreenImage(String url, ImageView photoView, Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		
		Point size = new Point();
				
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
			display.getRealSize(size);
		} else {
			display.getSize(size);			
		}
		//do not use sizes from photoView, it may be 0
		loadFullScreenImage(url, photoView, size.x, size.y, activity);	
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_v2, menu);
        if(menu.findItem(R.id.menu_search) != null) {
        	searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        	searchView.setOnQueryTextListener(this);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {    	    	
    	menu.removeItem(R.id.menu_search);
    	menu.removeItem(R.id.menu_search_categories);
    	return super.onPrepareOptionsMenu(menu);
    }
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {    		
    	case android.R.id.home:    		
    		onBackPressed();
    		return true;
    	case R.id.menu_search:
    		Log.d(TAG, "on menu R.id.menu_search");
    		return true;
    		
    	case R.id.menu_search_categories:
    		Log.d(TAG, "on menu R.id.menu_search");
    		categoriesSearch();
//    		EditSearchOptionsDialog configSeach = new EditSearchOptionsDialog();
//    		configSeach.setSession(getDataSession(), this);
//    		configSeach.show(getFragmentManager(), "searchOptions");
    		return true;
    	case R.id.menu_announcement:
			startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.EXTRAS_KEY_INTERVIEW_ONLY, false).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
    		return true;
    	case R.id.menu_friends_labels:
    		startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.EXTRAS_KEY_FRIENDS_ONLY, true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
    	case R.id.menu_interview:
			startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.EXTRAS_KEY_INTERVIEW_ONLY, true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
    		return true;
    	case R.id.menu_places:
    		startActivity(new Intent(this, PlaceCategorisListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    		return true;
    	case R.id.menu_profile:
    		startActivity(new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));    		
    		return true;
    	case R.id.menu_exit:
    		showExitAlert();    		
    		return true;    		
		}
	    return super.onOptionsItemSelected(item);
	}   
	private static final int CODE_CAT_SEARCH = 1293;
	
	private void categoriesSearch(){
		
		startActivityForResult(new Intent(this, SelectSeachCategoriesActivity.class), CODE_CAT_SEARCH);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CODE_CAT_SEARCH){
			if(resultCode != RESULT_OK)
				return;
			String qr = data.getStringExtra(PARAM_SEARCH_QUERY);
			if(!TextUtils.isEmpty(qr)){
				searchView.setQuery(qr, true);
			}
		}else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "searching for:" + query);
		
		return true;
	}
	
	public void showExitAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle(R.string.settings_title_exit);

		// set dialog message
		alertDialogBuilder
			.setMessage(R.string.alert_exit)
			.setCancelable(false)
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {							
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//do nothing
				}
			})
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					getLocalService().logout();
		    		finish();
				}
			  }); 
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create(); 
			// show it
			alertDialog.show();						
	}
	
	//private static final String LOAP_PHOTO_URL =  AuraDataSession.BASE_URL + "photos/followed_users?auth_token=%s&ids=%d";
	private static final String LOAP_PHOTO_URL =  AuraDataSession.BASE_URL_OLD + "photos/followed_users?auth_token=%s&ids=%d";
	public long requestLoadOnePhoto(long photoId){
		String urlQery = String.format(LOAP_PHOTO_URL, getDataSession().getAuthTooken(), photoId);
		long requestId = System.currentTimeMillis();
		getLocalService().executeSimpleGet(requestId, urlQery);
		return requestId;
	}
}
