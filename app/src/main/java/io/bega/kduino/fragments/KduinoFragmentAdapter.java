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
import io.bega.kduino.fragments.bluetooth.BluetoothManagerFragment;
import io.bega.kduino.fragments.bluetooth.DataCommandsFragment;
import io.bega.kduino.fragments.bluetooth.PositionCommandsFragment;
import io.bega.kduino.fragments.bluetooth.SensorTabFragment;
import io.bega.kduino.fragments.bluetooth.TimeCommandsFragment;

public class KduinoFragmentAdapter extends FragmentPagerAdapter implements IClickable {
    final int PAGE_COUNT = 2;

    BluetoothManagerFragment bluetoothManagerTabFragment = null;

    PositionCommandsFragment positionFragment = null;


    private String tabTitles[] = null;

    private String buoyID;

    private String macAdress;

    private int[] imageResId = {
            R.drawable.ic_one,
            R.drawable.ic_dashboard_black_24dp
    };


    private Context context;

    public KduinoFragmentAdapter(FragmentManager fm, Context context,
                                 String buoyID,
                                 String macAdress) {
        super(fm);
        this.bluetoothManagerTabFragment = null;
        this.positionFragment = null;
        this.context = context;
        this.tabTitles = new String[] {
                context.getString(R.string.tab_kduino),
                context.getString(R.string.tab_position)
              };
        this.buoyID = buoyID;
        this.macAdress = macAdress;
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
                if (bluetoothManagerTabFragment == null)
                {
                    bluetoothManagerTabFragment =  BluetoothManagerFragment.newInstance(buoyID, macAdress);
                }

                return bluetoothManagerTabFragment;
            }
            case 1:
            {
                if (positionFragment == null)
                {
                    positionFragment = PositionCommandsFragment.newInstance(buoyID);
                }

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
        return sb.toString();
    }

    @Override
    public void clickView(View v)
    {
        if (bluetoothManagerTabFragment != null)
        {
            bluetoothManagerTabFragment.clickView(v);
        }

        if (positionFragment != null)
        {
            positionFragment.clickView(v);
        }
    }

  /*  @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        bluetoothManagerTabFragment = null;
        positionFragment = null;
    } */

}
