package com.aod.clubapp;

import java.util.ArrayList;
import java.util.Arrays;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.SearchOptionItem;
import com.aod.clubapp.communicaton.datamodel.SearchOptions;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * 
 * Remove it, deprecated
 *
 */
public class EditSearchOptionsDialog extends DialogFragment implements OnClickListener {

	View btnOk;
	View btnCancel;
	Spinner spCategory;
	Spinner spCousine;
	Spinner spAverageBill;
	Spinner spMetro;
	
	AuraDataSession session;
	Context cntx;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(getString(R.string.title_search_category));
		View v = inflater.inflate(R.layout.edit_search_options, null);
		btnOk = v.findViewById(R.id.btn_ok); 
		btnOk.setOnClickListener(this);
		
		btnCancel = v.findViewById(R.id.btn_cancel); 
		btnCancel.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		} );

		spCategory = (Spinner)v.findViewById(R.id.sp_category);		
		spCousine = (Spinner)v.findViewById(R.id.sp_cousine);
		spAverageBill = (Spinner)v.findViewById(R.id.sp_bill);
		spMetro = (Spinner)v.findViewById(R.id.sp_metro);

		SearchOptions opt = session.getSearchOptions();
 		int pos;
		
 		fillSpinner(opt.getPlaceCategories(), spCategory, cntx);
		pos = opt.getPositionPlaceCategoriesPos();
		if(pos >= 0){
			spCategory.setSelection(pos + 1);
		} else {
			spCategory.setSelection(0);
		}
 		
		fillSpinner(opt.getCuisines(), spCousine, cntx);
		pos = opt.getPositionCousines();
		if(pos >= 0){
			spCousine.setSelection(pos + 1);
		} else {
			spCousine.setSelection(0);
		}
		
		fillSpinner(opt.getAverageBills(), spAverageBill, cntx);
		pos = opt.getAverageBillPos();
		if(pos >= 0){
			spAverageBill.setSelection(pos + 1);
		} else {
			spAverageBill.setSelection(0);
		}
		
		fillSpinner(opt.getMetros(), spMetro, cntx);
		pos = opt.getPositionMetroPos();
		if(pos >= 0){
			spMetro.setSelection(pos + 1);
		} else {
			spMetro.setSelection(0);
		}
		
		return v;
	}

	public void setSession(AuraDataSession session, Context cntx) {
		this.session = session;
		this.cntx = cntx;

	}
	
	private void fillSpinner(SearchOptionItem[] data, Spinner sp, Context cntx){
		ArrayList<SearchOptionItem> items = new ArrayList<SearchOptionItem>(Arrays.asList(data));
		SearchOptionItem emptyItem = new SearchOptionItem(-1, cntx.getString(R.string.search_empty));
		items.add(0, emptyItem);
		ArrayAdapter<SearchOptionItem> adapter = new ArrayAdapter<SearchOptionItem>(cntx, android.R.layout.simple_spinner_dropdown_item,items);
		sp.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {

		SearchOptions opt = session.getSearchOptions();
		
		SearchOptionItem item = (SearchOptionItem)spCategory.getSelectedItem();
		opt.setPlaceCategory(item.getId());
		
		item = (SearchOptionItem)spAverageBill.getSelectedItem();
		opt.setAverageBill(item.getId());
		
		item = (SearchOptionItem)spCousine.getSelectedItem();
		opt.setCuisine(item.getId());
		
		item = (SearchOptionItem)spMetro.getSelectedItem();
		opt.setMetro(item.getId()); 
		dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	@Override
	public void onCancel(DialogInterface dialog) {

		super.onCancel(dialog);
	}
	
}
