package com.android.aftab.retrofeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by aftab on 15-09-06.
 */
public class SplashScreenActivity extends AppCompatActivity {

	private static final String MY_PREFERENCES = "prefFile";
	private static final String IS_FIRST_TIME = "IS_FIRST_TIME";



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (isFirstTime()) {
			// set content view
		}
		else {
			startActivity(new Intent(this, CalendarActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}
	}


	private boolean isFirstTime () {

		SharedPreferences preferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
		boolean firstTime = preferences.getBoolean(IS_FIRST_TIME, true);

		if (firstTime) {
			preferences.edit().putBoolean(IS_FIRST_TIME, false);
			preferences.edit().apply();
		}

		return firstTime;
	}
}
