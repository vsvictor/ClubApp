package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Dialogs {
	
	@SerializedName("dialogs")
	private Dialog[]  dialogs;

	public Dialog[] getDialogs() {
		return dialogs;
	}

	public void setDialogs(Dialog[] dialogs) {
		this.dialogs = dialogs;
	}	
}

