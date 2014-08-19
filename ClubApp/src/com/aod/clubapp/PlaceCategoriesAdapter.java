package com.aod.clubapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.PlaceCategories;
import com.squareup.picasso.Picasso;

public class PlaceCategoriesAdapter extends ArrayAdapter<PlaceCategories.Category> {
	
	private final Context context;
	private final PlaceCategories.Category[] values;
	
	public PlaceCategoriesAdapter(Context context, PlaceCategories.Category[] values) {
		super(context, R.layout.categories_list_elem, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = convertView;
		if(rowView == null) {
			rowView = inflater.inflate(R.layout.categories_list_elem, parent, false);
		}
		TextView title = (TextView) rowView.findViewById(R.id.tv_title);
		final ImageView imageView = (ImageView) rowView.findViewById(R.id.thumb_pic);
		
		PlaceCategories.Category val = values[position];
		title.setText(val.getName());
		/////////
		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		imageView.setMinimumHeight(width);
		//////////////////
		if (!TextUtils.isEmpty(val.getImageThumbUrl())) {
			Picasso.with(context).load(val.getImageThumbUrl())
			.placeholder(R.drawable.transparent_placeholder)
	        .noFade()			
			.into(imageView);
		}		
		return rowView;
	}
	
}
