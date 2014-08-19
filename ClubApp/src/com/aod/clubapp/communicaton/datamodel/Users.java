package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Users {
	@SerializedName("users")
	private User[]  users;

	public User[] getUsers() {
		return users;
	}
	
}
