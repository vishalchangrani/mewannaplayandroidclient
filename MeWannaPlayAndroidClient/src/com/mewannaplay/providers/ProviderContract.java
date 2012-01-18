package com.mewannaplay.providers;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * A little helper class that keeps all of the columns organized and readily accessible
 * @author vishal
 *
 */
public final class ProviderContract
{
	private ProviderContract() {
	}
	
	public static final String AUTHORITY = "com.mewannaplay.providers.TennisCourtProvider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://"+AUTHORITY);
	
	//One BaseColumn implementation for each table 
	//Each implementation has content_uri to access the table, content_types and table columns and more.
	public final static class TennisCourts implements BaseColumns {
	
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
		            AUTHORITY_URI, DatabaseHelper.TENNIS_COURT_TABLE_NAME);
		 
		public static final String TENNIS_COURTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mewannaplay.tenniscourts";

		public static final String TENNIS_COURT_ID = "_id";

		public static final String TENNIS_LATITUDE = "latitude";

		public static final String TENNIS_LONGITUDE = "longitude";
		
		public static final String TENNIS_SUBCOURTS = "subcourts";

		public static final String OCCUPIED = "occupied";
		
		public static final String TENNIS_FACILITY_TYPE = "facility_type";

		public static final String TENNIS_NAME = "name";
		
		public static final String MESSAGE_COUNT = "message_count";

	}
}

