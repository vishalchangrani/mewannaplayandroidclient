package com.mewannaplay;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.mapoverlay.MyItemizedOverlay;
import com.mewannaplay.mapoverlay.TennisCourtOverlayItemAdapter;
import com.mewannaplay.model.City;
import com.mewannaplay.model.TennisCourt;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.syncadapter.SyncAdapter;

public class MapViewActivity extends MapActivity {

	private static final String TAG = "MapViewActivity";
//	private LocationManager myLocationManager;
	private MyLocationOverlay myLocationOverlay;
	private static Account annonymousAccount;
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	private BroadcastReceiver syncFinishedReceiverForCourtDetails;
	private MyItemizedOverlay myItemizedOverlay;
	private static City currentCity; //if null means currentLocation selected
	public static MapViewActivity mapViewActivity;
	
	
	static final int DIALOG_STATE_CITY_CHOICE = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapViewActivity = this;
		setContentView(R.layout.mapviewlayout);
		
		// Restore UI state from the savedInstanceState.
		  if (savedInstanceState != null && savedInstanceState.containsKey("current_city"))
		  {
			  setCurrentCity((City) savedInstanceState.getSerializable("current_city"));
			  ((TextView) (MapViewActivity.mapViewActivity.findViewById(R.id.dropdown_city))).setText(currentCity.getName()+","+currentCity.getAbbreviation());				  
		  }
	
		
	 	progressDialog = ProgressDialog.show(MapViewActivity.this, "", 
                "Fetching courts...", true);
    	progressDialog.show();
    	
