package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Photos {
	@SerializedName("photos")
	private Photo[]  photos;

	public Photo[] getPhotos() {
		return photos;
	}
	public void setPhotos(Photo[] arr){
		photos = arr.clone();
	}
}
