package com.mewannaplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.syncadapter.SyncAdapter;

public class ViewMessageActivity extends Activity {

	
	private static final String TAG = "ViewMessageActivity";
	private Message message;
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int messageId = this.getIntent().getExtras().getInt(SyncAdapter.MESSAGE_ID);
		if (messageId <= 0 )
		{
			Log.e(TAG," Message Id not specified");
			this.finish();
			return;
		}
		
		setContentView(R.layout.view_message_layout);
		
		
		Cursor cursor = getContentResolver().query(
				Messages.CONTENT_URI, null, " _id = ?",
    			new String[] { messageId + "" }, null);
    	if (cursor.getCount() == 0)
    	{
    		Log.e(TAG," Court message not found");
			this.finish();
			return;
    	}
    	cursor.moveToFirst();
    	message = Message.fromCursor(cursor);
    	cursor.close();
    	
    	TextView nameTextView = (TextView) findViewById(R.id.schedule_time_view);
    	nameTextView.setText(message.getScheduleTime());
    	((TextView) findViewById(R.id.level_view)).setText(message.getLevel());
    	((TextView) findViewById(R.id.players_needed_view)).setText(message.getPlayerNeeded());
    	((TextView) findViewById(R.id.contact_info_view)).setText(message.getContactInfo());
    	((TextView) findViewById(R.id.message)).setText(message.getText());
    	((TextView) findViewById(R.id.posted_on_view)).setText(message.getTimeposted());
    	((TextView) findViewById(R.id.user_name_view)).setText(message.getUserName());
    	
    	Button deleteButton = ((Button) findViewById(R.id.delete_message));
    	if (message.getUserName().equals(MapViewActivity.getAccount(this).name)) //If the message was posted by this user
    		deleteButton.setEnabled(true); //he can delete, its his message
    	else
    		deleteButton.setEnabled(false); //else he can't
    		
    	
	}
	
	 public void onDelete(View v)
	 {
		 AlertDialog.Builder builder = new AlertDialog.Builder(ViewMessageActivity.this);
		 builder.setMessage("Are you sure you want to delete this message? ")
		 .setNegativeButton("No", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   dialog.cancel();
		        	           }
		        	       })
		        	      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   dialog.dismiss();
		        	        	   progressDialog = ProgressDialog.show(ViewMessageActivity.this, "", 
	                "Deleting message...", true);
	    	progressDialog.show();
	    	deleteMessage();
		        	           }
		        	       });
		 builder.create().show();
		 
		   
	 }
	 
	 
	 public void deleteMessage()
	 {
		 registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED_ACTION));
			ContentResolver.requestSync(MapViewActivity.getAccount(this),
					ProviderContract.AUTHORITY, SyncAdapter.getDeleteMessageBundle(message.getId()));
	 }
		private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

		    @Override
		    public void onReceive(Context context, Intent intent) {
		        Log.d(TAG, "sync for delete message done");
		        
		        unregisterReceiver(this);
		        
		        if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR))
		        {
		        	progressDialog.dismiss();
		        	AlertDialog.Builder builder = new AlertDialog.Builder(ViewMessageActivity.this);
		        	builder.setMessage("Error while deleting message")
		        	       .setCancelable(false)
		        	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   ViewMessageActivity.this.finish();
		        	           }
		        	       });
		        	      
		        	alert = builder.create();
		        	alert.show();
		        }
		        else
		        {
		        	progressDialog.setMessage("Message deleted successfully");
		        	progressDialog.dismiss();
		        	ViewMessageActivity.this.finish();
		        }
		    }
		};
	
}
