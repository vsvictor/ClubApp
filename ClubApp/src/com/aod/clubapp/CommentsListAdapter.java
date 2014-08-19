package com.aod.clubapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.Comment;
import com.aod.clubapp.communicaton.datamodel.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


public class CommentsListAdapter extends ArrayAdapter<Comment> { 
	
	private static final String TAG = "CommentsListAdapter";
	private final Context context;
	private final Comment[] values;

	public CommentsListAdapter(Context context, Comment[] values) {
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
		TextView labelTime = (TextView) rowView.findViewById(R.id.tv_time);
		TextView commentBody = (TextView) rowView.findViewById(R.id.tv_comment_text);
		final ImageView userPic = (ImageView) rowView.findViewById(R.id.profile_pic);
		Comment cmt = values[position];
		
		String uname = "anonymous";
		if(cmt.getUser() != null && !TextUtils.isEmpty(cmt.getUser().getLogin())) {
			uname = cmt.getUser().getLogin();
		}
		userName.setText("@"+ uname + ":");
		
		Picasso.with(context).cancelRequest(userPic);
		userPic.setImageResource(R.drawable.default_avatar);
		commentBody.setText(cmt.getBody());		
		if(cmt.getUser() != null) {
			makeAvatarClickable(userPic, cmt.getUser());
			if (!TextUtils.isEmpty(cmt.getUser().getProfilePictureThumb())) {
				Picasso.with(context).load(cmt.getUser().getProfilePictureThumb())
				.placeholder(R.drawable.default_avatar)
		        .noFade()
		        .transform(new Transformation() {						
						@Override
						public Bitmap transform(Bitmap bitmap) {										
							Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
							Paint paint = new Paint();
							paint.setStrokeWidth(5);
							Canvas c = new Canvas(circleBitmap);
							 //This draw a circle of grey color which will be the border of image.
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
	
	private void makeAvatarClickable(ImageView imgView, User usr){
		if(imgView == null || usr == null){
			imgView.setTag(null);
			return;
		}
		imgView.setTag(usr.getId());
		imgView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(v.getTag() != null){
					long uid = ((Long)v.getTag()).longValue();
					ViewUserProfileActivity.showViewUserPrifileActivity(uid, (Activity)context);
				}else {
					Log.w(TAG, "makeAvatarClickable.OnClickListener view have no tag");
				}					
				
				
			}
		});
	}
	
}
