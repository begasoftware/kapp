package io.bega.kduino.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.fragments.UserActivityFragment;

public class UserActivity extends BaseActivity implements IClickable {

    UserActivityFragment userActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle(getString(R.string.activity_title_user));
        setOnlyToolbar();

        userActivityFragment = new UserActivityFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_container_user, userActivityFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onPrepareOptionsMenu (Menu menu) {

        /*Boolean enabled = this.bluetoothService.isEnabledBluetooth();
        menu.getItem(1).setVisible(!enabled);
        menu.getItem(2).setVisible(enabled && !this.bluetoothService.isConnected());
        menu.getItem(3).setVisible(this.bluetoothService.isConnected()); */


        /*if (this.bluetoothService.isConnected()) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_connect));
        }
        else
        {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_disconnect_white_off));
        } */


        super.onPrepareOptionsMenu(menu);
        return true;
    }


    @Override
    public void clickView(View v) {
        if (userActivityFragment != null)
        {
            userActivityFragment.clickView(v);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
        finish();
    }
}
