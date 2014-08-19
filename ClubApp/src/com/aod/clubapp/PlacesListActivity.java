package com.aod.clubapp;

import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.aod.clubapp.communicaton.datamodel.AuraDataSession;
import com.aod.clubapp.communicaton.datamodel.Place;
import com.aod.clubapp.communicaton.datamodel.PlacesList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PlacesListActivity  extends AuraBaseActivity {

	public static final String TAG = "PlaceCategorisListActivity";
	
	private View emptyView;
	private ListView lv;
	private int actionBarHeight = 0;
	private long categoryId = -1;
	private List<Place> places;	
	private String searchQery;
	private long requestId = -1;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "catched broadcast AuraDataSession.ACTION_LOAD_PLACES_RESULT");
			int status = intent.getIntExtra(AuraDataSession.OPERATION_STATUS, AuraDataSession.STATUS_FAIL);						
			long id = intent.getLongExtra(AuraDataSession.PLACE_CATEGORY_ID, -1);
			Log.i(TAG, "catched broadcast AuraDataSession.ACTION_LOAD_PLACES_RESULT category id = " + id);
			if (status == AuraDataSession.STATUS_FAIL) {				
				Log.i(TAG, "commentsReceiver operation failed");				
			} else {
				if(categoryId == id) {
					fillContent();
				}
			}			
		}
	};	
	
	public static void showPlacesListActivity(long category, Activity cntx) {
		Log.d(TAG, "showPlacesListActivity category id= " + category);
		Intent intent = new Intent(cntx, PlacesListActivity.class).putExtra(PARAM_CATEGORY_ID, category);
    	cntx.startActivity(intent);    	
	}
	
	public static void showPlacesListActivity(String searchQery, Activity cntx) {
		Log.d(TAG, "showPlacesListActivity searchQuery = " + searchQery);
		Intent intent = new Intent(cntx, PlacesListActivity.class).putExtra(PARAM_SEARCH_QUERY, searchQery);
    	cntx.startActivity(intent);
	}

	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		fillContent();		
	}

	@Override
	public void onDataServiceDisConnected() {
		//nothing to do
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	LocalBroadcastManager
    		.getInstance(this)
    		.registerReceiver(receiver, new IntentFilter(AuraDataSession.ACTION_LOAD_PLACES_RESULT));
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_simple_image_list);
		
		categoryId = getIntent().getLongExtra(PARAM_CATEGORY_ID, -1);
		searchQery = getIntent().getStringExtra(PARAM_SEARCH_QUERY);
		
		lv = (ListView)findViewById(R.id.list);
		emptyView = findViewById(android.R.id.empty);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "clicked item in place list list: " + position);
				try{
					//TODO: may be null here???
					if(places != null) {
						Place pls = places.get(position);
						PlaceViewActivity.showPlace(categoryId, pls.getId(), PlacesListActivity.this);
					} else {
						Log.w(TAG, "onCreate.onItemClick: unexpected behaviour, places should not be null here");
					}
				} catch (IndexOutOfBoundsException ex) {
					Log.e(TAG, "places.get(position)", ex);
				}

			}
		});
		// calculate action bar height
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		
		lv.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
			int callsCnt = 0;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				ActionBar abar = getActionBar();
				if(abar == null)
					return;
				if(visibleItemCount == 0)
					return;
		        if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE && callsCnt > 5)
		            return;
				moveTitleOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				callsCnt++;
			}
		});			
