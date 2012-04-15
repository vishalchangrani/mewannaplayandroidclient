package com.mewannaplay;

import java.util.Timer;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;
import com.mewannaplay.syncadapter.SyncAdapter;

public class CourtDetailsActivity extends ListActivity{

	private TennisCourtDetails tennisCourtDetails;
	 private static final String TAG = "CourtDetailsActivity";
	 private  SimpleCursorAdapter cursorAdapter;
	 Cursor messageCursor; 
	 private ProgressDialog progressDialog;
	private AlertDialog alert;
	int courtId;
	private ContentObserver messageContentObserver;
	private Timer messageRefreshTimer; 
	private AsyncTask<Void, Void, Void> getMessageTask;
	private Handler handler = new Handler();
	private ServiceResultReceiver receiver;
	 
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		courtId = this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
		setContentView(R.layout.court_details_layout);
		
		
		if (RestClient.isLoggedIn())
		{
			Button postMsgButton = (Button) findViewById(R.id.post_msg_button);
			postMsgButton.setEnabled(true);
			Button markCourtOccupied = (Button) findViewById(R.id.marl_occu_button);
			markCourtOccupied.setEnabled(true);
		}
		
      	// TODO this should ideally be not done on the UI thread..but
    	// will fix later
    	Cursor cursor = getContentResolver().query(
    			TennisCourtsDetails.CONTENT_URI, null, " _id = ?",
    			new String[] { courtId + "" }, null);
    	if (cursor.getCount() == 0)
    		this.finish();
    	cursor.moveToFirst();
    	
    	Cursor activityCursor = getContentResolver().query(
    			ProviderContract.Acitivity.CONTENT_URI, null, " tennis_court = ?",
    			new String[] { courtId + "" }, null);
    	
    	Cursor amenityCursor = getContentResolver().query(
    			ProviderContract.Amenity.CONTENT_URI, null, " tennis_court = ?",
    			new String[] { courtId + "" }, null);
    	
    	
    	tennisCourtDetails = TennisCourtDetails.fromCursor(cursor,activityCursor,amenityCursor);
      	cursor.close(); // dont need court details from db anymore. we
      	activityCursor.close();
      	amenityCursor.close();
      	
      	getContentResolver().delete(ProviderContract.Messages.CONTENT_URI, null, null); //clean message table
    	// have cached it
    	if (tennisCourtDetails == null)
    	{
    		Log.e(TAG, "courtdetails object null!");
    		this.finish();
    	}
  

    
    	populateView();
    	ExpandableListView v = (ExpandableListView) findViewById(R.id.exapandableList);
    	v.setAdapter(new ExpadableAdapter());

    	// the desired columns to be bound
    	String[] columns = new String[] { "scheduled_time", "user",
    			"contact_info", "level", "players_needed", "text",
    	"time_posted" };
    	// the XML defined views which the data will be bound to
    	int[] to = new int[] { R.id.scheduled_time, R.id.user,
    			R.id.contact_info, R.id.level, R.id.players_needed,
    			R.id.message_text, R.id.time_posted };
    	messageCursor = getContentResolver().query(
    			Messages.CONTENT_URI, null, null, null, null);
    	startManagingCursor(messageCursor);

    	cursorAdapter = new SimpleCursorAdapter(CourtDetailsActivity.this,
    			R.layout.court_message_row, messageCursor, columns, to);
    	// View header =
    	// getLayoutInflater().inflate(R.id.msg_details_table, null);
    	// getListView().addHeaderView(header);
    	CourtDetailsActivity.this.setListAdapter(cursorAdapter);
    	
    	// Set our receiver

   // 	receiver = new ServiceResultReceiver(new Handler());

