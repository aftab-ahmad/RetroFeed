/***********************************************************************
 * SettingsActivity.java: Class which handles the settings page which
 *                        includes the login and logout credentials for
 *                        the user.
 *
 * Authors: Aftab Ahmad, Ryan Haque
 * Additional contributions By: Kyle Wilson, Kyle Genoe
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.example.aftab.facebookexamples;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.widget.LoginButton;

import java.io.PrintWriter;
import java.util.ArrayList;

public class SettingsActivity extends ActionBarActivity {

    private static final String TAG = "SettingsActivity"; // Tag for log output

    private Toolbar toolbar; // Stores toolbar data
    private FacebookFragment facebookFragment; // Stores/processes Facebook data
    private TwitterFragment twitterFragment; //Stores/processes Twitter data

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private PagerAdapter tabAdapter;

    /*                      onCreate()                           */
    /* Creates the Settings page when settings page is launched  */
    /* Sets up toolbar and loads the Facebook fragment           */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the layout from activity_settings.xml
        setContentView(R.layout.activity_settings);

        Log.d(TAG,"created SettingsActivity");

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(R.color.colorPrimaryDark));
        }

        /* Add the tabs to the toolbar */
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        tabAdapter = new PagerAdapter(getSupportFragmentManager());

        tabs.setTextColor(Color.WHITE);
        tabs.setDividerColor(Color.WHITE);

        pager.setAdapter(tabAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        /*if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookFragment = new FacebookFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookFragment)
                    .commit();


            Log.d(TAG,"in savedInstanceState");
            twitterFragment = new TwitterFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, twitterFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            facebookFragment = (FacebookFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);

            twitterFragment = (TwitterFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);

        }*/
    }

    public void facebookClick (View view) {
        Log.d (TAG, "Clicked Facebook");


    }

    public void twitterClick (View view) {
        Log.d (TAG, "Clicked Twitter");






    }

    /*              setStatusBarColor()          */
    /* Sets the status bar's color and appearance*/
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

        /* Set the main menu on the toolbar */
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

        if (id == R.id.action_settings) { // user clicked settings on toolbar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will
        // then pass the result to the login button.

        Fragment fragment = tabAdapter.getCurrentFragment();

        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
            Log.d (TAG, "in onActivityResult - SettingsActivity");
        }
    }
}