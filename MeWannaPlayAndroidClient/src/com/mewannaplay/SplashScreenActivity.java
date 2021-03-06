package com.mewannaplay;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.mewannaplay.authenticator.ConnectivityReceiver;

//There are two types of account we support - 1. Anonymous 2. Actual
//Anonymous account doesn't require password and its not authenticated against the server. With anonymous account user can only view the map, court details and messages but cannot 
//post a message or mark any court occupied.
//Actual account requires a password and is authenticated against the server. A user who is authenticated can use all 
//These accounts are created in the account manager
public class SplashScreenActivity extends Activity {

	private final static String TAG = "SplashScreenActivity";
	private final Handler handler = new Handler();
	AccountManagerFuture<Bundle> accountManagerFuture = null;
	public static final String DELAY_FOR_SPLASH = "DELAY_FOR_SPLASH";
	ConnectivityReceiver connection;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		connection = new ConnectivityReceiver(this);
		Thread timer = new Thread() {

			public void run() {

				try {
					final Intent intent = getIntent();
					final int delayInSecondsToShowSplashScreen = intent
							.getIntExtra(DELAY_FOR_SPLASH, 2000);
					sleep(delayInSecondsToShowSplashScreen);

				} catch (InterruptedException e) {

					e.printStackTrace();
					finish();
				} finally {
					if (connection.hasConnection())  {
						if (!ContentResolver.getMasterSyncAutomatically())
							doAutoSyncSettings();
						else
							continueToApp();
					} else {
						SplashScreenActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										SplashScreenActivity.this);

								alertDialogBuilder
										.setMessage("MWP requires active data connection, please enable it via settings and re-launch MWP");

								alertDialogBuilder

								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												finish();
											

											}
										});

								AlertDialog alertDialog = alertDialogBuilder
										.create();

								alertDialog.show();

							}
						});

					}

				}
			}

		};
		timer.start();

		// Do something long

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		connection.unbind(SplashScreenActivity.this);
		super.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		connection.bind(SplashScreenActivity.this);

	}

	Runnable startMapViewActivity = new Runnable() {
		@Override
		public void run() {
			startActivity(new Intent(SplashScreenActivity.this,
					MapViewActivity.class));

		}
	};

	
	
	private void doAutoSyncSettings() {

		SplashScreenActivity.this.runOnUiThread(
				new Runnable() {
					
			public void run()
			{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				SplashScreenActivity.this);
		
		alertDialogBuilder
				.setMessage("MeWannaPlay uses Auto-Sync to conserve data usage and play along well with other applications on your phone.");
		
		alertDialogBuilder.setTitle("Auto-Sync is off");
	
		alertDialogBuilder.setPositiveButton("Turn it on",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int id) {

							// Log.d(TAG, " Turning on master auto-sync");
							ContentResolver.setMasterSyncAutomatically(true);
							dialog.dismiss();
							continueToApp();
					}
				});

		alertDialogBuilder.setNeutralButton("I will do it",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int id)
			{
				dialog.dismiss();
				finish();
				SplashScreenActivity.this.startActivity(new Intent(Settings.ACTION_SYNC_SETTINGS));
			}
		});
		
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int id) {
						dialog.dismiss();
						finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder
				.create();

		alertDialog.show();
		
		// Must call show() prior to fetching text view
		TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);

	

			}
				});
		
	}
	
	private void continueToApp()
	{
		final AccountManager mAccountManager = AccountManager
				.get(SplashScreenActivity.this);
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		// Log.d(TAG, "Accounts found: " + accounts.length);

		// If no accounts defined yet or the only account is
		// that of
		// anonymous prompt for login
		if (accounts.length == 0
				|| (accounts.length == 1 && accounts[0].name
						.equals(Constants.ANONYMOUS_USER))) {
			accountManagerFuture = mAccountManager.addAccount(
					Constants.ACCOUNT_TYPE,
					Constants.AUTHTOKEN_TYPE, null, null,
					SplashScreenActivity.this, null, null);
		} else {
			Account validUserAccount = null;

			// Find that one actual non-anon account to login
			// with
			for (Account account : accounts) {
				if (!account.name
						.equals(Constants.ANONYMOUS_USER)) {
					validUserAccount = account;
					break;
				}
			}

			accountManagerFuture = mAccountManager
					.getAuthToken(validUserAccount,
							Constants.AUTHTOKEN_TYPE, null,
							SplashScreenActivity.this, null,
							null);
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// Blocking call on accountmanager..
					// This tells what happened on the
					// authenticatoractivity screen...
					if (accountManagerFuture.isCancelled()) {
						finish();
						return;
					}

					Bundle resultOfLogin = accountManagerFuture
							.getResult();
					String accountName = resultOfLogin
							.getString(AccountManager.KEY_ACCOUNT_NAME);

					for (Account account : mAccountManager
							.getAccountsByType(Constants.ACCOUNT_TYPE)) {
						if (account.name.equals(accountName)) {
							MapViewActivity.setAccount(account);// Initialize
																// MapView
																// with
																// the
																// account
																// used
																// to
																// login
																// (Anonymous
																// or
																// other)
						}
					}

					handler.post(startMapViewActivity);
				} catch (OperationCanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};
		new Thread(runnable).start();
	}
	
}
