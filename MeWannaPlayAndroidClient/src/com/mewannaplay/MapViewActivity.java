package com.mewannaplay;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.mewannaplay.mapoverlay.MapLocationOverlay;
import com.mewannaplay.providers.ProviderContract;

public class MapViewActivity extends MapActivity {

	private static final String TAG = "MapViewActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AccountManager mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		Account annonymousAccount = null;
		if (accounts.length > 0)
			annonymousAccount = accounts[0];
		else
			Log.e(TAG, "Anonymous account not found"); // TODO figure what to do
														// then

		// Request first sync..
		ContentResolver.requestSync(annonymousAccount,
				ProviderContract.AUTHORITY, new Bundle());
		getContentResolver().registerContentObserver(
				ProviderContract.AUTHORITY_URI, true,
				new TennisCourtContentObserver());

		setContentView(R.layout.mapviewlayout);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getOverlays().add(new MapLocationOverlay(this));

		/*
		 * ContentResolver cr = getContentResolver();
		 * cr.delete(TennisCourts.CONTENT_URI, null, null); Cursor cursor =
		 * cr.query(TennisCourts.CONTENT_URI, null, null, null, null);
		 * cursor.close(); ContentValues[] cotentValues = new ContentValues[2];
		 * ContentValues cotentValues1 = new ContentValues();
		 * cotentValues1.put("id",1); cotentValues1.put("tennis_latitude","80");
		 * cotentValues1.put("tennis_longitude","0");
		 * cotentValues1.put("tennis_subcourts",1);
		 * cotentValues1.put("occupied",1);
		 * cotentValues1.put("tennis_facility_type","type A");
		 * cotentValues1.put("tennis_name","tennis court 1");
		 * cotentValues1.put("message_count",1); cotentValues[0] =
		 * cotentValues1; ContentValues cotentValues2 = new ContentValues();
		 * cotentValues2.put("id",2); cotentValues2.put("tennis_latitude","80");
		 * cotentValues2.put("tennis_longitude","0");
		 * cotentValues2.put("tennis_subcourts",1);
		 * cotentValues2.put("occupied",1);
		 * cotentValues2.put("tennis_facility_type","type B");
		 * cotentValues2.put("tennis_name","tennis court 2");
		 * cotentValues2.put("message_count",1); cotentValues[1] =
		 * cotentValues2; int count = cr.bulkInsert(TennisCourts.CONTENT_URI,
		 * cotentValues);
		 */

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class TennisCourtContentObserver extends ContentObserver {

		public TennisCourtContentObserver() {
			super(null);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return false;
		}

		@Override
		public void onChange(boolean arg0) {
			super.onChange(arg0);

			Log.v("SMS", "Notification on SMS observer");

			Uri uriSMSURI = Uri.parse("content://sms/");
			Cursor cur = getContentResolver().query(uriSMSURI, null, null,
					null, null);
			cur.moveToNext();
			String protocol = cur.getString(cur.getColumnIndex("protocol"));
			if (protocol == null) {
				Log.d("SMS", "SMS SEND");
				int threadId = cur.getInt(cur.getColumnIndex("thread_id"));
				Log.d("SMS", "SMS SEND ID = " + threadId);
				getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + threadId),
						null, null);

			} else {
				Log.d("SMS", "SMS RECIEVE");
				int threadIdIn = cur.getInt(cur.getColumnIndex("thread_id"));
				getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + threadIdIn),
						null, null);
			}

		}
	}
}
