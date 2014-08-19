package com.aod.clubapp;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.IAuraOperations;
import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Messages;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.aod.clubapp.communicaton.datamodel.User;
import com.aod.clubapp.communicaton.datamodel.Users;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SubscriptionManagmentActivity extends AuraBaseActivity {

	public static final String TAG = "SubscriptionManagmentActivity ";
	
	public static final int MODE_SHOW_FOLLOWERS = 0;
	public static final int MODE_SHOW_FOLLOWED = 1;
	
	public static final String PARAM_MODE = "param_show_mode";
	private ImageView photoView;
	private ListView lv;
	private int mode = MODE_SHOW_FOLLOWED;
	private long showForUserId = -1;
	private long requestId = -1;//simple_get request id
	private boolean dataLoaded = false;
	
	List<User> users;
	
	public static void showSubscriptionManagment(int mode, Activity cntx) {
		Log.d(TAG, "SubscriptionManagmentActivity mode= " + mode);
		Intent intent = new Intent(cntx, SubscriptionManagmentActivity.class)
			.putExtra(PARAM_MODE, mode);
    	cntx.startActivity(intent);
	}
	
	public static class UsersListAdapter  extends ArrayAdapter<User> {
		
		private final Context context;
		private final User[] values;
		private int mode = MODE_SHOW_FOLLOWED;
		private AuraDataSession session;
		private IAuraOperations service;
		
		public UsersListAdapter(Context context, User[] values, int mode, AuraDataSession session, IAuraOperations service) {
			super(context, R.layout.subcription_user_element, values);
			this.context = context;
			this.values = values;
			this.mode = mode;
			this.session = session;
			this.service = service;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = convertView;
			if(rowView == null) {
				rowView = inflater.inflate(R.layout.subcription_user_element, parent, false);
			}
			TextView title = (TextView) rowView.findViewById(R.id.tv_text);			
			final ImageView imageView = (ImageView) rowView.findViewById(R.id.picture);
			
			User val = values[position];
			rowView.setTag(new Long(val.getId()));
			title.setText("@" + val.getLogin());
			
			imageView.setTag(val.getId());
			
			imageView.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(v.getTag() != null){
						long uid = ((Long)v.getTag()).longValue();
						ViewUserProfileActivity.showViewUserPrifileActivity(uid, (Activity)context);
					}					
				}
			});

			Picasso.with(context).cancelRequest(imageView);
			if (!TextUtils.isEmpty(val.getProfilePictureThumb())) {
				Picasso.with(context).load(val.getProfilePictureThumb())
				.placeholder(R.drawable.default_avatar)
		        .noFade()
		        .transform(new Transformation() {						
						@Override
						public Bitmap transform(Bitmap bitmap) {										
							Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
							Paint paint = new Paint();
							paint.setStrokeWidth(5);
							Canvas c = new Canvas(circleBitmap);
							 //This draw a circle of Gerycolor which will be the border of image.
							c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
							BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
							paint.setAntiAlias(true);
							paint.setShader(shader);
							// This will draw the 
							c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2-2, paint);
							
					    	bitmap.recycle();
						    return circleBitmap;
						}
						
						@Override
						public String key() {
							return "cirlke_user_pic_1";				
						}
					})		        
				.into(imageView);
			} else {
				imageView.setImageResource(R.drawable.default_avatar);
			}
			
			Button btn = (Button) rowView.findViewById(R.id.btn_change_subscription);
			btn.setTag(Long.valueOf(val.getId()));
			
			if(session.isFriend(val.getId())){
				btn.setText(R.string.btn_subcribed);
			} else {
				btn.setText(R.string.btn_subcribe);
			}
			
			btn.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					long targetId = ((Long)v.getTag()).longValue();
					Log.d(TAG, "(Un)Subscribing user id=" + targetId);					
					if(session.isFriend(targetId)){						
						service.unsubscribeFrom(targetId);
						((Button)v).setText(R.string.btn_subcribe);
					} else {
						service.subscribeFor(targetId);
						((Button)v).setText(R.string.btn_subcribed);						
					}					
				}
			});

			return rowView;
		}
		
	}
	
	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		showForUserId = getDataSession().getMyProfile().getId();
		fillContent();
	}

	@Override
	public void onDataServiceDisConnected() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_select_person);
		lv = (ListView)findViewById(R.id.listview);
		photoView = (ImageView )findViewById(R.id.photo_view);
		
		mode = getIntent().getIntExtra(PARAM_MODE, MODE_SHOW_FOLLOWED);

		showForUserId = getIntent().getLongExtra(PARAM_USER_ID, showForUserId);
		
		if(mode == MODE_SHOW_FOLLOWED)
			updateActionBarTitle(getString(R.string.title_following));
		else
			updateActionBarTitle(getString(R.string.title_followers));
		
		fillContent();
		
	}

	@Override
	public void onSimpleGet(long requestId, boolean success) {
		if(requestId < 0 || requestId != this.requestId)
			return;
		try {
			if(success){
				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
				Gson gson = BaseGetApiCommand.getGson();		
				Users data  =  gson.fromJson(qRes, Users.class);
				users = Arrays.asList(data.getUsers());
				dataLoaded = true;
			}		
			fillContent();
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
		}
	}
	
	private void fillContent(){
		if(getDataSession() == null) {
			return;
		}
		
		Profile profile = getDataSession().getMyProfile();
		if(profile != null){
			Photo photo = profile.getProfilePicture();
			if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
				loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
			}
		}

		List<User> users = getData();
		if(users != null && users.size() > 0){
			UsersListAdapter adapter = new UsersListAdapter(this, users.toArray(new User[users.size()]), mode, getDataSession(), getLocalService());
			lv.setAdapter(adapter);
		}
	}

	private List<User> getData() {
		if(dataLoaded){
			return users;
		}
		if(mode == MODE_SHOW_FOLLOWED){
			/*dataLoaded = true;
			users = getDataSession().getFollowedUsers();
			return users;
			*/
			reqestFollowed();
		} else {
			reqestFollowers();
		}
		
		return users;
	}
	
	private void reqestFollowed(){
		String  apiPath = "http://fixapp-clubs.herokuapp.com/api/v1/users/" + showForUserId +"/followed_users?auth_token=" + getDataSession().getAuthTooken();
		requestId = System.currentTimeMillis();
		getLocalService().executeSimpleGet(requestId, apiPath);
	}
	
	private void reqestFollowers(){
		String  apiPath = "http://fixapp-clubs.herokuapp.com/api/v1/users/" + showForUserId +"/followers?auth_token=" + getDataSession().getAuthTooken();
		requestId = System.currentTimeMillis();
		getLocalService().executeSimpleGet(requestId, apiPath);
	}
	
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}	
}
