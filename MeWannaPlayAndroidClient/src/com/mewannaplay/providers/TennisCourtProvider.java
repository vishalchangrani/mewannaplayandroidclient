package com.mewannaplay.providers;

import static com.mewannaplay.providers.DatabaseHelper.TENNIS_COURT_DETAILS_TABLE_NAME;
import static com.mewannaplay.providers.DatabaseHelper.TENNIS_COURT_TABLE_NAME;
import static com.mewannaplay.providers.DatabaseHelper.MESSAGES_TABLE_NAME;
import static com.mewannaplay.providers.DatabaseHelper.CITIES_TABLE_NAME;
import static com.mewannaplay.providers.DatabaseHelper.TENNIS_COURT_AMENITY_TABLE_NAME;
import static com.mewannaplay.providers.DatabaseHelper.TENNIS_COURT_ACIVITY_TABLE_NAME;
import static com.mewannaplay.providers.ProviderContract.AUTHORITY;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.util.Log;

import com.mewannaplay.providers.ProviderContract.TennisCourts;

/**
 * @author Vishal Changrani
 * 
 */
public class TennisCourtProvider extends ContentProvider {

	private static final String TAG = "TennisCourtProvider";

	private static final UriMatcher sUriMatcher;

	public static HashMap<String, String> tennisCourtProjectMap;

	private DatabaseHelper dbHelper;

	private final static int TENNISCOURTS = 1;
	private final static int TENNISCOURTSDETAILS = 2;
	private final static int TENNISCOURTSDETAILS_ID = 3;
	private final static int MESSAGES = 4;
	private final static int CITIES = 5;
	private final static int ACTIVITY = 6;
	private final static int AMENITY = 7;

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		db.beginTransaction();
		try
		{
		switch (sUriMatcher.match(uri)) {
		case TENNISCOURTS:
			count = db.delete(TENNIS_COURT_TABLE_NAME, where, whereArgs);
			break;
		case TENNISCOURTSDETAILS:
			count = db.delete(TENNIS_COURT_DETAILS_TABLE_NAME, where, whereArgs);//triggers cascade delete for activity and amenity
			break;
		case MESSAGES:
			count = db.delete(MESSAGES_TABLE_NAME, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
			//db.close();
		} 

		getContext().getContentResolver().notifyChange(uri, null, true);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TENNISCOURTS:
			return TennisCourts.TENNIS_COURTS_CONTENT_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		SQLiteStatement insert = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case TENNISCOURTSDETAILS_ID:

			db.beginTransaction();
			try {
				String sql = "insert OR REPLACE into "
						+ TENNIS_COURT_DETAILS_TABLE_NAME
						+ " (_id, name,address, zipcode, url,facility_type, subcourts, timings, city, state, abbreviation, phone) values (?,?,?,?,?,?,?,?,?,?,?,?)";
				insert = db.compileStatement(sql);

				insert.bindLong(1, contentValues.getAsInteger("_id"));
				insert.bindString(2, contentValues.getAsString("name"));
				insert.bindString(3, contentValues.getAsString("address"));
				insert.bindString(4, contentValues.getAsString("zipcode"));
				insert.bindString(5, contentValues.getAsString("url"));
				insert.bindString(6, contentValues.getAsString("facility_type"));
				insert.bindLong(7, contentValues.getAsInteger("subcourts"));
				insert.bindString(8, contentValues.getAsString("timings"));
				insert.bindString(9, contentValues.getAsString("city"));
				insert.bindString(10, contentValues.getAsString("state"));
				insert.bindString(11, contentValues.getAsString("abbreviation"));
				insert.bindString(12, contentValues.getAsString("phone"));
				insert.executeInsert();
				db.setTransactionSuccessful();
				
				getContext().getContentResolver().notifyChange(uri, null, false);
				return ContentUris.withAppendedId(uri,contentValues.getAsInteger("_id"));
			}catch (SQLException e)
			{
				Log.e(TAG,"Failed to insert row into " + uri+ " "+ e.getMessage());
				return null;
			}
			finally {
				db.endTransaction();
				if (insert != null) 
					insert.close();
			} 
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int count = 0;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteStatement insert = null;
		try {
			db.beginTransaction();
			switch (sUriMatcher.match(uri)) {
			case TENNISCOURTS:

				String sql = "insert OR REPLACE into "
						+ TENNIS_COURT_TABLE_NAME
						+ "(_id, latitude,longitude, subcourts, occupied,facility_type, name, message_count) values (?,?,?,?,?,?,?,?)";
				insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("_id"));
					insert.bindDouble(2, contentValues.getAsDouble("latitude"));
					insert.bindDouble(3, contentValues.getAsDouble("longitude"));
					insert.bindLong(4, contentValues.getAsInteger("subcourts"));
					insert.bindLong(5, contentValues.getAsInteger("occupied"));
					insert.bindString(6,
							contentValues.getAsString("facility_type"));
					insert.bindString(7, contentValues.getAsString("name"));
					insert.bindLong(8,
							contentValues.getAsInteger("message_count"));
					insert.executeInsert();
					count++;
				}

				Log.d(TAG, " inserted " + count + " tenniscourts");

				break;
			case TENNISCOURTSDETAILS:

			

				sql = "insert into "
						+ TENNIS_COURT_DETAILS_TABLE_NAME
						+ "(_id, name,address, zipcode, url,facility_type, subcourts, timings, city, state, abbreviation, phone) values (?,?,?,?,?,?,?,?,?,?,?,?)";
				insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("_id"));
					insert.bindString(2, contentValues.getAsString("name"));
					insert.bindString(3, contentValues.getAsString("address"));
					insert.bindString(4, contentValues.getAsString("zipcode"));
					insert.bindString(5, contentValues.getAsString("url"));
					insert.bindString(6,
							contentValues.getAsString("facility_type"));
					insert.bindLong(7, contentValues.getAsInteger("subcourts"));
					insert.bindString(8, contentValues.getAsString("timings"));
					insert.bindString(9, contentValues.getAsString("city"));
					insert.bindString(10, contentValues.getAsString("state"));
					insert.bindString(11,
							contentValues.getAsString("abbreviation"));
					insert.bindString(12, contentValues.getAsString("phone"));
					insert.executeInsert();
					count++;
				}

