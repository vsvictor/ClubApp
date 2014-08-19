package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class PlacesList {
	@SerializedName("places")
	Place places[];

	public Place[] getPlaces() {
		return places;
	}
	
}
