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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.syncadapter.SyncAdapter;

public class ViewMessageActivity extends Activity implements OnClickListener {

	private static final String TAG = "ViewMessageActivity";
	private Message message;
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	TextView viewcontact;
	int courtId; // HACK Alert - court id should be part of message object but
					// its not hence need to be passed around seperately

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
		viewcontact = (TextView) findViewById(R.id.contact_info_view);

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
		nameTextView.setText(message.getScheduleTime());
		((TextView) findViewById(R.id.level_view)).setText(message.getLevel());
		((TextView) findViewById(R.id.players_needed_view)).setText(message
				.getPlayerNeeded());
		viewcontact.setText(message.getContactInfo());
		((TextView) findViewById(R.id.message)).setText(message.getText());
		((TextView) findViewById(R.id.posted_on_view)).setText(message
				.getTimeposted());
		((TextView) findViewById(R.id.user_name_view)).setText(message
				.getUserName());

		Button deleteButton = ((Button) findViewById(R.id.delete_message));
		ImageView viewBack=(ImageView)findViewById(R.id.view_back_icon);
		Button deleteButtonPartnerFound = ((Button) findViewById(R.id.delete_message_partner_found));
		viewcontact.setOnClickListener(this);
		if (message.getUserName().equals(MapViewActivity.getAccount(this).name)) // If
																					// the
																					// message
																					// was
																					// posted
																					// by
																					// this
																					// user
		{
			deleteButton.setEnabled(true); // he can delete, its his message
			deleteButtonPartnerFound.setEnabled(true);
		} else {
			deleteButton.setEnabled(false); // else he can't
			deleteButtonPartnerFound.setEnabled(false);
		}

	}
	public void viewBack(View v)
	{
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
								progressDialog = ProgressDialog.show(
										ViewMessageActivity.this, "",
										"Deleting message...", true);
								progressDialog.show();
								deleteMessage(partnerFound);
							}
						});
		builder.create().show();

	}

	public void deleteMessage(boolean partnerFound) {
		registerReceiver(syncFinishedReceiver, new IntentFilter(
				SyncAdapter.SYNC_FINISHED_ACTION));
		ContentResolver.requestSync(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY, SyncAdapter.getDeleteMessageBundle(
						courtId, message.getId(), partnerFound));
	}

	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "sync for delete message done");

			unregisterReceiver(this);

			if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR)) {
				progressDialog.dismiss();
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
				progressDialog.setMessage("Message deleted successfully");
				progressDialog.dismiss();
				ViewMessageActivity.this.finish();
				MapViewActivity.setCourtPostedMessageOn(-1);// To make the shout
															// out icon
															// disappear
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contact_info_view:
			if (!viewcontact.getText().toString().contentEquals("")) {

				if (viewcontact.getText().toString().matches("[0-9]+")) {
					
					
					Intent callIntent = new Intent(
							Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+viewcontact.getText().toString().trim()));
					startActivity(callIntent);

				}
				if (viewcontact.getText().toString()
						.matches("[a-zA-Z0-9]+@[a-zA-Z].com")) {
					
					String[] emailaddress={viewcontact.getText().toString()};
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

			break;

	
		}

	}

}
