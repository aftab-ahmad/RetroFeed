/***********************************************************************
 * InstagramFragment.java:  Handles responses to login/logout and stores
 *                          Instagram key strings
 *
 * Authors: Kyle Wilson, Aftab Ahmed
 *
 * Last Updated: April 12th, 2015
 **********************************************************************/

package com.example.aftab.facebookexamples;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class InstagramFragment extends Fragment {

    private static final String TAG = "InstagramFragment";
    private static final String KEY = "isLoggedIn";
    private static final String USER = "username";

    private View view;

    /* SharedPreferences and editor for storing boolean loggedIn outside of scope */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private String user;

    private InstagramManager instagramManager;

    private ImageButton loginButton;
    private Button logoutButton;
    private TextView username;


    /* Called when the Fragment is launched.
    *  Also sets the lifecycle of a Twitter Session */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.instagram_fragment, container, false);

        final Context context = view.getContext();

        username = (TextView) view.findViewById(R.id.username);
        loginButton = (ImageButton) view.findViewById(R.id.insta_login_button);
        logoutButton = (Button) view.findViewById(R.id.insta_logout_button);

        sharedPref = getActivity().getSharedPreferences("filename", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        /* Instagram key & url strings */
        String client_id = getResources().getString(R.string.CLIENT_ID);
        String client_secret = getResources().getString(R.string.CLIENT_SECRET);
        String callback = getResources().getString(R.string.CALLBACKURL);

        instagramManager = new InstagramManager(context, client_id, client_secret, callback);

        /* Listener for successful Instagram login authentication */
        InstagramManager.OAuthAuthenticationListener listener = new InstagramManager.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                username.setText("Logged in as " + instagramManager.getUserName());
                logoutButton.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);

                editor.putBoolean(KEY, true); // Store boolean externally
                editor.putString(USER, instagramManager.getUserName()); // Store username externally
                editor.commit();
            }

            @Override
            public void onFail(String error) {
                Log.d(TAG, "Error is: " + error);
            }
        };

        instagramManager.setListener(listener);

        user = sharedPref.getString(USER, null); // Fetch initial username value
        if (user == null) {
            username.setText("Not Logged In");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
        else {
            if (instagramManager.hasAccessToken()) {
                logoutButton.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);
                username.setText("Logged in as " + user);
            }
        }

        /* Login button click listener */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instagramManager.authorize();

                if (instagramManager.hasAccessToken()) {
                    username.setText("Logged in as " + instagramManager.getUserName());
                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);

                    editor.putBoolean(KEY, true); // Store boolean externally
                    editor.putString(USER, instagramManager.getUserName()); // Store username externally
                    editor.commit();
                }
            }
        });

        /* Logout button click listener */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instagramManager.resetAccessToken();

                username.setText("Not Logged In");
                logoutButton.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);

                editor.putBoolean(KEY, false); // Store boolean externally
                editor.putString(USER, null); // Store username externally
                editor.commit();
            }
        });

        return view;
    }
}