		// Request first sync..
    	 registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED_ACTION));
		ContentResolver.requestSync(getAccount(this),
				ProviderContract.AUTHORITY, SyncAdapter.getAllCourtsBundle());
	

	
		initMap();
		
		TextView dropDownCity = (TextView) findViewById(R.id.dropdown_city);
		dropDownCity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MapViewActivity.this.showDialog(DIALOG_STATE_CITY_CHOICE);
				
			}

		});


	}


	private final void initMap()
	{
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		myItemizedOverlay = new MyItemizedOverlay(getResources().getDrawable(R.drawable.tennisball_yellow_16), mapView);
		mapView.getOverlays().add(myItemizedOverlay);
		myLocationOverlay = new MyMyLocationOverlay(this,
				mapView);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.runOnFirstFix(new Runnable() {
		    public void run() {
		    	if (currentCity == null) //user has not selected any city
		    		centerLocation(myLocationOverlay.getMyLocation());
		    }
		});
		mapView.invalidate();
		mapView.getController().setZoom(13);
		
		
	}
	
	   public void onPause() {
	        super.onPause();
	        Log.i(TAG,"Removing GPS update requests to save power");
	      //  myLocationOverlay.disableCompass();
	        myLocationOverlay.disableMyLocation();
	        MapView mapView = (MapView) findViewById(R.id.mapview);
	        mapView.getOverlays().remove(myLocationOverlay);
	        if (syncFinishedReceiverForCourtDetails != null)
	        {
	        	unregisterReceiver(syncFinishedReceiverForCourtDetails);
	        }
	        	
	        syncFinishedReceiverForCourtDetails = null;
	    }
	    
	    public void onResume(){
	        super.onResume();
	        Log.i(TAG,"Resuming GPS update requests");	   
	       // myLocationOverlay.enableCompass();
	       if (!myLocationOverlay.enableMyLocation())
	       {
	    		Toast.makeText(this, "location unavialable", Toast.LENGTH_LONG).show();
	       }
	       MapView mapView = (MapView) findViewById(R.id.mapview);
	        mapView.getOverlays().add(myLocationOverlay);
	       
	    }
	    
	    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

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
	
	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Log.d(TAG, "Sync finished, should refresh nao!!");
	        
	        if (intent.getExtras().getInt(SyncAdapter.OPERATION) != SyncAdapter.GET_ALL_COURTS)
	        	return;
	        
	        unregisterReceiver(this);
	        
	        if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR))
	        {
	        	progressDialog.dismiss();
	        	AlertDialog.Builder builder = new AlertDialog.Builder(MapViewActivity.this);
	        	builder.setMessage("Error while fetching courts")
	        	       .setCancelable(false)
	        	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   MapViewActivity.this.finish();
	        	           }
	        	       });
	        	      
	        	alert = builder.create();
	        	alert.show();
	        }
	        else
	        {
	        	final MapView mapView = (MapView) findViewById(R.id.mapview);
				Runnable waitForMapTimeTask = new Runnable() {
					public void run() {
						if (mapView.getLatitudeSpan() == 0
								|| mapView.getLongitudeSpan() == 360000000) {
							mapView.postDelayed(this, 100);
						} else {
							//redrawMarkers();
							Log.d(TAG," Adding all tenniscourts to overlay");
							progressDialog.setMessage("Parsing data...");
							getAllTennisCourts();
							progressDialog.dismiss();
							//MyItemizedOverlay.setMapMoving(true);
							//Approach 2 - optimize at sql level - sluggish
							//mapLocationOverlay.getTennisCourtsInView3(mapView);
							mapView.invalidate(); //causes draw to be invoked which will do the magic
						}
					}
				};
				mapView.postDelayed(waitForMapTimeTask, 100);
	        }
	    }
	};

	
	 public void getTennisCourtDetails(int id)
		{
			
			progressDialog = ProgressDialog.show(this, "", 
	                "Fetching court details...", true);
		    Log.d(TAG, " --> Requesting sync for "+id);
		    syncFinishedReceiverForCourtDetails = new SyncFinishedReceiverForCourtDetails(id);
			registerReceiver(syncFinishedReceiverForCourtDetails, new IntentFilter(SyncAdapter.SYNC_FINISHED_ACTION));
			ContentResolver.requestSync(MapViewActivity.getAccount(this),
					ProviderContract.AUTHORITY, SyncAdapter.getAllCourtsDetailBundle(id));
		}
	 
		private final class SyncFinishedReceiverForCourtDetails extends BroadcastReceiver {

			final int courtId;
			
			public SyncFinishedReceiverForCourtDetails(int courtId)
			{
				this.courtId = courtId;
			}
			
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        Log.d(TAG, "Sync finished, should refresh nao!!");
		        unregisterReceiver(this);
		        syncFinishedReceiverForCourtDetails = null;
		        progressDialog.dismiss();
		        
		        if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR))
		        {
		        	AlertDialog.Builder builder = new AlertDialog.Builder(MapViewActivity.this);
		        	builder.setMessage("Error while fetching court details")
		        	       .setCancelable(false)
		        	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	  
		        	           }
		        	       });
		        	      
		        	alert = builder.create();
		        	alert.show();
		        }
		        else
		        {
		        	startTennisCourtDetailActivity(courtId);
		        }
		    }
		};

		
		private void startTennisCourtDetailActivity(int id) {			
			Intent intentForTennisCourtDetails = new Intent(this, CourtDetailsActivity.class);
			Bundle extras = new Bundle(); 
			extras.putInt(SyncAdapter.COURT_ID,id);
			intentForTennisCourtDetails.putExtras(extras);
			startActivity(intentForTennisCourtDetails);//fire it up baby		
		}
		
		private void getAllTennisCourts() {
	
			Cursor cursor = this
					.getContentResolver()
					.query(ProviderContract.TennisCourts.CONTENT_URI,
							null,
							null,
							null, null);
			try
			{
			List<TennisCourtOverlayItemAdapter> newListOfOverlays = new ArrayList<TennisCourtOverlayItemAdapter>();
			if (cursor.moveToFirst()) {
				while (cursor.isAfterLast() == false) {
					//Log.d(TAG, cursor.getString(cursor.getColumnIndex("name")));

					double latitude = cursor.getDouble(cursor
							.getColumnIndex("latitude"));
					double longitude = cursor.getDouble(cursor
							.getColumnIndex("longitude"));
//					Log.d(TAG, " Latitude: " + latitude + " Longitude: "
//							+ longitude);

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
					newListOfOverlays.add(new TennisCourtOverlayItemAdapter(tennisCourt));
					cursor.moveToNext();
				}
				myItemizedOverlay.addOverlays(newListOfOverlays);
				Log.d(TAG, "total courts added = "+myItemizedOverlay.size());
			} else
				Log.e(TAG, "cursor for tennis courts found to be empty");
			}
			finally
			{
			cursor.close();
			}
		}
		
		@Override
		protected Dialog onCreateDialog(int id) {
			Dialog dialog = null;
		    switch(id) {
		    case DIALOG_STATE_CITY_CHOICE:
		    	dialog = new StateCityChoiceDialog(this);
		    	
		    	break;
		    
		    
		    }
		    return dialog;
		}

		public City getCurrentCity() {
			return currentCity;
		}


		public void setCurrentCity(City currentCity) {
			MapViewActivity.currentCity = currentCity;
			MapView mapView = (MapView)findViewById(R.id.mapview);
			if (currentCity != null)
			{
				
				mapView.getController().animateTo(new GeoPoint((int)(currentCity.getLatitude()*1e6),(int)(currentCity.getLongitude()* 1e6)));
			}
			else
			{
				GeoPoint lastLocation = myLocationOverlay.getMyLocation();
				if (lastLocation != null)
					mapView.getController().animateTo(lastLocation);
			}
		}
		
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) 
 {
		super.onSaveInstanceState(savedInstanceState);
		// Store UI state to the savedInstanceState.
		// This bundle will be passed to onCreate on next call.

		if (currentCity != null) {
			savedInstanceState.putSerializable("current_city", currentCity);
		}
	}
		
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.map_activity_menu, menu);
		    return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		        case R.id.logout:
		            RestClient.logout();
		            this.finish();
		            return true;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
		
	private class MyMyLocationOverlay extends MyLocationOverlay
	{

		public MyMyLocationOverlay(Context context, MapView mapView) {
			super(context, mapView);
		}
		
		@Override
		public synchronized void onLocationChanged(Location location) {
			
			super.onLocationChanged(location);
			if (currentCity == null)
				centerLocation(this.getMyLocation());
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			super.onProviderDisabled(provider);
			if (!this.isMyLocationEnabled())
			{
				Toast t = new Toast(MapViewActivity.this);
				t.setText("location unavialable");
				t.show();
				
			}
		}
	}
	
	public Location getMyCurrentLocation()
	{
		return myLocationOverlay.getLastFix();
	}
}
