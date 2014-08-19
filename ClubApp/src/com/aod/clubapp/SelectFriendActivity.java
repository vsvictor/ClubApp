package com.aod.clubapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.aod.clubapp.communicaton.datamodel.User;
import com.aod.clubapp.communicaton.datamodel.Users;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SelectFriendActivity extends AuraBaseActivity {

	public static final String TAG = "SelectFriendActivity ";
	
	public static final String PARAM_REM_USERS = "param_remove_users";
	
	private ListView lv;
	private HashSet<Long> removeUsers;
	private long requestId;
	
	public static class UsersListAdapter  extends ArrayAdapter<User> {
		
		private final Context context;
		private final User[] values;
		
		public UsersListAdapter(Context context, User[] values) {
			super(context, R.layout.icon_with_text_elem, values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = convertView;
			if(rowView == null) {
				rowView = inflater.inflate(R.layout.icon_with_text_elem, parent, false);
			}
			TextView title = (TextView) rowView.findViewById(R.id.tv_text);			
			final ImageView imageView = (ImageView) rowView.findViewById(R.id.picture);
			
			User val = values[position];
			rowView.setTag(new Long(val.getId()));
			title.setText(val.getLogin());
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
			return rowView;
		}
		
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Have the system blur any windows behind this one.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
		setContentView(R.layout.activity_select_person);
		lv = (ListView)findViewById(R.id.listview);
		updateActionBarTitle(getString(R.string.title_who_is));
		
		removeUsers = new HashSet<Long>();
		
		fillRemovedList();
		
		fillContent();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "clicked item in list: " + position);
				Long userId = (Long)view.getTag();
				Log.d(TAG, "clicked item in list for user with id: " + userId);
				if(userId > 0){
					setResult(RESULT_OK, new Intent().putExtra(PARAM_USER_ID, userId.longValue()));
					finish();
				}				
			}
		});		
	}
	
	private void fillRemovedList(){
		String strList = getIntent().getStringExtra(PARAM_REM_USERS);
		if(TextUtils.isEmpty(strList)){
			return;
		}
		String[] ids = strList.split(" ");
		for (String str : ids){
			try{
				long uid = Long.parseLong(str);
				removeUsers.add(uid);
			}catch(NumberFormatException ex){				
			}
		}
	}
	
	@Override
	public void onSimpleGet(long requestId, boolean success) {
		if(requestId < 0 || requestId != this.requestId)
			return;
		try {
			if(success){
//				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
//				Gson gson = BaseGetApiCommand.getGson();		
//				Users data  =  gson.fromJson(qRes, Users.class);
//				 users = Arrays.asList(data.getUsers());
//				 dataLoaded = true;
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
		List<User> followed = getDataSession().getFollowedUsers();
		ArrayList<User> users;
		if(followed != null){
			users = new ArrayList(followed);
		} else {
			users = new ArrayList();
		}
		Profile prof = getDataSession().getMyProfile();
		User me = new User();
		me.setId(prof.getId());
		me.setLogin(getString(R.string.i_am));
		if(prof.getProfilePicture() != null){
			me.setProfilePictureThumb(prof.getProfilePicture().getImageThumbUrl());
		}
		users.add(0, me);
		
		ListIterator<User> iter = users.listIterator();
		while(iter.hasNext()){
			User test = iter.next();
			if(removeUsers.contains(test.getId())){
				iter.remove();
			}
		}
		

		UsersListAdapter adapter = new UsersListAdapter(this, users.toArray(new User[users.size()]));
		lv.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}	
}
