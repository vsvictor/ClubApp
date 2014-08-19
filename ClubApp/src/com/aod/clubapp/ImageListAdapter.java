package com.aod.clubapp;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.aod.clubapp.ui.SquaredImageView;

public class ImageListAdapter extends BaseAdapter {
	private static final String TAG = "ImageListAdapter";
	private Context context;
	Album album;
	int screenWidth;
	
	public ImageListAdapter(Context c, Album album) {
        context = c;
        this.album = album;
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay(); 
        screenWidth = display.getWidth();
    }
	
	public ImageListAdapter(Context c, Photo[] photos) {
        context = c;
        this.album = new Album(photos);
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay(); 
        screenWidth = display.getWidth();
    }
	
	@Override
	public int getCount() {
		
		int cnt = album.getPhotosCount();
		return cnt;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {  
        	// if it's not recycled, initialize some attributes
            imageView = new SquaredImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(R.drawable.gray_bg);
            
        } else {
            imageView = (ImageView) convertView;
        }
        final int positionStored = position;
        Picasso.with(context).cancelRequest(imageView);
        if(position  < album.getPhotosCount()) {
		        com.aod.clubapp.communicaton.datamodel.Photo pha[] = album.getPhotos();
		        com.aod.clubapp.communicaton.datamodel.Photo ph = pha[position];
		        imageView.setTag(Long.valueOf(ph.getId()));
		        if((position % 2) == 0){
		        	Picasso.with(context)
		        	.load(ph.getImageThumbUrl())
		        	.noFade()
					.transform(new Transformation() {						
						@Override
						public Bitmap transform(Bitmap source) {					
							Bitmap result;
					    	result = Bitmap.createBitmap(source.getWidth(), source.getHeight(),  Bitmap.Config.ARGB_8888);
					    	result.eraseColor(Color.WHITE);
					        Canvas canvas = new Canvas(result);				        
					        int padding = 1;
					        canvas.drawBitmap(source, 
					        		new Rect(0, 0, source.getWidth(), source.getHeight()),
					        		(positionStored > 1) ?
					        				new Rect(0, 0, source.getWidth() - padding, source.getHeight() -padding) :		
					        				new Rect(0, padding, source.getWidth() - padding, source.getHeight() -padding),					        		
					        		null);
					    	source.recycle();
						    return result;
						}
						
						@Override
						public String key() {
							return "loadThumbWithWhiteBorder_left()";				
						}
					})        	
		        	
		        	.into(imageView);
	        } else {
	        	Picasso.with(context)
	        	.load(ph.getImageThumbUrl())
	        	.noFade()
				.transform(new Transformation() {						
					@Override
					public Bitmap transform(Bitmap source) {					
						Bitmap result;
				    	result = Bitmap.createBitmap(source.getWidth(), source.getHeight(),  Bitmap.Config.ARGB_8888);
				    	result.eraseColor(Color.WHITE);
				        Canvas canvas = new Canvas(result);				        
				        int padding = 1;
				        canvas.drawBitmap(source, 
				        		new Rect(0, 0, source.getWidth(), source.getHeight()),  
				        		(positionStored > 1)?
				        				new Rect(0, 0, source.getWidth(), source.getHeight() -padding) :
			        					new Rect(0, padding, source.getWidth(), source.getHeight() -padding),
				        		null);
				    	source.recycle();
					    return result;
					}
					
					@Override
					public String key() {
						return "loadThumbWithWhiteBorder_right()";
			
					}
				})        	
	        	
	        	.into(imageView);	        	
	        }
        }
        return imageView;		
	}

}
