
package com.services;

import com.mewannaplay.client.RestClient;
import com.net.RequestMethods;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MWPBgWebSerive extends IntentService implements Runnable {

    public static final String EXTRAS_RESPONSE_MESSAGE = "mewannaplay.BackgroundWebSerive.response_message";
    public static final String EXTRAS_SUCCESS = "mewannaplay.BackgroundWebSerive.success";

    public static final String EXTRAS_CALLER_ID = "mewannaplay.BackgroundWebSerive.callerid";

    public static final String WEB_INTENT_FILTER1 = "mewannaplay.BackgroundWebSerive.intent.filter1";

    public static final String EXTRAS_URL = "mewannaplay.BackgroundWebSerive.url";

    private String meWannaPlayCourtsUrl = "http://api.mewannaplay.com/v1/cities";

    private String meWannaPlayCallerId = "";

    private final IBinder binder = new WebBinder();

    private final Handler handler = new Handler();
    protected Context context;

    public MWPBgWebSerive() {
        super("UpdateService");
        Log.e("MeWannaPlay", "BackgroundWebSerive");
    }

    protected void doWakefulWork(Intent intent) {
        Log.e("MeWannaPlay", "doWakefulWork");

        context = getApplicationContext();
        // preventing this from running unless specifically called from within
        // the activity or through the alarm manager

        Bundle extras = intent.getExtras();

        meWannaPlayCallerId = extras
                           .getString(MWPBgWebSerive.EXTRAS_CALLER_ID);

        meWannaPlayCourtsUrl = extras
                .getString(MWPBgWebSerive.EXTRAS_URL);

        // service.putExtra(BackgroundWebSerive.EXTRAS_CALLER_ID,
        // "getCityListForSpinner");

        if (!intent.getBooleanExtra("fromApplication", false))
            new Thread(this).start();
    }

    @Override
    public void run() {
 /*       Log.e("MeWannaPlay", "run 1 ");

        boolean getCityListForSpinner1 = meWannaPlayCallerId.equals("getCityListForSpinner");
        if (getCityListForSpinner1) {
            Log.e("MeWannaPlay", "run 222222 " + meWannaPlayCourtsUrl);
            RestClient client = new RestClient(meWannaPlayCourtsUrl);
            client.addParam("q", "android");
            try {
                Log.e("MeWannaPlay", "run 2 ");
                client.execute(RequestMethods.GET);
                if (client.getResponseCode() == 200) {
                    // Successfully connected
                    Log.e("MeWannaPlay", "run 3 ");
                    String responseResult = client.getResponse();

                    Log.e("MeWannaPlay", responseResult);

                    broadCast(true, responseResult, meWannaPlayCallerId);

                } else {
                    Log.e("MeWannaPlay", "run 4 ");
                    // error connecting to server, lets just return an error
                    broadCast(false, "Error Connecting", meWannaPlayCallerId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("MeWannaPlay", "run 333333333 " + meWannaPlayCourtsUrl);

            RestClient client = new RestClient(meWannaPlayCourtsUrl);
            client.addParam("q", "android");
            try {
                Log.e("MeWannaPlay", "run 333333333  2 ");
                client.execute(RequestMethods.GET);
                if (client.getResponseCode() == 200) {
                    // Successfully connected
                    Log.e("MeWannaPlay", "run 333333333 3 ");
                    String responseResult = client.getResponse();

                    Log.e("MeWannaPlay", responseResult);

                    broadCast(true, responseResult, meWannaPlayCallerId);

                } else {
                    Log.e("MeWannaPlay", "run 333333333 4 ");
                    // error connecting to server, lets just return an error
                    broadCast(false, "Error Connecting", meWannaPlayCallerId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    private void broadCast(boolean success, String message, String meWPCallerId) {
        Log.e("MeWannaPlay", "broadCast");
        Intent intent = new Intent();
        intent.putExtra(EXTRAS_SUCCESS, success);
        intent.putExtra(EXTRAS_RESPONSE_MESSAGE, message);
        intent.putExtra(EXTRAS_CALLER_ID, meWPCallerId);
        intent.setAction(WEB_INTENT_FILTER1);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        Log.e("MeWannaPlay", "onCreate");
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MeWannaPlay", "onBind");
        return binder;
    }

    public class WebBinder extends Binder {

        public MWPBgWebSerive getService() {
            Log.e("MeWannaPlay", "WebBinder getService ");
            return MWPBgWebSerive.this;
        }
    }

    @Override
    public void onDestroy() {
        Log.e("MeWannaPlay", "onDestroy ");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        Log.e("MeWannaPlay", "onHandleIntent ");
        // TODO Auto-generated method stub
        doWakefulWork(arg0);
    }
}
