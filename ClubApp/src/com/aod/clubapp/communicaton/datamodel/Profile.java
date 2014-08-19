package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Profile implements Cloneable{
	public static class Settings implements Cloneable{
		@SerializedName("tags_private")
		boolean tags_private;
		
		@SerializedName("messages_private")
		boolean messagesPrivate;
				
		@SerializedName("push_settings_likes")
		boolean pushSettingsLikes;
		
		@SerializedName("push_settings_comments")
		boolean pushSettingsComments;
		
		@SerializedName("push_settings_tags")
		boolean pushSettingsTags;
		
		@SerializedName("push_settings_messages")
		boolean pushSettingsMessages;
		
		@SerializedName("push_settings_contacts")
		boolean pushSettingsContacts;
		
		@Override
		protected Settings clone() throws CloneNotSupportedException {
			return (Settings)super.clone();
		}
		
		public boolean isTags_private() {
			return tags_private;
		}

		public void setTags_private(boolean tags_private) {
			this.tags_private = tags_private;
		}

		public boolean isMessagesPrivate() {
			return messagesPrivate;
		}

		public void setMessagesPrivate(boolean messagesPrivate) {
			this.messagesPrivate = messagesPrivate;
		}

		public boolean isPushSettingsLikes() {
			return pushSettingsLikes;
		}

		public void setPushSettingsLikes(boolean pushSettingsLikes) {
			this.pushSettingsLikes = pushSettingsLikes;
		}

		public boolean isPushSettingsComments() {
			return pushSettingsComments;
		}

		public void setPushSettingsComments(boolean pushSettingsComments) {
			this.pushSettingsComments = pushSettingsComments;
		}

		public boolean isPushSettingsTags() {
			return pushSettingsTags;
		}

		public void setPushSettingsTags(boolean pushSettingsTags) {
			this.pushSettingsTags = pushSettingsTags;
		}

		public boolean isPushSettingsMessages() {
			return pushSettingsMessages;
		}

		public void setPushSettingsMessages(boolean pushSettingsMessages) {
			this.pushSettingsMessages = pushSettingsMessages;
		}

		public boolean isPushSettingsContacts() {
			return pushSettingsContacts;
		}

		public void setPushSettingsContacts(boolean pushSettingsContacts) {
			this.pushSettingsContacts = pushSettingsContacts;
		}			
	}
	
	@SerializedName("id")
	long id;

	@SerializedName("name")
	String name;
	
	@SerializedName("login")
	String login;
	
	@SerializedName("email")
	String email;
	
	@SerializedName("phone")
	String phone;
	
	@SerializedName("unread_comments")
	int unreadComments;
	
	@SerializedName("unread_likes")
	int unreadLikes;
	
	@SerializedName("unread_messages_count")
	int unreadMessagesCount;
	
	@SerializedName("photos_tagged_in_count")
	int photosTaggedInCount;
	
	@SerializedName("likes_count")
	int likesCount;
	
	@SerializedName("comments_count")
	int commentsCount;
	
	@SerializedName("followers_count")
	int followersCount;
	
	@SerializedName("followed_users_count")
	int followedUsersCount;
	
	@SerializedName("settings")
	Profile.Settings settings;
	
	@SerializedName("profile_picture")
	Photo profilePicture;

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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getUnreadMessagesCount() {
		return unreadMessagesCount;
	}

	public void setUnreadMessagesCount(int unreadMessagesCount) {
		this.unreadMessagesCount = unreadMessagesCount;
	}

	public int getPhotosTaggedInCount() {
		return photosTaggedInCount;
	}

	public void setPhotosTaggedInCount(int photosTaggedInCount) {
		this.photosTaggedInCount = photosTaggedInCount;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getFollowedUsersCount() {
		return followedUsersCount;
	}

	public void setFollowedUsersCount(int followedUsersCount) {
		this.followedUsersCount = followedUsersCount;
	}

	public Profile.Settings getSettings() {
		return settings;
	}

	public void setSettings(Profile.Settings settings) {
		this.settings = settings;
	}

	public Photo getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(Photo profilePicture) {
		this.profilePicture = profilePicture;
	}

	@Override
	public Profile clone() throws CloneNotSupportedException {
		return (Profile)super.clone();
	}
}
