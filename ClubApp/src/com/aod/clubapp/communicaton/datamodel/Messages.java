package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Messages {
	@SerializedName("messages")
	private Message[]  messages;

	public Message[] getMessages() {
		return messages;
	}
}
