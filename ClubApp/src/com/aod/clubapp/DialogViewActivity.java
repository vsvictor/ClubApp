package com.aod.clubapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Dialog;
import com.aod.clubapp.communicaton.datamodel.Message;
import com.aod.clubapp.communicaton.datamodel.Messages;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class DialogViewActivity extends AuraBaseActivity {
	public static final String TAG = "DialogViewActivity ";
	private static final String apiPath = AuraDataSession.BASE_URL_OLD + "messages?";
	
	private ImageView photoView;
	private ListView lv;
	
	private long dialogId;
	private long userId;
	private long reqOpenDlg = -1;
	private long requestId = -1;
	Messages messages;
	Handler handler;
	
	public static void showDialogViewActivity(long dialogId, String title, Activity cntx) {
		Log.d(TAG, "showDialogViewActivity  id= " + dialogId + " title: " + title);
		Intent intent = new Intent(cntx, DialogViewActivity.class)
			.putExtra(PARAM_DIALOG_ID, dialogId)
			.putExtra(PARAM_TITLE, title);
    	cntx.startActivity(intent);    	
	}
	
	public static void showNewDialogViewActivity(long userId,  Activity cntx) {
		Log.d(TAG, "showDialogViewActivity  userId= " + userId );
		Intent intent = new Intent(cntx, DialogViewActivity.class)
			.putExtra(PARAM_USER_ID, userId);
    	cntx.startActivity(intent);    	
	}
	
	
	public static class MessageListAdapter extends ArrayAdapter<Message> {
		
		private final Context context;
		private final Message[] values;
		private long myId;
		
		public MessageListAdapter(Context context, Message[] values, long myId) {
			super(context, R.layout.two_icon_with_text_elem, values);
			this.context = context;
			this.values = values;
			this.myId = myId;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = convertView;
			if(rowView == null) {
				rowView = inflater.inflate(R.layout.two_icon_with_text_elem, parent, false);
				
				((Activity)context).registerForContextMenu(rowView);
			}
			
			TextView userName = (TextView) rowView.findViewById(R.id.tv_text_title);
			TextView commentBody = (TextView) rowView.findViewById(R.id.tv_text_body);
			ImageView leftImg = (ImageView) rowView.findViewById(R.id.first_picture);			
			ImageView rightImg = (ImageView) rowView.findViewById(R.id.second_picture);
			ImageView targetImg;
			
			Message msg = values[position];

			if(msg.getSenderId() == myId) {
				rowView.setTag(msg.getId());
			} else {
				rowView.setTag(null);
			}
			
			String uname = "anonymous";
			if(msg.getLogin() != null && !TextUtils.isEmpty(msg.getLogin())) {
				uname = msg.getLogin();
			}
			userName.setText("@"+ uname + ":");						
			commentBody.setText(msg.getBody());
			
			//fill avatar
			if(msg.getSenderId() == myId){
				targetImg = rightImg;
				leftImg.setVisibility(View.GONE);
			} else {
				targetImg = leftImg;
				rightImg.setVisibility(View.GONE);
			}
			Picasso.with(context).cancelRequest(targetImg);
			targetImg.setVisibility(View.VISIBLE);
			targetImg.setImageResource(R.drawable.default_avatar);
			if(msg.getProfilePictureThumb() != null) {
				if (!TextUtils.isEmpty(msg.getProfilePictureThumb())) {
					Picasso.with(context).load(msg.getProfilePictureThumb())
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
					.into(targetImg);
				}
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
	public void onSimpleGet(long requestId, boolean success) {
		if(requestId < 0){
			return;
		}
		if(requestId == this.requestId){			
			try{
				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
				Gson gson = BaseGetApiCommand.getGson();		
				messages  =  gson.fromJson(qRes, Messages.class);
				fillContent();
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "bad json comes from server", e);
			}
		} else if(reqOpenDlg == requestId){
			try{
				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
				Gson gson = BaseGetApiCommand.getGson();		
				Dialog dlg  =  gson.fromJson(qRes, Dialog.class);
				dialogId = dlg.getId();
				String title = dlg.getLogin();
				if(!TextUtils.isEmpty(title)){
					updateActionBarTitle(title);	
				}
				fillContent();
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "bad json comes from server", e);
			}			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_chat);
		handler = new Handler();
		lv = (ListView)findViewById(R.id.listview);
		photoView = (ImageView )findViewById(R.id.photo_view);
		dialogId = getIntent().getLongExtra(PARAM_DIALOG_ID, -1);
		userId = getIntent().getLongExtra(PARAM_USER_ID, -1);
		String title = getIntent().getStringExtra(PARAM_TITLE);
		updateActionBarTitle(title);				
		
		fillContent();
	}
	
	private void openNewDialog(){
		reqOpenDlg = System.currentTimeMillis();
		String url = AuraDataSession.BASE_URL + "dialogs";
		String data = "auth_token=" + getDataSession().getAuthTooken() +"&user_id=" + userId;
		getLocalService().executeSimplePost(reqOpenDlg, url, data);
	}
	
	private void fillContent(){
		if(getDataSession() == null) {
			return;
		}
		
		if(userId > 0 && dialogId == -1){
			openNewDialog();
			return;
		}
		
		if(getDataSession() != null && requestId < 0) {
			requestUpdate();
			return;
		}
		Profile profile = getDataSession().getMyProfile();
		if(profile != null){
			Photo photo = profile.getProfilePicture();
			if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
				loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
			}
		}
		if(getDataSession().getMyProfile() != null){
			if(messages.getMessages() != null){
				MessageListAdapter adapter = new MessageListAdapter(this, messages.getMessages(), getDataSession().getMyProfile().getId());
				lv.setAdapter(adapter);
			}
		}
	}
	
	public void onClickSend(View view){
		//send and update chat list
		Log.d(TAG, "Send btn clicked");	
		EditText editor = (EditText) findViewById(R.id.my_comment);
		String msgBody = editor.getText().toString();
		if(!TextUtils.isEmpty(msgBody)) {
			getLocalService().postDialogMessage(dialogId, msgBody);
		}
		((EditText) findViewById(R.id.my_comment)).setText("");
		handler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				requestUpdate();				
			}
		}, 1000);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() {		
		super.onBackPressed();
	}
	
	private void requestUpdate() {
		if(requestId > 0) {
			getDataSession().removeSimpleHttpResponse(requestId);
		}
		requestId = System.currentTimeMillis();
		StringBuilder url = new StringBuilder()
			.append(apiPath)
			.append("auth_token=")
			.append(getDataSession().getAuthTooken())
			.append("&dialog_id=")
			.append(dialogId);
		getLocalService().executeSimpleGet(requestId, url.toString());
	}
	

	private long lastMenuItem = -1;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	      super.onCreateContextMenu(menu, v, menuInfo);
	    	  if(v.getTag() != null) {
	    		  Long msgId = (Long)v.getTag();
	    		  MenuInflater inflater = getMenuInflater();
		          inflater.inflate(R.menu.chat_context, menu);
		          lastMenuItem = msgId;
	    	  } else {
	    		  lastMenuItem = -1;
	    	  }

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      switch(item.getItemId()) {
	          case R.id.delete:
	          {
	        	  if(lastMenuItem > 0){
	        		  deleteSelectedMsg(lastMenuItem);
	        	  }
	        	  return true;
	          }
	          default:
	        	  return super.onContextItemSelected(item);
	      }
	}
	
	private void deleteSelectedMsg(long msgId){
		getLocalService().deleteChatMessage(msgId);
		handler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				requestUpdate();				
			}
		}, 250); 
	}
	
	
}