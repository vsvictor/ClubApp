package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class SimpleImage {
	@SerializedName("id")
	long id;
		
	@SerializedName("image_thumb_url")
	String imageThumbUrl;
	
	@SerializedName("image_medium_url")
	String imageMediumUrl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getImageThumbUrl() {
		return imageThumbUrl;
	}

	public void setImageThumbUrl(String imageThumbUrl) {
		this.imageThumbUrl = imageThumbUrl;
	}

	public String getImageMediumUrl() {
		return imageMediumUrl;
	}

	public void setImageMediumUrl(String imageMediumUrl) {
		this.imageMediumUrl = imageMediumUrl;
	}	
}
