/***********************************************************************
 * MediaApapter.java: Class which acts as the bridge between the media information
 *                    and the display of that information
 *
 * Authors: Ryan Haque, Aftab Ahmad
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.example.aftab.facebookexamples;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/* Adapter for social media data */
public class MediaAdapter extends RecyclerView.Adapter<MediaViewHolder>{

    private List<MediaInfo> mediaList; // List of social media data
    private Context mContext; // Holds current state of application
    private View itemView;

    /* Default Class Constructor */
    public MediaAdapter(List<MediaInfo> routeList, Context context) {
        this.mediaList = routeList;
        this.mContext = context;
    }

    /*                      onBindViewHolder()                            */
    /* Fetches data from mediaList array and builds the content of a card */
    @Override
    public void onBindViewHolder(MediaViewHolder mediaViewHolder, int i) {

        MediaInfo info = mediaList.get(i);

        mediaViewHolder.setTitle(info.getTitle());
        mediaViewHolder.setContent(info.getContent());
        mediaViewHolder.setPhoto(info.getPhoto(), mContext);

        if (info.getTitle().equals("Twitter")) {
            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView content = (TextView) itemView.findViewById(R.id.content);

            title.setBackgroundColor(mContext.getResources().getColor(R.color.twitterColor));
            content.setTextColor(mContext.getResources().getColor(R.color.twitterColor));
        }
        else if (info.getTitle().equals("Instagram")) {
            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView content = (TextView) itemView.findViewById(R.id.content);

            title.setBackgroundColor(mContext.getResources().getColor(R.color.instaColor));
            content.setTextColor(mContext.getResources().getColor(R.color.instaColor));
        }
    }

    /*         onCreateViewHolder()       */
    /* Handles what a card will look like */
    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
         itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new MediaViewHolder(itemView);
    }

    /*          getItemCount()       */
    /* Fetches the size of mediaList */
    @Override
    public int getItemCount() {
        return mediaList.size();
    }

}
