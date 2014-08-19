package com.aod.clubapp;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewFragment extends Fragment {
	private static final String TAG = "ImageViewFragment"; 
	private static final String KEY_CONTENT = "ImageViewFragment:Content";
	
	private String imageUrl;
	private ImageView photoView;
	
	
    public static ImageViewFragment newInstance(String content) {
    	ImageViewFragment fragment = new ImageViewFragment();
    	Log.d(TAG, "ImageViewFragment::newInstance " + content);
    	fragment.setImageUrl(content);
        return fragment;
    }
	
	public void setImageUrl(String url) {
		imageUrl = url;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(photoView != null  && !TextUtils.isEmpty(imageUrl)) {
			//Picasso.with(getActivity()).load(imageUrl).into(photoView);
			AuraBaseActivity.loadFullScreenImage(imageUrl, photoView, getActivity());
//			AuraBaseActivity.loadFullScreenImage(imageUrl, 
//					photoView,photoView.getWidth(), 
//					photoView.getHeight(), 
//					getActivity());
			
		} else {
			Log.e(TAG, "something wron, will now load image for " + imageUrl);
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
        	setImageUrl(savedInstanceState.getString(KEY_CONTENT));
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, imageUrl);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View root = inflater.inflate(R.layout.image_view_fragment, container, false);
    	photoView = (ImageView) root.findViewById(R.id.photo_view);
    	
    	root.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(getActivity() != null) {
					AuraBaseActivityFullscreen parnt = (AuraBaseActivityFullscreen )getActivity();
					parnt.toggleFullScreen();
				}
				
			}
		});
    	
        return root;
    }
}
