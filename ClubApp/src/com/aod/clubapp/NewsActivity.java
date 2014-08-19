package com.aod.clubapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Profile;
import com.aod.clubapp.communicaton.datamodel.UserActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class NewsActivity extends AuraBaseActivity {
	public static final String TAG = "NewsActivity";

	private ListView lv;
	private ImageView photoView;
	
	public static class NewsListAdapter  extends ArrayAdapter<UserActivity> {
		
		private final Context context;
		private final UserActivity[] values;
		
		public NewsListAdapter(Context context, UserActivity[] values) {
			//super(context, R.layout.two_icon_with_text_elem, values);
			super(context, R.layout.news_item, values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = convertView;
			if(rowView == null) {
				rowView = inflater.inflate(R.layout.news_item, parent, false);
			}
			
			final ImageView imageFirst = (ImageView) rowView.findViewById(R.id.first_picture);
			final ImageView imageSecond = (ImageView) rowView.findViewById(R.id.second_picture);
			
			ImageView imageThumb = (ImageView) rowView.findViewById(R.id.pic_thumb_picture);
			
			View part1 = rowView.findViewById(R.id.coment_type);
			View part2 = rowView.findViewById(R.id.like_type);
			
			UserActivity val = values[position];
			Picasso.with(context).cancelRequest(imageFirst);
			Picasso.with(context).cancelRequest(imageSecond);
			Picasso.with(context).cancelRequest(imageThumb);
			if(val.isComment()) {
				TextView title = (TextView) part1.findViewById(R.id.tv_text_title);
				TextView body = (TextView) part1.findViewById(R.id.tv_text_body);
				if(part2 != null){
					part2.setVisibility(View.GONE);
				}
				if(part1 != null) {
					part1.setVisibility(View.VISIBLE);
				}
				title.setText(context.getString(R.string.news_author_title, "@" + val.getAuthorName()));
				if(val.getComment() != null)
					body.setText(val.getComment().getBody());
				
				String firstPicUrl = val.getAuthorAvatar() ;
				if (!TextUtils.isEmpty(firstPicUrl)) {
					Picasso.with(context).load(firstPicUrl)
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
					.into(imageFirst);
				} else {
					imageFirst.setImageResource(R.drawable.default_avatar);
				}
				
				String secondPicUrl = val.getCommentedPhotoUrl();
				if (!TextUtils.isEmpty(secondPicUrl)) {
					Picasso.with(context).load(secondPicUrl)
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
					.into(imageSecond);
				} else {
					imageSecond.setImageResource(R.drawable.empty_img);
				}
			} else if(val.isLike()){
				TextView title = (TextView) part2.findViewById(R.id.tv_text_title);
				TextView body = (TextView) part2.findViewById(R.id.tv_text_body);
				if(part2 != null){
					part2.setVisibility(View.VISIBLE);
				}
				if(part1 != null) {
					part1.setVisibility(View.GONE);
				}
				if(val.getPhoto() != null){
					String url = val.getPhoto().getImageThumbUrl();
					if (!TextUtils.isEmpty(url)) {
						Picasso.with(context).load(url)
				        .noFade()    
						.into(imageThumb);
					}
				}
				body.setText(context.getString(R.string.msg_photo_liked_cnt, val.getLikersCount()));
				
				
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
		
		lv = (ListView)findViewById(R.id.listview);
		photoView = (ImageView )findViewById(R.id.photo_view);
		updateActionBarTitle(getString(R.string.title_news));
		
		View emptView = getLayoutInflater().inflate(R.layout.nodata_msg_view, null);
		addContentView(emptView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((TextView)emptView.findViewById(R.id.tv_msg)).setText(R.string.msg_no_news);
		lv.setEmptyView(emptView);
		lv.setAdapter(null);		
		
		fillContent();		
	}
	
	private void fillContent(){
		if(getDataSession() == null) {
			return;
		}
		List<UserActivity> news = getDataSession().getNews();
		ArrayList<UserActivity> data = new ArrayList<UserActivity>();
		for(UserActivity item : news) {
			if(item.isComment() || item.isLike()) {
				data.add(item);
			}
		}
		
		Profile profile = getDataSession().getMyProfile();
		if(profile != null){
			Photo photo = profile.getProfilePicture();
			if (photo != null && !TextUtils.isEmpty(photo.getImageMediumUrl())) {			
				loadFullScreenImage(photo.getImageMediumUrl(), photoView, this);
			}
		}
		
		NewsListAdapter adapter = new NewsListAdapter(this, data.toArray(new UserActivity[data.size()]));
		lv.setAdapter(adapter);
		
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
}
