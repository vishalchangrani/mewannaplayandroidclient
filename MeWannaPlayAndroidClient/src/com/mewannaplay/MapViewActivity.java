package com.mewannaplay;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.mewannaplay.mapoverlay.MapLocationOverlay;
import com.mewannaplay.model.TennisCourt;
import com.mewannaplay.providers.ProviderContract;

public class MapViewActivity extends MapActivity {

	private static final String TAG = "MapViewActivity";
//	private LocationManager myLocationManager;
	private MyLocationOverlay myLocationOverlay;
	private MapLocationOverlay mapLocationOverlay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AccountManager mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		Account annonymousAccount = null;
		if (accounts.length > 0)
			annonymousAccount = accounts[0];
		else
			Log.e(TAG, "Anonymous account not found"); // TODO figure what to do
														// then
		
		// Request first sync..
		ContentResolver.requestSync(annonymousAccount,
				ProviderContract.AUTHORITY, new Bundle());
		getContentResolver().registerContentObserver(
				ProviderContract.AUTHORITY_URI, true,
				new TennisCourtContentObserver());

		setContentView(R.layout.mapviewlayout);
		initMap();


	/*	myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, new MyLocationListener());
		
		 //Get the current location in start-up
		Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null)
		{
		  GeoPoint initGeoPoint = new GeoPoint(
		   (int)(myLocationManager.getLastKnownLocation(
		    LocationManager.GPS_PROVIDER)
		    .getLatitude()*1000000),
		   (int)(myLocationManager.getLastKnownLocation(
		    LocationManager.GPS_PROVIDER)
		    .getLongitude()*1000000));
		  centerLocation(initGeoPoint);
		}
*/
		/*
		 * ContentResolver cr = getContentResolver();
		 * cr.delete(TennisCourts.CONTENT_URI, null, null); Cursor cursor =
		 * cr.query(TennisCourts.CONTENT_URI, null, null, null, null);
		 * cursor.close(); ContentValues[] cotentValues = new ContentValues[2];
		 * ContentValues cotentValues1 = new ContentValues();
		 * cotentValues1.put("id",1); cotentValues1.put("tennis_latitude","80");
		 * cotentValues1.put("tennis_longitude","0");
		 * cotentValues1.put("tennis_subcourts",1);
		 * cotentValues1.put("occupied",1);
		 * cotentValues1.put("tennis_facility_type","type A");
		 * cotentValues1.put("tennis_name","tennis court 1");
		 * cotentValues1.put("message_count",1); cotentValues[0] =
		 * cotentValues1; ContentValues cotentValues2 = new ContentValues();
		 * cotentValues2.put("id",2); cotentValues2.put("tennis_latitude","80");
		 * cotentValues2.put("tennis_longitude","0");
		 * cotentValues2.put("tennis_subcourts",1);
		 * cotentValues2.put("occupied",1);
		 * cotentValues2.put("tennis_facility_type","type B");
		 * cotentValues2.put("tennis_name","tennis court 2");
		 * cotentValues2.put("message_count",1); cotentValues[1] =
		 * cotentValues2; int count = cr.bulkInsert(TennisCourts.CONTENT_URI,
		 * cotentValues);
		 */

	}

	private final void initMap()
	{
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapLocationOverlay = new MapLocationOverlay(this);
		mapView.getOverlays().add(mapLocationOverlay);
		myLocationOverlay = new MyLocationOverlay(this,
				mapView);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.runOnFirstFix(new Runnable() {
		    public void run() {
		        centerLocation(myLocationOverlay.getMyLocation());
		    }
		});
		mapView.invalidate();
		mapView.getController().setZoom(13);
		
	}
	
	   public void onPause() {
	        super.onPause();
	        Log.i(TAG,"Removing GPS update requests to save power");
	        myLocationOverlay.disableCompass();
	        myLocationOverlay.disableMyLocation();
	    }
	    
	    public void onResume(){
	        super.onResume();
	        Log.i(TAG,"Resuming GPS update requests");	   
	        myLocationOverlay.enableCompass();
	        myLocationOverlay.enableMyLocation();
	    }
	    
	    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class TennisCourtContentObserver extends ContentObserver {

		public TennisCourtContentObserver() {
			super(null);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return false;
		}

		@Override
		public void onChange(boolean arg0) {
			Log.v(TAG, "Notification on TennisCourtContentObserver");
			super.onChange(arg0);
			
			final MapView mapView = (MapView) findViewById(R.id.mapview);
			Runnable waitForMapTimeTask = new Runnable() {
				public void run() {
					if (mapView.getLatitudeSpan() == 0
							|| mapView.getLongitudeSpan() == 360000000) {
						mapView.postDelayed(this, 100);
					} else {
						redrawMarkers(); // draw here
					}
				}
			};
			mapView.postDelayed(waitForMapTimeTask, 100);


			/*
			 * String protocol = cur.getString(cur.getColumnIndex("protocol"));
			 * if (protocol == null) { Log.d("SMS", "SMS SEND"); int threadId =
			 * cur.getInt(cur.getColumnIndex("thread_id")); Log.d("SMS",
			 * "SMS SEND ID = " + threadId); getContentResolver().delete(
			 * Uri.parse("content://sms/conversations/" + threadId), null,
			 * null);
			 * 
			 * } else { Log.d("SMS", "SMS RECIEVE"); int threadIdIn =
			 * cur.getInt(cur.getColumnIndex("thread_id"));
			 * getContentResolver().delete(
			 * Uri.parse("content://sms/conversations/" + threadIdIn), null,
			 * null); }
			 */

		}
	}
	private final void redrawMarkers()
	{
		this.mapLocationOverlay.clearAllMapLocations();
		Cursor cursor = getContentResolver().query(
				ProviderContract.TennisCourts.CONTENT_URI, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				Log.d(TAG,cursor.getString(cursor
						.getColumnIndex("name")));
				double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
				double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
				Log.d(TAG, " Latitude: "+latitude+" Longitude: "+longitude);
				Log.d(TAG,getMapBounds().flattenToString());
				Log.d(TAG,""+ getMapBounds().contains((int)(longitude * 1e6),(int)(latitude * 1e6)));
				if (getMapBounds().contains((int)(longitude * 1e6),(int)(latitude * 1e6)))
				{
				 int id = cursor.getInt(cursor.getColumnIndex("_id"));
				 int subcourts = cursor.getInt(cursor.getColumnIndex("subcourts"));
				 int occupied = cursor.getInt(cursor.getColumnIndex("occupied"));
				 int messageCount = cursor.getInt(cursor.getColumnIndex("message_count"));
				 String name = cursor.getString(cursor.getColumnIndex("name"));
				 String facilityType = cursor.getString(cursor.getColumnIndex("facility_type"));
				 TennisCourt tennisCourt = new TennisCourt(id, latitude, longitude, subcourts, occupied, facilityType, name, messageCount);
				 this.mapLocationOverlay.addMapLocation(tennisCourt);
				}
				cursor.moveToNext();
			}
		} else
			Log.e(TAG, "cursor for tennis courts found to be empty");
		cursor.close();
		final MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.invalidate();
	}
	
	public Rect getMapBounds() {
		final MapView mapView = (MapView) findViewById(R.id.mapview);
		return new Rect(
		mapView.getMapCenter().getLongitudeE6() - mapView.getLongitudeSpan()/2,
		mapView.getMapCenter().getLatitudeE6() - mapView.getLatitudeSpan()/2,
		mapView.getMapCenter().getLongitudeE6() + mapView.getLongitudeSpan()/2,
		mapView.getMapCenter().getLatitudeE6() + mapView.getLatitudeSpan()/2
		);
		}


/*	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location argLocation) {
			// TODO Auto-generated method stub
			GeoPoint myGeoPoint = new GeoPoint(
					(int) (argLocation.getLatitude() * 1000000),
					(int) (argLocation.getLongitude() * 1000000));

			centerLocation(myGeoPoint);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}*/

	private final void centerLocation(GeoPoint geoPoint) {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.getController().animateTo(geoPoint);
	}

}
