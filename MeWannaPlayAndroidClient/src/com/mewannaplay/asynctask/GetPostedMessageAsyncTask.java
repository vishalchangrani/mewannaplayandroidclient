package com.mewannaplay.asynctask;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;

import com.mewannaplay.Constants;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.client.RequestMethods;
import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;

public class GetPostedMessageAsyncTask extends BasicAsyncTask {

	public GetPostedMessageAsyncTask(Activity context, int courtId) {
		super(context, Constants.GET_TENNISCOURT_MESSAGE_BY_COURTID + courtId, RequestMethods.GET,
				"Retreiving posted message...");
		
	}
	
	 @Override
	 protected Boolean doInBackground(Void... v) {
		ownerAcitivty.getContentResolver().delete(ProviderContract.Messages.CONTENT_URI,
					null, null); // clean message table
		 return super.doInBackground(v);
		
	}

	@Override
	protected void parseResponse(JSONObject jsonObject) throws IOException {
	   	Log.d(TAG, " in getCourtMessageByTennisCourtId...");
   
    	ContentValues contentValue = new ContentValues();
    	try {
			Message message = Message.fromSingleJSONObject(jsonObject);
			contentValue = message.toContentValue();
    	}
    	 catch (Exception e) {
 			Log.e(TAG, e.getMessage());
 			throw new IOException(" Conversion error ");
 		}
			Log.d(TAG, "now calling bulk insert for this message");
			ownerAcitivty
					.getContentResolver()
					.bulkInsert(ProviderContract.Messages.CONTENT_URI,
							new ContentValues[] { contentValue});
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		((MapViewActivity) ownerAcitivty).onPostExecutePostedMessageTask(result);
	}
}
