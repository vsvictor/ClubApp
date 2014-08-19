package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Comments {
	@SerializedName("comments")
	Comment comments[];

	public Comment[] getComments() {
		return comments;
	}
	
}
