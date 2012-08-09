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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.android.widgets.DateSlider.SliderContainer;
import com.mewannaplay.asynctask.PostMessageAsyncTask;
import com.mewannaplay.model.Message;
import com.mewannaplay.providers.ProviderContract;
import com.mewannaplay.syncadapter.SyncAdapter;

public class PostMessageActivity extends Activity implements
		OnCheckedChangeListener {

	private static final String TAG = "PostMessageActivity";

	private AlertDialog alert;
	private int courtId;
	static final int DEFAULTDATESELECTOR_ID = 0;

	protected int mLayoutID;
	private final static int minuteInterval = 30;
	private SliderContainer mContainer;
	RadioGroup rgcontactinfo;
	RadioButton rgPhone,rgmail;
	EditText econtactinfo;
	Button post,cancel;
	public static String filenames = "courtdetails";
	String contactInfo;
	String regexStr = "^[0-9]{8,20}$";
	private TextView errorMessage,title,titleplay,titlenoofplayer,titlelevel,titlecontact,titlecomment,hours,mintues;
	Typeface bold,heavy,light,normal;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar mInitialTime = Calendar.getInstance();
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("time")) {
			Calendar c = (Calendar) savedInstanceState.getSerializable("time");
			if (c != null && c.after(mInitialTime)) { // TODO Add check for
														// before

				mInitialTime = c;
			}
		}

		if (minuteInterval > 1) {
			int minutes = mInitialTime.get(Calendar.MINUTE);
			int diff = ((minutes + minuteInterval / 2) / minuteInterval)
					* minuteInterval - minutes;
			mInitialTime.add(Calendar.MINUTE, diff);
		}

		setContentView(R.layout.post_message_layout);
		  bold=Typeface.createFromAsset(this.getAssets(),"Folks-Bold.ttf");
			 heavy=Typeface.createFromAsset(this.getAssets(),"Folks-Heavy.ttf");
			 light=Typeface.createFromAsset(this.getAssets(),"Folks-Light.ttf");
			 normal=Typeface.createFromAsset(this.getAssets(),"Folks-Normal.ttf");
		rgcontactinfo = (RadioGroup) findViewById(R.id.rgcontact);
	
		econtactinfo = (EditText) findViewById(R.id.contact_info);
		errorMessage = (TextView) findViewById(R.id.txterrormsg);
		title=(TextView)findViewById(R.id.titlepost);
		titleplay=(TextView)findViewById(R.id.titleplayat);
		titlenoofplayer=(TextView)findViewById(R.id.titlenoofplayer);
		titlelevel=(TextView)findViewById(R.id.titlelevel);
		titlecontact=(TextView)findViewById(R.id.titlepreferedcontact);
titlecomment=(TextView)findViewById(R.id.titlecomment);
post=(Button)findViewById(R.id.post_message);
cancel=(Button)findViewById(R.id.cancel);
hours=(TextView)findViewById(R.id.hours);
mintues=(TextView)findViewById(R.id.minutes);
post.setTypeface(bold);
cancel.setTypeface(bold);
		title.setTypeface(bold);
		titleplay.setTypeface(bold);
		titlenoofplayer.setTypeface(bold);
		titlelevel.setTypeface(bold);
		titlecontact.setTypeface(bold);
		titlecomment.setTypeface(bold);
		hours.setTypeface(bold);
		mintues.setTypeface(bold);
		
		ImageView postBack = (ImageView) findViewById(R.id.post_back_icon);
		postBack.setEnabled(true);

		rgcontactinfo.setOnCheckedChangeListener(this);
		rgPhone = (RadioButton) findViewById(R.id.rbphn);
rgPhone.setTypeface(normal);
rgmail = (RadioButton) findViewById(R.id.rbemail);
rgmail.setTypeface(normal);
		mContainer = (SliderContainer) this
				.findViewById(R.id.dateSliderContainer);
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

				contactInfo = econtactinfo.getText().toString();
				// ----------- Validation ------------------------

				String userErrorMessage = getMessage();
				if (userErrorMessage != null)
					errorMessage.setText(userErrorMessage);
				else {
					if (rgPhone.isChecked()) // phone selected
					{
						if (!contactInfo.matches(regexStr) == true) {
							// TODO REMOVE TOAST! USE TEXT MESSAGE
							errorMessage.setText("Not a valid Phone number");
							return;
						}
					} else // email selected
					{
						if (!new EmailValidator().validate(contactInfo)) {
							errorMessage.setText("Not a valid email address");
							return;
						}
					}

					postMessage();

				}

			}
		});
	}

	public void postBack(View v) {
		finish();
	}

	private void postMessage() {
		Message message = new Message(); // Keep the message object local
		if (rgPhone.isChecked())
			message.setContactTypeId(1);
		else
			message.setContactTypeId(2);
		// hack!courtId should also be a column in message table but its not
		message.setTennisCourtId(courtId);
		// -------------------------------

		String utcScheduleTime = Util.getUTCTimeForHourMinute(mContainer
				.getTime().get(Calendar.HOUR_OF_DAY),
				mContainer.getTime().get(Calendar.MINUTE));
		message.setScheduleTime(utcScheduleTime);
		Object item = ((Spinner) this.findViewById(R.id.players_needed))
				.getSelectedItem();
		if (item != null)
			message.setPlayerNeeded(item.toString());
		else
			message.setPlayerNeeded("1");

		message.setContactInfo(contactInfo);
		item = ((Spinner) this.findViewById(R.id.level)).getSelectedItem();
		if (item != null)
			message.setLevel(item.toString());
		else
			message.setLevel("Beginner");
		message.setText(((TextView) this.findViewById(R.id.message)).getText()
				.toString());
		new PostMessageAsyncTask(this, message.toJSONObject()).execute(null);
	}


		public void onPostExecutePostMessageTask(boolean error) {
			Log.d(TAG, "post message done");


			if (error) {
	
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PostMessageActivity.this);
				builder.setMessage("Error while posting message")
						.setCancelable(false)
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										PostMessageActivity.this.finish();
									}
								});

				alert = builder.create();
				alert.show();
			} else {
				ProgressDialog progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("Message posted successfully");
				progressDialog.dismiss();
				progressDialog = null;
				MapViewActivity.setCourtPostedMessageOn(courtId);
				this.finish();
			}
		}


	public void onCancel(View v) {
		PostMessageActivity.this.finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState == null)
			outState = new Bundle();
		outState.putSerializable("time", mContainer.getTime());
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		switch (checkedId) {
		case R.id.rbphn:
			errorMessage.setText("");
			econtactinfo.setText("");
			econtactinfo.setInputType(InputType.TYPE_CLASS_PHONE);
			econtactinfo.setHint("e.g. 9999999999");
			// int maxLength = 10;
			//
			// InputFilter[] FilterArray = new InputFilter[1];
			// FilterArray[0] = new InputFilter.LengthFilter(maxLength);
			// econtactinfo.setFilters(FilterArray);
			econtactinfo
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							10) });
			break;

		case R.id.rbemail:
			errorMessage.setText("");
			econtactinfo.setText("");
			econtactinfo
					.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			econtactinfo.setHint("e.g johndoe@mwp.com");
			econtactinfo
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							50) });
			break;
		}

	}

	/**
	 * Returns the message to be displayed at the top of the login dialog box.
	 */
	private String getMessage() {
		if (TextUtils.isEmpty(contactInfo)) {
			return "Invalid Phone or Email";
		} else
			return null;
	}
}
