package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class PlaceCategories {
	public static class Category {
		@SerializedName("id")
		long id;
		
		@SerializedName("name")
		String name;
		
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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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
	@SerializedName("place_categories")
	PlaceCategories.Category[] placeCategories;
	public PlaceCategories.Category[] getPlaceCategories() {
		return placeCategories;
	}
	
	public void setPlaceCategories(PlaceCategories.Category[] placeCategories) {
		this.placeCategories = placeCategories;
	}
	
	public String getNameForId(long id) {
		if(placeCategories == null)
			return null;
		for(PlaceCategories.Category category : placeCategories) {
			if(category.id == id){
				return category.getName();
			}
		}
		return null;
	}
	
}
