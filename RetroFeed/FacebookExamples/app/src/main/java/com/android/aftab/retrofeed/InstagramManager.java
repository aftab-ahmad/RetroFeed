/***********************************************************************
 * InstagramManager.java:  Directly handles all Instagram requests for
 *                         data
 *
 * Authors: Kyle Wilson, Aftab Ahmed
 *          startPhotos class & JSON parsing: Kyle Genoe
 *
 * Last Updated: April 12th, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.aftab.retrofeed.InstagramDialog.OAuthDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 *
 *
 * More major edits here
 *
 *
 */
public class InstagramManager {

	private InstagramSession mSession;
	private InstagramDialog mDialog;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;
	private String mAuthUrl;
	private String mTokenUrl;
	private String mAccessToken;
	private Context mCtx;

	private Context context;

	private String mClientId;
	private String mClientSecret;


	private static int WHAT_FINALIZE = 0;
	private static int WHAT_ERROR = 1;
	private static int WHAT_FETCH_INFO = 2;

	private ArrayList<MediaInfo> photos = new ArrayList <> (); // List of Instagram photos

    /* Instagram URL strings */
	public static String mCallbackUrl = "";
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	private static final String API_URL = "https://api.instagram.com/v1";

	private static final String TAG = "InstagramAPI";

	public InstagramManager(Context context, String clientId, String clientSecret,
							String callbackUrl) {
		
		mClientId = clientId;
		mClientSecret = clientSecret;
		mCtx = context;
		mSession = new InstagramSession(context);
		mAccessToken = mSession.getAccessToken();
		mCallbackUrl = callbackUrl;
		mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
				+ clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
		mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
				+ mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";
		

		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

    /* Makes request to fetch access token & user info from Instagram server */
	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				int what = WHAT_FETCH_INFO;

                /* Try request */
				try {
					URL url = new URL(TOKEN_URL);
					//URL url = new URL(mTokenUrl + "&code=" + code);
					Log.i(TAG, "Opening Token URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					//urlConnection.connect();

					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					writer.write("client_id="+mClientId+
								"&client_secret="+mClientSecret+
								"&grant_type=authorization_code" +
								"&redirect_uri="+mCallbackUrl+
								"&code=" + code);
				    writer.flush();

					String response = streamToString(urlConnection.getInputStream());
					Log.i(TAG, "response " + response);

					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					
					mAccessToken = jsonObj.getString("access_token");
					Log.i(TAG, "Got access token: " + mAccessToken);

					String id = jsonObj.getJSONObject("user").getString("id");
					String user = jsonObj.getJSONObject("user").getString("username");
					String name = jsonObj.getJSONObject("user").getString("full_name");
					
					mSession.storeAccessToken(mAccessToken, id, user, name);
					
				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

    /* Fetch photos array */
	public ArrayList<MediaInfo> getInstaPhotos () {
		return photos;
	}

    /* Executes fetching photos */
	public void startInstaData (Context context){
		this.context = context;
		new startPhotos().execute();
	}

    /* startPhotos Class */
    /* Makes requests to fetch Instagram user data asynchronously */
	private class startPhotos extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... test) {

			Log.d(TAG, "Starting to fetch photos");
			try {
				URL url = new URL(API_URL + "/users/" + mSession.getId() + "/media/recent/?access_token=" + mAccessToken);

				/* Open connection with Instagram */
				InputStream inputStream = url.openConnection().getInputStream();
				String response = streamToString(inputStream); // Fetch response as a string

				Log.d (TAG, "response is: " + response);

				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				JSONArray jsonArray = jsonObject.getJSONArray("data"); // JSON array of data

				Log.d(TAG, "SUCCESSFUL MEDIA GRAB");
				Log.d(TAG, jsonArray.toString()); // JSON data logged

				//parse json
				for (int i = 0; i < jsonArray.length(); i++) { // go through the JSON data

					JSONObject object = (JSONObject) jsonArray.get(i);

					/**** Get the TAGS we require. ****/
					//Get url of image (given 3: lowres, highres, thumbnail)
					JSONObject picUrlsObject = (JSONObject) object.get("images");
					JSONObject picUrlObject = (JSONObject) picUrlsObject.get("standard_resolution"); //object contains width and height aswell
					String content = picUrlObject.get("url").toString();
					content.replace("\\", "");

					//Get time created, in proper form, from JSON
					String unixTimestampString = object.get("created_time").toString();
					long timeInMiliSeconds = Long.valueOf(unixTimestampString) * 1000;
					Date createdDate = new Date(timeInMiliSeconds);
					String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(createdDate);

					Log.d(TAG, "Time = " + dateString);
					Log.d(TAG, "source = " + content);

					MediaInfo pic = new MediaInfo("Instagram", dateString, content, dateString);
					photos.add(pic);
				}

			} catch (Exception e) {
				Log.d (TAG, e.toString());
			}

			return null;
		}

		protected void onPostExecute(String result) {
			Log.d (TAG, "Completed - getPhotos");

			CalendarActivity a = (CalendarActivity) context;
			a.setIcon();

		}
	}

    /* fetchUserName() */
    /* Makes request to fetch Instagram user's username */
	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");
		
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user info");
				int what = WHAT_FINALIZE;
				try {
					URL url = new URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken);

					Log.d(TAG, "Opening URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();

					String response = streamToString(urlConnection.getInputStream());
					System.out.println(response);

					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					String name = jsonObj.getJSONObject("data").getString("full_name");
					String bio = jsonObj.getJSONObject("data").getString("bio");
					Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

    /* Handles messages after requests */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_ERROR) {
				mProgress.dismiss();
				if(msg.arg1 == 1) {
					mListener.onFail("Failed to get access token");
				}
				else if(msg.arg1 == 2) {
					mListener.onFail("Failed to get user information");
				}
			} 
			else if(msg.what == WHAT_FETCH_INFO) {
				fetchUserName();
			}
			else {
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};

    /* hasAccessToken() */
    /* Check if access token is already stored */
	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

    /* setListener () */
    /* Sets listener for changes in authentication */
	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}

    /* getUserName() */
    /* Execute the fetch of Instagram username */
	public String getUserName() {
		return mSession.getUsername();
	}

    /* getId() */
    /* Executes the fetch of Instagram user ID */
	public String getId() {
		return mSession.getId();
	}

    /* getName() */
    /* Executes the fetch of Instagram first/last name */
	public String getName() {
		return mSession.getName();
	}

    /* authorize () */
    /* Creates connection with Instagram server */
	public void authorize() {
		//Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        //webAuthIntent.setData(Uri.parse(AUTH_URL));
        //mCtx.startActivity(webAuthIntent);
		OAuthDialogListener listener = new OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};

		mDialog = new InstagramDialog(mCtx, mAuthUrl, listener);
		mDialog.show();
	}

	private String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();

		public abstract void onFail(String error);
	}
}