package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class LikeInfo {
/*
 {
   "id":469,
   "login":"dasha.guryleva",
   "profile_picture_thumb":"http://s3-eu-west-1.amazonaws.com/fixappclubs/photos/images/9647/thumb.jpg?1398893842",
   "liked_at":"2014-05-04T21:56:59.700+04:00"
} 
 */
	
	@SerializedName("id")
	long id;
	
	@SerializedName("login")
	String login;
	
	@SerializedName("profile_picture_thumb")
	String profilePictureThumb;

	public long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getProfilePictureThumb() {
		return profilePictureThumb;
	}
	
	
	
}
