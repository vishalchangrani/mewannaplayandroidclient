package com.mewannaplay.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class DatabaseHelper extends SQLiteOpenHelper {

	 private static final String DATABASE_NAME = "tenniscourts.db";
	 private static final int DATABASE_VERSION = 1;
	 public static final String TENNIS_COURT_TABLE_NAME = "tenniscourt";
	 public static final String TENNIS_COURT_DETAILS_TABLE_NAME = "tenniscourtdetail";
	 public static final String MESSAGES_TABLE_NAME = "messages";
	 public static final String CITIES_TABLE_NAME = "cities";

	 private static final String TAG = "DatabaseHelper";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL("DROP TABLE IF EXISTS " + TENNIS_COURT_TABLE_NAME);//TODO remove this
    	db.execSQL("DROP TABLE IF EXISTS " + TENNIS_COURT_DETAILS_TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + CITIES_TABLE_NAME);
    	db.execSQL("CREATE TABLE " + TENNIS_COURT_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
        		+ " latitude  REAL," 
        		+ " longitude REAL," 
        		+ " subcourts INTEGER,"
        		+ " occupied INTEGER,"
        		+ " facility_type VARCHAR(100),"
        		+ " name VARCHAR(250),"
        		+ " message_count INTEGER"
        		+ ");");
       	db.execSQL("CREATE TABLE " + TENNIS_COURT_DETAILS_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
        		+ " name VARCHAR(250),"
          		+ " address VARCHAR(500),"
          		+ " zipcode VARCHAR(20),"
          		+ " url VARCHAR(500),"
          		+ " facility_type VARCHAR(45),"
          		+ " subcourts INTEGER,"
          		+ " timings VARCHAR(500),"
          		+ " city VARCHAR(250),"
          		+ " state VARCHAR(100),"
          		+ " abbreviation VARCHAR(10),"
          		+ " phone VARCHAR(100)"
        		+ ");");
    	db.execSQL("CREATE TABLE " + MESSAGES_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
          		+ " text VARCHAR(500),"
          		+ " user VARCHAR(20),"
          		+ " level VARCHAR(45),"
          		+ " scheduled_time VARCHAR(500),"
          		+ " contact_info VARCHAR(250),"
        		+ " contact_type INTEGER,"
          		+ " players_needed VARCHAR(10),"
          		+ " time_posted VARCHAR(500)"
        		+ ");");
    	db.execSQL("CREATE TABLE " + CITIES_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
          		+ " name VARCHAR(500),"
          		+ " abbreviation VARCHAR(20),"
          		+ " latitude  REAL," 
        		+ " longitude REAL" 
        		+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TENNIS_COURT_TABLE_NAME);
        onCreate(db);
    }
    
    
}
