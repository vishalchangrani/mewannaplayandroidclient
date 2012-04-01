package com.mewannaplay;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;
import com.mewannaplay.syncadapter.SyncAdapter;

public class CourtDetailsActivity extends ListActivity {

	private TennisCourtDetails tennisCourtDetails;
	 private static final String TAG = "CourtDetailsActivity";
	 private  SimpleCursorAdapter cursorAdapter;
	 Cursor messageCursor; 
	 private ProgressDialog progressDialog;
	private AlertDialog alert;
	int courtId;
	 
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
    	cursor.moveToFirst();
    	tennisCourtDetails = TennisCourtDetails.fromCursor(cursor);
      	cursor.close(); // dont need court details from db anymore. we
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
	
	
	public void postMessage(View v)
	{
		Intent intentForTennisCourtDetails = new Intent(this, PostMessageActivity.class);
		Bundle extras = new Bundle(); 
		extras.putInt(SyncAdapter.COURT_ID,courtId);
		intentForTennisCourtDetails.putExtras(extras);
		startActivity(intentForTennisCourtDetails);//fire it up baby		
	}
}
