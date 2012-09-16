package com.mewannaplay.asynctask;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;

import com.mewannaplay.Constants;
import com.mewannaplay.CourtDetailsActivity;
import com.mewannaplay.client.RequestMethods;
import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;

public class GetAllMessagesForCourtAsyncTask extends BasicAsyncTask {

        public GetAllMessagesForCourtAsyncTask(Activity context, int courtId) {
                super(context, Constants.GET_TENNISCOURT_MESSAGES + courtId, RequestMethods.GET,
                                null);
                
        }
        
         @Override
         protected Boolean doInBackground(Void... v) {
                ownerAcitivty.getContentResolver().delete(ProviderContract.Messages.CONTENT_URI,
                                        null, null); // clean message table
                 return super.doInBackground(v);
                
        }

         
        @Override
	protected void parseResponse(JSONObject jsonObject) throws IOException {

		ContentValues[] contentValues = null;
		try {
			Message[] messages = Message.fromJSONObject(jsonObject);
			contentValues = new ContentValues[messages.length];
			int i = 0;
			for (Message message : messages) {
				contentValues[i++] = message.toContentValue();
			}
			// Log.d(TAG, "now calling bulk insert for messages");
			ownerAcitivty.getContentResolver().bulkInsert(
					ProviderContract.Messages.CONTENT_URI, contentValues);

		} catch (Exception e) {
			// Log.e(TAG, e.getMessage());
			throw new IOException(" Conversion error ");
		}

	}

        @Override
        protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                ((CourtDetailsActivity) ownerAcitivty).onPostExecutePostedMessageTask(result);
        }
}
