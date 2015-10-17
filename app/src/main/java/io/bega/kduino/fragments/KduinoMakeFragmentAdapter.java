package io.bega.kduino.fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.fragments.bluetooth.PositionCommandsFragment;
import io.bega.kduino.fragments.make.DefineBuoyFragment;

public class KduinoMakeFragmentAdapter extends FragmentPagerAdapter implements IClickable {

    final int PAGE_COUNT = 2;

    DefineBuoyFragment defineBuoyFragment = null;

    PositionCommandsFragment positionFragment = null;

    public String BuoyID = "";


    private String tabTitles[] = null;

    private int[] imageResId = {
            R.drawable.ic_one,
            R.drawable.ic_dashboard_black_24dp
    };


    private Context context;

    public KduinoMakeFragmentAdapter(FragmentManager fm, Context context, String buoyID
                                    ) {
        super(fm);
        this.defineBuoyFragment = null;
        this.positionFragment = null;
        this.context = context;
        this.tabTitles = new String[] {
                context.getString(R.string.tab_make_kduino),
                context.getString(R.string.tab_position)
              };
        this.BuoyID = buoyID;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
            {
               // if (defineBuoyFragment == null)
               // {
                    defineBuoyFragment =  DefineBuoyFragment.newInstance(BuoyID);
               // }

                return defineBuoyFragment;
            }
            case 1:
            {
               // if (positionFragment == null)
               // {
                    positionFragment = PositionCommandsFragment.newInstance(BuoyID);
               // }

                return positionFragment;
            }

            default:
            {
                return null;
            }
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public void clickView(View v)
    {
        if (defineBuoyFragment != null)
        {
            defineBuoyFragment.clickView(v);
        }

        if (positionFragment != null)
        {
            positionFragment.clickView(v);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        defineBuoyFragment = null;
        positionFragment = null;
    }

}
