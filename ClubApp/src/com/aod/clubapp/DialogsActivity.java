package com.aod.clubapp;

import java.util.List;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.Dialog;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class DialogsActivity extends AuraBaseActivity {
	public static final String TAG = "DialogsActivity";

	private ListView lv;
	private ImageView photoView;
	
	public static class DialogListAdapter  extends ArrayAdapter<Dialog> {
		
		private final Context context;
		private final Dialog[] values;
		
		public DialogListAdapter  (Context context, Dialog[] values) {
			super(context, R.layout.comment_list_elem, values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = convertView;
			if(rowView == null) {
				rowView = inflater.inflate(R.layout.comment_list_elem, parent, false);
			}
			
			TextView userName = (TextView) rowView.findViewById(R.id.tv_user_name);
			TextView commentBody = (TextView) rowView.findViewById(R.id.tv_comment_text);
			final ImageView userPic = (ImageView) rowView.findViewById(R.id.profile_pic);
			Dialog dlg = values[position];
			
			String uname = "anonymous";
			if(dlg.getLogin() != null && !TextUtils.isEmpty(dlg.getLogin())) {
				uname = dlg.getLogin();
			}
			userName.setText("@"+ uname + ":");
			Picasso.with(context).cancelRequest(userPic);
			userPic.setImageResource(R.drawable.default_avatar);
			commentBody.setText(dlg.getLastMessage());
			if(dlg.getProfilePictureThumb() != null) {
				if (!TextUtils.isEmpty(dlg.getProfilePictureThumb())) {
					Picasso.with(context).load(dlg.getProfilePictureThumb())
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
					.into(userPic);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_news);
		photoView = (ImageView )findViewById(R.id.photo_view);
		lv = (ListView)findViewById(R.id.listview);
		updateActionBarTitle(getString(R.string.messages));
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "clicked item position: " + position);
				List<Dialog> dialogs = getDataSession().getDialogs();
				if(dialogs != null && dialogs.size() > 0) {
					Dialog dlg = dialogs.get(position);
					DialogViewActivity.showDialogViewActivity(dlg.getId(), dlg.getLogin(), DialogsActivity.this);
				}
			}
		});
		
		fillContent();		
	}
	
	private void fillContent(){
		if(getDataSession() == null) {
			return;
		}
		List<Dialog> dialogs = getDataSession().getDialogs();
		DialogListAdapter adapter = new DialogListAdapter(this, dialogs.toArray(new Dialog[dialogs.size()]));
		lv.setAdapter(adapter);
		
		Profile profile = getDataSession().getMyProfile();
		if(profile != null){
			Photo photo = profile.getProfilePicture();
			if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
				loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
}