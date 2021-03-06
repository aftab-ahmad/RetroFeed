/***********************************************************************
 * CalendarActivity.java: Acts as central hub for communication between
 *                        Facebook and the calendar interface
 *
 * Authors: Kyle Wilson, Kyle Genoe
 * Additional contributions By: Aftab Ahmad, Ryan Haque (help with functions)
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    private ArrayList <MediaInfo> photos = new ArrayList <> ();
    private ArrayList <MediaInfo> posts = new ArrayList<>();
    private ArrayList <MediaInfo> tweets = new ArrayList<>();
    private ArrayList <MediaInfo> instaPhotos = new ArrayList<>();

	private CaldroidFragment calendarFragment;
    private FacebookFragment fbFragment;
    private TwitterFragment twitterFragment;
    private InstagramManager instagramManager;

    private static final String KEY = "isLoggedIn";
    private static final String USER = "username";
	private static final String CALDROID_SAVE = "CALDROID_SAVED_STATE";

    private boolean facebookLogin = false;
    private boolean twitterLogin = false;
    private boolean instagramLogin = false;

    private ProgressDialog progress;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private int month=0;


    /*                    onCreate()                    */
    /* Method that handles initial creation of calendar */
    /* Sets up toolbar and calendar fragment            */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        progress = new ProgressDialog(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret));
        Fabric.with(this, new Twitter(authConfig));

        /* Set the toolbar as action bar */
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        /* Create a calendar fragment */
        calendarFragment = new CalendarFragment();

        if (savedInstanceState != null) {
            calendarFragment.restoreStatesFromKey(savedInstanceState,
		            CALDROID_SAVE);
        }

        // If activity is created from fresh
        else
        {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            calendarFragment.setArguments(args);
        }

        Calendar cal = Calendar.getInstance();
        calendarFragment.setMaxDate(cal.getTime());

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, calendarFragment);
        t.commit();

        /*              CaldroidListener               */
        /* Event listener for clicks on calendar cells */
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onChangeMonth (int month, int year){

                /* Facebook check if posts/photos are empty */
                if (photos.size() != 0 && posts.size() != 0) {
                    setMonth (month);
                    setIcon();
                }

                /* Twitter check if tweets are empty */
                if (tweets.size() != 0) {
                    setMonth(month);
                    setIcon();
                }

                /* Instagram check if photos are empty */
                if (instaPhotos.size() != 0) {
                    setMonth(month);
                    setIcon();
                }
            }

            @Override
            public void onSelectDate(Date date, View view) {

                Log.d(TAG, "Size is: " + photos.size());

                // Condition that a click is made but user is not logged in to any media outlet
                if (!facebookLogin && !twitterLogin && !instagramLogin){
                    Toast.makeText(getApplicationContext(), "Please login first.",
                            Toast.LENGTH_SHORT).show();
                }
                // Cell is ready to be clicked
                else {
                    Log.d (TAG, "Date is: " + date);

                    int day = date.getDate();
                    int month = date.getMonth() + 1;
                    int year = date.getYear() + 1900;

                    Log.d (TAG, "Day is: " + day);
                    Log.d (TAG, "Month is: " + month);
                    Log.d (TAG, "Year is: " + year);

                    Intent intent = new Intent(CalendarActivity.this, FeedActivity.class);
                    intent.putParcelableArrayListExtra("pics", photos);
                    intent.putParcelableArrayListExtra("statuses", posts);
                    intent.putParcelableArrayListExtra("tweets", tweets);
                    intent.putParcelableArrayListExtra("instaPics", instaPhotos);

                    intent.putExtra("day", day);
                    intent.putExtra("month", month);
                    intent.putExtra("year", year);

                    startActivity(intent);
                }
            }
        };

        calendarFragment.setCaldroidListener(listener);

        sharedPref = this.getSharedPreferences("filename", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        getFacebookSession();
        getTwitterSession();
        getInstagramSession();
    }

    private void startProgressBar () {
        /* Animate progress bar */
        if (facebookLogin || twitterLogin || instagramLogin) {
            Log.d(TAG, "Progress started here: " + facebookLogin);
            progress.setMessage("Fetching Data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }
    }

    /*         getFacebookSession()       */
    /* Fetches a Facebook session */
    private void getFacebookSession() {
        Session.openActiveSession(this, false, callback);
    }

    /*         getTwitterSession()       */
    /* Fetches a Twitter session */
    private void getTwitterSession () {

        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            twitterLogin = true;
            startProgressBar();

            Log.d(TAG, "Logged in - Twitter");
            Log.d (TAG, session.getUserName());

            twitterFragment = new TwitterFragment();
            twitterFragment.startTweets(CalendarActivity.this);
        }
        else {
            Log.d(TAG, "Logged out - Twitter");
            twitterLogin = false;
        }
    }

    /*         getInstagramSession()       */
    /* Fetches a Instagram session */
    private void getInstagramSession () {

        boolean loggedIn = sharedPref.getBoolean(KEY, false); // Fetch initial boolean value
        String user = sharedPref.getString(USER, null); // Fetch initial username value

        Log.d(TAG, "loggedIn value is: " + loggedIn);
        if (user != null) {
            Log.d(TAG, "Logged in - Instagram as " + user);
            instagramLogin = true;

            startProgressBar();

            String client_id = getString(R.string.CLIENT_ID);
            String client_secret = getString(R.string.CLIENT_SECRET);
            String callback = getString(R.string.CALLBACKURL);

            instagramManager = new InstagramManager(CalendarActivity.this, client_id, client_secret, callback);
            if (instagramManager.hasAccessToken()) {
                Log.d (TAG, "In here - startInstaData");
                instagramManager.startInstaData(CalendarActivity.this);
            }
        }
        else {
            Log.d (TAG, "Logged out - Instagram.");
            instagramLogin = false;
        }
    }

    private void hideProgressBar() {
        /*if (twitterLogin && !facebookLogin) {
            if (tweets.size() > 0)
                progress.hide();
        }
        else if (!twitterLogin && facebookLogin) {
            if (posts.size() > 0 && photos.size() > 0)
                progress.hide();
        }
        else if (twitterLogin && facebookLogin) {
            if (photos.size() > 0 && posts.size() > 0 && tweets.size() > 0)
                progress.hide();
        }
        else if (instagramLogin && !instaPhotos.isEmpty()) {
                progress.hide();
        }*/
	    progress.hide();
    }

    private void setMonth (int month){
        this.month = month;
    }

    public void setIcon () {

        if (facebookLogin) {
            photos = fbFragment.getPhotos();
            posts = fbFragment.getStatuses();
        }

        if (twitterLogin) {
            tweets = twitterFragment.getTweets();

            for (int i = 0; i < tweets.size(); i++) {
                Log.d(TAG, tweets.get(i).toString());
            }
        }
        if (instagramLogin) {
            instaPhotos = instagramManager.getInstaPhotos();

            for (int i = 0; i < instaPhotos.size(); i++) {
                Log.d(TAG, instaPhotos.get(i).toString());
            }
        }

        int curMonth = 0;
        if (this.month == 0){
            Date date;

            String temp [] = calendarFragment.getMonthTitleTextView().getText().toString().split(" ");
            try {
                date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(temp[0]);

	            Calendar cal = Calendar.getInstance();
	            cal.setTime(date);
	            curMonth = cal.get(Calendar.MONTH)+1;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
            curMonth = this.month;

        HashMap<String, Object> extraData = calendarFragment.getExtraData();
        extraData.put("facebookPhotos", photos);
        extraData.put ("facebookPosts", posts);
        extraData.put ("twitterTweets", tweets);
        extraData.put ("instagramPhotos", instaPhotos);
        extraData.put("currentMonth", Integer.toString(curMonth));

        // refresh the view to rebuild the calendar
        calendarFragment.refreshView();

        hideProgressBar(); // Hide the progress bar after data has been fetched
    }

    /*                    callBack                      */
    /* Validates if Facebook session is active          */
    /* Checks if user is currently logged into Facebook */
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state,
                         Exception exception) {

            if (session.isOpened()) {
                facebookLogin = true;
                startProgressBar();
                Log.d(TAG, "Logged in - facebook");

                fbFragment = new FacebookFragment();
                fbFragment.startPhotosTagged(CalendarActivity.this);
                fbFragment.startPhotosUploaded(CalendarActivity.this);
                fbFragment.startStatuses(CalendarActivity.this);
            }
            else if (session.isClosed()) {
                facebookLogin = false;
                Log.d (TAG, "Logged Out. - Facebook");
            }
        }
    };

    /*          onCreateOptionsMenu()        */
    /* Loads all contents of the top toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Set the search view */
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /* WORK IN PROGRESS */
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();

        return true;
    }

    /*                  onOptionsItemSelected                   */
    /* Handles activity when items in the sub-menu are selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) { // settings
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
            showSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    /*         showSettings()       */
    /* Launches the Settings screen */
    private void showSettings() // go to settings activity
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity (intent);
    }

    /*                    onSaveInstanceState()                         */
    /* Handles ability to re-launch app from where it was last left off */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (calendarFragment != null) {
            calendarFragment.saveStatesToKey(outState, CALDROID_SAVE);
        }
    }
}