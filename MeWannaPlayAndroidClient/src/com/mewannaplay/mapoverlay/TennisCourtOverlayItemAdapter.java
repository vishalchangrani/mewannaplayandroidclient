package com.mewannaplay.mapoverlay;


import java.util.Comparator;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.mewannaplay.Constants;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.MeWannaPlay;
import com.mewannaplay.R;
import com.mewannaplay.model.TennisCourt;

public class TennisCourtOverlayItemAdapter extends OverlayItem {

	private static final String TAG = "TennisCourtOverlayItemAdapter";

	final private TennisCourt tc;

	// Store these as global instances so we don't keep reloading every time
	private static Drawable privateCourt, publicSemiOccupiedCourt, publicNotOccupiedCourt,
				publicFullyOccupiedCourt;
	private static Drawable privateCourtInProximity, publicSemiOccupiedCourtInProximity, publicNotOccupiedCourtInProximity,
	publicFullyOccupiedCourtInProximity;
		
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
		privateCourtInProximity = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_red_15);
		privateCourtInProximity.setBounds(		privateCourtInProximity.getIntrinsicWidth() /- 2, 		privateCourtInProximity.getIntrinsicHeight() / -2,
				privateCourtInProximity.getIntrinsicWidth() / 2, 		privateCourtInProximity.getIntrinsicHeight() / 2);
		publicFullyOccupiedCourtInProximity = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_red_15);
		publicFullyOccupiedCourtInProximity.setBounds(publicFullyOccupiedCourtInProximity.getIntrinsicWidth() /- 2, publicFullyOccupiedCourtInProximity.getIntrinsicHeight() / -2,
				publicFullyOccupiedCourtInProximity.getIntrinsicWidth() / 2, publicFullyOccupiedCourtInProximity.getIntrinsicHeight() / 2);
		publicSemiOccupiedCourtInProximity = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_red_15);
		publicSemiOccupiedCourtInProximity.setBounds(publicSemiOccupiedCourtInProximity.getIntrinsicWidth() /- 2, publicSemiOccupiedCourtInProximity.getIntrinsicHeight() / -2,
				publicSemiOccupiedCourtInProximity.getIntrinsicWidth() / 2, publicSemiOccupiedCourtInProximity.getIntrinsicHeight() / 2);
		publicNotOccupiedCourtInProximity = MeWannaPlay.getAppContext().getResources().getDrawable(R.drawable.tennisball_red_15);
		publicNotOccupiedCourtInProximity.setBounds(publicNotOccupiedCourtInProximity.getIntrinsicWidth() /- 2, publicNotOccupiedCourtInProximity.getIntrinsicHeight() / -2,
				publicNotOccupiedCourtInProximity.getIntrinsicWidth() / 2, publicNotOccupiedCourtInProximity.getIntrinsicHeight() / 2);
		
				
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
		Location currentLocation = MapViewActivity.mapViewActivity.getMyCurrentLocation();
		Location thisCourtsLocation = this.getLocation();
		boolean isInProximity = currentLocation!= null  ? currentLocation.distanceTo(thisCourtsLocation) <= Constants.PROXIMITY : false;
		if (isInProximity)
			Log.d(TAG, currentLocation!= null  ? currentLocation.distanceTo(thisCourtsLocation) + "meters ": "current locatio null");
		if (tc.isPrivate())
			return isInProximity ? privateCourtInProximity : privateCourt;
		else {
			if (tc.isNotOccupied())
				return isInProximity ? publicNotOccupiedCourtInProximity : publicNotOccupiedCourt;
			else if (tc.isPartiallyOccupied())
				return isInProximity ? publicSemiOccupiedCourtInProximity : publicSemiOccupiedCourt;
			else
				// if (tc.isOccupied())
				return isInProximity ? publicFullyOccupiedCourtInProximity :  publicFullyOccupiedCourt;
		}

	}
	
	
	public TennisCourt getTennisCourt()
	{
		return tc;
	}
	
	public Location getLocation()
	{
		Location location = new Location("");
		GeoPoint geoPoint = tc.getGeoPoint();
		float latitude = geoPoint.getLatitudeE6() / 1E6F;
		float longitude = geoPoint.getLongitudeE6() / 1E6F;

		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;

	}

	public static class TennisCourtComparator implements Comparator<TennisCourtOverlayItemAdapter>
	{

		@Override
		public int compare(TennisCourtOverlayItemAdapter arg0,
				TennisCourtOverlayItemAdapter arg1) {
		
			GeoPoint gp1 = arg0.getPoint();
			GeoPoint gp2 = arg1.getPoint();
			 if (gp1.getLongitudeE6() > gp2.getLongitudeE6()) {
			      return 1;
			    } else if (gp1.getLongitudeE6() < gp2.getLongitudeE6()) {
			      return -1;
			    } else if (gp1.getLatitudeE6() > gp2.getLatitudeE6()) {
			      return 1;
			    } else if (gp1.getLatitudeE6() < gp2.getLatitudeE6()) {
			      return -1;
			    }
			    return 0;
		}
		
	}
	
	 
	
}
