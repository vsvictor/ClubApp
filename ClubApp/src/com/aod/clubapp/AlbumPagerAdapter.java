package com.aod.clubapp;

import com.aod.clubapp.communicaton.datamodel.Album;
import com.aod.clubapp.communicaton.datamodel.Photo;
import com.viewpagerindicator.IconPagerAdapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AlbumPagerAdapter extends FragmentPagerAdapter  implements IconPagerAdapter {
	private Album album; 
	public AlbumPagerAdapter(FragmentManager fm, Album album) {
		super(fm);
		this.album = album;
	}

	@Override
	public Fragment getItem(int position) {
		Photo photos[] = album.getPhotos();
		String url = "";
		if(position < photos.length) {
			Photo pht = photos[position];
			if(pht != null)
				url = pht.getImageMediumUrl();
		}
		ImageViewFragment result = ImageViewFragment.newInstance(url);
		return result;
	}

	@Override
	public int getCount() {
		return album.getPhotosCount();
	}

	@Override
	public int getIconResId(int index) {
		return R.drawable.a_icon;
	}

}
