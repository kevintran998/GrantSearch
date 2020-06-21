/* WelcomeScreenActivity.java
* Layout: activity_welcome_screen.xml
*
* This is the first activity the application displays. The activity displays the application
* name (logo) during start up. After a set amount of time, the application starts the next
* activity to be shown.
*/
package com.kkt160130.grantsearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        // hides device status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // hides action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // automatically begins TapToStart activity after a delay
        // delay defaulted to 1000 milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeScreenActivity.this, TapToStartActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
