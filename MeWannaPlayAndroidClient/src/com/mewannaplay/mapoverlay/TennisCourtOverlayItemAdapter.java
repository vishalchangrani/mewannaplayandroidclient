package com.mewannaplay.mapoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;
import com.mewannaplay.R;
import com.mewannaplay.model.TennisCourt;

public class TennisCourtOverlayItemAdapter extends OverlayItem {

	final private TennisCourt tc;
	final private Context c;
	// Store these as global instances so we don't keep reloading every time
	private Bitmap publicSemiOccupiedCourt, publicNotOccupiedCourt,
				publicFullyOccupiedCourt, privateCourt;
		
	public TennisCourtOverlayItemAdapter(TennisCourt tennisCourt, Context c) {
		super(tennisCourt.getGeoPoint(), tennisCourt.getName(), "Messages :"
				+ tennisCourt.getMessageCount() + " Occupied: "
				+ tennisCourt.getOccupied() + " of "
				+ tennisCourt.getSubcourts());
		tc = tennisCourt;	
		this.c = c;
	}
	
	@Override
	public Drawable getMarker(int stateBitset) {
		Drawable d = null;

		if (tc.isPrivate())
			d = c.getResources().getDrawable(
					R.drawable.tennisball_yellow_locked);
		else {
			if (tc.isNotOccupied())
				d = c.getResources()
						.getDrawable(R.drawable.tennisball_green_18);
			else if (tc.isPartiallyOccupied())
				d = c.getResources().getDrawable(
						R.drawable.tennisball_yellow_16);
			else if (tc.isOccupied())
				d = c.getResources().getDrawable(R.drawable.tennisball_red_15);
		}
		if (d != null)
		//	d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			d.setBounds(d.getIntrinsicWidth() /- 2, d.getIntrinsicHeight() / -2,
	                 d.getIntrinsicWidth() / 2, d.getIntrinsicHeight() / 2);
		return d;
	}
	
	
	public TennisCourt getTennisCourt()
	{
		return tc;
	}

}
