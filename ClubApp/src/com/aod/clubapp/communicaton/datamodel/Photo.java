package com.aod.clubapp.communicaton.datamodel;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

public class Photo {

	
	public static class PhotoUserTag {
		@SerializedName("id")
		long id;
		
		@SerializedName("x")
		float x;
		
		@SerializedName("user_id")
		long userId;
		
		@SerializedName("user_login")
		String login;

		public PhotoUserTag(long id, float x, long userId, String login){
			this.id = id;
			this.x = x;
			this.login = login;
			this.userId = userId;
		}
		public long getId() {
			return id;
		}

		public float getX() {
			return x;
		}

		public long getUserId() {
			return userId;
		}

		public String getLogin() {
			return login;
		}
		
		
	}
	
	@SerializedName("id")	
	long id;
	@SerializedName("likes_count")
	long likesCount;
	
	@SerializedName("liked")
	boolean liked;

	@SerializedName("purchased")
	boolean purchased;
	
	@SerializedName("image_thumb_url")
	String imageThumbUrl;

	@SerializedName("image_cover_url")
	String imageCoverUrl;
	
	@SerializedName("image_medium_url")
	String imageMediumUrl;

	@SerializedName("photo_user_tags")	
	PhotoUserTag photoUserTags [];
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public void incLikesCount() {
		this.likesCount += 1;
	}
	
	public void decLikesCount() {
		this.likesCount -= 1;
		if(likesCount  < 0)
			likesCount = 0;
	}
	
	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
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

	public int getUserTagsCount() {
		if(photoUserTags == null) {
			return 0;
		}
		return photoUserTags.length;
	}
	
	public PhotoUserTag[] getUserTags() {
		return photoUserTags;
	}

	public void addNewTag(PhotoUserTag tag){
		ArrayList<PhotoUserTag> newList= new ArrayList();
		
		if(photoUserTags != null && photoUserTags.length >0)
			newList.addAll(Arrays.asList(photoUserTags));
		newList.add(tag);
		photoUserTags = newList.toArray(new PhotoUserTag[newList.size()]);
	}
	
}
