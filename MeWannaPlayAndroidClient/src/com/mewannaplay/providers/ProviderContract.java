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
	
	//One BaseColumn implementation for each table 
		//Each implementation has content_uri to access the table, content_types and table columns and more.
		public final static class TennisCourtsDetails implements BaseColumns {
		
			public static final Uri CONTENT_URI = Uri.withAppendedPath(
			            AUTHORITY_URI, DatabaseHelper.TENNIS_COURT_DETAILS_TABLE_NAME);
			 
			public static final String TENNIS_COURTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mewannaplay.tenniscourts";

			public static final String TENNIS_COURT_ID = "_id";

			public static final String TENNIS_LATITUDE = "name";

			public static final String TENNIS_LONGITUDE = "address";
			
			public static final String TENNIS_SUBCOURTS = "zipcode";

			public static final String OCCUPIED = "url";
			
			public static final String FACILITY_TYPE = "facility_type";

			public static final String TENNIS_NAME = "subcourts";
			
			public static final String MESSAGE_COUNT = "timings";
			
			public static final String CITY = "city";
			
			public static final String STATE = "state";
			
			public static final String ABBREVIATION = "abbreviation";
			
			public static final String PHONE = "phone";	
			
			public static final String ZIPCODE = "zipcode";	

		}
		
		//One BaseColumn implementation for each table 
		//Each implementation has content_uri to access the table, content_types and table columns and more.
		public final static class Messages implements BaseColumns {
		
			public static final Uri CONTENT_URI = Uri.withAppendedPath(
			            AUTHORITY_URI, DatabaseHelper.MESSAGES_TABLE_NAME);
			 
			public static final String TENNIS_COURTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mewannaplay.tenniscourts";

		}
		
		
		public final static class Cities implements BaseColumns {
			
			public static final Uri CONTENT_URI = Uri.withAppendedPath(
			            AUTHORITY_URI, DatabaseHelper.CITIES_TABLE_NAME);
			 
			public static final String CITIES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mewannaplay.cities";

		}
}

