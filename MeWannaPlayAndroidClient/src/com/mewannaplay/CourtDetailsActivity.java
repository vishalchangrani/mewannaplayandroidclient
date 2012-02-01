package com.mewannaplay;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;
import com.mewannaplay.syncadapter.SyncAdapter;

public class CourtDetailsActivity extends Activity {

	private TennisCourtDetails tennisCourtDetails;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		int courtId = this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
		//TODO this should ideally be not done on the UI thread..but will fix later
		 Cursor cursor = getContentResolver().query(TennisCourtsDetails.CONTENT_URI, null, " _id = ?", new String[] { courtId+"" }, null);
		 cursor.moveToFirst();
		 tennisCourtDetails = TennisCourtDetails.fromCursor(cursor);
		 
		
		
		setContentView(R.layout.court_details_layout);
		populateView();
		ExpandableListView v = (ExpandableListView) findViewById(R.id.exapandableList);
		v.setAdapter(new ExpadableAdapter());
		
	
		// Request first sync..
		//ContentResolver.requestSync(MapViewActivity.getAccount(this),
		//		ProviderContract.AUTHORITY,extras);
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
