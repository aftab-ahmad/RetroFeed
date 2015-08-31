/***********************************************************************
 * RecyclerItemClickListener.java: Acts as an event listener for feed
 *                                 cards.
 *
 * Authors: Aftab Ahmad, Ryan Haque
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.example.aftab.facebookexamples;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener; // Click-event listener
    private GestureDetector mGestureDetector; // Gesture listener

    /* Click listener */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    /*        RecyclerItemClickListener()         */
    /* Event listener for card feed */
    /* Handles what type of touch gesture occured */
    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    /*            onInterceptTouchEvent()                */
    /* Handles the location on screen of a touch gesture */
    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}