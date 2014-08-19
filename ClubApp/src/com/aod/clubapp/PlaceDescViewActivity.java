package com.aod.clubapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.Albums;
import com.aod.clubapp.communicaton.datamodel.Place;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewpagerindicator.CirclePageIndicator;

public class PlaceDescViewActivity extends AuraBaseActivity {
	public static final String TAG = "PlaceDescViewActivity";
	private long placeId;
	private long placeCatId;
	private String imageUrl;
	private Place place;
	
	private ImageView photoView;
	private TextView placeDesc;
	
	public static void showPlaceDesc(long placeCatId, long placeId, String imageUrl, Activity cntx) {
		Log.d(TAG, "showPlace place id= " + Long.toString(placeId));
		Intent intent = new Intent(cntx, PlaceDescViewActivity.class)
			.putExtra(PARAM_PLACE_ID, placeId)
			.putExtra(PARAM_CATEGORY_ID, placeCatId)
			.putExtra(PARAM_URL, imageUrl);
    	cntx.startActivity(intent);
	}
	
	@Override
	public void onDataServiceConnected() {
		fillContent();		
	}
	@Override
	public void onDataServiceDisConnected() {
		Log.d(TAG, "onDataServiceDisConnected");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_place_desc);		
		placeId = getIntent().getLongExtra(PARAM_PLACE_ID, -1);
		placeCatId = getIntent().getLongExtra(PARAM_CATEGORY_ID, -1);
		imageUrl = getIntent().getStringExtra(PARAM_URL);
		placeDesc = (TextView) findViewById(R.id.tv_description);
		photoView = (ImageView)findViewById(R.id.photo_view);
		if (!TextUtils.isEmpty(imageUrl)) {			
			loadFullScreenImage(imageUrl, photoView, this);
		}
		fillContent();
	}

	private void fillContent() {
		if(getDataSession() == null)
			return;
			Log.d(TAG, "fillContent() placeid=" + placeId);
			place = getDataSession().getPlace(placeCatId, placeId);
			if(place == null) {
				return;
			}
			placeDesc.setText(
					getString(R.string.place_descripton, 
					TextUtils.isEmpty(place.getDescription()) ? " " : place.getDescription(),
					TextUtils.isEmpty(place.getMetro()) ? " " : place.getMetro(),
					TextUtils.isEmpty(place.getAddress()) ? " " : place.getAddress(),
					TextUtils.isEmpty(place.getCuisine()) ? " " : place.getCuisine(),
					TextUtils.isEmpty(place.getAverage_bill()) ? " " : place.getAverage_bill(),
					TextUtils.isEmpty(place.getWorking_hours()) ? " " : place.getWorking_hours(),
					TextUtils.isEmpty(place.getPhone()) ? " " : place.getPhone(),
					TextUtils.isEmpty(place.getWebsite()) ? " " : place.getWebsite()));
			updateActionBarTitle(place.getName());					
	}	

}