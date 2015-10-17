package io.bega.kduino.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Subscribe;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.fragments.make.KdUINOFragment;
import io.bega.kduino.fragments.make.MakeKduinoFragment;
import io.bega.kduino.kdUINOApplication;

public class MakeKdUINOActivity extends BaseActivity  implements IClickable{
    Fragment currentFragment;

    private final String stackName = "makekaduino";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_make_kd_uino);
        setTitle(getString(R.string.activity_title_make ));
        setActionNavigation();
        KdUINOFragment fragment = KdUINOFragment.newInstance("");
        currentFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_make_kd_uino, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
       // ft.addToBackStack("start");
        ft.commit();
        bus = ((kdUINOApplication)getApplication()).getBus();

    }

    @Override
    protected void onDestroy() {
        this.bus = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        this.bus.register(this);
        super.onStart();

    }

    @Override
    protected void onStop() {
        this.bus.unregister(this);
        super.onStop();
    }



    @Subscribe
    public void getMessage(KdUinoMessageBusEvent message) {

        if (message.getMessage() == KdUINOMessages.EDIT_MAKE_KDUINO)
        {
            KDUINOBuoy buoy = (KDUINOBuoy)message.getData();
            MakeKduinoFragment fragment = MakeKduinoFragment.newInstance(buoy.getId().toString());
            currentFragment = fragment;


            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
            ft.replace(R.id.fragment_make_kd_uino, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.addToBackStack(stackName);
            ft.commit();
        }
        else if (message.getMessage() == KdUINOMessages.ADD_MAKE_KDUINO)
        {
            MakeKduinoFragment fragment = MakeKduinoFragment.newInstance("0");
            currentFragment = fragment;

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
            ft.replace(R.id.fragment_make_kd_uino, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.addToBackStack(stackName);
            ft.commit();
        }
        else
        {
            this.processNavigationMessage(message.getMessage());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_make_kd_uino, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickView(View v) {
        if (currentFragment instanceof  IClickable)
        {
            ((IClickable)currentFragment).clickView(v);
        }
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack(stackName, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        super.onBackPressed();
    }
}
