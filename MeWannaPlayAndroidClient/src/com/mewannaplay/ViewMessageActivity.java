package com.mewannaplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mewannaplay.asynctask.DeleteMessageAsyncTask;
import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.syncadapter.SyncAdapter;

public class ViewMessageActivity extends Activity implements OnClickListener {

	private static final String TAG = "ViewMessageActivity";
	private Message message;
	private AlertDialog alert;
	TextView viewcontact,titleview,titleuser,user,contact,posted,titlecontact,titleposted,titlesheduletime,sheduletime,playersneeded,titleplayersneeded,titlelevel,level,messageinfo,titlemessage;
	Typeface bold,heavy,light,normal;
	int courtId; // HACK Alert - court id should be part of message object but
					// its not hence need to be passed around seperately
	EmailValidator validator;
	Button delete,deletepartner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int messageId = this.getIntent().getExtras()
				.getInt(SyncAdapter.MESSAGE_ID);
		if (messageId <= 0) {
			Log.e(TAG, " Message Id not specified");
			this.finish();
			return;
		}
		courtId = this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
		if (courtId <= 0) {
			Log.e(TAG, " courtId not specified");
			this.finish();
			return;
		}
		setContentView(R.layout.view_message_layout);
		validator = new EmailValidator();
		  bold=Typeface.createFromAsset(this.getAssets(),"Folks-Bold.ttf");
			 heavy=Typeface.createFromAsset(this.getAssets(),"Folks-Heavy.ttf");
			 light=Typeface.createFromAsset(this.getAssets(),"Folks-Light.ttf");
			 normal=Typeface.createFromAsset(this.getAssets(),"Folks-Normal.ttf");
		viewcontact = (TextView) findViewById(R.id.contact_info_view);
		titleview = (TextView) findViewById(R.id.titleview);
		titleuser = (TextView) findViewById(R.id.tuser_name_view);
		titlecontact = (TextView) findViewById(R.id.tcontact_info_view);
		titleposted = (TextView) findViewById(R.id.tposted_on_view);
		titlesheduletime = (TextView) findViewById(R.id.tschedule_time_view);
		titleplayersneeded = (TextView) findViewById(R.id.tplayers_needed_view);
		titlelevel = (TextView) findViewById(R.id.tlevel_view);
		titlemessage = (TextView) findViewById(R.id.tmessage);
		
		user = (TextView) findViewById(R.id.user_name_view);
		
		posted = (TextView) findViewById(R.id.posted_on_view);
		sheduletime = (TextView) findViewById(R.id.schedule_time_view);
		playersneeded = (TextView) findViewById(R.id.players_needed_view);
		level = (TextView) findViewById(R.id.level_view);
		messageinfo = (TextView) findViewById(R.id.message);
		
		delete=(Button)findViewById(R.id.delete_message);
		deletepartner=(Button)findViewById(R.id.delete_message_partner_found);
		
		viewcontact.setTypeface(bold);
		
		
		viewcontact.setTypeface(bold);
		titleview.setTypeface(bold);
		titleuser.setTypeface(bold);
		titlecontact.setTypeface(bold);
		titleposted.setTypeface(bold);
		titlesheduletime.setTypeface(bold);
		titleplayersneeded.setTypeface(bold);
		titlelevel.setTypeface(bold);
		titlemessage.setTypeface(bold);
		user.setTypeface(bold);
		posted.setTypeface(bold);
		sheduletime.setTypeface(bold);
		playersneeded.setTypeface(bold);
		level.setTypeface(bold);
		messageinfo.setTypeface(bold);
		delete.setTypeface(bold);
		deletepartner.setTypeface(bold);
		messageinfo.setMovementMethod(ScrollingMovementMethod.getInstance());

		
		
		Cursor cursor = getContentResolver().query(Messages.CONTENT_URI, null,
				" _id = ?", new String[] { messageId + "" }, null);
		if (cursor.getCount() == 0) {
			Log.e(TAG, " Court message not found");
			this.finish();
			return;
		}
		cursor.moveToFirst();
		message = Message.fromCursor(cursor);
		cursor.close();

