/***********************************************************************
 * TwitterFragment.java:  The class with the Twitter API code. Handles
 *                        the Twitter permissions and requests along
 *                        with the helper functions which will be called
 *                        by other classes.
 *
 * Authors: Kyle Genoe
 * Additional contributions By:
 *
 * Last Updated: March 13th, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TwitterFragment extends Fragment {

    private static final String TAG = "TwitterFragment";

    /* Twitter Login/Logout Button and general user info fields */
    private TwitterLoginButton loginButton;
    private Button logoutButton;
    private TextView username;
    private ImageView userPic;

    /* Twitter session */
    TwitterSession session;

    /* Tweet Array */
    private ArrayList <MediaInfo> tweetArray = new ArrayList <> ();

    private boolean check;
    private Toolbar toolbar;

    private boolean twitterLogin = false;

    private Context context;

    /* Called when the Fragment is launched.
    *  Also sets the lifecycle of a Twitter Session */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* Called when the screen is loaded with views
    * Sets up the toolbar, and asks user to accept permissions for use. */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.twitter_fragment, container, false);

        /* Fields to print dummy data pulled from Twitter  */
        username = (TextView) view.findViewById(R.id.username);
        userPic = (ImageView) view.findViewById(R.id.userPic);
        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        logoutButton = (Button) view.findViewById(R.id.twitter_logout_button);

        // Set up the toolbar
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((ActionBarActivity)getActivity()).setSupportActionBar(toolbar);
            ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ActionBarActivity)getActivity()). getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        session = Twitter.getSessionManager().getActiveSession();

        if (session != null) {
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            username.setText("You are logged in as " + session.getUserName());
        }
        else {
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setText("Log in with Twitter");
            username.setText("You are not logged in.");
        }

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.d(TAG,"success logging into twitter!");
                username.setText("You are currently logged in as " + result.data.getUserName());
                loginButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);
                //TwitterSession session = Twitter.getSessionManager().getActiveSession();
                //getTweets(result.data);
                twitterLogin = true;

            }

            @Override
            public void failure(TwitterException exception) {
                twitterLogin = false;
                // Do something on failure
                Log.d(TAG,"error logging into twitter :(");
                username.setText("You are not logged in.");
            }
        });

        /* Logout Button Listener and Response */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twitter.getInstance();
                Twitter.logOut(); // Logout of current twitter session
                tweetArray.clear();

                logoutButton.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText("Log in with Twitter");
                username.setText("You are not logged in.");
            }
        });

        return view;
    }

    public ArrayList <MediaInfo> getTweets (){
        return tweetArray;
    }

    private void tweetsComplete (Result<List<Tweet>> listResult) {

        //Successfully pulled a group of tweets (minimum 200)
        int resultSize = listResult.data.size();    //length of results
        Log.d(TAG, "Number of results is: " + Integer.toString(resultSize));    //log the result length

        //Add this group of tweets to the array
        for (int i = 0; i < resultSize; i++){

            Log.d (TAG, listResult.data.get(i).createdAt);
            String photo = "photo";

            if (listResult.data.get(i).entities.media != null) {
                Log.d(TAG, listResult.data.get(i).entities.media.get(0).mediaUrl);
                photo = listResult.data.get(i).entities.media.get(0).mediaUrl;
            }
            Log.d (TAG, listResult.data.get(i).text);

            String content = listResult.data.get(i).text;
            String date = listResult.data.get(i).createdAt;

            String temp [] = date.split(" ");

            String month = temp[1];
            String day = temp[2];
            String year = temp[5];

            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("MMM").parse(month));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int monthInt = cal.get(Calendar.MONTH) + 1;

            if (monthInt < 10)
                month = "0"+monthInt;
            else
                month = Integer.toString(monthInt);

            date = year + "-" + month + "-" + day + "T"+temp[3];
            Log.d (TAG, "Date is: " + date);

            tweetArray.add(new MediaInfo("Twitter", content, photo, date));
        }
    }

    public void startTweets (Context context) {
        this.context = context;
        new getTweets().execute();
    }

    /*                          getPhotosUploaded                           */
    /* Class which handles retrieving the photos asynchronously.
       This way it doesn't block the main thread while waiting for photos   */
    private class getTweets extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... test) {

            TwitterSession session = Twitter.getSessionManager().getActiveSession();
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = twitterApiClient.getStatusesService();

            String userName = session.getUserName();

            statusesService.userTimeline(null,userName,null,null,null,null,null,null,null,
                    new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> listResult) {
                            tweetsComplete(listResult);

                            Log.d (TAG, "DONE");
                            check = true;

                            Log.d(TAG,"Array Size after adding: "+ Integer.toString(tweetArray.size()));

                            CalendarActivity a = (CalendarActivity) context;
                            a.setIcon();

                        }
                        @Override
                        public void failure(TwitterException e) {
                            Log.d(TAG, "Error. Didn't pull tweets");
                            check = false;
                        }
                    });

            return null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button
        Log.d (TAG, "in onActivityResult");

        loginButton.onActivityResult(requestCode, resultCode,
                data);
    }
}