package com.mewannaplay.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class DatabaseHelper extends SQLiteOpenHelper {

	 private static final String DATABASE_NAME = "tenniscourts.db";
	 private static final int DATABASE_VERSION = 1;
	 public static final String TENNIS_COURT_TABLE_NAME = "tenniscourt";

	 private static final String TAG = "DatabaseHelper";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TENNIS_COURT_TABLE_NAME + " ( id INTEGER PRIMARY KEY, " 
        		+ " latitude  VARCHAR(25)," 
        		+ " longitude VARCHAR(25)," 
        		+ " subcourts INTEGER,"
        		+ " occupied INTEGER,"
        		+ " facility_type VARCHAR(100),"
        		+ " name VARCHAR(100),"
        		+ " message_count INTEGER"
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
