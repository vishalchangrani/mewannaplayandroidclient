package com.mewannaplay;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

//There are two types of account we support - 1. Anonymous 2. Actual
//Anonymous account doesn't require password and its not authenticated against the server. With anonymous account user can only view the map, court details and messages but cannot 
//post a message or mark any court occupied.
//Actual account requires a password and is authenticated against the server. A user who is authenticated can use all 
//These accounts are created in the account manager
public class SplashScreenActivity extends Activity {

	private final static int secondsDelayed = 1;
	private final static String TAG = "SplashScreenActivity";
	private final Handler handler = new Handler();
	AccountManagerFuture<Bundle> accountManagerFuture = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Thread timer = new Thread() {

			public void run() {

				try {
					sleep(5000);

				} catch (InterruptedException e) {

					e.printStackTrace();
				} finally {

					final AccountManager mAccountManager = AccountManager
							.get(SplashScreenActivity.this);
					Account[] accounts = mAccountManager
							.getAccountsByType(Constants.ACCOUNT_TYPE);
					Log.d(TAG, "Accounts found: " + accounts.length);

					// If no accounts defined yet or the only account is that of
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
						for (Account account : accounts) {
							if (!account.name.equals(Constants.ANONYMOUS_USER))
								validUserAccount = account;

						}
						accountManagerFuture = mAccountManager.getAuthToken(
								validUserAccount, Constants.AUTHTOKEN_TYPE,
								null, SplashScreenActivity.this, null, null);
					}

					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							try {
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

		};
		timer.start();

		// Do something long

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	Runnable startMapViewActivity = new Runnable() {
		@Override
		public void run() {
			startActivity(new Intent(SplashScreenActivity.this,
					MapViewActivity.class));

		}
	};

}
