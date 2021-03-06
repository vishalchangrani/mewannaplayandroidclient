package com.mewannaplay.asynctask;



import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.mewannaplay.Constants;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.client.RequestMethods;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;

public class GetCourtDetailsAsyncTask extends BasicAsyncTask {

        protected String TAG = "GetCourtDetailsAsyncTask";
        private final int courtId;
        private final Location location;
        
        public GetCourtDetailsAsyncTask(Activity context ,int courtId, Location location) {
                super(context ,Constants.GET_TENNISCOURT_DETAILS + courtId, RequestMethods.GET, "Retreiving court details...");
                this.courtId = courtId;
                this.location = location;
        }

        @Override
        protected boolean isInDatabase()
        {
                Cursor cursor = ownerAcitivty.getContentResolver().query(
                    TennisCourtsDetails.CONTENT_URI, new String[]{"_id"}, " _id = ?",
                    new String[] { courtId + "" }, null);
         try
         {
                 return (cursor.getCount() > 0); //found in local db
         }
         finally
         {
                 if (cursor != null)
                         cursor.close();
         }
        }

        @Override
        protected void parseResponse(JSONObject jsonObject) throws IOException {
                try {
                        TennisCourtDetails tdc = TennisCourtDetails
                                        .fromJSONObject(jsonObject);
                        ownerAcitivty
                                        .getContentResolver()
                                        .insert(ProviderContract.TennisCourtsDetails.CONTENT_URI
                                                        .buildUpon().appendPath(courtId + "").build(),
                                                        tdc.toContentValue());
                        ownerAcitivty
                        .getContentResolver()
                        .bulkInsert(ProviderContract.Acitivity.CONTENT_URI,
                                        tdc.contentValuesForActivity());
                        ownerAcitivty
                        .getContentResolver()
                        .bulkInsert(ProviderContract.Amenity.CONTENT_URI,
                                        tdc.contentValuesForAmenity());
                } catch (Exception e) {
                        // Log.e(TAG, " getCourtDetails "+e.getMessage());
                        throw new IOException(" Conversion error ");
                }
                
        }
        
         
        @Override
        protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                ((MapViewActivity) ownerAcitivty).onPostExecuteGetCourtDetails(result, courtId, location);
        }
        
}
