package com.mewannaplay;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.mewannaplay.mapoverlay.MapLocationOverlay;
import com.mewannaplay.mapoverlay.MyItemizedOverlay;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.TennisCourts;
import com.mewannaplay.syncadapter.SyncAdapter;

public class MapViewActivity extends MapActivity {

	private static final String TAG = "MapViewActivity";
//	private LocationManager myLocationManager;
	private MyLocationOverlay myLocationOverlay;
	private MapLocationOverlay mapLocationOverlay;
	private static Account annonymousAccount;
	private AccountManagerFuture<Bundle> amFuture;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	
		getContentResolver().registerContentObserver(
				TennisCourts.CONTENT_URI, true,
				new TennisCourtContentObserver());
		// Request first sync..
		ContentResolver.requestSync(getAccount(this),
				ProviderContract.AUTHORITY, SyncAdapter.getAllCourtsBundle());
	

		setContentView(R.layout.mapviewlayout);
		initMap();


	}


	private final void initMap()
	{
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		
		mapView.getOverlays().add(new MyItemizedOverlay(getResources().getDrawable(R.drawable.tennisball_yellow_16), mapView));
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
			getContentResolver().unregisterContentObserver(this);
			
			final MapView mapView = (MapView) findViewById(R.id.mapview);
			Runnable waitForMapTimeTask = new Runnable() {
				public void run() {
					if (mapView.getLatitudeSpan() == 0
							|| mapView.getLongitudeSpan() == 360000000) {
						mapView.postDelayed(this, 100);
					} else {
						//redrawMarkers();
						Log.d(TAG," Adding all tenniscourts to overlay");
						//Approach 2 - optimize at sql level - sluggish
						//mapLocationOverlay.getTennisCourtsInView3(mapView);
						mapView.invalidate(); //causes draw to be invoked which will do the magic
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

	public static Account getAccount(Context context)
 {
		if (annonymousAccount == null) {
			AccountManager mAccountManager = AccountManager.get(context);
			Account[] accounts = mAccountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			if (accounts.length > 0)
				annonymousAccount = accounts[0];
			else
				Log.e(TAG, "Anonymous account not found"); // TODO figure what
															// to do
		} // then
		return annonymousAccount;
	}
}
