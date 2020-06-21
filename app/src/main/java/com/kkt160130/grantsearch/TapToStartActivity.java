/* TapToStartActivity.java
 * Layout: activity_tap_to_start.xml
 *
 * This is the second activity the application displays. The activity displays text on the screen
 * that says "Tap to Start". Once the user taps on the screen, the main activity will start.
 */
package com.kkt160130.grantsearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TapToStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_to_start);

        // hides device status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // hides action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    // onClick
    public void onTapToStartClick(View view)
    {
        Intent intent = new Intent(TapToStartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
