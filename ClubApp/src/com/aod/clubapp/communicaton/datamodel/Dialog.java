package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Dialog {
	@SerializedName("id")
	long id;
	
	@SerializedName("last_message")
	String LastMessage;
	
	@SerializedName("interlocutor_id")
	long interlocutorId;

	@SerializedName("interlocutor_login")
	String login;
	
	@SerializedName("interlocutor_profile_picture_thumb")
	String profilePictureThumb;

	public long getId() {
		return id;
	}

	public String getLastMessage() {
		return LastMessage;
	}

	public long getInterlocutorId() {
		return interlocutorId;
	}

	public String getLogin() {
		return login;
	}

	public String getProfilePictureThumb() {
		return profilePictureThumb;
	}
	
	
	
}
