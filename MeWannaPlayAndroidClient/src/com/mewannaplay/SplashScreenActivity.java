package com.mewannaplay;

import java.io.IOException;

import com.mewannaplay.client.RestClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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

		AccountManager mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		Log.d(TAG, "Accounts found: "+accounts.length);
		
		
	//If no accounts defined yet or the only account is that of anonymous prompt for login
		if (accounts.length == 0 || (accounts.length == 1 && accounts[0].name.equals(Constants.ANONYMOUS_USER))) 			
			accountManagerFuture = mAccountManager
					.addAccount(Constants.ACCOUNT_TYPE,
							Constants.AUTHTOKEN_TYPE, null, null, this, null,
							null);
		else 
		{
			Account validUserAccount = null;
			for (Account account : accounts)
			{
				if (!account.name.equals(Constants.ANONYMOUS_USER))
					validUserAccount = account;
					
			}
			accountManagerFuture = mAccountManager.getAuthToken(validUserAccount,
					Constants.AUTHTOKEN_TYPE, null, this, null,
					null);
		}
			// Do something long
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						accountManagerFuture.getResult();
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


		

	

	Runnable startMapViewActivity = new Runnable() {
		@Override
		public void run() {	
			startActivity(new Intent(SplashScreenActivity.this,
					MapViewActivity.class));
			finish();
		}
	};
}
