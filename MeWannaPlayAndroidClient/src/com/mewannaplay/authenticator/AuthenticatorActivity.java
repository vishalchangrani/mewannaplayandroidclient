package com.mewannaplay.authenticator;

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

import java.io.UnsupportedEncodingException;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mewannaplay.Constants;
import com.mewannaplay.MapViewActivity;
import com.mewannaplay.NewUserRegisterationActivity;
import com.mewannaplay.R;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.providers.ProviderContract;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
	/** The Intent flag to confirm credentials. */
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

	/** The Intent extra to store password. */
	public static final String PARAM_PASSWORD = "password";

	/** The Intent extra to store username. */
	public static final String PARAM_USERNAME = "username";

	/** The Intent extra to store username. */
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

	/** The tag used to log to adb console. */
	private static final String TAG = "AuthenticatorActivity";
	private AccountManager mAccountManager;

	/** Keep track of the login task so can cancel it if requested */
	private UserLoginTask mAuthTask = null;

	/** Keep track of the progress dialog so we can dismiss it */
	private ProgressDialog mProgressDialog = null;

	/**
	 * If set we are just checking that the user knows their credentials; this
	 * doesn't cause the user's password or authToken to be changed on the
	 * device.
	 */
	private Boolean mConfirmCredentials = false;

	/** for posting authentication attempts back to UI thread */
	private final Handler mHandler = new Handler();

	private TextView mMessage;
	Typeface bold,heavy,light,normal;
	Button newuserregi,go;
	private String mPassword;

	private EditText mPasswordEdit;
	private ConnectivityReceiver connection;

	/** Was the original caller asking for an entirely new account? */
	protected boolean mRequestNewAccount = false;

	private String mUsername;
TextView login;
	private EditText mUsernameEdit;
	private final Handler handler = new Handler();
	AccountManagerFuture<Bundle> accountManagerFuture = null;
	
