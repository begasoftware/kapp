package io.bega.kduino.fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import io.bega.kduino.R;
import io.bega.kduino.fragments.bluetooth.DataCommandsFragment;
import io.bega.kduino.fragments.bluetooth.PositionCommandsFragment;
import io.bega.kduino.fragments.bluetooth.SensorTabFragment;
import io.bega.kduino.fragments.bluetooth.TimeCommandsFragment;

public class CommandsFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;

    SensorTabFragment sensorTabFragment;

    TimeCommandsFragment timeCommandsFragment;

    DataCommandsFragment dataCommandsFragment;

    private String tabTitles[] = null;

    private String buoyID;

    private int[] imageResId = {
            R.drawable.ic_one,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_settings_applications_black_24dp,
            R.drawable.ic_settings_applications_white_24dp
    };


    private Context context;

    public CommandsFragmentAdapter(FragmentManager fm, Context context, String buoyID) {
        super(fm);
        this.context = context;
        this.tabTitles = new String[] {
                context.getString(R.string.tab_sensors),
                context.getString(R.string.tab_data) ,
                context.getString(R.string.tab_control) };
        this.buoyID = buoyID;
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
                if (sensorTabFragment == null)
                {
                    sensorTabFragment = SensorTabFragment.newInstance(buoyID);
                }

                sensorTabFragment.refreshList();
                return sensorTabFragment;
            }
            case 1:
            {
                if (dataCommandsFragment == null)
                {
                    dataCommandsFragment = DataCommandsFragment.newInstance(buoyID,"");
                }

                return dataCommandsFragment;
            }
            case 2:
            {
                if (timeCommandsFragment == null)
                {
                    timeCommandsFragment = TimeCommandsFragment.newInstance(buoyID, "");
                }

                return timeCommandsFragment;
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
}
