/***********************************************************************
 * MediaViewHolder.java: Handles all visual portions of a feed card
 *
 * Authors: Aftab Ahmad, Ryan Haque
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/* Class which holds the media info (contained in the card) */
public class MediaViewHolder extends RecyclerView.ViewHolder  {

    private TextView vTitle; // Title of card as a TextView
    private TextView vContent; // Content text of a card as a TextView
    private ImageView vPhoto; // Photo of a card as a TextView

    /* Default class constructor */
    public MediaViewHolder(View v) {
        super(v);

        vTitle =  (TextView) v.findViewById(R.id.title);
        vContent = (TextView)  v.findViewById(R.id.content);
        vPhoto = (ImageView) v.findViewById(R.id.photo);
    }

    /*                getTitle()                */
    /* Fetches the title for a card as a string */
    public String getTitle (){
        return (String) vTitle.getText();
    }

    /*                 getContent()                    */
    /* Fetches the content text for a card as a string */
    public String getContent (){
        return (String) vContent.getText();
    }

    /*          getPhoto()        */
    /* Fetches a photo for a card */
    public Drawable getPhoto (){
        return (Drawable) vPhoto.getDrawable();
    }

    /*             setTitle()                */
    /* Sets the title for a card as a string */
    public void setTitle (String title) {
        vTitle.setText(title);
    }

    /*               setContent()                   */
    /* Sets the content text for a card as a string */
    public void setContent (String content) {
        vContent.setText(content);
    }

    /*               setPhoto()                */
    /* Sets photo for a card using Picasso API */
    public void setPhoto (String photo, Context context) {
        Picasso.with(context).load(photo).into(vPhoto);
    }
}
