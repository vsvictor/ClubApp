package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Banner {
	@SerializedName("image_url")
	String image_url;
	@SerializedName("position")
	String position;
	@SerializedName("action")
	String action;
	@SerializedName("url")
	String url;
	@SerializedName("place_id")
	long place_id;
	@SerializedName("album_id")
	long album_id;
	

}
