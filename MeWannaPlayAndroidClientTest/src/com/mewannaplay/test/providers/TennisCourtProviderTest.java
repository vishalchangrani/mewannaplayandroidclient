package com.mewannaplay.test.providers;

import static com.mewannaplay.providers.ProviderContract.*;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

import com.mewannaplay.providers.ProviderContract.TennisCourts;
import com.mewannaplay.providers.TennisCourtProvider;

public class TennisCourtProviderTest extends ProviderTestCase2<TennisCourtProvider> {

    
	public TennisCourtProviderTest() {
		super(TennisCourtProvider.class, AUTHORITY);		
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	 public void testCreate() {
	        ContentResolver cr = getMockContentResolver();
	        Cursor cursor = cr.query(TennisCourts.CONTENT_URI, null, null, null, null);
	        assertNotNull(cursor);
	        assertEquals(0, cursor.getCount());
	 }
	 
	 public void testBulkInsert()
	 {
		 ContentResolver cr = getMockContentResolver();
		 ContentValues[] cotentValues = new ContentValues[2];
		 ContentValues cotentValues1 = new ContentValues();
		 cotentValues1.put("id",1);
		 cotentValues1.put("tennis_latitude",80);
		 cotentValues1.put("tennis_longitude",0);
		 cotentValues1.put("tennis_subcourts",1);
		 cotentValues1.put("occupied",1);
		 cotentValues1.put("tennis_facility_type","type A");
		 cotentValues1.put("tennis_name","tennis court 1");
		 cotentValues1.put("message_count",1);
		 cotentValues[0] = cotentValues1;
		 ContentValues cotentValues2 = new ContentValues();
		 cotentValues2.put("id",2);
		 cotentValues2.put("tennis_latitude","80");
		 cotentValues2.put("tennis_longitude","0");
		 cotentValues2.put("tennis_subcourts",1);
		 cotentValues2.put("occupied",1);
		 cotentValues2.put("tennis_facility_type","type B");
		 cotentValues2.put("tennis_name","tennis court 2");
		 cotentValues2.put("message_count",1);
		 cotentValues[1] = cotentValues2;
		 int count = cr.bulkInsert(TennisCourts.CONTENT_URI, cotentValues);
		 assertEquals(2,count);
		 Cursor cursor = cr.query(TennisCourts.CONTENT_URI, null, null, null, null);
		 assertNotNull(cursor);
	     assertEquals(2, cursor.getCount());
	     cursor.moveToFirst();
	        while (cursor.isAfterLast() == false) {
	            System.out.println(cursor.getString(cursor.getColumnIndex("tennis_name")));
	            cursor.moveToNext();
	        }
	     cursor.close();
	 }
}
