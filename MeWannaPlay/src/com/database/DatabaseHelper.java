
package com.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DATABASE_NAME = "mewannaplay.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CITY_LIST_TABLE_NAME = "cityListTable";
    private final Context context;
    private final SQLiteDatabase db;
    private final SQLiteStatement insertStmt;
    private static final String INSERT = "insert into "
            + CITY_LIST_TABLE_NAME + "(name) values (?)";

    public DatabaseHelper(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
        this.insertStmt = this.db.compileStatement(INSERT);
    }

    public long insert(String name) {
        this.insertStmt.bindString(1, name);
        return this.insertStmt.executeInsert();
    }

    public void deleteAll() {
        this.db.delete(CITY_LIST_TABLE_NAME, null, null);
    }

    public List<String> selectAll() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query(CITY_LIST_TABLE_NAME, new String[] {
                "name"
        },
                null, null, null, null, "name desc");
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // db.execSQL("CREATE TABLE " + CITY_LIST_TABLE_NAME
            // + "(id INTEGER PRIMARY KEY, name TEXT)");
            String CITY_ID = "city_id";
            String CITY_NAME = "city_name";
            String CITY_SLNO = "city_no";

            db.execSQL("CREATE TABLE " + CITY_LIST_TABLE_NAME + "(" + CITY_ID
                    + " integer primary key, " + CITY_SLNO + " integer, " + CITY_NAME
                    + " text is not null )");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Example", "Upgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + CITY_LIST_TABLE_NAME);
            onCreate(db);
        }
    }
}
