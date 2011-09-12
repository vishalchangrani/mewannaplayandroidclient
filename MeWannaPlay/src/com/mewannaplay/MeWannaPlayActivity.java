
package com.mewannaplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MeWannaPlayActivity extends Activity {

    private final int secondsDelayed = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new
                Intent(MeWannaPlayActivity.this, MapViewActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);

    }
}
