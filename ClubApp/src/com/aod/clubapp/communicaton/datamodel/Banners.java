package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class Banners {
	@SerializedName("albums")
	Banner banners[];

	public Banners(Banner[] bann) {
		this.banners = bann;
	}

	public Banner[] getBanners() {
		return banners;
	}

}
