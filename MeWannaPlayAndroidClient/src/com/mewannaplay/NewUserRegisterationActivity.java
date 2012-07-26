package com.mewannaplay;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mewannaplay.authenticator.AuthenticatorActivity;
import com.mewannaplay.client.RestClient;

public class NewUserRegisterationActivity extends Activity {

	
	private String userName;
	private String password, confirmPassword;
	private String email;
	
	private EditText newUserNameEditText,newUserPasswordEditText, newUserEmailEditText, newUserPasswordConfirmEditText;
	private TextView errorMessage;
	
	private NewUserRegisterTask newUserRegisterationTask = null;
	private ProgressDialog mProgressDialog = null;
	
	String regexStr = "[a-zA-Z0-9]+";
	private static final String TAG = "NewUserRegisterationActivity";
	Typeface bold,heavy,light,normal;
	Button cancel,register;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.new_user_registeration_layout); 
		  newUserNameEditText = (EditText) findViewById(R.id.newuser_username);
	      newUserPasswordEditText = (EditText) findViewById(R.id.newuser_password);
	      newUserPasswordConfirmEditText = (EditText) findViewById(R.id.confirm_password);
	      newUserEmailEditText = (EditText) findViewById(R.id.newuser_email);
	      errorMessage = (TextView) findViewById(R.id.newuser_errormessage);
	      bold=Typeface.createFromAsset(this.getAssets(),"Folks-Bold.ttf");
			 heavy=Typeface.createFromAsset(this.getAssets(),"Folks-Heavy.ttf");
			 light=Typeface.createFromAsset(this.getAssets(),"Folks-Light.ttf");
			 normal=Typeface.createFromAsset(this.getAssets(),"Folks-Normal.ttf");
			 cancel=(Button)findViewById(R.id.cancel);
			 cancel.setTypeface(bold);
			 register=(Button)findViewById(R.id.Register);
			 register.setTypeface(bold);
	}
	
	
	 public void onCancel(View v)
	 {
		finish();
	 }
	 
	 public void onRegister(View v)
	 {
		 userName = newUserNameEditText.getText().toString().trim();
		 password = newUserPasswordEditText.getText().toString().trim();
		 confirmPassword = newUserPasswordConfirmEditText.getText().toString().trim();
		 email = newUserEmailEditText.getText().toString().trim();
		 
		 String userErrorMessage = getMessage();
		 if (userErrorMessage != null)
			 errorMessage.setText(userErrorMessage);
		 else
		 {
			    // Show a progress dialog, and kick off a background task to perform
	            // the user login attempt.
			 if (new EmailValidator().validate(email)){
					if (userName.matches(regexStr) == true) {
	            showProgress();
	            newUserRegisterationTask = new NewUserRegisterTask();
	            newUserRegisterationTask.execute();
	            }else{
	            	
	            	errorMessage.setText("Invalid Username");
	            }
			 }else{
				 
				 errorMessage.setText("Invalid Email");
			 }
		 }
	 
	 }
	 
	  /**
	     * Shows the progress UI for a lengthy operation.
	     */
	    private void showProgress() {
	        showDialog(0);
	    }

	 @Override
	    protected Dialog onCreateDialog(int id, Bundle args) {
	        final ProgressDialog dialog = new ProgressDialog(this);
	        dialog.setMessage("Registering new user...");
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(true);
	        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                Log.i(TAG, "user cancelling authentication");
	                if (newUserRegisterationTask != null) {
	                	newUserRegisterationTask.cancel(true);
	                }
	            }
	        });
	        // We save off the progress dialog in a field so that we can dismiss
	        // it later. We can't just call dismissDialog(0) because the system
	        // can lose track of our dialog if there's an orientation change.
	        mProgressDialog = dialog;
	        return dialog;
	    }

	    /**
	     * Represents an asynchronous task used to authenticate a user against the
	     * backend Service
	     */
	    public class NewUserRegisterTask extends AsyncTask<Void, Void, Void> {

	    	private String error = null;
	    	
	    	public String getError()
	    	{
	    		return error;
	    	}
	    	
	        @Override
	        protected Void doInBackground(Void... params) {

	            try {
	                RestClient.registerNewUser(userName, password, email);
	            	error = "";
	            	return null;
	            } catch (Exception ex) {
	                Log.e(TAG, "NewUserRegisterTask.doInBackground: failed to register user");
	                Log.i(TAG, ex.toString());
	                error = ex.getMessage();
	                return null;
	            }
	        }
	        
	        @Override
	        protected void onPostExecute(Void nothing) {
	        	onRegisterationResult();
	        	return;
	        }
	    }
	    
	    
	    private void onRegisterationResult()
	    {
	        String error = newUserRegisterationTask.getError();
	    	boolean success =  error.trim().equals("");
	        Log.i(TAG, "onRegisterationResult(" + success + ")");

	        // Our task is complete, so clear it out
	        newUserRegisterationTask = null;

	        // Hide the progress dialog
	       
           int resultCode;
           Intent resultIntent = new Intent();
	        if (success) {
	        	
	        	resultCode = Activity.RESULT_OK;
	        	resultIntent.putExtra(AuthenticatorActivity.PARAM_USERNAME, userName);
	        	resultIntent.putExtra(AuthenticatorActivity.PARAM_PASSWORD, password);
	        	
	        	Log.d(TAG, "onRegisterationResult: Created user "+userName+" successfully");
	        	mProgressDialog.setMessage("Welcome to MeWannaPlay "+userName);
	        	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e(TAG,e.getMessage());
				}
	        	finally
	        	{
	        		hideProgress();
	        		
	        	}
	        	
	        } else {
	        	resultCode = Activity.RESULT_CANCELED;
	        	hideProgress();
	        	Log.e(TAG, "onRegisterationResult: failed to create user");
	            errorMessage.setTextColor(Color.RED);
	            errorMessage.setText(error);
	            return;
	        }

	        setResult(resultCode, resultIntent); //Tell the calling activity what happened
	        finish();

	    }
	    
	    /**
	     * Hides the progress UI for a lengthy operation.
	     */
	    private void hideProgress() {
	        if (mProgressDialog != null) {
	            mProgressDialog.dismiss();
	            mProgressDialog = null;
	        }
	    }
	    
	    /**
	     * Returns the message to be displayed at the top of the login dialog box.
	     */
	    private String getMessage() {
	        if (TextUtils.isEmpty(userName)) {
	            return "Invalid username";
	        }
	        else if (TextUtils.isEmpty(password)) {
	            // We have an account but no password
	            return "Invalid password";
	        }
	        
	        else if (TextUtils.isEmpty(confirmPassword)) {
	            // We have an account but no password
	            return "Invalid password";
	        }
	        else if (TextUtils.isEmpty(email)) {
	            // We have an account but no password
	            return "Invalid email";
	        }
	        else if (!TextUtils.equals(password, confirmPassword))
	        	return "Password and confirm password fields dont match" ;
	        else
	        	return null;
	    }
}

