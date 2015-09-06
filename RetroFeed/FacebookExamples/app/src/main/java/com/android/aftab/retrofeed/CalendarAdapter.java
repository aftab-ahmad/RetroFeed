/***********************************************************************
 * CalendarAdapter.java: The class with builds the calendar view and
 *                       updates the calendar view each time a new month
 *                       is shown, builds a custom cell.
 *
 * Authors: Kyle Wilson, Kyle Genoe
 * Additional contributions By: Aftab Ahmad, Ryan Haque (help with functions)
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import hirondelle.date4j.DateTime;

public class CalendarAdapter extends CaldroidGridAdapter {

    private static final String TAG = "CalendarAdapter"; // Tag for log output

    /* Default Class constructor */
    public CalendarAdapter(Context context, int month, int year,
                                       HashMap<String, Object> caldroidData,
                                       HashMap<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    /*                        getView()                          */
    /* Fetches the View object of an individual calendar cell    */
    /* Stores cells and cell dates in their own respective lists */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.cell, null);
        }

        /* Declare ImageView accessors and set invisible */
        ImageView fbView, twitterView, instaView;
        fbView = (ImageView) cellView.findViewById(R.id.facebook);
        fbView.setVisibility(View.INVISIBLE);
        twitterView = (ImageView) cellView.findViewById(R.id.twitter);
        twitterView.setVisibility(View.INVISIBLE);
        instaView = (ImageView) cellView.findViewById(R.id.instagram);
        instaView.setVisibility(View.INVISIBLE);

        /* Store cell padding */
        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        /* Media post counter variables */
        int fbCounter = 0;
        int tweetCounter = 0;
        int instaCounter = 0;

        /* Declare TextView accessors */
        TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
        TextView fbText = (TextView) cellView.findViewById(R.id.fbCount);
        TextView tweetText = (TextView) cellView.findViewById(R.id.twitCount);
        TextView instaText = (TextView) cellView.findViewById(R.id.instaCount);

        fbText.setVisibility(View.INVISIBLE);
        tweetText.setVisibility(View.INVISIBLE);
        instaText.setVisibility(View.INVISIBLE);

        tv1.setTextColor(Color.BLACK);

        /* Get dateTime of this cell */
        DateTime dateTime = this.datetimeList.get(position);

        /* Lists of social media posts */
        ArrayList <MediaInfo> photos = (ArrayList) extraData.get("facebookPhotos");
        ArrayList <MediaInfo> statuses = (ArrayList) extraData.get("facebookPosts");
        ArrayList <MediaInfo> tweets = (ArrayList) extraData.get("twitterTweets");
        ArrayList <MediaInfo> instaPhotos = (ArrayList) extraData.get("instagramPhotos");
        String currentMonth = (String) extraData.get("currentMonth");

        int mon =0;
        if (currentMonth != null) {
            mon = Integer.parseInt(currentMonth);
            if (mon == this.month) {

                if (photos != null) {
                    String list[] = dateTime.toString().split(" ");
                    //Log.d (TAG, "Date in cell is: " + list[0]);

                    for (int i = 0; i < photos.size(); i++) {
                        String temp[] = photos.get(i).getDate().split("T");
                        //Log.d (TAG, "Photo date is: " + temp[0]);
                        if (temp[0].equalsIgnoreCase(list[0])) {
                            fbCounter++;

                            //Log.d(TAG, "YEAH!!!");
                            fbView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (statuses != null) {
                    String list[] = dateTime.toString().split(" ");
                    //Log.d (TAG, "Date in cell is: " + list[0]);

                    for (int i = 0; i < statuses.size(); i++) {
                        String temp[] = statuses.get(i).getDate().split("T");
                        //Log.d (TAG, "Photo date is: " + temp[0]);
                        if (temp[0].equalsIgnoreCase(list[0])) {
                            fbCounter++;

                            //Log.d(TAG, "YEAH!!!");
                            fbView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (tweets != null) {
                    String list[] = dateTime.toString().split(" ");

                    for (int i = 0; i < tweets.size(); i++) {
                        String temp[] = tweets.get(i).getDate().split("T");
                        //Log.d (TAG, "Photo date is: " + temp[0]);
                        if (temp[0].equalsIgnoreCase(list[0])) {
                            tweetCounter++;

                            //Log.d(TAG, "YEAH!!!");
                            twitterView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (instaPhotos != null) {
                    String list [] = dateTime.toString().split(" ");

                    for (int i=0; i<instaPhotos.size(); i++) {
                        String temp [] = instaPhotos.get(i).getDate().split("T");
                        //Log.d (TAG, "Photo date is: " + temp[0]);
                        if (temp[0].equalsIgnoreCase(list[0]))  {
                            instaCounter++;

                            //Log.d(TAG, "YEAH!!!");
                            instaView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        Resources resources = context.getResources();

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            tv1.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        boolean shouldResetDisabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            tv1.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }

            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
            }

        } else {
            shouldResetDisabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            if (CaldroidFragment.selectedBackgroundDrawable != -1) {
                cellView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
            } else {
                cellView.setBackgroundColor(resources
                        .getColor(com.caldroid.R.color.caldroid_sky_blue));
            }

            tv1.setTextColor(CaldroidFragment.selectedTextColor);

        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetDisabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
            } else {
                cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }

        // set date for the cell
        tv1.setText("" + dateTime.getDay());

        /* Set counters in cell */
        if (fbCounter > 0) {
            fbText.setText("x" + Integer.toString(fbCounter));
            fbText.setVisibility(View.VISIBLE);
        }
        if (tweetCounter > 0) {
            tweetText.setText("x" + Integer.toString(tweetCounter));
            tweetText.setVisibility(View.VISIBLE);
        }
        if (instaCounter > 0) {
            instaText.setText("x" + Integer.toString(instaCounter));
            instaText.setVisibility(View.VISIBLE);
        }

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);

        // Set custom color if required
        setCustomResources(dateTime, cellView, tv1);

        return cellView;
    }
}
