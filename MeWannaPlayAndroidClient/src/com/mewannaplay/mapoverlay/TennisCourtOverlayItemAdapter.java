package com.mewannaplay.mapoverlay;

import java.util.Comparator;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.mewannaplay.Constants;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.MeWannaPlay;
import com.mewannaplay.R;
import com.mewannaplay.model.TennisCourt;

public class TennisCourtOverlayItemAdapter extends OverlayItem {

		private static final String TAG = "TennisCourtOverlayItemAdapter";
		private int id;
        final private TennisCourt tc;
        public static final String SNIPPET = "Messages: %d Occupied: %d of %d";

        // Store these as global instances so we don't keep reloading every time

        public void setId(int id)
        {
        	this.id = id;
        }
        public int getId() {
			return id;
		}

		private static Drawable  publicSemiOccupiedCourt,
                        publicNotOccupiedCourt, publicFullyOccupiedCourt,privateSemiOccupiedCourt,
                        privateNotOccupiedCourt, privateFullyOccupiedCourt;
		
        private static Drawable privateCourtInProximity,publicCourtInProximity;
   
        private static Drawable flagOccupied;

        static {

                
                publicSemiOccupiedCourt = MeWannaPlay.getAppContext().getResources()
                                .getDrawable(R.drawable.partiallyoccu);
                publicSemiOccupiedCourt.setBounds(
                                publicSemiOccupiedCourt.getIntrinsicWidth() / -2,
                                publicSemiOccupiedCourt.getIntrinsicHeight() / -2,
                                publicSemiOccupiedCourt.getIntrinsicWidth() / 2,
                                publicSemiOccupiedCourt.getIntrinsicHeight() / 2);
                publicNotOccupiedCourt = MeWannaPlay.getAppContext().getResources()
                                .getDrawable(R.drawable.tenniscourtavailable);
                publicNotOccupiedCourt.setBounds(
                                publicNotOccupiedCourt.getIntrinsicWidth() / -2,
                                publicNotOccupiedCourt.getIntrinsicHeight() / -2,
                                publicNotOccupiedCourt.getIntrinsicWidth() / 2,
                                publicNotOccupiedCourt.getIntrinsicHeight() / 2);
                publicFullyOccupiedCourt = MeWannaPlay.getAppContext().getResources()
                                .getDrawable(R.drawable.allcourtsoccu);
                publicFullyOccupiedCourt.setBounds(
                                publicFullyOccupiedCourt.getIntrinsicWidth() / -2,
                                publicFullyOccupiedCourt.getIntrinsicHeight() / -2,
                                publicFullyOccupiedCourt.getIntrinsicWidth() / 2,
                                publicFullyOccupiedCourt.getIntrinsicHeight() / 2);
                
                publicCourtInProximity = MeWannaPlay.getAppContext()
                        .getResources().getDrawable(R.drawable.proximitycourt);
                publicCourtInProximity.setBounds(
                		publicCourtInProximity.getIntrinsicWidth() / -2,
                		publicCourtInProximity.getIntrinsicHeight() / -2,
                		publicCourtInProximity.getIntrinsicWidth() / 2,
                		publicCourtInProximity.getIntrinsicHeight() / 2);
                
                flagOccupied = MeWannaPlay.getAppContext().getResources()
                                .getDrawable(R.drawable.flag);
                flagOccupied.setBounds(flagOccupied.getIntrinsicWidth() / -2,
                                flagOccupied.getIntrinsicHeight() / -2,
                                flagOccupied.getIntrinsicWidth() / 2,
                                flagOccupied.getIntrinsicHeight() / 2);
                
                
                //**for private court**//
                privateCourtInProximity = MeWannaPlay.getAppContext().getResources()
                        .getDrawable(R.drawable.pproximity);
                privateCourtInProximity.setBounds(
                        privateCourtInProximity.getIntrinsicWidth() / -2,
                        privateCourtInProximity.getIntrinsicHeight() / -2,
                        privateCourtInProximity.getIntrinsicWidth() / 2,
                        privateCourtInProximity.getIntrinsicHeight() / 2);
     
                privateSemiOccupiedCourt = MeWannaPlay.getAppContext().getResources()
                        .getDrawable(R.drawable.ppartialoccu);
        privateSemiOccupiedCourt.setBounds(
                        privateSemiOccupiedCourt.getIntrinsicWidth() / -2,
                        privateSemiOccupiedCourt.getIntrinsicHeight() / -2,
                        privateSemiOccupiedCourt.getIntrinsicWidth() / 2,
                        privateSemiOccupiedCourt.getIntrinsicHeight() / 2);
        
        privateNotOccupiedCourt = MeWannaPlay.getAppContext().getResources()
                .getDrawable(R.drawable.pavailable);
        privateNotOccupiedCourt.setBounds(
        		privateNotOccupiedCourt.getIntrinsicWidth() / -2,
        		privateNotOccupiedCourt.getIntrinsicHeight() / -2,
        		privateNotOccupiedCourt.getIntrinsicWidth() / 2,
        		privateNotOccupiedCourt.getIntrinsicHeight() / 2);
        
        privateFullyOccupiedCourt = MeWannaPlay.getAppContext().getResources()
                .getDrawable(R.drawable.pfullyoccu);
        privateFullyOccupiedCourt.setBounds(
        		privateFullyOccupiedCourt.getIntrinsicWidth() / -2,
        		privateFullyOccupiedCourt.getIntrinsicHeight() / -2,
        		privateFullyOccupiedCourt.getIntrinsicWidth() / 2,
        		privateFullyOccupiedCourt.getIntrinsicHeight() / 2);
   
        }

