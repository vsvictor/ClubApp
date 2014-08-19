package com.aod.clubapp;

import android.view.View;

/**
 * On some screens(login) we switching views.  
 * Logic for each view is incapsulated in BaseScreen derived class  
 * 
 * @author Anatoliy Odukha <aodukha@gmail.com>
 *
 */
public abstract class BaseScreen implements View.OnClickListener{

	protected View view;

	public BaseScreen(View view) {
		super();
		this.view = view;
	}
	
	public BaseScreen() {
		super();
	}

	protected View bindToView(int id, View.OnClickListener listener) {
		 View result = view.findViewById(id);
		 if(result != null && listener != null) {
			 result.setOnClickListener(listener);
		 }
		 return result;
	}

}