    	//receiver.setReceiver(this);


    	
    
	}

	private class MessagesContentObserver extends ContentObserver 
	{

		public MessagesContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onChange(boolean selfChange) {
			Log.d(TAG, "onChange for message");
			super.onChange(selfChange);
			CourtDetailsActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "In observer for messages");
					cursorAdapter.notifyDataSetChanged();					
				}
				
			});
		}
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getContentResolver().unregisterContentObserver(messageContentObserver);
		messageContentObserver = null;
	//	messageRefreshTimer.cancel();
	//	messageRefreshTimer = null;
		ContentResolver.removePeriodicSync(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY, SyncAdapter.getAllMessagesBundle(courtId)); 
		ContentResolver.cancelSync(null, ProviderContract.AUTHORITY);//cancel all syncs
		ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this), ProviderContract.AUTHORITY, false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		messageContentObserver = new MessagesContentObserver(null);
    	getContentResolver().registerContentObserver(Messages.CONTENT_URI, true, messageContentObserver);
    	//messageRefreshTimer = new Timer("message refresher for "+courtId);
    	//messageRefreshTimer.schedule(new MessageRefresh(), new Date(), 60*1000);
    	//getMessageTask = new GetMessagesTask();
    	//getMessageTask.execute();
    	 ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this), ProviderContract.AUTHORITY, true);
    	ContentResolver.addPeriodicSync(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY, SyncAdapter.getAllMessagesBundle(courtId), 10);
    	//ContentResolver.requestSync(MapViewActivity.getAccount(CourtDetailsActivity.this),
			//	ProviderContract.AUTHORITY,  SyncAdapter.getAllMessagesBundle(courtId));
	}
	
	private void populateView()
	{
		  TextView tv = (TextView) this.findViewById(R.id.court_name);
		  tv.setText(tennisCourtDetails.getName().trim());
		  tv = (TextView) this.findViewById(R.id.court_addr_1);
		  tv.setText(tennisCourtDetails.getAddress().trim());
//		  tv = (TextView) this.findViewById(R.id.court_addr_2);
//		  tv.setText(tennisCourtDetails.getCity()+","+tennisCourtDetails.getState()+" "+tennisCourtDetails.getZipcode());
		  tv = (TextView) this.findViewById(R.id.court_phone_1);
		  tv.setText(tennisCourtDetails.getPhone().trim());
		  tv = (TextView) this.findViewById(R.id.text_sub_courts);
		  tv.setText(""+tennisCourtDetails.getSubcourts());
		 // tv = (TextView) this.findViewById(R.id.court_type);?? //TODO what do set here
		  tv = (TextView) this.findViewById(R.id.court_facility_type);
		  tv.setText(tennisCourtDetails.getFacilityType());
		  tv = (TextView) this.findViewById(R.id.court_timings);
		  tv.setText(tennisCourtDetails.getTennisTimings());
		  tv = (TextView) this.findViewById(R.id.no_of_sub_courts);
		  tv.setText(""+tennisCourtDetails.getSubcourts());
		  
	}
	public class ExpadableAdapter extends BaseExpandableListAdapter {


		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LayoutInflater infalInflater = (LayoutInflater) CourtDetailsActivity.this
					.getApplicationContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			switch(groupPosition)
			{
			case 0:
				View view = infalInflater.inflate(R.layout.child_layout_ameneties_services, null);
				
				((ImageView)(view.findViewById(R.id.lockerRoomIcon))).setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.LOCKER_ROOM.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.parking))).setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.PARKING.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.lessons))).setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.LESSONS.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.lights))).setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.LIGHTS.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.snackBar))).setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.SNACK_BAR.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.shop))).setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.SHOP.ordinal()] != null ? 1 : 0); 
				
		
				return view;
			case 1:
				
				view = infalInflater.inflate(R.layout.child_layout_activities, null);				
				((ImageView)(view.findViewById(R.id.adultProgram))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.NEW_PLAYER_ADULT_PROGRAM.ordinal()] != null ? 1 : 0);
				((ImageView)(view.findViewById(R.id.teamTennis))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.TEAM_TENNIS.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.juniorProgram))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.NEW_PLAYER_JUNIOR_PROGRAM.ordinal()]!= null ? 1 : 0);
				((ImageView)(view.findViewById(R.id.tournaments))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.TOURNAMENTS.ordinal()] != null? 1 : 0);
				((ImageView)(view.findViewById(R.id.ladders))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.LADDERS.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.roundRobin))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.ROUND_ROBINS.ordinal()] != null ? 1 : 0);
				((ImageView)(view.findViewById(R.id.socialMixers))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.SOCIAL_MIXERS.ordinal()] != null ? 1 : 0); 
				((ImageView)(view.findViewById(R.id.seniors))).setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.SENIORS.ordinal()] != null ? 1 : 0);
				return view;

			}
			return null;
		}

		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		public Object getGroup(int groupPosition) {
			return null;
		}

		public int getGroupCount() {
			return 2;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			LayoutInflater infalInflater = (LayoutInflater) CourtDetailsActivity.this
					.getApplicationContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			switch(groupPosition)
			{
			case 0:
				return infalInflater.inflate(R.layout.group_layout_ameneties_services, null);
			case 1:
				return infalInflater.inflate(R.layout.group_layout_activities, null);
			}
			return null;

		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}
	
	
	public void postMessage(View v)
	{
		Intent intentForTennisCourtDetails = new Intent(this, PostMessageActivity.class);
		Bundle extras = new Bundle(); 
		extras.putInt(SyncAdapter.COURT_ID,courtId);
		intentForTennisCourtDetails.putExtras(extras);
		startActivity(intentForTennisCourtDetails);//fire it up baby		
	}
	
	
	/*class MessageRefresh extends TimerTask {
		   public void run() {
			   Log.d(TAG, "scheduling timertask to get messages ");
			   ContentResolver.requestSync(MapViewActivity.getAccount(CourtDetailsActivity.this),
				ProviderContract.AUTHORITY,  SyncAdapter.getAllMessagesBundle(courtId));
		   }
		}*/
	
	private class GetMessagesTask extends AsyncTask<Void, Void, Void> {
	    @Override 
		protected Void doInBackground(Void... arg0) {
	    	 Log.d(TAG, "scheduling timertask to get messages ");
			   ContentResolver.requestSync(MapViewActivity.getAccount(CourtDetailsActivity.this),
				ProviderContract.AUTHORITY,  SyncAdapter.getAllMessagesBundle(courtId));
			   return null;
	     }

	 }
	
	/*@Override
	public void onReceiveResult(int resultCode, Bundle resultBundle) {
		Log.d(TAG, " onRecvResult ");
		
	}*/
}
