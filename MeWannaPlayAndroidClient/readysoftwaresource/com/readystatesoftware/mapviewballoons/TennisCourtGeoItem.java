package com.readystatesoftware.mapviewballoons;

import com.erdao.android.mapviewutil.GeoItem;
import com.mewannaplay.model.TennisCourt;

public class TennisCourtGeoItem extends GeoItem {

	private TennisCourt tc;
	
	public TennisCourt getTc() {
		return tc;
	}

	public void setTc(TennisCourt tc) {
		this.tc = tc;
	}

	public TennisCourtGeoItem(GeoItem src) {
		super(src);
		// TODO Auto-generated constructor stub
	}
	
	public TennisCourtGeoItem(TennisCourt tc)
	{
		super(tc.getId(), (int) (tc.getLatitude()*1E6), (int) (tc.getLongitude()* 1E6));
		this.tc = tc;
	}



}

