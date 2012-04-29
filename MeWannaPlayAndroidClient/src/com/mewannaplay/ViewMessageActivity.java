package com.mewannaplay;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.syncadapter.SyncAdapter;

public class ViewMessageActivity extends Activity {

	
	private static final String TAG = "ViewMessageActivity";
	private Message message;
	
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
    	
	}
	
}