        public TennisCourtOverlayItemAdapter(TennisCourt tennisCourt) {
        		super(tennisCourt.getGeoPoint(), tennisCourt.getName(),String.format(SNIPPET, tennisCourt.getMessageCount(),tennisCourt.getOccupied(),tennisCourt.getSubcourts()));
                tc = tennisCourt;
                id = tc.getId();
        }

        @Override
        public String getSnippet() {        
        	return String.format(SNIPPET, tc.getMessageCount(),tc.getOccupied(),tc.getSubcourts());
        }
        
        
        
        public TennisCourtOverlayItemAdapter(int id)
        {
        	super(null,null,null);
        	this.id = id;
        	this.tc = null;
        }
        @Override
        public Drawable getMarker(int stateBitset) {
                Location currentLocation = MapViewActivity.mapViewActivity
                                .getMyCurrentLocation();
                Location thisCourtsLocation = this.getLocation();
                boolean isInProximity = currentLocation != null ? currentLocation
                                .distanceTo(thisCourtsLocation) <= Constants.PROXIMITY : false;
                
                
                // Select the overlay image for this court based on conditions
                if (tc.getId() == MapViewActivity.getCourtMarkedOccupied()) 
                        return flagOccupied;
                if (tc.isPrivate())
                {
                        
                	if (isInProximity)
                		return privateCourtInProximity;
                	else if (tc.isNotOccupied())
                        return privateNotOccupiedCourt;
                    else if (tc.isPartiallyOccupied())
                        return privateSemiOccupiedCourt;
                    else
                    	return privateFullyOccupiedCourt; 
                }       
                        
                else {
                		if (isInProximity)
                			return publicCourtInProximity;
                		else if (tc.isNotOccupied())
                                return publicNotOccupiedCourt;
                        else if (tc.isPartiallyOccupied())
                                return  publicSemiOccupiedCourt;
                        else
                        	return publicFullyOccupiedCourt;
                }

        }

        public TennisCourt getTennisCourt() {
                return tc;
        }

        public Location getLocation() {
                Location location = new Location("");
                GeoPoint geoPoint = tc.getGeoPoint();
                float latitude = geoPoint.getLatitudeE6() / 1E6F;
                float longitude = geoPoint.getLongitudeE6() / 1E6F;

                location.setLatitude(latitude);
                location.setLongitude(longitude);
                return location;

        }

        public static class TennisCourtComparator implements
                        Comparator<TennisCourtOverlayItemAdapter> {

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


        @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tc == null) ? 0 : Integer.valueOf(tc.getId()).hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TennisCourtOverlayItemAdapter other = (TennisCourtOverlayItemAdapter) obj;
		return (this.id == other.id);

	}

}