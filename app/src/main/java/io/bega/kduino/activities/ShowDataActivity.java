package io.bega.kduino.activities;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.bega.kduino.R;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.fragments.KdUINOMapFragment;
import io.bega.kduino.kdUINOApplication;

public class ShowDataActivity extends BaseActivity {

    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        this.setActionNavigation();
        this.setTitle(getString(R.string.fragment_title_show_transparency_data));
        KdUINOMapFragment fragment = KdUINOMapFragment.newInstance("","");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_container_show_data, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();

        this.bus = ((kdUINOApplication)  this.getApplication()).getBus();

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

    @Override
    protected void onDestroy() {
        this.bus = null;
        super.onDestroy();
    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent event)
    {
        this.processNavigationMessage(event.getMessage());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_show_data, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
        } */

        return super.onOptionsItemSelected(item);
    }
}
