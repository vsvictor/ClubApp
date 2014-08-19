package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Place {
	@SerializedName("id")
	long id;
	
	@SerializedName("name")
	String name;
	
	///
	@SerializedName("description")
	String description;
	
	@SerializedName("metro")
	String metro;

	@SerializedName("address")
	String address;

	@SerializedName("cuisine")
	String cuisine;

	@SerializedName("average_bill")
	String average_bill;

	@SerializedName("working_hours")
	String working_hours;

	
	@SerializedName("phone")
	String phone;

	@SerializedName("website")
	String website;

	@SerializedName("likes_count")
	long likesCount;
	
	@SerializedName("liked")
	boolean liked;
	
	@SerializedName("place_photos")
	SimpleImage placePhotos[];

	@SerializedName("cover_image")
	SimpleImage coverImage;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMetro() {
		return metro;
	}

	public void setMetro(String metro) {
		this.metro = metro;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getAverage_bill() {
		return average_bill;
	}

	public void setAverage_bill(String average_bill) {
		this.average_bill = average_bill;
	}

	public String getWorking_hours() {
		return working_hours;
	}

	public void setWorking_hours(String working_hours) {
		this.working_hours = working_hours;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public SimpleImage[] getPlacePhotos() {
		return placePhotos;
	}

	public void setPlacePhotos(SimpleImage[] placePhotos) {
		this.placePhotos = placePhotos;
	}

	public SimpleImage getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(SimpleImage coverImage) {
		this.coverImage = coverImage;
	}
	
	
}

