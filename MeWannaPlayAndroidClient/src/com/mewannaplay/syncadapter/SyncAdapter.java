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

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.mewannaplay.Constants;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.client.RequestMethods;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.City;
import com.mewannaplay.model.Message;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.TennisCourtProvider;

/**
 * SyncAdapter implementation for syncing all tennis courts details on client
 * side with server
 * 
 * The general pattern is,
 * 1. Activity registers a broadcast receiver with intent SYNC_FINISHED_ACTION
 * 2. Acitivty calls ContentResolver.requestSync with desired operation as parameter in the intent bundle along with other
 *    needed params such as court id or message id.
 * 3. Syncadapter runs in a different thread and calls the onPerformSync method.
 * 4. When it finishes it sends a broadcast to with intent containing operation which was finished and is there was an error.
 * 5. Broadcast receiver (registered in step 1 above) checks if error - on error displays error dialog else any other operation and unregisters itself.
 * 
 * NOTE:
 * a. Syncadapter does not retries. Here Syncadapter is only used a service. The advantage being its managed by android.
 * b. Only one broadcast receiver should be registered at one time before calling requestSync. Else all registered broadcast receiver
 * will be notified even if the operation they were interested in is not done 
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = "SyncAdapter";

	//Keys used in intent extras to communicate back result
	public static final String SYNC_FINISHED_ACTION = "SYNC_FINISHED";
	public static final String SYNC_ERROR = "SYNC_ERROR";


	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	public final static String OPERATION = "operation";
	public final static String ACKNOWLEDGEMENT_NEEDED = "ACK_NEEDED"; //Flag to indicate if the caller has a broadcast receiver 
																	  //which needs to be notified when this operation finishes
																	  //True - onPerformSync sends a broadcast when operation finishes (default)
																	  //False - No broadcast is sent (used for periodic operations like courtstats etc. 
	
	//All types of operation
	public static final int GET_ALL_COURTS = 0;
	public static final int GET_COURT_DETAILS = 1;
	public static final int GET_COURT_MESSAGES = 2;
	public static final int POST_MESSAGE = 3;
	public static final int MARK_COURT_OCCUPIED = 4;
	public static final int GET_ALL_CITIES  = 5;
	public static final int DELETE_MESSAGE = 6;
	public static final int GET_OCCUPIED_COURT_AND_POSTED_MSG = 7;
	public static final int GET_COURT_MESSAGE_BY_ID = 8;
	public static final int GET_ALL_COURTS_STATS = 9;
	
	public static final String COURT_ID = "court_id";
	public static final String MESSAGE_ID = "message_id";
	public static final String PARTNER_FOUND = "partner_found";
	public static final String MESSAGE_OBJECT_KEY = "message_object_key";

	/*
	 * Called whenever sync-adapter tries to sync with server. 
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
    	
 
    	if (!extras.containsKey(OPERATION))
			return;
    	
    	int operationRequested = extras.getInt(OPERATION); //what operation is requested?
    	boolean ackNeeded = extras.getBoolean(ACKNOWLEDGEMENT_NEEDED, true); //Is acknowledgment requested for this operation?
    	try
    	{
		switch (operationRequested) {
		case GET_ALL_COURTS:
			//TODO Add code here to truncate tennis_courts and cities table
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
			break;
		case POST_MESSAGE:
			courtId = extras.getInt(COURT_ID);
			JSONObject message = new JSONObject(extras.getString(MESSAGE_OBJECT_KEY));
			postMessage(courtId, message);
			break;
		case MARK_COURT_OCCUPIED:
			markCourtOccupied(extras.getInt(COURT_ID));
			break;
		case GET_ALL_CITIES:
			getAllCities();
			break;
		case DELETE_MESSAGE:
			deleteMessage(extras.getInt(COURT_ID), extras.getInt(MESSAGE_ID),  extras.getBoolean(PARTNER_FOUND, false));
			break;
		case GET_OCCUPIED_COURT_AND_POSTED_MSG:
			getOccupiedCourtAndPostedMsg();
			break;
		case GET_COURT_MESSAGE_BY_ID:
			getCourtMessageByTennisCourtId(extras.getInt(COURT_ID));
			break;
		case GET_ALL_COURTS_STATS:
			new Thread()
			{
				public void run()
				{
					this.setName("getAllCourtStatsThread");
					try {
						Log.d(TAG, "spawning of thread for getAllCourtStats");
						getAllCourtStats();
					} catch (IOException e) {
						Log.e(TAG, "error while doing getcourtstats");
					}
				}
			}.start();
			
			
			break;
		}
    	}
    	catch (Throwable e)
    	{
    		// syncResult.stats.numParseExceptions++; //DO NOT record error..syncadapter otherwise keeps retrying which we dont want.
    		 isError = true;
    		 Log.e(TAG,"Exception for opertaion "+operationRequested);
    	}
    	finally
    	{
    		 
    		 if (ackNeeded) //send broadcast which represent the completion of the operation (broadcast receivers in the calling activity use this to update view)
    		 {
    		  Intent i = new Intent(SYNC_FINISHED_ACTION);
    		  i.putExtra(SYNC_ERROR, isError);
    		  i.putExtra(OPERATION, operationRequested);
    	      this.getContext().sendBroadcast(i);
    		 }
    	}

    }

	

	private void getOccupiedCourtAndPostedMsg() throws IOException {

		Log.d(TAG," getting occupied court id and posted message id");
		RestClient restClient = new RestClient(
				Constants.GET_OCCUPIED_COURT_AND_POSTED_MSG);
		JSONObject jsonObject = restClient.execute();

		try {
			String tennisCourtId = jsonObject.getString("occupied_courtid");
			int tenniscourtid = Integer.parseInt(tennisCourtId);
			Log.d(TAG, "Setting occupied court to "+tenniscourtid);
			MapViewActivity.setCourtMarkedOccupied(tenniscourtid);
		} catch (JSONException e) {
			Log.e(TAG, " Conversion error while retreiving tenniscourtid from "
					+ jsonObject.toString());
			MapViewActivity.setCourtMarkedOccupied(-1);
		} catch (NumberFormatException e) {
			// User has not marked any court occupied
			MapViewActivity.setCourtMarkedOccupied(-1);
		}

		try {
			String messageId = jsonObject.getString("postedmessage_courtid");
			int message = Integer.parseInt(messageId);
			Log.d(TAG, "Setting posted message id to "+message);
			MapViewActivity.setCourtPostedMessageOn(message);
		} catch (JSONException e) {
			Log.e(TAG, " Conversion error while retreiving message id from "
					+ jsonObject.toString());
			MapViewActivity.setCourtPostedMessageOn(-1);
		} catch (NumberFormatException e) {
			// User has not posted any message
			MapViewActivity.setCourtPostedMessageOn(-1);
		}

	}

	private void markCourtOccupied(int courtId) throws IOException {
		RestClient restClient = new RestClient(
				Constants.MARK_COURT_OCCUPIED + courtId);
		restClient.execute();
		
	}


	private void getAllCourts() throws IOException {
	
			RestClient restClient = new RestClient(
					Constants.GET_ALL_TENNISCOURTS);
			
			try {
				JSONObject jsonObject = restClient.execute();
				ContentProviderClient contentProviderClient = this.getContext()
				.getContentResolver().acquireContentProviderClient(ProviderContract.TennisCourts.CONTENT_URI);
				TennisCourtProvider tcp = (TennisCourtProvider) contentProviderClient.getLocalContentProvider();
				tcp.bullkInsertCourts(jsonObject);
				
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				throw new IOException(" Conversion error ");
			}
			
			//commenting out original way of doing json to object to database insertion using google json library since
			//it was extremely slow.
		/*	TennisCourt[] tennisCourts;
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
							contentValues);*/
	
	}

	//Here is where we will pull tennis court details such as occupied, free etc. from the server time to time.
	private void getAllCourtStats() throws IOException
	{

			RestClient restClient = new RestClient(
					Constants.GET_TENNIS_COURT_STATS);
			
			try {
				ContentProviderClient contentProviderClient = this.getContext()
				.getContentResolver().acquireContentProviderClient(ProviderContract.TennisCourts.CONTENT_URI);
				TennisCourtProvider tcp = (TennisCourtProvider) contentProviderClient.getLocalContentProvider();
				tcp.bulkUpdateTennisCourts(restClient.excuteGetAndReturnStream()); 

			} catch (Exception e) {
				Log.e(TAG, " Exception while doing get all court stats "+e.getMessage());
				throw new IOException(" Conversion error ");
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
			this.getContext()
			.getContentResolver()
			.bulkInsert(ProviderContract.Acitivity.CONTENT_URI,
					tdc.contentValuesForActivity());
			this.getContext()
			.getContentResolver()
			.bulkInsert(ProviderContract.Amenity.CONTENT_URI,
					tdc.contentValuesForAmenity());
		} catch (Exception e) {
			Log.e(TAG, " getCourtDetails "+e.getMessage());
			throw new IOException(" Conversion error ");
		}
	}

	public void getCourtMessages(int courtId) throws IOException {

		Log.d(TAG, " in getCourtMessages...");
		ContentValues[] contentValues = null;
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
		Log.d(TAG, "now calling bulk insert for messages");
		this.getContext()
				.getContentResolver()
				.bulkInsert(ProviderContract.Messages.CONTENT_URI,
						contentValues);
	}

	
   /* Very similar to getCourtMessages except only the message posted by this user is retrieved
    */
	private void getCourtMessageByTennisCourtId(int courtId) throws IOException {
    	Log.d(TAG, " in getCourtMessageByTennisCourtId...");
    	RestClient restClient = new RestClient(
				Constants.GET_TENNISCOURT_MESSAGE_BY_COURTID + courtId);
    	ContentValues contentValue = new ContentValues();
    	try {
			Message message = Message.fromSingleJSONObject(restClient.execute());
			contentValue = message.toContentValue();
    	}
    	 catch (Exception e) {
 			Log.e(TAG, e.getMessage());
 			throw new IOException(" Conversion error ");
 		}
			Log.d(TAG, "now calling bulk insert for this message");
			this.getContext()
					.getContentResolver()
					.bulkInsert(ProviderContract.Messages.CONTENT_URI,
							new ContentValues[] { contentValue});
  
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
			//getContext().getContentResolver().notifyChange(
			//		ProviderContract.Cities.CONTENT_URI, null, false);
			throw e;
		}
	}
	
	public void postMessage(int courtId, JSONObject message) throws IOException, JSONException
	{
		
			RestClient restClient = new RestClient(
					Constants.POST_MESSAGE);
			restClient.execute(RequestMethods.POST, message);	
	}
	
	public void deleteMessage(int courtId, int messageId, boolean partnerFound) throws IOException
	{
		String deleteMessageURL = Constants.DELETE_MESSAGE.replace("?", Integer.toString(courtId)) + partnerFound;
		
		RestClient restClient = new RestClient(
				deleteMessageURL);
		restClient.execute(RequestMethods.DELETE,null);	
		//Delete from android database
		this.getContext()
		.getContentResolver().delete(ProviderContract.Messages.CONTENT_URI, " _id = ? ", new String[] {""+messageId});
		
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

	
	public static Bundle getMessagePostedByUserBundle(int courtId) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_COURT_MESSAGE_BY_ID);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		return extras;
	}
	
	//Different than the other bundles since in get all messages we want to continously refresh
	//the list by letting the syncadapter run periodically.
	public static Bundle getAllMessagesBundle(int courtId) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_COURT_MESSAGES);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putBoolean(SyncAdapter.ACKNOWLEDGEMENT_NEEDED, false); //default is true hence we have to specify fault here explicitly
		//extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		//extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		//extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		//extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		//extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		return extras;
	}
	
	
	//Different than the other bundles since  court occupied by user and message posted by him if any
		//is queried periodically from mapviewactivity if user is not anonymous
		public static Bundle getOccupiedCourtAndPostedMsgBundle() {
			Bundle extras = new Bundle();
			extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_OCCUPIED_COURT_AND_POSTED_MSG);
			extras.putBoolean(SyncAdapter.ACKNOWLEDGEMENT_NEEDED, false);
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
	
	public static Bundle getPostMessageBundle(int courtId, String messageAsJSONString) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.POST_MESSAGE);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putString(SyncAdapter.MESSAGE_OBJECT_KEY, messageAsJSONString);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		
		return extras;
	}
	
	public static Bundle getDeleteMessageBundle(int courtId, int messageId, boolean partnerFound) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.DELETE_MESSAGE);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putInt(SyncAdapter.MESSAGE_ID, messageId);
		extras.putBoolean(SyncAdapter.PARTNER_FOUND, partnerFound);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);	
		return extras;
	}
	
	public static Bundle getMarkOccupiedBundle(int courtId) {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.MARK_COURT_OCCUPIED);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY , true);
		return extras;
	}
	
	public static Bundle getAllCourtsStatsBundle() {
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.OPERATION, SyncAdapter.GET_ALL_COURTS_STATS);
		extras.putBoolean(SyncAdapter.ACKNOWLEDGEMENT_NEEDED, false); 
		return extras;
	}
}
