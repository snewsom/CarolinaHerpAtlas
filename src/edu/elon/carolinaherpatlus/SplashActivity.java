/**
 * SplashActivity.java 1.0 May 8, 2013
 * 
 * COPYRIGHT (c) 2013 David B. Belyea. All Rights Reserved
 */
package edu.elon.carolinaherpatlus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Start with summary description line
 * 
 * @author dbelyea
 * @version 1.0
 * 
 */
public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, CreateRecordActivity.class);

                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}