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
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.android.widgets.DateSlider.SliderContainer;
import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.syncadapter.SyncAdapter;

public class PostMessageActivity extends Activity implements OnCheckedChangeListener {

	
	private static final String TAG = "PostMessageActivity";
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	private int courtId;
	static final int DEFAULTDATESELECTOR_ID = 0;

	    protected int mLayoutID;
	    private final static int minuteInterval = 30;
	    private SliderContainer mContainer;
	    RadioGroup rgcontactinfo;
	    EditText econtactinfo;
	    
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar mInitialTime = Calendar.getInstance();
		   if (savedInstanceState!=null && savedInstanceState.containsKey("time")) {
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

		rgcontactinfo=(RadioGroup)findViewById(R.id.rgcontact);
		econtactinfo=(EditText)findViewById(R.id.contact_info);
		rgcontactinfo.setOnCheckedChangeListener(this);
		
		 mContainer = (SliderContainer) this.findViewById(R.id.dateSliderContainer);
		   mContainer.setMinuteInterval(minuteInterval);
		  mContainer.setTime(mInitialTime);
	        mContainer.setMinTime(mInitialTime);
	 
	     
	       
	        
	        Calendar maxTime = Calendar.getInstance();
	        maxTime.set(Calendar.HOUR_OF_DAY, 24);
	        maxTime.set(Calendar.MINUTE, 0);
	        maxTime.set(Calendar.SECOND, 0);
	        mContainer.setMaxTime(maxTime);
	        
	        
		courtId = this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
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
		
		//hack!courtId should also be a column in message table but its not
		message.setTennisCourtId(courtId);
		// -------------------------------
		
		String utcScheduleTime = Util.getUTCTimeForHourMinute(mContainer.getTime().get(Calendar.HOUR_OF_DAY), mContainer.getTime().get(Calendar.MINUTE));
		message.setScheduleTime(utcScheduleTime);
		Object item= ((Spinner)this.findViewById(R.id.players_needed)).getSelectedItem();
		if (item != null)
			message.setPlayerNeeded(item.toString());
		else
			message.setPlayerNeeded("1");
		String contactInfo = econtactinfo.getText().toString();
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



	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
		switch (checkedId) {
		case R.id.rbphn:
			econtactinfo.setInputType(InputType.TYPE_CLASS_PHONE);
			econtactinfo.setHint("e.g. 9999999999");
//			int maxLength = 10;
//		
//			InputFilter[] FilterArray = new InputFilter[1];
//			FilterArray[0] = new InputFilter.LengthFilter(maxLength);
//			econtactinfo.setFilters(FilterArray);
			econtactinfo.setFilters( new InputFilter[] { new InputFilter.LengthFilter(10) } ); 
			break;

		case R.id.rbemail:
			econtactinfo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			econtactinfo.setHint("e.g abcd@efgh.com");
			econtactinfo.setFilters( new InputFilter[] { new InputFilter.LengthFilter(100) } ); 
			break;
		}
		
	}
}
