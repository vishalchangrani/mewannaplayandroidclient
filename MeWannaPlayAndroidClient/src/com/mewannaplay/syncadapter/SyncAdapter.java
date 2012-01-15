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

import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.mewannaplay.Constants;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.TennisCourt;
import com.mewannaplay.providers.ProviderContract;

/**
 * SyncAdapter implementation for syncing all tennis courts details on client side with server
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";

    private final AccountManager mAccountManager;
 
    public static final Intent SYNC_FINISHED = new Intent();

    private Date mLastUpdated;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

 /*
  * Called whenever sync-adapter tries to sync with server. 
  * This function in turns tries to get the authtoken from account manager.
  * @see android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts.Account, android.os.Bundle, java.lang.String, android.content.ContentProviderClient, android.content.SyncResult)
  */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
   
    	Log.d(TAG,"in onPerform sync");
    	//Here is where we will pull tennis court details such as occupied, free etc. from the server time to time.
  /*      List<User> users;
        List<Status> statuses;
        String authtoken = null;
         try {
             // use the account manager to request the credentials
             authtoken =
                mAccountManager.blockingGetAuthToken(account,
                    Constants.AUTHTOKEN_TYPE, true  notifyAuthFailure );
             // fetch updates from the sample service over the cloud
             users =
                NetworkUtilities.fetchFriendUpdates(account, authtoken,
                    mLastUpdated);
            // update the last synced date.
            mLastUpdated = new Date();
            // update platform contacts.
            Log.d(TAG, "Calling contactManager's sync contacts");
            ContactManager.syncContacts(mContext, account.name, users);
            // fetch and update status messages for all the synced users.
            statuses = NetworkUtilities.fetchFriendStatuses(account, authtoken);
            ContactManager.insertStatuses(mContext, account.name, statuses);
        } catch (final AuthenticatorException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "AuthenticatorException", e);
        } catch (final OperationCanceledException e) {
            Log.e(TAG, "OperationCanceledExcetpion", e);
        } catch (final IOException e) {
            Log.e(TAG, "IOException", e);
            syncResult.stats.numIoExceptions++;
        } catch (final AuthenticationException e) {
            mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE,
                authtoken);
            syncResult.stats.numAuthExceptions++;
            Log.e(TAG, "AuthenticationException", e);
        } catch (final ParseException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "ParseException", e);
        } catch (final JSONException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "JSONException", e);
        }*/
    //	Intent i = new Intent(SYNC_FINISHED);
    //	sendBroadcast(i);
    	getAllCourts();
    	
    }
    
    private void getAllCourts()
    {
    	
    	RestClient restClient = new RestClient(Constants.GET_ALL_TENNISCOURTS);
    	TennisCourt[] tennisCourts;
		try {
			tennisCourts = (TennisCourt[]) restClient.execute(TennisCourt[].class, "tenniscourt", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	ContentValues[] contentValues = new ContentValues[tennisCourts.length];
    	int i = 0;
    	for (TennisCourt tennisCourt : tennisCourts)
    	{
    		contentValues[i++] = tennisCourt.toContentValue();
    	}
    	this.getContext().getContentResolver().bulkInsert(ProviderContract.TennisCourts.CONTENT_URI, contentValues);
    }
}
