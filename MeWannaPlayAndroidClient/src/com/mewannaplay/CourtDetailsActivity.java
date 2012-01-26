package com.mewannaplay;

import com.mewannaplay.providers.ProviderContract;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

public class CourtDetailsActivity extends Activity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.court_details_layout);
		ExpandableListView v = (ExpandableListView) findViewById(R.id.exapandableList);
		v.setAdapter(new ExpadableAdapter());
		
		Bundle extras = new Bundle();
		extras.putString("test", "testvalue");
		// Request first sync..
		ContentResolver.requestSync(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY,extras);
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
