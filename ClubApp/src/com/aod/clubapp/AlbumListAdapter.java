package com.aod.clubapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.aod.clubapp.communicaton.datamodel.Photo.PhotoUserTag;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class AlbumListAdapter extends ArrayAdapter<Album> {
	private final Context context;
	private Album[] values;
	private boolean showUserLabels = false;

	public AlbumListAdapter(Context context, Album[] values) {
		super(context, R.layout.album_covers_list_elem, values);
		this.context = context;
		this.values = values;
		this.showUserLabels = false;
	}

	public AlbumListAdapter(Context context, Album[] values, boolean showUserLabels) {
		super(context, R.layout.album_covers_list_elem, new ArrayList(Arrays.asList(values)));
		this.context = context;
		this.values = values;
		this.showUserLabels = showUserLabels;
	}
	
	public void setNewValues(Album[] values){
		this.values = values;
		clear();
		addAll(new ArrayList(Arrays.asList(values)));		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = convertView;
		if(rowView == null) {
			rowView = inflater.inflate(R.layout.album_covers_list_elem, parent, false);
		}
		TextView album = (TextView) rowView.findViewById(R.id.tv_album_name);
		TextView place = (TextView) rowView.findViewById(R.id.tv_place_name);
		TextView albumTime = (TextView) rowView.findViewById(R.id.tv_time);
		TextView likesCount = (TextView) rowView.findViewById(R.id.tv_likes);
		TextView photoCount = (TextView) rowView.findViewById(R.id.photo_count);
		
		TextView userMark = (TextView) rowView.findViewById(R.id.tv_user);
		TextView userMarkCount = (TextView) rowView.findViewById(R.id.tv_marked_users_msg);
		userMark.setVisibility(View.INVISIBLE);
		userMarkCount.setVisibility(View.INVISIBLE);
		
		final ImageView imageView = (ImageView) rowView.findViewById(R.id.album_cover);

		Album data = values[position];
		album.setText(data.getName());
		place.setText(data.getPlaceName());
		
		if(showUserLabels) {
			Photo cover = data.getCoverImage();
			if(cover != null) {
				if(cover.getUserTagsCount() > 0) {
					PhotoUserTag[] tags = cover.getUserTags();
					userMark.setVisibility(View.VISIBLE);
					userMark.setText("@" + tags[0].getLogin());
					if(tags.length > 1){
						userMarkCount.setText(context.getString(R.string.marked_users_count, tags.length  - 1));
						userMarkCount.setVisibility(View.VISIBLE);
					}
				}
			}
		}		
		
		// albumTime
		if(data.getDate() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data.getDate());
			albumTime.setText(createTimeDescriptionString(cal));
		} else {
			albumTime.setText("");
		}
		
		updateLikesCount(likesCount, data.getLikesCount(), true);
		photoCount.setText(context.getString(R.string.photo_count, data.getPhotosCount()));
		Picasso.with(context).cancelRequest(imageView);
		if (!TextUtils.isEmpty(data.getCoverImageUrl())) {
			Picasso.with(context).load(data.getCoverImageUrl())
			.placeholder(R.drawable.transparent_placeholder)
	        .noFade()			
			.into(imageView);
		}
		return rowView;
	}
	private String createTimeDescriptionString(Calendar cal) {		
		String day = context.getResources().getStringArray(R.array.days_of_week_short)[cal.get(Calendar.DAY_OF_WEEK)];
		String mmmm = context.getResources().getStringArray(R.array.month_name_short)[cal.get(Calendar.MONTH)];
		return String.format("%s, %d %s, %02d:%02d", day, cal.get(Calendar.DAY_OF_MONTH), mmmm, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));		
	}
	
	public void updateLikesCount(TextView tv, long likesCount, boolean isLiked) {
		Drawable left = isLiked ? 
					context.getResources().getDrawable(R.drawable.heart_liked) :
					context.getResources().getDrawable(R.drawable.heart_unliked);
		tv.setCompoundDrawablesWithIntrinsicBounds (left, null, null, null);
		tv.setText(Long.toString(likesCount));
	}
}
