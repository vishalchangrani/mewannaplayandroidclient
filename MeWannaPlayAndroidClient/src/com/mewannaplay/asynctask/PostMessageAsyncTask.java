package com.mewannaplay.asynctask;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;

import com.mewannaplay.Constants;
import com.mewannaplay.PostMessageActivity;
import com.mewannaplay.client.RequestMethods;

public class PostMessageAsyncTask extends BasicAsyncTask {

	
	  protected String TAG = "PostMessageAsyncTask";
   
 
      
      public PostMessageAsyncTask(Activity context ,JSONObject message) {
              super(context ,Constants.POST_MESSAGE, RequestMethods.POST, message, "Posting message...");
         
      }
	@Override
	protected void parseResponse(JSONObject jsonObject) throws IOException {
		return;

	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPreExecute();
		((PostMessageActivity)ownerAcitivty).onPostExecutePostMessageTask(result);
	}

}
