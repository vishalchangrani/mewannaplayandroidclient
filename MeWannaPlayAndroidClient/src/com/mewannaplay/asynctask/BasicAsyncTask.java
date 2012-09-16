package com.mewannaplay.asynctask;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.mewannaplay.client.RequestMethods;
import com.mewannaplay.client.RestClient;



public abstract class BasicAsyncTask extends AsyncTask<Void, Void, Boolean>  {

        protected String TAG = "BasicAsyncTask";
        protected Activity ownerAcitivty;
        
        private volatile boolean running = true;
        private final ProgressDialog progressDialog;
 

        
        private final String SERVER_URL;
        private final RequestMethods httpRequestMethod;
        private final JSONObject jsonObjectToPost;
        private boolean isError = false;
        private String errorMessage = null;
        
        
        
        public boolean isError() {
                return isError;
        }



        public String getErrorMessage() {
                return errorMessage;
        }



        public BasicAsyncTask(Activity context, String serverUrl,RequestMethods httpRequestMethodToUse, String progressDialogMessage)
        {
                this(context,serverUrl,httpRequestMethodToUse, null, progressDialogMessage);
        }

        public BasicAsyncTask(Activity context, String serverUrl,RequestMethods httpRequestMethodToUse, JSONObject jsonObjectToPost, String progressDialogMessage)
        {
                SERVER_URL = serverUrl;
                httpRequestMethod = httpRequestMethodToUse;
                this.ownerAcitivty = context;
                this.jsonObjectToPost = jsonObjectToPost;
        
                if (progressDialogMessage != null)
                {
                	progressDialog = new ProgressDialog(ownerAcitivty);
                	progressDialog.setTitle("");
                	progressDialog.setMessage(progressDialogMessage);
                	progressDialog.setCancelable(true);
                	progressDialog.setOnCancelListener(new OnCancelListener() {
                		@Override
                		public void onCancel(DialogInterface dialog) {
                			// actually could set running = false; right here, but I'll
                			// 	stick to contract.
                			cancel(true);
                		}
                	});
                }
                else
                	progressDialog = null;
                	
         }

        
        
        @Override
    protected void onCancelled() {
        running = false;
        if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
    }


        @Override
    protected void onPreExecute() {
        	if (progressDialog != null)
                progressDialog.show();
    }

        
        
    @Override
    protected void onPostExecute(Boolean result) {
       super.onPostExecute(result);
       // Log.d(TAG," executing onPostExecute for server url - "+SERVER_URL+" result is "+result);
       if (progressDialog != null)
    	   progressDialog.dismiss();
    }

    @Override
    protected Boolean doInBackground(Void... v) {
       // Log.d(TAG, " executing asynctask for server url -"+SERVER_URL);
       JSONObject response = null;
       try {
        if (running)
        {
                        if (isInDatabase())
                        {
                                // Log.d(TAG, " Found in database");
                                return false;
                        }
        }
        if (running)
                response = sendRequest();
        if(running)
                parseResponse(response);
        } catch (Exception e) {
                isError = true;
                errorMessage = e.getMessage();
                return isError;
        }
       return isError;
    }
    
    protected boolean isInDatabase()
    {
        return false;
    }
    protected JSONObject sendRequest() throws IOException
    {
        RestClient restClient = new RestClient(SERVER_URL);
        return restClient.execute(httpRequestMethod, jsonObjectToPost);         
    }
    
    protected abstract void parseResponse(JSONObject jsonObject) throws IOException;
 
}