Button forgotusername,anologin;
TextView usertext,passtext;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle icicle) {

		// Log.i(TAG, "onCreate(" + icicle + ")");
		super.onCreate(icicle);
		mAccountManager = AccountManager.get(this);
		// Log.i(TAG, "loading data from Intent");
		final Intent intent = getIntent();
		mUsername = intent.getStringExtra(PARAM_USERNAME);
		mPassword = intent.getStringExtra(PARAM_PASSWORD);

		mRequestNewAccount = mUsername == null;
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS,
				false);
		// Log.i(TAG, "    request new: " + mRequestNewAccount);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.login_activity);
		connection = new ConnectivityReceiver(this);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				android.R.drawable.ic_dialog_alert);
		mMessage = (TextView) findViewById(R.id.message);
		mUsernameEdit = (EditText) findViewById(R.id.username_edit);
		mPasswordEdit = (EditText) findViewById(R.id.password_edit);
		forgotusername=(Button)findViewById(R.id.forgotusername);
		usertext=(TextView)findViewById(R.id.usernametext);
		passtext=(TextView)findViewById(R.id.passwordtext);
		login=(TextView)findViewById(R.id.login);
		anologin=(Button)findViewById(R.id.anonymous_login);
	     bold=Typeface.createFromAsset(this.getAssets(),"Folks-Bold.ttf");
				 heavy=Typeface.createFromAsset(this.getAssets(),"Folks-Heavy.ttf");
				 light=Typeface.createFromAsset(this.getAssets(),"Folks-Light.ttf");
				 normal=Typeface.createFromAsset(this.getAssets(),"Folks-Normal.ttf");
				 newuserregi=(Button)findViewById(R.id.newuser_register_btn);
				 newuserregi.setTypeface(normal);
				 go=(Button)findViewById(R.id.ok_button);
				 go.setTypeface(bold);
				 forgotusername.setTypeface(normal);
				 mMessage.setTypeface(normal);
				 login.setTypeface(bold);
				 anologin.setTypeface(bold);
				 usertext.setTypeface(normal);
				 passtext.setTypeface(normal);
		if (!TextUtils.isEmpty(mUsername))
			mUsernameEdit.setText(mUsername);
		if (!TextUtils.isEmpty(mPassword))
			mUsernameEdit.setText(mPassword);
		mMessage.setText(getMessage());

	}
	public void onForgotusername(View v) {
		Uri uri = Uri.parse(Constants.FORGOT_PASSWORD_URL);
		 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		 startActivity(intent);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		connection.unbind(AuthenticatorActivity.this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		connection.bind(AuthenticatorActivity.this);
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getText(R.string.ui_activity_authenticating));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				// Log.i(TAG, "user cancelling authentication");
				if (mAuthTask != null) {
					mAuthTask.cancel(true);
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
	 * Handles onClick event on the Submit button. Sends username/password to
	 * the server for authentication. The button is configured to call
	 * handleLogin() in the layout XML.
	 * 
	 * @param view
	 *            The Submit button for which this method is invoked
	 */
	public void handleLogin(View view) {

		mUsername = mUsernameEdit.getText().toString();
		mPassword = mPasswordEdit.getText().toString();
	if(TextUtils.isEmpty(mUsername) && TextUtils.isEmpty(mPassword)){
		
		mMessage.setText("Please enter Username and Password");
		mMessage.setTextColor(Color.RED);
	}else{
		if (TextUtils.isEmpty(mUsername)) {
			mMessage.setText("Please enter a Username");
			mMessage.setTextColor(Color.RED);
		}
		else if (TextUtils.isEmpty(mPassword)) {
			mMessage.setText("Please enter a Password");
			mMessage.setTextColor(Color.RED);
		}
		else {
			// Show a progress dialog, and kick off a background task to perform
			// the user login attempt.
			showProgress();
			mAuthTask = new UserLoginTask();
			mAuthTask.execute();
		}}
	}
	
	

	public void handleAnnonymousLogin(View view) {
		// Log.i(TAG, "handleAnnonymousLogin");
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		boolean alreadyAdded = false;
		for (Account account : accounts) {
			if (account.name.equals(Constants.ANONYMOUS_USER)) {
				alreadyAdded = true;
				break;
			}
		}
		if (!alreadyAdded) {
			final Account anonymousAccount = new Account(
					Constants.ANONYMOUS_USER, Constants.ACCOUNT_TYPE);
			mAccountManager.addAccountExplicitly(anonymousAccount, "", null);
			ContentResolver.setSyncAutomatically(anonymousAccount,
					ProviderContract.AUTHORITY, false);
			ContentResolver.setIsSyncable(anonymousAccount,
					ProviderContract.AUTHORITY, 1);
		}
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME,
				Constants.ANONYMOUS_USER);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		// startActivity(new Intent(AuthenticatorActivity.this,
		// MapViewActivity.class));

		finish();
	}

	/**
	 * Called when response is received from the server for confirm credentials
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller.
	 * 
	 * @param result
	 *            the confirmCredentials result.
	 */
	private void finishConfirmCredentials(boolean result) {
		// Log.i(TAG, "finishConfirmCredentials()");
		final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
		mAccountManager.setPassword(account, mPassword);
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Called when response is received from the server for authentication
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller. We store the
	 * authToken that's returned from the server as the 'password' for this
	 * account - so we're never storing the user's actual password locally.
	 * 
	 * @param result
	 *            the confirmCredentials result.
	 */
	private void finishLogin() {

		// Log.i(TAG, "finishLogin()");
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		final Account accountToAdd = new Account(mUsername,
				Constants.ACCOUNT_TYPE);
		boolean existingAccount = false;
		for (Account account : accounts) {
			if (!account.name.equals(mUsername)) // If user logged in
													// successfully as a valid
													// account then remove the
													// anonymous account
			{
				mAccountManager.removeAccount(account, null, null); // remove
																	// anonymous
																	// account
			} else {
				mAccountManager.setPassword(account, mPassword);
				existingAccount = true;
			}
		}
		if (!existingAccount) {
			mAccountManager.addAccountExplicitly(accountToAdd, mPassword, null);
			ContentResolver.setSyncAutomatically(accountToAdd,
					ProviderContract.AUTHORITY, false);
			ContentResolver.setIsSyncable(accountToAdd,
					ProviderContract.AUTHORITY, 1);

		}

		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
//		 startActivity(new Intent(AuthenticatorActivity.this,
//		 MapViewActivity.class));

		finish();
	}

	private void finishLoginWithFailure() {
		// Log.e(TAG, "finishLoginWithFailure()");
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);

		for (Account account : accounts)
			mAccountManager.removeAccount(account, null, null); // remove ALL
																// com.mewannaplay
																// account

		final Intent intent = new Intent();

		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		setAccountAuthenticatorResult(null); // indicate failure to calling
												// activity
		finish();

	}

	/**
	 * Called when the authentication process completes (see attemptLogin()).
	 * 
	 * @param authToken
	 *            the authentication token returned by the server, or NULL if
	 *            authentication failed.
	 */
	public void onAuthenticationResult() {

		String error = mAuthTask.getError();
		boolean success = error.trim().equals("");
		// Log.i(TAG, "onAuthenticationResult(" + success + ")");

		// Our task is complete, so clear it out
		mAuthTask = null;

		// Hide the progress dialog
		hideProgress();

		if (success) {
			if (!mConfirmCredentials) {
				finishLogin();
			} else {
				finishConfirmCredentials(success);
			}
		} else {
			// Log.e(TAG, "onAuthenticationResult: failed to authenticate");
			if(connection.hasConnection()){
			if (mRequestNewAccount) {
				// "Please enter a valid username/password.
				// mMessage.setText(error);
				mMessage.setTextColor(Color.RED);
				mMessage.setText("Please enter a valid username/password.");
			} else {
				// "Please enter a valid password." (Used when the
				// account is already in the database but the password
				// doesn't work.)
				// mMessage.setText(error);
				mMessage.setTextColor(Color.RED);
				mMessage.setText("Please enter a valid password.");

			}
			}else{
				mMessage.setTextColor(Color.RED);
				mMessage.setText("Network Unavailable");
				
			}
		}
	}

	/**
	 * Returns the message to be displayed at the top of the login dialog box.
	 */
	private CharSequence getMessage() {
		getString(R.string.label);
		if (TextUtils.isEmpty(mUsername)) {
			// If no username, then we ask the user to log in using an
			// appropriate service.
//			final CharSequence msg = getText(R.string.login_activity_newaccount_text);
//	
//			return msg;
		}
		if (TextUtils.isEmpty(mPassword)) {
			// We have an account but no password
//			return getText(R.string.login_activity_loginfail_text_pwmissing);
		}
	
		return null;
	}

	/**
	 * Shows the progress UI for a lengthy operation.
	 */
	private void showProgress() {
		showDialog(0);
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
	 * Represents an asynchronous task used to authenticate a user against the
	 * SampleSync Service
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Void> {

		private String error = null;

		public String getError() {
			return error;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// We do the actual work of authenticating the user
			// in the NetworkUtilities class.
			try {
				RestClient.login(mUsername, mPassword);
				error = "";
				return null;
			} catch (Exception ex) {
				// Log.e(TAG,
				//		"UserLoginTask.doInBackground: failed to authenticate");
				// Log.i(TAG, ex.toString());
				error = ex.toString();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Void nothing) {
			
			onAuthenticationResult();
			
			return;
		}
	}

	public final static String encryptPassword(String password) {

		try {
			return "acbdefgh"
					+ Base64.encodeToString(password.getBytes("UTF-8"),
							Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	public void onNewUserRegister(View v) {
		Intent i = new Intent(this, NewUserRegisterationActivity.class);
		startActivityForResult(i, 0);
	}

	// Callback form NewUserRegesterationActivity
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (intent != null) {
			mUsername = intent.getStringExtra(PARAM_USERNAME);
			mPassword = intent.getStringExtra(PARAM_PASSWORD);
		} else {
			mUsername = null;
			mPassword = null;
		}

		if (requestCode == 0 && resultCode == RESULT_OK
				&& !TextUtils.isEmpty(mUsername)
				&& !TextUtils.isEmpty(mPassword)) {
			mUsernameEdit.setText(mUsername);
			mPasswordEdit.setText(mPassword);
			handleLogin(null); // Login this newly created user
		}
		else if (RESULT_CANCELED == resultCode)
		{
			return;
		}
		else {
			finishLoginWithFailure();

		}
	}
}
