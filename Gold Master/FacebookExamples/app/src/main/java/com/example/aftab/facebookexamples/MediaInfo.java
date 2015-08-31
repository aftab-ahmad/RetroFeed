/***********************************************************************
 * Mediainfo.java: An generic object which stores information about
 *                 social media.
 *                 It stores the Title, date, content, photo. Class also
 *                 acts as a parcel so that this object can be passed
 *                 between classes.
 *
 * Authors: Ryan Haque, Aftab Ahmad
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.example.aftab.facebookexamples;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaInfo implements Parcelable{

    private String title; // Title of feed card (Facebook, Twitter, etc.)
    private String content; // Content of card (Post, status, tweet, etc.)
    private String photo; // Photo of card (Facebook Photo or Instagram Photo)
    private String date; // Date of post for card

    /* Default Constructor */
    public MediaInfo(String title, String content, String photo, String date) {
        this.title = title;
        this.content = content;
        this.photo = photo;
        this.date = date;
    }

    public MediaInfo() {
        // TODO Auto-generated constructor stub
    }

    /*        getTitle()           */
    /* Fetches the title of a card */
    public String getTitle () {
        return title;
    }

    /*           getContent()             */
    /* Fetches the content text of a card */
    public String getContent () {
        return content;
    }

    /*        getPhoto()           */
    /* Fetches the photo of a card */
    public String getPhoto () {
        return photo;
    }

    /*            getDate()              */
    /* Fetches the date of a card's post */
    public String getDate () {
        return date;
    }

    /*        setTitle()         */
    /* Sets the title for a card */
    public void setTitle (String title) {
        this.title = title;
    }

    /*          setContent()            */
    /* Sets the content text for a card */
    public void setContent (String content) {
        this.content = content;
    }

    /*        setPhoto()         */
    /* Sets the photo for a card */
    public void setPhoto (String photo) {
        this.photo = photo;
    }

    /*        setDate()         */
    /* Sets the date for a card */
    public void setDate (String date) {
         this.date = date;
    }

    /*                        toString()                            */
    /* Builds and returns a string containing all aspects of a card */
    @Override
    public String toString ()
    {
        String message = "Title is: " + title + " Content is: " + content + " Photo is: " + photo + " Date is: " + date;
        return message;
    }

    /*           MediaInfo(Parcel in)        */
    /* Reads in all card aspects as a string */
    private MediaInfo (Parcel in) {
        title = in.readString();
        content = in.readString();
        photo = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /*                 writeToParcel()                 */
    /* Writes out card aspects to a parcel as a string */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(photo);
        dest.writeString(date);
    }

    /*                Parcel and Object Creator                     */
    /* Creates a new parcel from MediaInfo object and vice-versa    */
    public static final Parcelable.Creator<MediaInfo> CREATOR = new Parcelable.Creator<MediaInfo>() {

        /* Creates a new MediaInfo from a parcel */
        public MediaInfo createFromParcel(Parcel in) {
            return new MediaInfo(in);
        }

        /* Creates a new MediaInfo list */
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };
}
