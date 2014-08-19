package com.aod.clubapp.communicaton.auraapi;

import android.content.Context;

public interface IApiCommand extends Runnable {
	void onSuccesExecution(Context cntx);
	void onFailExecution(Context cntx);
	boolean isFailed();
}
