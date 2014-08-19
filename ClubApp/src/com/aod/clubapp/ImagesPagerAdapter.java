package com.aod.clubapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import com.aod.clubapp.communicaton.datamodel.SimpleImage;
import com.viewpagerindicator.IconPagerAdapter;

public class ImagesPagerAdapter extends FragmentPagerAdapter  implements IconPagerAdapter {
	private SimpleImage[] images; 
	public ImagesPagerAdapter(FragmentManager fm, SimpleImage[] images) {
		super(fm);
		this.images = images;
	}

	@Override
	public Fragment getItem(int position) {		
		String url = "";
		if(position < images.length) {
			SimpleImage img = images[position];
			url = img.getImageMediumUrl();
			if(TextUtils.isEmpty(url))
				url = img.getImageThumbUrl();
		}
		ImageViewFragment result = ImageViewFragment.newInstance(url);
		return result;
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public int getIconResId(int index) {
		return R.drawable.a_icon;
	}

}
