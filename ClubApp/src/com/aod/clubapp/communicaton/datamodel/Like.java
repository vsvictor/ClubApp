package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Like {
/*
	likes: [
	{
		id: 4,
		user: {
			id: 5,
			login: "qwerty",
			profile_picture_thumb: null
		},
		photo: {
			id: 21,
			likes_count: 1,
			liked: false,
			image_thumb_url: "http://s3-eu-west-1.amazonaws.com/fixappclubs/photos/images/21/thumb.jpg?1389648150",
			image_medium_url: "http://s3-eu-west-1.amazonaws.com/fixappclubs/photos/images/21/medium.jpg?1389648150",
			photo_user_tags: [ ]
		}
	},	
*/	
//	public static class User {
//		@SerializedName("id")
//		private long id;
//		
//		@SerializedName("login")
//		private String login;
//		
//		@SerializedName("profile_picture_thumb")		
//		private String profilePictureThumb;
//	}
	
	@SerializedName("id")
	private long id;
	
	@SerializedName("user")
	private User user;
	
	@SerializedName("photo")
	private Photo photo;
}
