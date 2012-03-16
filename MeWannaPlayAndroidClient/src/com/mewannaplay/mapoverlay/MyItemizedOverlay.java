/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.mewannaplay.mapoverlay;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.mewannaplay.CourtDetailsActivity;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.model.TennisCourt;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.syncadapter.SyncAdapter;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class MyItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<TennisCourtOverlayItemAdapter> m_overlays = new ArrayList<TennisCourtOverlayItemAdapter>();
	private final Context c;
	private final MapView mapView;
	private static final String TAG = "MyItemizedOverlay";
	private ProgressDialog progressDialog;
	
	 private static volatile GeoPoint lastLatLon = new GeoPoint(0, 0);
	 private static volatile GeoPoint currLatLon;
	 protected volatile static boolean isMapMoving = false;
	
	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
		this.mapView = mapView;
		populate();//fix for google bug
	}

	public void addOverlay(TennisCourtOverlayItemAdapter overlay) {
	    m_overlays.add(overlay);
	    setLastFocusedIndex(-1);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index) {
		int id = m_overlays.get(index).getTennisCourt().getId();
	    ((MapViewActivity)c).getTennisCourtDetails(id);
		return true;
	}

	

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		
	   Log.d(TAG, " onTouchEvent "+event.getAction()) ;
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
	        {
	            
	    	 	// Added to example to make more complete
				clear();
				isMapMoving = true;
	        }

		return false;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow)
			return;
		
		Log.d(TAG," Map found to be moving? "+isMapMoving);
		if (isMapMoving) {
		
			currLatLon = mapView.getProjection().fromPixels(0, 0);
			if (currLatLon.equals(lastLatLon)) {
				isMapMoving = false;
				Log.d(TAG, " Map stopped moving");
			} else {
				Log.d(TAG, " Map still moving "+lastLatLon+" "+currLatLon);
				lastLatLon = currLatLon;
				return;
			}
		}
		Log.d(TAG," ---------- in draw of overlay");
		getTennisCourtsInView(mapView);
		super.draw(canvas, mapView, shadow);
		
	}
	private void getTennisCourtsInView(MapView mapView) {
		//TODO: skip all this if mapview bounds havent changed
		clear();
		String topLeftX = Double.toString((mapView.getMapCenter()
				.getLongitudeE6() - mapView.getLongitudeSpan() / 2) / 1e6);
		String topLeftY = Double.toString((mapView.getMapCenter()
				.getLatitudeE6() - mapView.getLatitudeSpan() / 2) / 1e6);
		String bottomRightX = Double.toString((mapView.getMapCenter()
				.getLongitudeE6() + mapView.getLongitudeSpan() / 2) / 1e6);
		String bottomLeftY = Double.toString((mapView.getMapCenter()
				.getLatitudeE6() + mapView.getLatitudeSpan() / 2) / 1e6);

//		Log.d(TAG, " Left: " + topLeftX + "," + topLeftY + " Bottom: "
//				+ bottomRightX + "," + bottomLeftY);
		Cursor cursor = c
				.getContentResolver()
				.query(ProviderContract.TennisCourts.CONTENT_URI,
						null,
						" (longitude >= ? and longitude < ?) and (latitude >= ? and latitude < ?) ",
						new String[] { topLeftX, bottomRightX, topLeftY,
								bottomLeftY }, null);
		

		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				//Log.d(TAG, cursor.getString(cursor.getColumnIndex("name")));

				double latitude = cursor.getDouble(cursor
						.getColumnIndex("latitude"));
				double longitude = cursor.getDouble(cursor
						.getColumnIndex("longitude"));
//				Log.d(TAG, " Latitude: " + latitude + " Longitude: "
//						+ longitude);

				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				int subcourts = cursor.getInt(cursor
						.getColumnIndex("subcourts"));
				int occupied = cursor.getInt(cursor.getColumnIndex("occupied"));
				int messageCount = cursor.getInt(cursor
						.getColumnIndex("message_count"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String facilityType = cursor.getString(cursor
						.getColumnIndex("facility_type"));
				TennisCourt tennisCourt = new TennisCourt(id, latitude,
						longitude, subcourts, occupied, facilityType, name,
						messageCount);
				addOverlay(new TennisCourtOverlayItemAdapter(tennisCourt, this.c));

				cursor.moveToNext();
			}
			Log.d(TAG, "total courts added = "+m_overlays.size());
		} else
			Log.e(TAG, "cursor for tennis courts found to be empty");
		cursor.close();
	}
	
	public void clear() {
		m_overlays.clear();
			      //  mapView.removeAllViews();
			        // Workaround for another issue with this class:
			        // <a href="http://groups.google.com/group/android-developers/browse_thread/thread/38b11314e34714c3">http://groups.google.com/group/android-developers/browse_thread/thread/38b11314e34714c3</a>
			    //    setLastFocusedIndex(-1);
			      //  populate();
			    }
}
