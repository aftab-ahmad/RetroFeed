package com.android.aftab.retrofeed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/* *Adapter for loading fragments on main activity */
public class PagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Facebook", "Twitter", "Instagram"};

    private Fragment mCurrentFragment;

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }


    @Override
    public Fragment getItem(int position) {

        Bundle data = new Bundle();

        switch (position)
        {
            case 0:
                FacebookFragment facebookFragment = new FacebookFragment();
                data.putInt("current_page", position+1);
                facebookFragment.setArguments(data);
                return facebookFragment;

            case 1:
                TwitterFragment twitterFragment = new TwitterFragment();
                data.putInt("current_page", position+1);
                twitterFragment.setArguments(data);
                return twitterFragment;

            case 2:
                InstagramFragment instagramFragment = new InstagramFragment();
                data.putInt("current_page", position+1);
                instagramFragment.setArguments(data);

                return instagramFragment;
        }

        return null;
    }
}