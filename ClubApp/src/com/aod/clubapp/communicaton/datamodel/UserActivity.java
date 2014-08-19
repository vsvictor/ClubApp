package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class UserActivity {
	
	@SerializedName("type")
	private String type;
	
	@SerializedName("comment")
	private Comment comment;
	
	@SerializedName("photo")
	private Photo photo;

	@SerializedName("likers")
	private LikeInfo[] likers;
	
	public String getType() {
		return type;
	}

	public Comment getComment() {
		return comment;
	}

	public Photo getPhoto() {
		return photo;
	}
	
	public boolean isComment() {
		return type.compareToIgnoreCase("comment") == 0;
	}
	
	public boolean isLike() {
		return type.compareToIgnoreCase("like") == 0;
	}
	
	public String getAuthorName(){
		if(comment != null && comment.getUser() != null){
			return comment.getUser().getLogin();
		}
		return "";
	}
	
	public String getAuthorAvatar(){
		if(comment != null && comment.getUser() != null){
			return comment.getUser().profilePictureThumb;
		}
		return "";
	}
	
	public String getCommentedPhotoUrl(){
		if(photo != null && photo.getImageThumbUrl() != null){
			return photo.getImageThumbUrl();
		}
		return "";
	}
	public long getLikersCount() {
		if(likers == null)
			return 0;
		return likers.length;
	}
}
