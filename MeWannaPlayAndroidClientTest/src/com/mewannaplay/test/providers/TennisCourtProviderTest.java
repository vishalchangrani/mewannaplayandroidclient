package com.mewannaplay.test.providers;

import static com.mewannaplay.providers.ProviderContract.AUTHORITY;
import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.test.ProviderTestCase2;

import com.mewannaplay.Constants;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.TennisCourts;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;
import com.mewannaplay.providers.TennisCourtProvider;
import com.mewannaplay.syncadapter.SyncAdapter;

public class TennisCourtProviderTest extends ProviderTestCase2<TennisCourtProvider> {

  
	public TennisCourtProviderTest() {
		super(TennisCourtProvider.class, AUTHORITY);		
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		ContentProvider cp  = getMockContentResolver()
            .acquireContentProviderClient(TennisCourts.CONTENT_URI)
            .getLocalContentProvider();
		cp.delete(TennisCourts.CONTENT_URI, null, null);
		cp.delete(TennisCourtsDetails.CONTENT_URI, null, null);

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
		 cotentValues1.put("_id",3);
		 cotentValues1.put("latitude",80);
		 cotentValues1.put("longitude",0);
		 cotentValues1.put("subcourts",1);
		 cotentValues1.put("occupied",1);
		 cotentValues1.put("facility_type","type A");
		 cotentValues1.put("name","tennis court 1");
		 cotentValues1.put("message_count",1);
		 cotentValues[0] = cotentValues1;
		 ContentValues cotentValues2 = new ContentValues();
		 cotentValues2.put("_id",2);
		 cotentValues2.put("latitude","80");
		 cotentValues2.put("longitude","0");
		 cotentValues2.put("subcourts",1);
		 cotentValues2.put("occupied",1);
		 cotentValues2.put("facility_type","type B");
		 cotentValues2.put("name","tennis court 2");
		 cotentValues2.put("message_count",1);
		 cotentValues[1] = cotentValues2;
		 int count = cr.bulkInsert(TennisCourts.CONTENT_URI, cotentValues);
		 assertEquals(2,count);
		 Cursor cursor = cr.query(TennisCourts.CONTENT_URI, null, null, null, null);
		 assertNotNull(cursor);
	     assertEquals(2, cursor.getCount());
	     cursor.moveToFirst();
	        while (cursor.isAfterLast() == false) {
	            System.out.println(cursor.getString(cursor.getColumnIndex("name")));
	            cursor.moveToNext();
	        }
	     cursor.close();
	 }
	 
	 public void testContentResolverQuery()
 {

		Cursor cursor = getMockContentResolver()
				.query(ProviderContract.TennisCourts.CONTENT_URI,
						null,
						" (longitude >= ? and longitude < ?) and (latitude >= ? and latitude < ?) ",
						new String[] { "-122.084095", "-122.084095",
								"37.422006", "37.422006" }, null);
	

		RectF testRect = new RectF(-122.084095f, 37.422006f, -120.084095f, 40.422006f);
		cursor.moveToFirst();
		
	}
	 
	 
	 public void testSyncAdapterOnPerformSync() throws InterruptedException
	 {
		 	testBulkInsert();
		 	Bundle extras = new Bundle(); 
			extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_ALL_COURTS);
//			extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//			extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
//			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			TestContentObserver co = new TestContentObserver();
			getMockContentResolver().registerContentObserver(
					ProviderContract.TennisCourts.CONTENT_URI, true,
					co);
			 final Account account = new Account("anonymous", Constants.ACCOUNT_TYPE);
			// Request first sync..
//			 getMockContentResolver().requestSync(account,
//					ProviderContract.AUTHORITY,extras);	
		
			Thread.sleep(5000);
			assertEquals(true, co.cursorObserverIsTriggered);

	 }
	 
	 public void testSyncAdapterOnPerformSync2() throws InterruptedException
	 {
		 	Bundle extras = new Bundle(); 
			extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_COURT_DETAILS);
			extras.putInt(SyncAdapter.COURT_ID, 13604);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			TestContentObserver co = new TestContentObserver();
			getMockContentResolver().registerContentObserver(
					ProviderContract.TennisCourtsDetails.CONTENT_URI, true,
					co);
			 final Account account = new Account("anonymous", Constants.ACCOUNT_TYPE);
			// Request first sync..
			 //NOTE -  getMockContentResolver().requestSync DOES NOT WORK..the dbhelper returns actual db and not test db
			 //hence not using here !!! but this needs to be fixed. dont know how to test with syncadapter in picture
				SyncAdapter sq = new SyncAdapter(this.getMockContext(), false);
				sq.getCourtDetails(13604);
				 Cursor cursor = getMockContentResolver().query(TennisCourtsDetails.CONTENT_URI, null, null, null, null);
				assertEquals(cursor.getCount(),1);
				 cursor.moveToFirst();
				assertEquals(cursor.getInt(cursor.getColumnIndex("_id")),13604);
				
		

	 }
	 
	public class TestContentObserver extends ContentObserver {

		public boolean cursorObserverIsTriggered = false;
		public TestContentObserver() {
			super(null);

		}

		@Override
		public boolean deliverSelfNotifications() {
			return false;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			cursorObserverIsTriggered = true;
		}
	};
}
