package com.aod.clubapp.communicaton.datamodel;
import com.google.gson.annotations.SerializedName;

/**
 * query "http://fixapp-clubs.herokuapp.com/api/v1/albums?auth_token=dda538b8ae267a62bb432e0b6e7bf4ee&page=1"
 * will return this object
 *
 * @author Anatoliy Odukha <aodukha@gmail.com>
 */
public class Albums {
	
	@SerializedName("albums")
	Album albums[];

	public Albums(Album[] albums) {
		this.albums = albums;
	}

	public Album[] getAlbums() {
		return albums;
	}
	
}