				break;
			case MESSAGES:
				
			
				sql = "insert OR REPLACE into "
						+ MESSAGES_TABLE_NAME
						+ "(_id, text,user, level, scheduled_time,contact_info, contact_type, players_needed, time_posted) values (?,?,?,?,?,?,?,?,?)";
				Log.d(TAG, "executing to SQL "+sql);
				insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("_id"));
					insert.bindString(2, contentValues.getAsString("text"));
					insert.bindString(3, contentValues.getAsString("user"));
					insert.bindString(4, contentValues.getAsString("level"));
					insert.bindString(5,
							contentValues.getAsString("scheduled_time"));
					insert.bindString(6,
							contentValues.getAsString("contact_info"));
					insert.bindLong(7,
							contentValues.getAsInteger("contact_type"));
					insert.bindString(8,
							contentValues.getAsString("players_needed"));
					insert.bindString(9,
							contentValues.getAsString("time_posted"));
					insert.executeInsert();
					count++;
				}
				Log.d(TAG, " inserted "+count+" messages");
				break;
			case CITIES:

				sql = "insert OR REPLACE into "
						+ CITIES_TABLE_NAME
						+ " (_id, name,abbreviation, latitude, longitude) values (?,?,?,?,?)";
				insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("_id"));
					insert.bindString(2, contentValues.getAsString("name"));
					insert.bindString(3,
							contentValues.getAsString("abbreviation"));
					insert.bindDouble(4, contentValues.getAsDouble("latitude"));
					insert.bindDouble(5, contentValues.getAsDouble("longitude"));
					insert.executeInsert();
					count++;
				}

				break;
			case ACTIVITY:

				sql = "insert OR REPLACE into "
						+ TENNIS_COURT_ACIVITY_TABLE_NAME
						+ " (_id, remark, tennis_court) values (?,?,?)";
				insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("_id"));
					insert.bindString(2, contentValues.getAsString("remark"));
					insert.bindLong(3, contentValues.getAsInteger("tennis_court"));
					insert.executeInsert();
					count++;
				}

				break;
			case AMENITY:

				sql = "insert OR REPLACE into "
						+ TENNIS_COURT_AMENITY_TABLE_NAME
						+ " (_id, remark, tennis_court) values (?,?,?)";
				insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("_id"));
					insert.bindString(2, contentValues.getAsString("remark"));
					insert.bindLong(3, contentValues.getAsInteger("tennis_court"));
					insert.executeInsert();
					count++;
				}

				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			if (insert != null)
				insert.close();
		}
		getContext().getContentResolver().notifyChange(uri, null, false);
		return count;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case TENNISCOURTS:
			qb.setTables(TENNIS_COURT_TABLE_NAME);
			qb.setProjectionMap(tennisCourtProjectMap);
			break;
		case TENNISCOURTSDETAILS:
			qb.setTables(TENNIS_COURT_DETAILS_TABLE_NAME);
			break;
		case MESSAGES:
			qb.setTables(MESSAGES_TABLE_NAME);
			break;
		case CITIES:
			qb.setTables(CITIES_TABLE_NAME);
			
			break;
		case ACTIVITY:
			qb.setTables(TENNIS_COURT_ACIVITY_TABLE_NAME);
			
			break;
		case AMENITY:
			qb.setTables(TENNIS_COURT_AMENITY_TABLE_NAME);
			
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		
		c.setNotificationUri(getContext().getContentResolver(), uri); // ??What is this
		return c;
		
																
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TENNISCOURTS:
			count = db
					.update(TENNIS_COURT_TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null, true);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, TENNIS_COURT_TABLE_NAME, TENNISCOURTS);
		sUriMatcher.addURI(AUTHORITY, TENNIS_COURT_DETAILS_TABLE_NAME,
				TENNISCOURTSDETAILS);
		sUriMatcher.addURI(AUTHORITY, TENNIS_COURT_DETAILS_TABLE_NAME+"/#",
				TENNISCOURTSDETAILS_ID);
		sUriMatcher.addURI(AUTHORITY, MESSAGES_TABLE_NAME, MESSAGES);
		sUriMatcher.addURI(AUTHORITY, CITIES_TABLE_NAME, CITIES);
		sUriMatcher.addURI(AUTHORITY, TENNIS_COURT_ACIVITY_TABLE_NAME, ACTIVITY);
		sUriMatcher.addURI(AUTHORITY, TENNIS_COURT_AMENITY_TABLE_NAME, AMENITY);

		tennisCourtProjectMap = new HashMap<String, String>();
		tennisCourtProjectMap.put("_id", "_id");
		tennisCourtProjectMap.put("latitude", "latitude");
		tennisCourtProjectMap.put("longitude", "longitude");
		tennisCourtProjectMap.put("subcourts", "subcourts");
		tennisCourtProjectMap.put("occupied", "occupied");
		tennisCourtProjectMap.put("facility_type", "facility_type");
		tennisCourtProjectMap.put("name", "name");
		tennisCourtProjectMap.put("message_count", "message_count");
	}
}
