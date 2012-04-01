package com.mewannaplay;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.android.widgets.DateSlider.SliderContainer;
import com.googlecode.android.widgets.DateSlider.SliderContainer.OnTimeChangeListener;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.syncadapter.SyncAdapter;

public class PostMessageActivity extends Activity {

	
	private static final String TAG = "PostMessageActivity";
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	private int courtId;
	static final int DEFAULTDATESELECTOR_ID = 0;

	    protected int mLayoutID;
	    private final static int minuteInterval = 30;
	    private SliderContainer mContainer;
	    
	    
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar mInitialTime = Calendar.getInstance();
		   if (savedInstanceState!=null) {
	            Calendar c = (Calendar)savedInstanceState.getSerializable("time");
	            if (c != null && c.after(mInitialTime)) { //TODO Add check for before
	                
	            	mInitialTime = c;
	            }
	        }

		
		   if (minuteInterval > 1) {
	        	int minutes = mInitialTime.get(Calendar.MINUTE);
	    		int diff = ((minutes+minuteInterval/2)/minuteInterval)*minuteInterval - minutes;
	    		mInitialTime.add(Calendar.MINUTE, diff);
	        }
		
		setContentView(R.layout.post_message_layout);

		
		
		 mContainer = (SliderContainer) this.findViewById(R.id.dateSliderContainer);
		   mContainer.setMinuteInterval(minuteInterval);
		  mContainer.setTime(mInitialTime);
	        mContainer.setMinTime(mInitialTime);
	 
	     
	       
	        
	        Calendar maxTime = Calendar.getInstance();
	        maxTime.set(Calendar.HOUR_OF_DAY, 24);
	        maxTime.set(Calendar.MINUTE, 0);
	        maxTime.set(Calendar.SECOND, 0);
	        mContainer.setMaxTime(maxTime);
	        
	        
		courtId = 13604;//this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
		Button cancelButton = (Button) findViewById(R.id.post_message);
		cancelButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	progressDialog = ProgressDialog.show(PostMessageActivity.this, "", 
		                "Posting message...", true);
		    	progressDialog.show();
		    	postMessage();
		    	
		    }
		});
	}
	

	
	private void postMessage()
	{
		Message message = new Message();
		String utcScheduleTime = Util.getUTCTimeForHourMinute(mContainer.getTime().get(Calendar.HOUR_OF_DAY), mContainer.getTime().get(Calendar.MINUTE));
		message.setScheduleTime(utcScheduleTime);
		Object item= ((Spinner)this.findViewById(R.id.players_needed)).getSelectedItem();
		if (item != null)
			message.setPlayerNeeded(item.toString());
		else
			message.setPlayerNeeded("1");
		String contactInfo = ((TextView)this.findViewById(R.id.contact_info)).getText().toString();
		if (contactInfo.matches("[0-9]+"))
			message.setContactTypeId(0);
		else
			message.setContactTypeId(1);
		message.setContactInfo(contactInfo);
		item = ((Spinner)this.findViewById(R.id.level)).getSelectedItem();
		if (item != null)
			message.setLevel(item.toString());
		else
			message.setLevel("Beginner");
		message.setText(((TextView)this.findViewById(R.id.message)).getText().toString());
		registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED_ACTION));
			ContentResolver.requestSync(MapViewActivity.getAccount(this),
					ProviderContract.AUTHORITY, SyncAdapter.getPostMessageBundle(courtId,message.toJSONObject().toString()));
	}
	
	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Log.d(TAG, "sync for post post message done");
	        
	        unregisterReceiver(this);
	        
	        if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR))
	        {
	        	progressDialog.dismiss();
	        	AlertDialog.Builder builder = new AlertDialog.Builder(PostMessageActivity.this);
	        	builder.setMessage("Error while posting message")
	        	       .setCancelable(false)
	        	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   PostMessageActivity.this.finish();
	        	           }
	        	       });
	        	      
	        	alert = builder.create();
	        	alert.show();
	        }
	        else
	        {
	        	progressDialog.setMessage("Message posted successfully");
	        	progressDialog.dismiss();
	        	PostMessageActivity.this.finish();
	        }
	    }
	};
	    
	 public void onCancel(View v)
	 {
		 PostMessageActivity.this.finish();
	 }
	 
	 @Override
	protected void onSaveInstanceState(Bundle outState) {
		    super.onSaveInstanceState(outState);
	        if (outState==null) outState = new Bundle();
	        outState.putSerializable("time", mContainer.getTime());
	}
}
