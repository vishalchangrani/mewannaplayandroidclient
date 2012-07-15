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
	 public static final String TENNIS_COURT_ACIVITY_TABLE_NAME = "activity";
	 public static final String TENNIS_COURT_AMENITY_TABLE_NAME = "amenity";
	 
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
    	db.execSQL("DROP TABLE IF EXISTS " + TENNIS_COURT_ACIVITY_TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + TENNIS_COURT_AMENITY_TABLE_NAME);
    	db.execSQL("CREATE TABLE " + TENNIS_COURT_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
        		+ " latitude  REAL," 
        		+ " longitude REAL," 
        		+ " subcourts INTEGER,"
        		+ " occupied INTEGER,"
        		+ " facility_type VARCHAR(20),"
        		+ " name VARCHAR(50),"
        		+ " message_count INTEGER,"
        		+ " city VARCHAR(30),"
        		+ " county VARCHAR(20),"
          		+ " state VARCHAR(20),"
          		+ " abbreviation VARCHAR(3)"
        		+ ");");
    	
    	db.execSQL("CREATE INDEX city_index ON "+ TENNIS_COURT_TABLE_NAME+"(city);");
    	db.execSQL("CREATE INDEX state_index ON "+ TENNIS_COURT_TABLE_NAME+"(abbreviation);");
    	
       	db.execSQL("CREATE TABLE " + TENNIS_COURT_DETAILS_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
        		+ " name VARCHAR(30),"
          		+ " address VARCHAR(100),"
          		+ " zipcode VARCHAR(10),"
          		+ " url VARCHAR(100),"
          		+ " facility_type VARCHAR(20),"
          		+ " subcourts INTEGER,"
          		+ " timings VARCHAR(100),"
          		+ " city VARCHAR(30),"
          		+ " state VARCHAR(20),"
          		+ " abbreviation VARCHAR(3),"
          		+ " phone VARCHAR(20),"
          		+ " surface_type VARCHAR(50)"
        		+ ");");
    	db.execSQL("CREATE TABLE " + MESSAGES_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
          		+ " text VARCHAR(500),"
          		+ " user VARCHAR(20),"
          		+ " user_name VARCHAR(20),"
          		+ " level VARCHAR(45),"
          		+ " scheduled_time VARCHAR(100),"
          		+ " contact_info VARCHAR(250),"
        		+ " contact_type INTEGER,"
          		+ " players_needed VARCHAR(10),"
          		+ " time_posted VARCHAR(100)"
        		+ ");");
    	db.execSQL("CREATE TABLE " + CITIES_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
          		+ " name VARCHAR(30),"
          		+ " abbreviation VARCHAR(20),"
          		+ " latitude  REAL," 
        		+ " longitude REAL" 
        		+ ");");
    	db.execSQL("CREATE TABLE " + TENNIS_COURT_ACIVITY_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
          		+ " remark VARCHAR(100),"
          		+ " tennis_court INTEGER NOT NULL, "
          		+ " FOREIGN KEY(tennis_court) REFERENCES "+TENNIS_COURT_DETAILS_TABLE_NAME+"(_id) ON DELETE CASCADE"
        		+ ");");
    	db.execSQL("CREATE INDEX tennis_court_activity_index ON "+ TENNIS_COURT_ACIVITY_TABLE_NAME+"(tennis_court)");
    	
    	db.execSQL("CREATE TABLE " + TENNIS_COURT_AMENITY_TABLE_NAME + " ( _id INTEGER PRIMARY KEY, " 
          		+ " remark VARCHAR(100),"
          		+ " tennis_court INTEGER NOT NULL, "
          		+ " FOREIGN KEY(tennis_court) REFERENCES "+TENNIS_COURT_DETAILS_TABLE_NAME+"(_id) ON DELETE CASCADE"
        		+ ");");
    	db.execSQL("CREATE INDEX tennis_court_amenity_index ON "+ TENNIS_COURT_AMENITY_TABLE_NAME+"(tennis_court);");
          		
    	
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        onCreate(db);
    }
    
    
}
