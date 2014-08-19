package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Message {
	@SerializedName("id")
	long id;
	
	@SerializedName("body")
	String body;
	
	@SerializedName("user")
	User user;

	public long getId() {
		return id;
	}

	public String getBody() {
		return body;
	}

	public User getUser() {
		return user;
	}
	
	public String getLogin() {
		if(user == null)
			return null;
		return user.getLogin();
	}
	
	public long getSenderId() {
		if(user == null)
			return -1;
		return user.getId();
	}
	
	public String getProfilePictureThumb() {
		if(user == null)
			return null;
		return user.getProfilePictureThumb();
	}
	
}
