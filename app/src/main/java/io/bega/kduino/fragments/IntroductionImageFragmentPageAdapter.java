package io.bega.kduino.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.R;
import io.bega.kduino.fragments.intro.PlaceSlideFragment;


public class IntroductionImageFragmentPageAdapter extends FragmentPagerAdapter {

    // List of fragments which are going to set in the view pager widget
    private int[] Images = new int[] { R.drawable.intro1, R.drawable.intro2,
            R.drawable.intro3, R.drawable.intro4
    };



    /**
     * Constructor
     *
     * @param fm
     *            interface for interacting with Fragment objects inside of an
     *            Activity
     */
    public IntroductionImageFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int arg0) {
        return PlaceSlideFragment.newInstance(Images[arg0]);
    }

    @Override
    public int getCount() {
        return Images.length;
    }

}



