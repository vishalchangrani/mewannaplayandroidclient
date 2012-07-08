package com.mewannaplay;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.format.DateUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.TennisCourtDetails;
import com.mewannaplay.providers.GPS;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.providers.ProviderContract.Messages;
import com.mewannaplay.providers.ProviderContract.TennisCourtsDetails;
import com.mewannaplay.syncadapter.SyncAdapter;

public class CourtDetailsActivity extends ListActivity implements
		OnClickListener {

	private TennisCourtDetails tennisCourtDetails;
	private static final String TAG = "CourtDetailsActivity";
	public static final String SELECTED_COURTS_GEOPOINT = "SELECTED_COURTS_GEOPOINT";
	private SimpleCursorAdapter cursorAdapter;
	Cursor messageCursor;
	private ProgressDialog progressDialog;
	private AlertDialog alert;
	int courtId, postid, markid;
	private ContentObserver messageContentObserver;
	private Location thisCourtsLocation; // tennscourtdetails doesnt has this
	boolean messageposted = false; // info
	ImageView phone;
	TextView cmark;
	SharedPreferences preferences;
	public static String filenames = "courtdetails";
	TextView cmsg,cmsgprox;
	String user;
double lat, lng;
GPS gps;
	// Account loggedinaccount;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Log.i("account", loggedinaccount.name);
		courtId = this.getIntent().getExtras().getInt(SyncAdapter.COURT_ID);
		markid = this.getIntent().getExtras().getInt("mark");
		postid = this.getIntent().getExtras().getInt("post");

		setContentView(R.layout.court_details_layout);
		cmsg = (TextView) findViewById(R.id.cmessage);
		cmsgprox=(TextView)findViewById(R.id.cmessageprox);
		gps = new GPS();
		gps.gpsInitializer(this);
		gps.showCurrentLocation();
		lat = gps.getLatitude();
		lng = gps.getLongitude();
		if(lat==0.0 | lng==0.0){
			
			cmsgprox.setText("You are not in proximity of the court");
			
		}
		
		thisCourtsLocation = (Location) this.getIntent().getExtras()
				.getParcelable(SELECTED_COURTS_GEOPOINT);

		if (RestClient.isLoggedIn()) {

			Button postMsgButton = (Button) findViewById(R.id.post_msg_button);
			cmsg.setVisibility(View.GONE);
			if (postid != -1) {

				postMsgButton.setEnabled(false);
				postMsgButton
						.setBackgroundResource(R.drawable.disablepostmessage);
				cmsg.setVisibility(View.VISIBLE);
				cmsg.setText("You have already posted a message.");
				messageposted = true;

			} else {
				postMsgButton.setBackgroundResource(R.drawable.postmessage);
				postMsgButton.setEnabled(true);
			}

		} else {

			cmsg.setVisibility(View.VISIBLE);
			cmsg.setText("Anonymus");
		}

		// TODO this should ideally be not done on the UI thread..but
		// will fix later
		Cursor cursor = getContentResolver().query(
				TennisCourtsDetails.CONTENT_URI, null, " _id = ?",
				new String[] { courtId + "" }, null);
		if (cursor.getCount() == 0) {
			Log.e(TAG, " cursor for courtId " + courtId
					+ " was empty in tenniscourtdetails table");
			cursor.close();
			this.finish();
			return;
		}
		cursor.moveToFirst();

		Cursor activityCursor = getContentResolver().query(
				ProviderContract.Acitivity.CONTENT_URI, null,
				" tennis_court = ?", new String[] { courtId + "" }, null);

		Cursor amenityCursor = getContentResolver().query(
				ProviderContract.Amenity.CONTENT_URI, null,
				" tennis_court = ?", new String[] { courtId + "" }, null);

		tennisCourtDetails = TennisCourtDetails.fromCursor(cursor,
				activityCursor, amenityCursor);
		cursor.close(); // dont need court details from db anymore. we
		activityCursor.close();
		amenityCursor.close();

		getContentResolver().delete(ProviderContract.Messages.CONTENT_URI,
				null, null); // clean message table
		// have cached it
		if (tennisCourtDetails == null) {
			Log.e(TAG, "courtdetails object null!");
			this.finish();
		}

		populateView();
		ExpandableListView v = (ExpandableListView) findViewById(R.id.exapandableList);
		v.setAdapter(new ExpadableAdapter());

		// the desired columns to be bound
		String[] columns = new String[] { "scheduled_time", "user_name",
				"contact_info", "level", "players_needed", "text",
				"time_posted" };
		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.scheduled_time, R.id.user,
				R.id.contact_info, R.id.level, R.id.players_needed,
				R.id.message_text, R.id.time_posted };
		messageCursor = getContentResolver().query(Messages.CONTENT_URI, null,
				null, null, null);
		startManagingCursor(messageCursor);

		cursorAdapter = new MessagesCursorAdapter(CourtDetailsActivity.this,
				R.layout.court_message_row, messageCursor, columns, to);
		// View header =
		// getLayoutInflater().inflate(R.id.msg_details_table, null);
		// getListView().addHeaderView(header);
		CourtDetailsActivity.this.setListAdapter(cursorAdapter);

		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long rowId) {
				Log.d(TAG, "selected " + rowId);
				viewMessage(rowId);
			}

		});
		this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// Set our receiver

		// receiver = new ServiceResultReceiver(new Handler());

		// receiver.setReceiver(this);

	}

	private class MessagesContentObserver extends ContentObserver {

		public MessagesContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.d(TAG, "onChange for message");
			super.onChange(selfChange);
			CourtDetailsActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "In observer for messages");
					cursorAdapter.notifyDataSetChanged();
				}

			});
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getContentResolver().unregisterContentObserver(messageContentObserver);
		messageContentObserver = null;
		// messageRefreshTimer.cancel();
		// messageRefreshTimer = null;
		ContentResolver.removePeriodicSync(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY,
				SyncAdapter.getAllMessagesBundle(courtId));
		ContentResolver.cancelSync(null, ProviderContract.AUTHORITY);// cancel
																		// all
																		// syncs
		ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY, false);
	}

	@Override
	protected void onResume() {

		super.onResume();
		messageContentObserver = new MessagesContentObserver(null);
		getContentResolver().registerContentObserver(Messages.CONTENT_URI,
				true, messageContentObserver);
		// messageRefreshTimer = new Timer("message refresher for "+courtId);
		// messageRefreshTimer.schedule(new MessageRefresh(), new Date(),
		// 60*1000);
		// getMessageTask = new GetMessagesTask();
		// getMessageTask.execute();
		ContentResolver.setSyncAutomatically(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY, true);
		ContentResolver.addPeriodicSync(MapViewActivity.getAccount(this),
				ProviderContract.AUTHORITY,
				SyncAdapter.getAllMessagesBundle(courtId), 10);
		// ContentResolver.requestSync(MapViewActivity.getAccount(CourtDetailsActivity.this),
		// ProviderContract.AUTHORITY,
		// SyncAdapter.getAllMessagesBundle(courtId));
		Location currentLocation = MapViewActivity.mapViewActivity
				.getMyCurrentLocation();
		// If no court has been marked occupied by this user and current
		// location is known and he is not an anonymous user
		// if (MapViewActivity.getCourtMarkedOccupied() == -1 && currentLocation
		// != null && RestClient.isLoggedIn())
		{
			// and he is in proximity of the court
			// boolean isInProximity = currentLocation!= null ?
			// currentLocation.distanceTo(thisCourtsLocation) <=
			// Constants.PROXIMITY : false;
			// if (isInProximity)

			// then enable markoccupied button
			if (RestClient.isLoggedIn()) {

				Button markCourtOccupied = (Button) findViewById(R.id.marl_occu_button);
				if (markid != -1 && messageposted) {
					markCourtOccupied.setEnabled(false);
					markCourtOccupied
							.setBackgroundResource(R.drawable.disablemarkcourtoccupied);
					cmsg.setVisibility(View.VISIBLE);
					cmsg.setText("You have posted a message and you have already mark court occupied.");
					Log.i("markid not minus one?", "yes");
				}

				else if (markid != -1 && messageposted==false) {
					markCourtOccupied.setEnabled(false);
					markCourtOccupied
							.setBackgroundResource(R.drawable.disablemarkcourtoccupied);
					cmsg.setVisibility(View.VISIBLE);
					cmsg.setText("You have already mark court occupied.");
					Log.i("markid not minus one?", "yes");
				}

				else {
					markCourtOccupied.setEnabled(true);
					markCourtOccupied
							.setBackgroundResource(R.drawable.markcourtoccupied);

					//cmsg.setVisibility(View.GONE);
				}
			}

		}

	}

	private void populateView() {
		TextView tv = (TextView) this.findViewById(R.id.court_name);
		tv.setText(tennisCourtDetails.getName().trim());
		tv = (TextView) this.findViewById(R.id.court_addr_1);
		SpannableString content = new SpannableString((tennisCourtDetails.getAddress().trim()));
		content.setSpan(new UnderlineSpan(), 0, (tennisCourtDetails.getAddress().trim().length()), 0);
		tv.setText(content);
		tv.setOnClickListener(this);
		// tv = (TextView) this.findViewById(R.id.court_addr_2);
		// tv.setText(tennisCourtDetails.getCity()+","+tennisCourtDetails.getState()+" "+tennisCourtDetails.getZipcode());
		tv = (TextView) this.findViewById(R.id.court_phone_1);
		SpannableString content1 = new SpannableString((tennisCourtDetails.getPhone().trim()));
		content1.setSpan(new UnderlineSpan(), 0, (tennisCourtDetails.getPhone().trim().length()), 0);
		tv.setText(content1);
		tv = (TextView) this.findViewById(R.id.text_sub_courts);
		tv.setText("" + tennisCourtDetails.getSubcourts());
		// tv = (TextView) this.findViewById(R.id.court_type);?? //TODO what do
		// set here
		tv = (TextView) this.findViewById(R.id.court_facility_type);
		tv.setText(tennisCourtDetails.getFacilityType());
		tv = (TextView) this.findViewById(R.id.court_timings);
		tv.setText(tennisCourtDetails.getTennisTimings());
		tv.setSelected(true);
		tv.setEnabled(true);

		tv = (TextView) this.findViewById(R.id.no_of_sub_courts);
		tv.setText("" + tennisCourtDetails.getSubcourts());
		phone = (ImageView) findViewById(R.id.court_phone_icon);

		phone.setOnClickListener(this);

		ImageView onBack = (ImageView) findViewById(R.id.court_back_icon);
		onBack.setEnabled(true);

	}

	public class ExpadableAdapter extends BaseExpandableListAdapter {

		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LayoutInflater infalInflater = (LayoutInflater) CourtDetailsActivity.this
					.getApplicationContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			switch (groupPosition) {
			case 0:
				View view = infalInflater.inflate(
						R.layout.child_layout_ameneties_services, null);

				((ImageView) (view.findViewById(R.id.lockerRoomIcon)))
						.setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.LOCKER_ROOM
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.parking)))
						.setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.PARKING
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.lessons)))
						.setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.LESSONS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.lights)))
						.setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.LIGHTS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.snackBar)))
						.setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.SNACK_BAR
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.shop)))
						.setImageLevel(tennisCourtDetails.getTennisAmeneties()[Constants.AMENITY.SHOP
								.ordinal()] != null ? 1 : 0);

				return view;
			case 1:

				view = infalInflater.inflate(R.layout.child_layout_activities,
						null);
				((ImageView) (view.findViewById(R.id.adultProgram)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.NEW_PLAYER_ADULT_PROGRAM
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.teamTennis)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.TEAM_TENNIS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.juniorProgram)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.NEW_PLAYER_JUNIOR_PROGRAM
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.tournaments)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.TOURNAMENTS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.ladders)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.LADDERS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.roundRobin)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.ROUND_ROBINS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.socialMixers)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.SOCIAL_MIXERS
								.ordinal()] != null ? 1 : 0);
				((ImageView) (view.findViewById(R.id.seniors)))
						.setImageLevel(tennisCourtDetails.getTennisActivities()[Constants.ACTIVITY.SENIORS
								.ordinal()] != null ? 1 : 0);
				return view;

			}
			return null;
		}

		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		public Object getGroup(int groupPosition) {
			return null;
		}

		public int getGroupCount() {
			return 2;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			LayoutInflater infalInflater = (LayoutInflater) CourtDetailsActivity.this
					.getApplicationContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			switch (groupPosition) {
			case 0:
				return infalInflater.inflate(
						R.layout.group_layout_ameneties_services, null);
			case 1:
				return infalInflater.inflate(R.layout.group_layout_activities,
						null);
			}
			return null;

		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

	public void postMessage(View v) {
		Intent intentForTennisCourtDetails = new Intent(this,
				PostMessageActivity.class);
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		intentForTennisCourtDetails.putExtras(extras);
		startActivity(intentForTennisCourtDetails);// fire it up baby
	}

	public void markOccupied(View v) {

		progressDialog = ProgressDialog.show(CourtDetailsActivity.this, "",
				"Marking court occupied...", true);
		progressDialog.show();

		registerReceiver(syncFinishedReceiver, new IntentFilter(
				SyncAdapter.SYNC_FINISHED_ACTION));
		ContentResolver.requestSync(
				MapViewActivity.getAccount(CourtDetailsActivity.this),
				ProviderContract.AUTHORITY,
				SyncAdapter.getMarkOccupiedBundle(courtId));
	}

	public void onBack(View v) {
		this.finish();
	}

	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "sync for mark occupied done");

			unregisterReceiver(this);

			if (intent.getExtras().getBoolean(SyncAdapter.SYNC_ERROR)) {
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CourtDetailsActivity.this);
				builder.setMessage("Error while marking court occupied")
						.setCancelable(false)
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										CourtDetailsActivity.this.finish();
									}
								});

				alert = builder.create();
				alert.show();
			} else {
				// Server has been updated now update mapviewacitity's static
				// variables
				MapViewActivity.setCourtMarkedOccupied(courtId);
				// ContentValues contentValues = new ContentValues(5);
				// contentValues.put("occupied", "1");
				// getContentResolver().update(ProviderContract.TennisCourts.CONTENT_URI.buildUpon().appendPath(courtId
				// + "").build(), contentValues, " _id = ?", new
				// String[]{courtId+""});
				progressDialog.setMessage("Court marked occupied");
				progressDialog.dismiss();
				cmsg.setText("Not in proximity of the court");

			}
		}
	};

	public final void viewMessage(long rowId) {
		Intent intentForTennisCourtDetails = new Intent(this,
				ViewMessageActivity.class);
		Bundle extras = new Bundle();
		extras.putInt(SyncAdapter.MESSAGE_ID, (int) rowId);
		extras.putInt(SyncAdapter.COURT_ID, courtId);
		intentForTennisCourtDetails.putExtras(extras);
		startActivity(intentForTennisCourtDetails);
	}

	/*
	 * class MessageRefresh extends TimerTask { public void run() { Log.d(TAG,
	 * "scheduling timertask to get messages ");
	 * ContentResolver.requestSync(MapViewActivity
	 * .getAccount(CourtDetailsActivity.this), ProviderContract.AUTHORITY,
	 * SyncAdapter.getAllMessagesBundle(courtId)); } }
	 */

	private class GetMessagesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d(TAG, "scheduling timertask to get messages ");
			ContentResolver.requestSync(
					MapViewActivity.getAccount(CourtDetailsActivity.this),
					ProviderContract.AUTHORITY,
					SyncAdapter.getAllMessagesBundle(courtId));
			return null;
		}

	}

	/*
	 * @Override public void onReceiveResult(int resultCode, Bundle
	 * resultBundle) { Log.d(TAG, " onRecvResult ");
	 * 
	 * }
	 */

	private class MessagesCursorAdapter extends SimpleCursorAdapter {

		private final LayoutInflater mInflater;
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public MessagesCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.court_message_row, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// 1

			String scheduled_time = cursor.getString(cursor
					.getColumnIndex("scheduled_time"));
			sdfDate.setTimeZone(TimeZone.getTimeZone("gmt"));

			Date scheduledDate;
			try {
				scheduledDate = sdfDate.parse(scheduled_time);
				String scheduledDateOnlyTime = DateFormat
						.getTimeInstance(DateFormat.SHORT)
						.format(scheduledDate).toString();
				((TextView) view.findViewById(R.id.scheduled_time))
						.setText(scheduledDateOnlyTime);
			} catch (ParseException e) {
				Log.e(TAG, e.getMessage());
			}

			// 2
			user = TennisCourtDetails.nonNullString(cursor.getString(cursor
					.getColumnIndex("user_name")));
			if (user.length() > 20)
				user = user.substring(0, 20);
			((TextView) view.findViewById(R.id.user)).setText(user);

			// 3
			String contact_info = cursor.getString(cursor
					.getColumnIndex("contact_info"));
			if (contact_info.length() > 20)
				contact_info = contact_info.substring(0, 20);
			((TextView) view.findViewById(R.id.contact_info)).setText(user);

			// 4
			String level = cursor.getString(cursor.getColumnIndex("level"));
			((TextView) view.findViewById(R.id.level)).setText("Beginner");

			// 5
			String players_needed = cursor.getString(cursor
					.getColumnIndex("players_needed"));
			((TextView) view.findViewById(R.id.players_needed))
					.setText("Players needed: " + players_needed);

			// 6
			String time_posted = cursor.getString(cursor
					.getColumnIndex("time_posted"));
			Date timePostedDate;
			try {
				timePostedDate = sdfDate.parse(time_posted);
				String timepostedAgo = DateUtils.getRelativeTimeSpanString(
						timePostedDate.getTime()).toString();
				((TextView) view.findViewById(R.id.time_posted))
						.setText(timepostedAgo);
			} catch (ParseException e) {
				Log.e(TAG, e.getMessage());
			}

			// 7
			String messageText = cursor
					.getString(cursor.getColumnIndex("text"));
			if (messageText.length() > 50) {
				messageText = messageText.substring(0, 48);
				messageText = messageText + "...";
			}
			((TextView) view.findViewById(R.id.message_text))
					.setText(messageText);
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.court_phone_icon:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					CourtDetailsActivity.this);

			alertDialogBuilder.setTitle("Do you want to make a call?");

			alertDialogBuilder

			.setPositiveButton("okay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"
							+ tennisCourtDetails.getPhone().trim().toString()));
					startActivity(callIntent);

				}
			}).setNegativeButton("cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

							dialog.cancel();
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show();
			break;
		case R.id.court_addr_1:

			
			if (lat != 0.0 | lng != 0.0) {

				final Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?" + "saddr="
								+ gps.getLatitude() + "," + gps.getLongitude()
								+ "&daddr=" + thisCourtsLocation.getLatitude()
								+ "," + thisCourtsLocation.getLongitude()));

				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			} else {

				Toast.makeText(getApplicationContext(),
						"Current location is not available", Toast.LENGTH_LONG)
						.show();
			}
			break;

		}

	}
}
