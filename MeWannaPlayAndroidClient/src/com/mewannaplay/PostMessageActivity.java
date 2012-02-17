package com.mewannaplay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PostMessageActivity extends Activity {

	
	private static final String TAG = "PostMessageActivity";
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.post_message_layout);
		
		Button cancelButton = (Button) findViewById(R.id.post_message);
		cancelButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	progressDialog = ProgressDialog.show(PostMessageActivity.this, "", 
		                "Posting message", true);
		    	progressDialog.show();
		    	postMessage();
		    }
		});
	}
	
	private void postMessage()
	{
		
	}
}
