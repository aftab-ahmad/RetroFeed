/***********************************************************************
 * CalendarFragment.java: The class which creates the calendar
 *
 * Authors: Kyle Wilson, Kyle Genoe
 *
 * Last Updated: March 1st, 2015
 **********************************************************************/

package com.android.aftab.retrofeed;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

public class CalendarFragment extends CaldroidFragment {

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }
}
