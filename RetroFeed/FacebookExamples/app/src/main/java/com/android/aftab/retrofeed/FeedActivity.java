/***********************************************************************
 * FeedActivity.java: Creates a feed of most recent Facebook photo posts
 *                    formatted in a listView of cards for photos that
 *                    have been posted on specific days of the calendar.
 *
 * Authors: Kyle Wilson, Kyle Genoe
 * Additional contributions By: Aftab Ahmad, Ryan Haque
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedActivity extends ActionBarActivity{

    private ArrayList <MediaInfo> info = new ArrayList(); // ArrayList of Social Media data that need to be displayed for a date
    private ArrayList <MediaInfo> statuses = new ArrayList(); // ArrayList of Social Media data that need to be displayed for a date
    private ArrayList <MediaInfo> tweets = new ArrayList();
    private ArrayList <MediaInfo> instaPhotos = new ArrayList();

    private RecyclerView recList; // ListView for feed cards
    private Toolbar toolbar; // Toolbar for feed page
    private List<MediaInfo> mediaItems; // List of all social media posts

    private static final String TAG = "FeedActivity"; // Tag for log output

    /*                           onCreate()                            */
    /* Creates the feed page and sets up the listener for the ListView */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity);

        // set toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(R.color.colorPrimaryDark));
        }

        int day=0, month=0, year=0;

        /* Handle the intent */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            info = extras.getParcelableArrayList("pics");
            statuses = extras.getParcelableArrayList("statuses");
            tweets = extras.getParcelableArrayList("tweets");
            instaPhotos = extras.getParcelableArrayList("instaPics");

            day = extras.getInt("day");
            month = extras.getInt("month");
            year = extras.getInt("year");
        }

        // Set up RecyclerView
        recList = (RecyclerView) this.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        // add listener
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {

                            @Override public void onItemClick(View view, int position) {

                            }
                        })
        );

        // Set up ListView for RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        mediaItems = createList(day, month, year); // get the list of data that needs to be displayed

        if (mediaItems.isEmpty()){

            MediaInfo info = new MediaInfo();
            info.setTitle("No History");
            info.setContent("Sorry. No social media history available.");
            mediaItems.add(info);
        }

        MediaAdapter ca = new MediaAdapter(mediaItems, this); // Create an adapter that will link the data to the Views in the card
        recList.setAdapter(ca); // Set the cards to the ListView

    }

    /*              setStatusBarColor()                 */
    /* Sets the status bar's color and appearance       */
    private void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //status bar height
            int statusBarHeight = getStatusBarHeight();

            //action bar height
            statusBar.getLayoutParams().height = statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    /*          getStatusBarHeight()        */
    /* Fetches the height of the status bar */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /*          onCreateOptionsMenu()        */
    /* Loads all contents of the top toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Set the search view */
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /* WORK IN PROGRESS
        *  For searching data */
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

    /*               showSettings()            */
    /* Responsible for launching settings page */
    public void showSettings() // go to settings activity
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity (intent);
    }

    private void getPhotos (List <MediaInfo> results, int day, int month, int year) {

        // Go through the list of social media data
        for (int i=0; i< info.size(); i++) {

            MediaInfo in = new MediaInfo();
            Log.d (TAG, info.get(i).toString());

            String dtStart = info.get(i).getDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = format.parse(dtStart);

                int picDay = date.getDate();
                int picMonth = date.getMonth() +1;
                int picYear = date.getYear() + 1900;

                /*Log.d (TAG, "Date from photo is: " + date.toString());
                Log.d (TAG, "Date day is: " + picDay);
                Log.d (TAG, "Month is: " + picMonth);
                Log.d (TAG, "Year is: " + picYear);

                Log.d (TAG, "Date Selected");
                Log.d (TAG, "Day is: " + day);
                Log.d (TAG, "Month is: " + month);
                Log.d (TAG, "Year is: " + year);*/

                // If the data matches the date, then display it.
                if (picDay == day && picMonth == month && picYear == year) {
                    in.setTitle(info.get(i).getTitle());
                    in.setContent(date.toString());
                    in.setPhoto(info.get(i).getPhoto());
                    in.setDate(info.get(i).getDate());
                    results.add(in);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void getStatuses (List<MediaInfo> results, int day, int month, int year) {

        // Go through the list of social media data
        for (int i=0; i< statuses.size(); i++) {

            Log.d (TAG, statuses.toString());
            MediaInfo in = new MediaInfo();
            Log.d (TAG, statuses.get(i).toString());

            String dtStart = statuses.get(i).getDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = format.parse(dtStart);

                int picDay = date.getDate();
                int picMonth = date.getMonth() +1;
                int picYear = date.getYear() + 1900;

                /*Log.d (TAG, "Date from photo is: " + date.toString());
                Log.d (TAG, "Date day is: " + picDay);
                Log.d (TAG, "Month is: " + picMonth);
                Log.d (TAG, "Year is: " + picYear);

                Log.d (TAG, "Date Selected");
                Log.d (TAG, "Day is: " + day);
                Log.d (TAG, "Month is: " + month);
                Log.d (TAG, "Year is: " + year);*/

                // If the data matches the date, then display it.
                if (picDay == day && picMonth == month && picYear == year) {

                    in.setTitle(statuses.get(i).getTitle());
                    in.setContent(statuses.get(i).getContent());
                    in.setPhoto(statuses.get(i).getPhoto());
                    in.setDate(statuses.get(i).getDate());

                    results.add(in);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void getTweets (List<MediaInfo> results, int day, int month, int year) {

        // Go through the list of social media data
        for (int i=0; i< tweets.size(); i++) {

            MediaInfo in = new MediaInfo();
            Log.d (TAG, tweets.get(i).toString());

            String dtStart = tweets.get(i).getDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = format.parse(dtStart);

                int picDay = date.getDate();
                int picMonth = date.getMonth() +1;
                int picYear = date.getYear() + 1900;

                /*Log.d (TAG, "Date from photo is: " + date.toString());
                Log.d (TAG, "Date day is: " + picDay);
                Log.d (TAG, "Month is: " + picMonth);
                Log.d (TAG, "Year is: " + picYear);

                Log.d (TAG, "Date Selected");
                Log.d (TAG, "Day is: " + day);
                Log.d (TAG, "Month is: " + month);
                Log.d (TAG, "Year is: " + year);*/

                // If the data matches the date, then display it.
                if (picDay == day && picMonth == month && picYear == year) {

                    in.setTitle(tweets.get(i).getTitle());
                    in.setContent(tweets.get(i).getContent());
                    in.setPhoto(tweets.get(i).getPhoto());
                    in.setDate(tweets.get(i).getDate());

                    results.add(in);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void getInstaPhotos (List<MediaInfo> results, int day, int month, int year) {

        // Go through the list of social media data
        for (int i=0; i< instaPhotos.size(); i++) {

            MediaInfo in = new MediaInfo();
            Log.d (TAG, instaPhotos.get(i).toString());

            String dtStart = instaPhotos.get(i).getDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = format.parse(dtStart);

                int picDay = date.getDate();
                int picMonth = date.getMonth() +1;
                int picYear = date.getYear() + 1900;

                /*Log.d (TAG, "Date from photo is: " + date.toString());
                Log.d (TAG, "Date day is: " + picDay);
                Log.d (TAG, "Month is: " + picMonth);
                Log.d (TAG, "Year is: " + picYear);

                Log.d (TAG, "Date Selected");
                Log.d (TAG, "Day is: " + day);
                Log.d (TAG, "Month is: " + month);
                Log.d (TAG, "Year is: " + year);*/

                // If the data matches the date, then display it.
                if (picDay == day && picMonth == month && picYear == year) {

                    in.setTitle(instaPhotos.get(i).getTitle());
                    in.setContent(instaPhotos.get(i).getContent());
                    in.setPhoto(instaPhotos.get(i).getPhoto());
                    in.setDate(instaPhotos.get(i).getDate());

                    results.add(in);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*                             createList()                                   */
    /* Builds a MediaInfo object with social media data and stores it in the list */
    private List<MediaInfo> createList(int day, int month, int year) {

        List<MediaInfo> results = new ArrayList<>();

        getPhotos(results, day, month, year);
        getStatuses(results, day, month, year);
        getTweets(results, day, month, year);
        getInstaPhotos(results, day, month, year);

        for (int i=0; i<results.size(); i++) {
            Log.d (TAG, results.get(i).toString());
        }

        return results;
    }
}