/*		
		lv.setAfterOverScroll(new Runnable() {				
			@Override
			public void run() {
				View listItem = lv.getChildAt(0);
	            if (listItem == null)
	                return;		                        
	            LinearLayout title = (LinearLayout)listItem.findViewById(R.id.content_part1);		 
	            int topMargin = 0;		 
	            // set the margin.
	            ((ViewGroup.MarginLayoutParams) title.getLayoutParams()).topMargin = topMargin;
	            // request Android to layout again.
	            listItem.requestLayout();					
			}
		});		
*/		
		
		fillContent();		
	}

	private void fillContent() {
		if(getDataSession() == null)
			return;
		if((places == null || places.size() == 0) || requestId < 0){
			if(TextUtils.isEmpty(searchQery)) {
				//load all places in category
				if(places == null && getDataSession().getPlacesList(categoryId) == null && categoryId >= 0) {
					getLocalService().loadPlacesInCategory(categoryId);
					return;
				}
				if(getActionBar() != null) {
					String title = getDataSession().getCategoryName(categoryId);
					if(!TextUtils.isEmpty(title)){
						getActionBar().setTitle(title);
					}
				}		
				places = getDataSession().getPlacesList(categoryId);
				PlacesListAdapter adapter = new PlacesListAdapter(this, places.toArray(new Place[places.size()]));		
				lv.setAdapter(adapter);
				return;
			} else {
				if(requestId < 1){
					//execute search and show results
					String urlQery =  getDataSession().getSearchOptions().createSearchUrl(searchQery, getDataSession().getAuthTooken());				
					requestId = System.currentTimeMillis();
					getLocalService().executeSimpleGet(requestId, urlQery);
				}
			}
			return;
		}
		
		if(places != null && places.size() > 0){
			PlacesListAdapter adapter = new PlacesListAdapter(this, places.toArray(new Place[places.size()]));		
			lv.setAdapter(adapter);
		}
		if(places == null){
			Log.w(TAG, "unexpected behaviour, places list should be available for category = " + categoryId);
		}

	}		

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {    	    	
    	return true;
    }
    
	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "searching for:" + query);
		searchQery = query;
		String urlQery =  getDataSession().getSearchOptions().createSearchUrl(searchQery, getDataSession().getAuthTooken());				
		requestId = System.currentTimeMillis();
		getLocalService().executeSimpleGet(requestId, urlQery);
		return true;
	}
	
	@Override
	public void onSimpleGet(long requestId, boolean success) {
		try {
			if(requestId == this.requestId){
				String qRes = getDataSession().getSimpleHttpResponse(requestId);		
				Gson gson = BaseGetApiCommand.getGson();		
				PlacesList pls  =  gson.fromJson(qRes, PlacesList.class);
				if(pls.getPlaces() != null && pls.getPlaces().length > 0){
					places =  Arrays.asList(pls.getPlaces());
					fillContent();
				}
				((ViewFlipper)emptyView).setDisplayedChild(1);				
			}
		} catch (JsonSyntaxException e) {
			Log.e(TAG, "bad json comes from server", e);
		}
	}
	public void moveTitleOnScroll(AbsListView view, int firstVisibleItem, int visibleCount, int totalItemCount) {
		actionBarHeight = 0;
        for (int i=0; i < visibleCount; i++) {
            View listItem = lv.getChildAt(i);
            if (listItem == null)
                break;
                        
            LinearLayout title = (LinearLayout)listItem.findViewById(R.id.content_part1);
 
            int topMargin = 0;
            if (i == 0) {
                int top = listItem.getTop();
                int height = listItem.getHeight();
 
                // if top is negative, the list item has scrolled up.
                // if the title view falls within the container's visible portion,
                //     set the top margin to be the (inverse) scrolled amount of the container.
                // else
                //     set the top margin to be the difference between the heights.
                if (top < 0){
                	// topMargin = title.getHeight() < (top + height) ? -top : (height - title.getHeight());
                    topMargin = title.getHeight() < (top + height - actionBarHeight) ? (-top + actionBarHeight) : (height - title.getHeight());
                } else {
                	if(actionBarHeight < top){
	                	topMargin = 0;
	                } else {
	                	topMargin = actionBarHeight - top;
	                }
                }
            }
            if(i == 1){
            	int top = listItem.getTop();
                int height = listItem.getHeight();
                if (top > 0){
	                if(actionBarHeight < top){
	                	topMargin = 0;
	                } else {
	                	topMargin = actionBarHeight - top;
	                }
                }
            }
            if(listItem.getBottom() < 0){
            	topMargin = 0;
            }
            // set the margin.
            ((ViewGroup.MarginLayoutParams) title.getLayoutParams()).topMargin = topMargin;
 
            // request Android to layout again.
            listItem.requestLayout();
        }
	}	
	
}
