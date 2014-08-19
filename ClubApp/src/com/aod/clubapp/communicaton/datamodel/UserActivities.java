package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;
/**
 * return by 
 * http://fixapp-clubs.herokuapp.com/api/v1/activities?auth_token=22f877b807d58d42410865228ff9ff40
 * 
 *
 */
public class UserActivities {
	@SerializedName("activities")
	private UserActivity[] activities;

	public UserActivity[] getActivities() {
		return activities;
	}
	
	
}
