/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mewannaplay.syncadapter;

import java.io.IOException;
import java.util.Date;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import com.mewannaplay.Constants;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.City;
import com.mewannaplay.model.Message;
import com.mewannaplay.model.TennisCourt;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract;

/**
 * SyncAdapter implementation for syncing all tennis courts details on client
 * side with server
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = "SyncAdapter";

	private final AccountManager mAccountManager;

	public static final String SYNC_FINISHED_ACTION = "SYNC_FINISHED";
	public static final String SYNC_ERROR = "SYNC_ERROR";

	private Date mLastUpdated;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	public final static String OPERATION = "operation";
	public static final int GET_ALL_COURTS = 0;
	public static final int GET_COURT_DETAILS = 1;
	public static final int GET_COURT_MESSAGES = 2;
	public static final int POST_MESSAGE = 3;
	public static final int MARK_COURT_OCCUPIED = 4;
	public static final int GET_ALL_CITIES  = 5;
	public static final String COURT_ID = "court_id";

	/*
	 * Called whenever sync-adapter tries to sync with server. This function in
	 * turns tries to get the authtoken from account manager.
	 * 
	 * @see
	 * android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts
	 * .Account, android.os.Bundle, java.lang.String,
	 * android.content.ContentProviderClient, android.content.SyncResult)
	 */
	@Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
   
    	Log.d(TAG,"in onPerform sync");
    	boolean isError = false;
    	//Here is where we will pull tennis court details such as occupied, free etc. from the server time to time.
 
    	try
    	{
		
    	int operationRequested = extras.getInt(OPERATION);
		switch (operationRequested) {
		case GET_ALL_COURTS:
			getAllCourts();
			getAllCities();
			break;
		case GET_COURT_DETAILS:
			int courtId = extras.getInt(COURT_ID);
			getCourtDetails(courtId);
			break;
		case GET_COURT_MESSAGES:
			courtId = extras.getInt(COURT_ID);
			getCourtMessages(courtId);
		case POST_MESSAGE:
			break;
		case MARK_COURT_OCCUPIED:
			break;
		case GET_ALL_CITIES:
			getAllCities();
			break;
		}
    	}catch (IOException e)
    	{
    		 syncResult.stats.numIoExceptions++;
    		 isError = true;
    	}
    	catch (SQLException e)
    	{
    		syncResult.databaseError = true;
    		isError = true;
    	}
    	finally
    	{
    		  Intent i = new Intent(SYNC_FINISHED_ACTION);
    		  i.putExtra(SYNC_ERROR, isError);
    	      this.getContext().sendBroadcast(i);
    	}

    }

	private void getAllCourts() throws IOException {
		try {
			if (false) {
				getContext().getContentResolver().notifyChange(
						ProviderContract.TennisCourts.CONTENT_URI, null, false);
				return;
			}
			// if one day has elapsed then get new list...
			RestClient restClient = new RestClient(
					Constants.GET_ALL_TENNISCOURTS);
			TennisCourt[] tennisCourts;
			try {
				tennisCourts = TennisCourt.fromJSONObjectArray(restClient
						.execute());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				throw new IOException(" Conversion error ");
			}

			ContentValues[] contentValues = new ContentValues[tennisCourts.length];
			int i = 0;
			for (TennisCourt tennisCourt : tennisCourts) {
				contentValues[i++] = tennisCourt.toContentValue();
			}
			this.getContext()
					.getContentResolver()
					.bulkInsert(ProviderContract.TennisCourts.CONTENT_URI,
							contentValues);
		} catch (IOException e) {
			// Following is to notify listener to remove spinner.
			// Unfortunately there is no way at this point to listen for
			// sysnadapter perform sync completion
			getContext().getContentResolver().notifyChange(
					ProviderContract.TennisCourts.CONTENT_URI, null, false);
			throw e;
		}
	}

	public void getCourtDetails(int courtId) throws IOException {
		RestClient restClient = new RestClient(
				Constants.GET_TENNISCOURT_DETAILS + courtId);
		JSONObject jsonObject = restClient.execute();
		try {
			TennisCourtDetails tdc = TennisCourtDetails
					.fromJSONObject(jsonObject);
			this.getContext()
					.getContentResolver()
					.insert(ProviderContract.TennisCourtsDetails.CONTENT_URI
							.buildUpon().appendPath(courtId + "").build(),
							tdc.toContentValue());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			throw new IOException(" Conversion error ");
		}
	}

	public void getCourtMessages(int courtId) throws IOException {

		ContentValues[] contentValues = null;
		this.getContext().getContentResolver()
				.delete(ProviderContract.Messages.CONTENT_URI, null, null);
		courtId = 13604;
		RestClient restClient = new RestClient(
				Constants.GET_TENNISCOURT_MESSAGES + courtId);
		try {
			Message[] messages = Message.fromJSONObject(restClient.execute());
			contentValues = new ContentValues[messages.length];
			int i = 0;
			for (Message message : messages) {
				contentValues[i++] = message.toContentValue();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			throw new IOException(" Conversion error ");
		}
		this.getContext()
				.getContentResolver()
				.bulkInsert(ProviderContract.Messages.CONTENT_URI,
						contentValues);

	}

	public void getAllCities() throws IOException
	{
		try {
			RestClient restClient = new RestClient(
					Constants.GET_ALL_CITIES);
			City[] cities;
			try {
				cities = City.fromJSONObjectArray(restClient
						.execute());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				throw new IOException(" Conversion error ");
			}

			ContentValues[] contentValues = new ContentValues[cities.length];
			int i = 0;
			for (City city : cities) {
				contentValues[i++] = city.toContentValue();
			}
			this.getContext()
					.getContentResolver()
					.bulkInsert(ProviderContract.Cities.CONTENT_URI,
							contentValues);
		} catch (IOException e) {
			getContext().getContentResolver().notifyChange(
					ProviderContract.Cities.CONTENT_URI, null, false);
			throw e;
		}
	}
	
	
	public static Bundle getAllCourtsBundle() {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_ALL_COURTS);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		return extras;
	}

	public static Bundle getAllCourtsDetailBundle(int courtId) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_COURT_DETAILS);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		
		 
		return extras;
	}

	public static Bundle getAllMessagesBundle(int courtId) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_COURT_MESSAGES);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		return extras;
	}
	
	
	public static Bundle getAllCitiesBundle() {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_ALL_CITIES);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		return extras;
	}
}
