/***********************************************************************
 * FacebookActivity.java: The class with the Facebook API code. Handles
 *                        the Facebook permissions and requests along
 *                        with the helper functions which will be called
 *                        by other classes.
 *
 *
 * Authors: Aftab Ahmad, Ryan Haque
 * Additional contributions By: Kyle wilson, Kyle Genoe (help with functions)
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.example.aftab.facebookexamples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FacebookFragment extends Fragment {

    private static final String TAG = "FacebookFragment";
    private UiLifecycleHelper uiHelper;

    /* Facebook Login Button and general user info fields */
    private LoginButton loginBtn;
    private TextView username;
    private TextView userInfo;
    private ImageView userPic;

    /* ArrayList of MediaInfo objects*/
    private ArrayList <MediaInfo> photos = new ArrayList <> ();
    private ArrayList likes = new ArrayList();
    private ArrayList <MediaInfo> posts = new ArrayList <> ();

    private boolean check;

    private Toolbar toolbar;
    private Context context;

    /* Called when the Fragment is launched.
    *  Also sets the lifecycle of a Facebook Session */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    /* Called when the screen is loaded with views
    * Sets up the toolbar, and asks user to accept permissions for use. */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.facebook_fragment, container, false);

        Log.d(TAG,"in Facebook fragment");

        // Set up the toolbar
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((ActionBarActivity)getActivity()).setSupportActionBar(toolbar);
            ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ActionBarActivity)getActivity()). getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        /* Setup Facebook login button */
        loginBtn = (LoginButton) view.findViewById(R.id.authButton);
        loginBtn.setFragment(this);

        /* Feilds to print dummy data pulled from Facebook  */
        username = (TextView) view.findViewById(R.id.username);
        loginBtn = (LoginButton) view.findViewById(R.id.authButton);
        userInfo = (TextView) view.findViewById(R.id.userInfoTextView);
        userPic = (ImageView) view.findViewById(R.id.userPic);

        // Configure Facebook permissions to show to user.
        loginBtn.setReadPermissions(Arrays.asList("email", "user_location", "user_birthday", "user_likes", "user_photos", "read_stream"));

        loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    username.setText("You are currently logged in as " + user.getName());
                } else {
                    username.setText("You are not logged in.");
                }
            }
        });

        return view;
    }

    /*               checkPhotos()                            */
    /* Returns whether the photos are ready to extract or not */
    public boolean checkPhotos (){
        return check;
    }

    /*               getPhotos()            */
    /* Returns the list of photos           */
    public ArrayList <MediaInfo> getPhotos (){
        return photos;
    }

    public ArrayList <MediaInfo> getStatuses () { return posts; }

    /*               photoComplete()                    */
    /* Called when the response is given from Facebook.
       Works to parse the JSON data and extract out
       the URL of the photo along with the date.        */
    private void photoComplete (Response response) {

        Log.d(TAG, "Response is: " + response);
        GraphObject graphObject = response.getGraphObject();

        if (graphObject != null) {
            JSONObject jsonObject = graphObject.getInnerJSONObject();
            try {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) { // go through the JSON data

                    JSONObject object = (JSONObject) array.get(i);

                    /* Get the TAGS we require. */
                    String time = object.get("created_time").toString();
                    String timeArray [] = time.split("[+]");
                    String content = object.get("source").toString();

                    Log.d(TAG, "Time = " + timeArray[0]);
                    Log.d(TAG, "source = " + content);

                    /* Create a MediaInfo object */
                    MediaInfo pic = new MediaInfo ("Facebook", "Content", content, timeArray[0]);
                    photos.add(pic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        check = true;
    }

    private void statusComplete (Response response) {

        Log.d(TAG, "Response is: " + response);
        GraphObject graphObject = response.getGraphObject();

        if (graphObject != null) {
            JSONObject jsonObject = graphObject.getInnerJSONObject();
            try {
                JSONArray array = jsonObject.getJSONArray("data");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.get(i);

                    Log.d(TAG, "Message = " + object.get("message"));
                    Log.d(TAG, "Time = " + object.get("updated_time"));

                    String time = object.get("updated_time").toString();
                    String timeArray [] = time.split("[+]");
                    String content = object.get("message").toString();

                    MediaInfo status = new MediaInfo ("Facebook", content, "PIC", timeArray[0]);
                    posts.add(status);
                    //Log.d(TAG, "Likes = " + object.get("likes"));

//                    JSONArray arrayLikes = object.getJSONObject("likes").getJSONArray("data");
//                    for (int j = 0; j < arrayLikes.length(); j++) {
//                        JSONObject objectLikes = (JSONObject) arrayLikes.get(j);
//                        Log.d(TAG, "Name = " + objectLikes.get("name"));
//                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*               getLikes()                    */
    /* WORK IN PROGRESS                           */
    public ArrayList getLikes () {

        Session session = Session.getActiveSession();
        if (session == null){
            Log.d(TAG, "Error. Cannot get Session");
        }
        else {
            new Request(
                    session,
                    "/me/likes",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            /* handle the result */
                        }
                    }
            ).executeAsync();
        }

        return likes;
    }


    /*                               onSessionStateChange()                                     */
    /* Gets a Sessions Change and determines what to do whether the user is logged in/out       */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {

        // User is logged in
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            userInfo.setVisibility(View.VISIBLE);
            userPic.setVisibility(View.VISIBLE);

            // Pull basic user data
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        userInfo.setText(buildUserInfoDisplay(user));
                    }
                }
            });
        }
        else if (state.isClosed()) { // User is logged out.
            Log.i(TAG, "Logged out...");
            userInfo.setVisibility(View.INVISIBLE); // Hide the user information
            userPic.setVisibility(View.INVISIBLE);  // Hide the user information
        }
    }

    /*                         Session.StatusCallback(()                        */
    /* Listens for changes in the users Session and calls onSessionStateChange
       Which decides what to do                                                  */
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    /*              buildUserInfoDisplay()                  */
    /* Collects the basic profile information about the user */
    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // - requires user_location permission
        //userInfo.append(String.format("Location: %s\n\n",
                //user.getLocation().getProperty("name")));

        // - no special permissions required
        //userInfo.append(String.format("Locale: %s\n\n",
                //user.getProperty("locale")));

        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages != null) {
            if (languages.length() > 0) {
                ArrayList<String> languageNames = new ArrayList<String>();
                for (int i = 0; i < languages.length(); i++) {
                    JSONObject language = languages.optJSONObject(i);
                    // Add the language name to a list. Use JSON
                    // methods to get access to the name field.
                    languageNames.add(language.optString("name"));
                }
                userInfo.append(String.format("Languages: %s\n\n",
                        languageNames.toString()));
            }
        }

        return userInfo.toString();
    }


    public void startStatuses (Context context) {
        this.context = context;
        new getStatuses().execute();
    }

    /*              startPhotosTagged()                */
    /* Helper function which starts the request to
    *  access tagged photos asynchronously.
       Context is passed in, so when the photos have
       been retrieved, we know which screen to display
       a message on.                                    */
    public void startPhotosTagged (Context context) {
        this.context = context;
        new getPhotosTagged().execute();
    }


    /*              startPhotosUploaded()                */
    /* Helper function which starts the request to
    *  access uploaded photos asynchronously.
       Context is passed in, so when the photos have
       been retrieved, we know which screen to display
       a message on.                                    */
    public void startPhotosUploaded (Context context) {
        this.context = context;
        new getPhotosUploaded().execute();
    }

    /*                          getPhotosUploaded                           */
    /* Class which handles retrieving the photos asynchronously.
       This way it doesn't block the main thread while waiting for photos   */
    private class getPhotosUploaded extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... test) {

            Session session = Session.getActiveSession();
            if (session == null){
                Log.d(TAG, "Error. Cannot get Session");
            }
            else {
                new Request(
                        session,
                        "/me/photos/uploaded",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {
                                photoComplete(response);
                            }
                        }
                ).executeAndWait();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            check = true;
        }
    }

    /*                          getPhotosTagged                             */
    /* Class which handles retrieving the photos asynchronously.
       This way it doesn't block the main thread while waiting for photos   */
    private class getPhotosTagged extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... test) {

            Session session = Session.getActiveSession();
            if (session == null){
                Log.d(TAG, "Error. Cannot get Session");
            }
            else {
                new Request(
                        session,
                        "/me/photos",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {
                                photoComplete(response);
                            }
                        }
                ).executeAndWait();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            check = true;
        }
    }

    /*                          getPhotosTagged                             */
    /* Class which handles retrieving the photos asynchronously.
       This way it doesn't block the main thread while waiting for photos   */
    private class getStatuses extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... test) {

            Session session = Session.getActiveSession();
            if (session == null){
                Log.d(TAG, "Error. Cannot get Session");
            }
            else {
                new Request(
                        session,
                        "/me/statuses",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {
                                statusComplete(response);
                            }
                        }
                ).executeAndWait();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            check = true;

            CalendarActivity a = (CalendarActivity) context;
            a.setIcon();
        }
    }

    /* Block of functions help with
       dealing with the lifecycle of a
       Facebook Session */
    @Override
    public void onResume() {

        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
