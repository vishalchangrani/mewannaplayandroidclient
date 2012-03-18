package com.mewannaplay.mapoverlay;


import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;
import com.mewannaplay.MeWannaPlay;
import com.mewannaplay.R;
import com.mewannaplay.model.TennisCourt;

public class TennisCourtOverlayItemAdapter extends OverlayItem {

	final private TennisCourt tc;

	// Store these as global instances so we don't keep reloading every time
	private static Drawable privateCourt, publicSemiOccupiedCourt, publicNotOccupiedCourt,
				publicFullyOccupiedCourt;
		
	static 
	{
		
		privateCourt = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_yellow_locked);
		privateCourt.setBounds(privateCourt.getIntrinsicWidth() /- 2, privateCourt.getIntrinsicHeight() / -2,
				privateCourt.getIntrinsicWidth() / 2, privateCourt.getIntrinsicHeight() / 2);
		publicSemiOccupiedCourt = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_yellow_16);
		publicSemiOccupiedCourt.setBounds(publicSemiOccupiedCourt.getIntrinsicWidth() /- 2, publicSemiOccupiedCourt.getIntrinsicHeight() / -2,
				publicSemiOccupiedCourt.getIntrinsicWidth() / 2, publicSemiOccupiedCourt.getIntrinsicHeight() / 2);
		publicNotOccupiedCourt = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_green_18);
		publicNotOccupiedCourt.setBounds(publicNotOccupiedCourt.getIntrinsicWidth() /- 2, publicNotOccupiedCourt.getIntrinsicHeight() / -2,
				publicNotOccupiedCourt.getIntrinsicWidth() / 2, publicNotOccupiedCourt.getIntrinsicHeight() / 2);
		publicFullyOccupiedCourt = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_red_15);
		publicFullyOccupiedCourt.setBounds(publicFullyOccupiedCourt.getIntrinsicWidth() /- 2, publicFullyOccupiedCourt.getIntrinsicHeight() / -2,
				publicFullyOccupiedCourt.getIntrinsicWidth() / 2, publicFullyOccupiedCourt.getIntrinsicHeight() / 2);
				
	}
	public TennisCourtOverlayItemAdapter(TennisCourt tennisCourt) {
		super(tennisCourt.getGeoPoint(), tennisCourt.getName(), "Messages :"
				+ tennisCourt.getMessageCount() + " Occupied: "
				+ tennisCourt.getOccupied() + " of "
				+ tennisCourt.getSubcourts());
		tc = tennisCourt;	
	}
	
	@Override
	public Drawable getMarker(int stateBitset) {

		if (tc.isPrivate())
			return privateCourt;
		else {
			if (tc.isNotOccupied())
				return publicNotOccupiedCourt;
			else if (tc.isPartiallyOccupied())
				return publicSemiOccupiedCourt;
			else
				// if (tc.isOccupied())
				return publicFullyOccupiedCourt;
		}

	}
	
	
	public TennisCourt getTennisCourt()
	{
		return tc;
	}

}
