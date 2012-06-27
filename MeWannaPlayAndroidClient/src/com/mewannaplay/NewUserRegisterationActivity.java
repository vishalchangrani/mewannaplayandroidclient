package com.mewannaplay;

import com.mewannaplay.client.RestClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NewUserRegisterationActivity extends Activity {

	
	private String userName;
	private String password;
	private String email;
	
	private EditText newUserNameEditText,newUserPasswordEditText, newUserEmailEditText;
	private TextView errorMessage;
	
	
	private static final String TAG = "NewUserRegisterationActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user_registeration_layout);
		
		  newUserNameEditText = (EditText) findViewById(R.id.newuser_username);
	      newUserPasswordEditText = (EditText) findViewById(R.id.newuser_password);
	      newUserEmailEditText = (EditText) findViewById(R.id.newuser_email);
	    
	      
	}
	
	
	 public void onCancel(View v)
	 {
		 NewUserRegisterationActivity.this.finish();
	 }
	 
	 public void onRegister(View v)
	 {
		  errorMessage.setText(getMessage());
		 
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
	                error = ex.toString();
	                return null;
	            }
	        }
	    }
	    
	    
	    /**
	     * Returns the message to be displayed at the top of the login dialog box.
	     */
	    private CharSequence getMessage() {
	        getString(R.string.label);
	        if (TextUtils.isEmpty(userName)) {
	            return "Invalid username";
	        }
	        if (TextUtils.isEmpty(password)) {
	            // We have an account but no password
	            return "Invalid password";
	        }
	        
	        if (TextUtils.isEmpty(email)) {
	            // We have an account but no password
	            return "Invalid email";
	        }
	        return null;
	    }
}