		TextView nameTextView = (TextView) findViewById(R.id.schedule_time_view);
		nameTextView.setText(Util.getLocalTimeFromUTC(message.getScheduleTime()));
		((TextView) findViewById(R.id.level_view)).setText(message.getLevel());
	
		((TextView) findViewById(R.id.players_needed_view)).setText(message
				.getPlayerNeeded());
		SpannableString content = new SpannableString((message.getContactInfo()));
		content.setSpan(new UnderlineSpan(), 0, (message.getContactInfo().length()), 0);

		viewcontact.setText(content);
		((TextView) findViewById(R.id.message)).setText(message.getText());
		((TextView) findViewById(R.id.posted_on_view)).setText(Util.getLocalTimeFromUTC(message.getTimeposted()));
		((TextView) findViewById(R.id.user_name_view)).setText(message
				.getUserName());

		Button deleteButton = ((Button) findViewById(R.id.delete_message));
	    Button deleteButtonPartnerFound = ((Button) findViewById(R.id.delete_message_partner_found));

        if (message.getUserName().equals(MapViewActivity.getAccount(this).name)) // If
                                                                                                                                                                // the
                                                                                                                                                                // message
                                                                                                                                                                // was
                                                                                                                                                                // posted
                                                                                                                                                                // by
                                                                                                                                                                // this
                                                                                                                                                                // user
        {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButtonPartnerFound.setVisibility(View.VISIBLE);
                deleteButton.setEnabled(true); // he can delete, its his message
                deleteButtonPartnerFound.setEnabled(true);
        } else {
                deleteButton.setVisibility(View.INVISIBLE); //Dont even show the buttons if this is not his message
                deleteButtonPartnerFound.setVisibility(View.INVISIBLE);
        }

}

public void viewBack(View v) {
        finish();
}

public void onDelete(View v) {
        onDelete(false);
}

public void onDeletePartnerFound(View v) {
        onDelete(true);
}

public void onDelete(final boolean partnerFound) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                        ViewMessageActivity.this);
        builder.setMessage("Are you sure you want to delete this message? ")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                }
                        })
                        .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        deleteMessage(partnerFound);
                                                }
                                        });
        builder.create().show();

}

public void deleteMessage(boolean partnerFound) {
        
        new DeleteMessageAsyncTask(this, courtId, message.getId(), partnerFound);
}

        public void onPostExectureDeleteMessage(boolean isError)
        {
                if (isError) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                        ViewMessageActivity.this);
                        builder.setMessage("Error while deleting message")
                                        .setCancelable(false)
                                        .setNeutralButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog,
                                                                                int id) {
                                                                        ViewMessageActivity.this.finish();
                                                                }
                                                        });

                        alert = builder.create();
                        alert.show();
                } else {
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("Message deleted successfully");
                        progressDialog.dismiss();
                        ViewMessageActivity.this.finish();
                        MapViewActivity.setCourtPostedMessageOn(-1);// To make the shout
                                                                                                                // out icon
                                                                                                                // disappear
                }
        }


public void viewcontact(View v) {

        if (!viewcontact.getText().toString().contentEquals("")) {

                if (viewcontact.getText().toString().matches("[0-9]+")) {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"
                                        + viewcontact.getText().toString().trim()));
                        startActivity(callIntent);

                }
                if (validator.validate(viewcontact.getText().toString())) {

                        String[] emailaddress = { viewcontact.getText().toString() };
                        Intent emailintent = new Intent(
                                        android.content.Intent.ACTION_SEND);

                        emailintent.putExtra(android.content.Intent.EXTRA_EMAIL,
                                        emailaddress);
                        emailintent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                        "Responding to your message in MeWannaPlay.");
                        emailintent.setType("plain/text");

                        startActivity(Intent.createChooser(emailintent, "Email Via..."));

                }

        }
}

@Override
public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.contact_info_view:

                break;

        }

}

}