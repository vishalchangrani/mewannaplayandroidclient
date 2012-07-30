package com.mewannaplay.asynctask;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;

import com.mewannaplay.Constants;
import com.mewannaplay.ViewMessageActivity;
import com.mewannaplay.client.RequestMethods;
import com.mewannaplay.providers.ProviderContract;

public class DeleteMessageAsyncTask extends BasicAsyncTask {

        
          protected String TAG = "PostMessageAsyncTask";
          final int messageId;
 
      
      public DeleteMessageAsyncTask(Activity context, int courtId, int messageId, boolean partnerFound) {
          super(context ,Constants.DELETE_MESSAGE.replace("?", Integer.toString(courtId)) + partnerFound, RequestMethods.DELETE, null, "Deleting message...");
          this.messageId = messageId;
      }
      
    @Override
    protected Boolean doInBackground(Void... v)
    {
        
        boolean isError = super.doInBackground(v);
        if (!isError) //Delete from android database
                ownerAcitivty.getContentResolver().delete(ProviderContract.Messages.CONTENT_URI, " _id = ? ", new String[] {""+messageId});
        return isError;
    }
    
        @Override
        protected void parseResponse(JSONObject jsonObject) throws IOException {
                return;

        }
        
        @Override
        protected void onPostExecute(Boolean result) {
                super.onPreExecute();
                ((ViewMessageActivity)ownerAcitivty).onPostExectureDeleteMessage(result);
        }

}