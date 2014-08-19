package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Comment implements Comparable {
	
	@SerializedName("id")
	private long id;
	
	@SerializedName("body")
	private String body;
	
	@SerializedName("photo_id")
	private long photoId;
	
	@SerializedName("user_id")
	private long userId;
	
	@SerializedName("user")
	private User user;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int compareTo(Object another) {
		Comment cmp = (Comment)another;
		if(id == cmp.id)
			return 0;
		if(id < cmp.id)
			return -1;
		else 
			return 1;
		
	}
}
