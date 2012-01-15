
package com.mewannaplay.providers;

import static com.mewannaplay.providers.DatabaseHelper.TENNIS_COURT_TABLE_NAME;
import static com.mewannaplay.providers.ProviderContract.AUTHORITY;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

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
    
    

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case TENNISCOURTS:
                count = db.delete(TENNIS_COURT_TABLE_NAME, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
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
    public Uri insert(Uri uri, ContentValues initialValues) {
      /*  if (sUriMatcher.match(uri) != TENNISCOURTS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TENNIS_COURT_TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }*/

        throw new SQLException("Failed to insert row into " + uri);
    }
    
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
    	int count = 0;
		switch (sUriMatcher.match(uri)) {
		case TENNISCOURTS:
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.beginTransaction();
			try {
				String sql = "insert into "
						+ TENNIS_COURT_TABLE_NAME
						+ "(id, latitude,longitude, subcourts, occupied,facility_type, name, message_count) values (?,?,?,?,?,?,?,?)";
				SQLiteStatement insert = db.compileStatement(sql);

				for (ContentValues contentValues : values) {
					insert.bindLong(1, contentValues.getAsInteger("id"));
					insert.bindDouble(2,
							contentValues.getAsFloat("latitude"));
					insert.bindDouble(3,
							contentValues.getAsFloat("longitude"));
					insert.bindLong(4,
							contentValues.getAsInteger("subcourts"));
					insert.bindLong(5, contentValues.getAsInteger("occupied"));
					insert.bindString(6,
							contentValues.getAsString("facility_type"));
					insert.bindString(7,
							contentValues.getAsString("name"));
					insert.bindLong(8,
							contentValues.getAsInteger("message_count"));
					insert.executeInsert();
					count++;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case TENNISCOURTS:
                qb.setTables(TENNIS_COURT_TABLE_NAME);
                qb.setProjectionMap(tennisCourtProjectMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri); //??What is this
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case TENNISCOURTS:
                count = db.update(TENNIS_COURT_TABLE_NAME, values, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, true);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TENNIS_COURT_TABLE_NAME, TENNISCOURTS);

        tennisCourtProjectMap = new HashMap<String, String>();
        tennisCourtProjectMap.put("id", "id");
        tennisCourtProjectMap.put("latitude","latitude");
        tennisCourtProjectMap.put("longitude","longitude");
        tennisCourtProjectMap.put("subcourts","subcourts");
        tennisCourtProjectMap.put("occupied","occupied");
        tennisCourtProjectMap.put("facility_type","facility_type");
        tennisCourtProjectMap.put("name","name");
        tennisCourtProjectMap.put("message_count","message_count");
    }
}
