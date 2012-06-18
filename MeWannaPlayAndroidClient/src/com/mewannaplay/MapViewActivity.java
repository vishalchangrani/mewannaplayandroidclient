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
import android.widget.Button;
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
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.syncadapter.SyncAdapter;

public class MapViewActivity extends MapActivity {

	private static final String TAG = "MapViewActivity";
	private MyLocationOverlay myLocationOverlay;
	
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	private BroadcastReceiver syncFinishedReceiverForCourtDetails;
	private MyItemizedOverlay myItemizedOverlay;
	
	
	
	static final int DIALOG_STATE_CITY_CHOICE = 0;
	
	
	//State variables...information used by all activities
	private static Account loggedInUserAccount;//Gives the logged in user - anonymous or registered user
	private static City currentCity; //Current city and state selected 
	private static int courtMarkedOccupied = -1; //court id of the court mark occupied by this user (-1 if none or user is anonymous)
	private static int courtPostedMessageOn = -1; //court id of the court on which a message has been posted by this user else -1 (anonymous user cannot post message)
	public static MapViewActivity mapViewActivity; //reference to this activity itself
	public static boolean fetchedAllcourts = false; //This flag is needed to decide whether or not to start background refresh of court info. Only when all courts are fetch we start bgrnd threads
													
	//-------------------------------------------------------
	
	
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
		//mapView.getController().setZoom(5);
		
		
	}
	
	   public void onPause() {
	        super.onPause();
	        Log.d(TAG,"Removing GPS update requests to save power");
	      //  myLocationOverlay.disableCompass();
	        myLocationOverlay.disableMyLocation();
	        MapView mapView = (MapView) findViewById(R.id.mapview);
	        mapView.getOverlays().remove(myLocationOverlay);
	        if (syncFinishedReceiverForCourtDetails != null)
	        {
	        	unregisterReceiver(syncFinishedReceiverForCourtDetails);
	        }
	        	
	        syncFinishedReceiverForCourtDetails = null;
	        
	        //Remove all periodic syncs
	        //This should remove the two periodic refreshes and any other sync that might be happening at this time
	    	ContentResolver.removePeriodicSync(MapViewActivity.getAccount(this),
					ProviderContract.AUTHORITY, SyncAdapter.getOccupiedCourtAndPostedMsgBundle()); 
			ContentResolver.cancelSync(null, ProviderContract.AUTHORITY);//cancel all syncs
			ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this), ProviderContract.AUTHORITY, false);
			
		    stopBackGroundRefresh();
			
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
	        
	       startBackGroundRefresh(); //Start background refreshes from syncadapter
	      
	    }

	    private void startBackGroundRefresh()
	    {
	    	 if (!fetchedAllcourts)
	    		 return;//First time when the activity starts we have no courts fetched yet...hence dont start background refresh yet.
	        
	    	//Start the two background periodic refresh
	        //1.
	        //Kickoff continuous refresh for court statistics which keep changing (this includes message count and occupied count)
	    	Log.d(TAG, " Adding continous refresh for court statistics");
        	ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this), ProviderContract.AUTHORITY, true);
        	
        	//Periodically update courts occupied count and message count for all courts
        	//Set to 10 second period for development BUT in production this will be set to 5 minutes
        	ContentResolver.addPeriodicSync(MapViewActivity.getAccount(this),
 				ProviderContract.AUTHORITY, SyncAdapter.getAllCourtsStatsBundle(), 10);
        	//2.
        	//Also kickoff continuous  refresh of court mark occupied by user and message id posted by user (if user not anonymous)
	        if (RestClient.isLoggedIn()) //This is not an anonymous user
	        {	        	
	        	Log.d(TAG, " Adding continous refresh for court statistics");
	        	//Periodically update the two flags courtMarkedOccupied and courtPostedMessageOn
	        	//Set to 10 second period for development BUT in production this will be set to 5 minutes
	        	ContentResolver.addPeriodicSync(MapViewActivity.getAccount(this),
	 				ProviderContract.AUTHORITY, SyncAdapter.getOccupiedCourtAndPostedMsgBundle(), 20);
	        }
	    }
	    
	    private void stopBackGroundRefresh()
		   {
			    Log.d(TAG, "Stopping background refresh");
			    ContentResolver.cancelSync(null, ProviderContract.AUTHORITY);//cancel all syncs
	    	    //Remove all periodic syncs
		        //This should remove the two periodic refreshes and any other sync that might be happening at this time
		    	ContentResolver.removePeriodicSync(MapViewActivity.getAccount(this),
						ProviderContract.AUTHORITY, SyncAdapter.getAllCourtsStatsBundle()); 
		    	if (RestClient.isLoggedIn())
		    		ContentResolver.removePeriodicSync(MapViewActivity.getAccount(this),
						ProviderContract.AUTHORITY, SyncAdapter.getOccupiedCourtAndPostedMsgBundle()); 
				
				ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this), ProviderContract.AUTHORITY, false);
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
		if (loggedInUserAccount == null) {
			AccountManager mAccountManager = AccountManager.get(context);
			Account[] accounts = mAccountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			if (accounts.length > 0)
			{
				for (Account account : accounts)
				{
					loggedInUserAccount = accounts[0];
				}
			
			}
			else
				Log.e(TAG, "No account found"); 
															
		} 
		return loggedInUserAccount;
	}
	
	public static void setAccount(Account account)
	{
		loggedInUserAccount = account;
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
						
							progressDialog.dismiss();
							MapViewActivity.this.showDialog(DIALOG_STATE_CITY_CHOICE);
							
							//now that we have fetched all courts..start background threads to keep refreshing their status..
							//Also remember fetch for all courts is done so next time the activity resume restart the background thread.
							fetchedAllcourts = true;
							startBackGroundRefresh();
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
			extras.putParcelable(CourtDetailsActivity.SELECTED_COURTS_GEOPOINT, this.getMyCurrentLocation());
			intentForTennisCourtDetails.putExtras(extras);
			startActivity(intentForTennisCourtDetails);	
		}
	
		private void getAllTennisCourts(Location currentLocation)
		{
		
			Cursor cursor = this
					.getContentResolver()
					.query(ProviderContract.TennisCourts.CONTENT_URI,
							null,
							null,
							null, null);
			populateOverlays(cursor, currentLocation);
			
			
		}
		private void getAllTennisCourts(City cityToFocusOn) {
			//Loading all courts kills the app (ANRs.and out of memory since there is just 
			//too much of data and too many overlays
			//One simple strategy for now is to just load the courts of the selected court
			//TODO implement a way to cluster icons on the map
			Cursor cursor = this
					.getContentResolver()
					.query(ProviderContract.TennisCourts.CONTENT_URI,
							null,
							" abbreviation = ?",
							new String[] {cityToFocusOn.getAbbreviation()}, null);
			
			/*Cursor cursor = this
					.getContentResolver()
					.query(ProviderContract.TennisCourts.CONTENT_URI,
							null,
							null,
							null, null); */
		 populateOverlays(cursor, null);
		}
		
		
		
		private void populateOverlays(Cursor cursor, Location location)
		{
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

					if (location != null)
					{
						Location courtLocation = new Location("");
						courtLocation.setLatitude(latitude);
						courtLocation.setLongitude(longitude);
						if (location.distanceTo(courtLocation)  > (20 * 1609.344))
						{
							cursor.moveToNext();
							continue;
						}
						
					}
					int id = cursor.getInt(cursor.getColumnIndex("_id"));
					int subcourts = cursor.getInt(cursor
							.getColumnIndex("subcourts"));
					int occupied = cursor.getInt(cursor.getColumnIndex("occupied"));
					int messageCount = cursor.getInt(cursor
							.getColumnIndex("message_count"));
					String name = cursor.getString(cursor.getColumnIndex("name"));
					String facilityType = cursor.getString(cursor
							.getColumnIndex("facility_type"));
					String city = cursor.getString(cursor
							.getColumnIndex("city"));
					String county = cursor.getString(cursor
							.getColumnIndex("county"));
					String state = cursor.getString(cursor
							.getColumnIndex("state"));
					String abbr = cursor.getString(cursor
							.getColumnIndex("abbreviation"));
					TennisCourt tennisCourt = new TennisCourt(id, latitude,
							longitude, subcourts, occupied, facilityType, name,
							messageCount, city, county, state, abbr);
					newListOfOverlays.add(new TennisCourtOverlayItemAdapter(tennisCourt));
					//clusterer.addItem(new TennisCourtGeoItem(tennisCourt));
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
			final MapView mapView = (MapView)findViewById(R.id.mapview);
			if (currentCity != null)
			{
				
				//redrawMarkers();
				Log.d(TAG," Adding all tenniscourts for the state to which this city belongs to as overlays on the map");
		        myItemizedOverlay.clear();
				getAllTennisCourts(currentCity);
			
			
				mapView.postDelayed(new Runnable(){
					public void run(){
					mapView.invalidate(); //causes draw to be invoked which will do the magic
				}}, 2);
	
				mapView.getController().animateTo(new GeoPoint((int)(currentCity.getLatitude()*1e6),(int)(currentCity.getLongitude()* 1e6)));
				mapView.getController().setZoom(14);
			}
			else
			{
				GeoPoint lastLocation = myLocationOverlay.getMyLocation();
				if (lastLocation != null)
				{
					Location l = new Location("");
					l.setLatitude(lastLocation.getLatitudeE6()/1E6);
					l.setLongitude(lastLocation.getLongitudeE6()/1E6);
					getAllTennisCourts(l); //Load only courts in 20 mile radius of current location not all
					mapView.getController().animateTo(lastLocation);
					mapView.getController().setZoom(14);
				}
				//Else show dialog box that current location is not available
				
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
		            AccountManager.get(this).removeAccount(MapViewActivity.getAccount(this), null, null);
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


	public static int getCourtMarkedOccupied() {
		return courtMarkedOccupied;
	}


	public static void setCourtMarkedOccupied(int courtMarkedOccupied) {
		MapViewActivity.courtMarkedOccupied = courtMarkedOccupied;
	}


	public static void setCourtPostedMessageOn(final int messagePosted) {
		//if message is posted show the shoutout (view message) icon on the map else remove it
		
		if (messagePosted  != MapViewActivity.courtPostedMessageOn) //A changed happened 
		{
			//Enable/disable and visibility on/off needs be done on the thread that created the view
			//which is the UI thread..Hence posting runnable on it.
			MapViewActivity.mapViewActivity.runOnUiThread( new Runnable() {
				 
			 public void run()
			 {
			if (messagePosted == -1) //Posted message was removed from the server by a cron job on the server
			{
				 Button viewMessageButton = (Button) MapViewActivity.mapViewActivity.findViewById(R.id.ShoutOutButton);
				 viewMessageButton.setEnabled(false); //disable the button and make it invisible
				 viewMessageButton.setVisibility(View.INVISIBLE);
			}
			else //a new message was posted by the user
			{
				Button viewMessageButton = (Button) MapViewActivity.mapViewActivity.findViewById(R.id.ShoutOutButton);
				 viewMessageButton.setEnabled(true); //disable the button and make it invisible
				 viewMessageButton.setVisibility(View.VISIBLE);
			}
			 }
			 });
		}
			
		MapViewActivity.courtPostedMessageOn = messagePosted;
		
	}


	public static int getCourtPostedMessageOn() {
		return courtPostedMessageOn;
	}

	public void onPartnerFound(View v)
	{
				if (getCourtPostedMessageOn() < 0)
				{
					Log.e(TAG, " Court id is invalid "+getCourtPostedMessageOn());
					return;
				}
				progressDialog = ProgressDialog.show(this, "", 
		                "Fetching message", true);
			    Log.d(TAG, " --> Requesting sync for message for courtid"+getCourtPostedMessageOn());
				getContentResolver().delete(ProviderContract.Messages.CONTENT_URI, null, null); //clean message table
				registerReceiver(postedMessageReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED_ACTION));
				ContentResolver.requestSync(MapViewActivity.getAccount(this),
						ProviderContract.AUTHORITY, SyncAdapter.getMessagePostedByUserBundle(getCourtPostedMessageOn()));
	
	}
	
	
	public final void viewMessage(int messageId, int courtId)
	{
		Intent intentForTennisCourtDetails = new Intent(this, ViewMessageActivity.class);
		Bundle extras = new Bundle(); 
		extras.putInt(SyncAdapter.MESSAGE_ID, messageId);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		intentForTennisCourtDetails.putExtras(extras);
		startActivity(intentForTennisCourtDetails);
	}
	
	private BroadcastReceiver postedMessageReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Log.d(TAG, "Sync finished, should refresh nao!!");
	        
	        if (intent.getExtras().getInt(SyncAdapter.OPERATION) != SyncAdapter.GET_COURT_MESSAGE_BY_ID)
	        	return;
	        
	        unregisterReceiver(this);
	        
	        if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR))
	        {
	        	progressDialog.dismiss();
	        	AlertDialog.Builder builder = new AlertDialog.Builder(MapViewActivity.this);
	        	builder.setMessage("Error while fetching message")
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
	        	progressDialog.dismiss();
	        	//Retreive the message id of the message just fetched by syncadapter
	        	Cursor cursor = getContentResolver().query(
	    				Messages.CONTENT_URI, new String[]{"_id"}, null,
	        			null, " _id LIMIT 1");
	        	if (cursor.getCount() == 0)
	        	{
	        		Log.e(TAG," Court message not found");
	    			return;
	        	}
	        	cursor.moveToFirst();
	        	int messageId = cursor.getInt(cursor.getColumnIndex("_id"));
	        	cursor.close();
	        	cursor = null;
	        	if (messageId != 0)
	        		viewMessage(messageId, getCourtPostedMessageOn());
	        }
	    }
	};

}
