package com.mewannaplay;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mewannaplay.mapoverlay.MyItemizedOverlay.TennisCourtDetailsContentObserver;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;
import com.mewannaplay.syncadapter.SyncAdapter;

public class CourtDetailsActivity extends ListActivity {

	private TennisCourtDetails tennisCourtDetails;
	 private static final String TAG = "CourtDetailsActivity";
	 private  SimpleCursorAdapter cursorAdapter;
	 Cursor messageCursor; 
	 
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		int courtId = this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
		//TODO this should ideally be not done on the UI thread..but will fix later
		 Cursor cursor = getContentResolver().query(TennisCourtsDetails.CONTENT_URI, null, " _id = ?", new String[] { courtId+"" }, null);
		 cursor.moveToFirst();
		 tennisCourtDetails = TennisCourtDetails.fromCursor(cursor);
		 
		cursor.close(); //dont need court details from db anymore. we have cached it
		
		setContentView(R.layout.court_details_layout);
		populateView();
		ExpandableListView v = (ExpandableListView) findViewById(R.id.exapandableList);
		v.setAdapter(new ExpadableAdapter());
		
	
		// the desired columns to be bound
			            String[] columns = new String[] { "scheduled_time", "user", "contact_info",  "level", "players_needed", "text", "time_posted" };
			            // the XML defined views which the data will be bound to
			            int[] to = new int[] { R.id.scheduled_time,R.id.user, R.id.contact_info, R.id.level, R.id.players_needed,  R.id.message_text, R.id.time_posted };
		messageCursor = getContentResolver().query(Messages.CONTENT_URI, null, null, null, null);
		startManagingCursor(messageCursor);
		
		 cursorAdapter = new SimpleCursorAdapter(this, R.layout.court_message_row,  messageCursor, columns, to);
	//	 View header = getLayoutInflater().inflate(R.id.msg_details_table, null);
		// getListView().addHeaderView(header);
		 this.setListAdapter(cursorAdapter);
	
		 this.getContentResolver().registerContentObserver(
					ProviderContract.Messages.CONTENT_URI, true,
					new MessagesContentObserver(courtId));
		 ContentResolver.requestSync(MapViewActivity.getAccount(this),
					ProviderContract.AUTHORITY, SyncAdapter.getAllMessagesBundle(courtId));
	}

	private class MessagesContentObserver extends ContentObserver {

	final int courtId;
		
		public MessagesContentObserver(int courtId) {
			super(null);
			this.courtId = courtId;

		}

		@Override
		public void onChange(boolean selfChange) {
			stopManagingCursor(messageCursor);
			messageCursor = managedQuery(Messages.CONTENT_URI, null, null, null, null);
			runOnUiThread(new Runnable() { public void run() { cursorAdapter.changeCursor(messageCursor);}});
		}
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
				
				return infalInflater.inflate(R.layout.child_layout_ameneties_services, null);
			case 1:
				return infalInflater.inflate(R.layout.child_layout_activities, null);
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
	

}
