package io.bega.kduino.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.bega.kduino.R;
import io.bega.kduino.fragments.intro.PlaceSlideFragment;
import io.bega.kduino.fragments.intro.PlaceTextSlideFragment;


public class IntroductionTextFragmentPageAdapter extends FragmentPagerAdapter {


    private int count;

    /**
     * Constructor
     *
     * @param fm
     *            interface for interacting with Fragment objects inside of an
     *            Activity
     */
    public IntroductionTextFragmentPageAdapter(FragmentManager fm, int max) {
        super(fm);
        count = max;
    }


    @Override
    public Fragment getItem(int arg0) {
        return PlaceTextSlideFragment.newInstance(arg0);
    }

    @Override
    public int getCount() {
        return count;
    }

}



