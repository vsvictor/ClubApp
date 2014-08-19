package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class User {
	@SerializedName("id")
	long id;
	
	@SerializedName("login")
	String login;
	
	@SerializedName("name")
	String name;
	
	@SerializedName("profile_picture_thumb")
	String profilePictureThumb;
	
	
	@SerializedName("photos_tagged_in_count")
	long photosTaggedInCount;
	
	@SerializedName("likes_count")
	long likesCount;
	
	@SerializedName("comments_count")
	long commentsCount;
	
	@SerializedName("followers_count")
	long followersCount;
	
	@SerializedName("followed_users_count")
	long followedUsersCount;
	
	@SerializedName("following")
	boolean following = false;
	
	@SerializedName("profile_picture")
	Photo profilePicture;

	@SerializedName("outgoing_following_request")
	boolean  outFollowingRequest;
	
	@SerializedName("incoming_following_request")
	boolean  inFollowingRequest;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfilePictureThumb() {
		return profilePictureThumb;
	}

	public void setProfilePictureThumb(String profilePictureThumb) {
		this.profilePictureThumb = profilePictureThumb;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

	public long getFollowedUsersCount() {
		return followedUsersCount;
	}

	public void setFollowedUsersCount(long followedUsersCount) {
		this.followedUsersCount = followedUsersCount;
	}

	public Photo getProfilePicture() {
		return profilePicture;
	}

	public long getPhotosTaggedInCount() {
		return photosTaggedInCount;
	}

	public void setPhotosTaggedInCount(long photosTaggedInCount) {
		this.photosTaggedInCount = photosTaggedInCount;
	}

	public void setProfilePicture(Photo profilePicture) {
		this.profilePicture = profilePicture;
	}

	public boolean isOutFollowingRequest() {
		return outFollowingRequest;
	}

	public boolean isInFollowingRequest() {
		return inFollowingRequest;
	}

	public void  setInFollowingRequest(boolean val) {
		inFollowingRequest = val;
	}
	
	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(boolean following) {
		this.following = following;
	}
	
}
