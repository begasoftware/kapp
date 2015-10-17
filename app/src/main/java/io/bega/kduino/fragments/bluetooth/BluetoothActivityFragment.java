package io.bega.kduino.fragments.bluetooth;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.activities.BluetoothActivity;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * A placeholder fragment containing a simple view.
 */
public class BluetoothActivityFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, IClickable {

    ListView listViewDevices;

    ArrayAdapter<String> listOfDevices;

    BluetoothActivity parentActivity;

    FloatingActionButton actionButtonConnect;

    FloatingActionButton actionButtonSearch;

    Bus bus;

    public BluetoothActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(kdUINOApplication.TAG, "create view bluetooth view");

        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);


        FloatingActionsMenu menu = (FloatingActionsMenu)view.findViewById(R.id.multiple_actions);
        menu.expand();
        /*actionButtonConnect = (FloatingActionButton)view.findViewById(R.id.action_button_activate_bluetooth);
        actionButtonConnect.setOnClickListener(this); */

        actionButtonSearch = (FloatingActionButton)view.findViewById(R.id.action_button_search_bluetooth_devices);
        actionButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SEARCH_DEVICES, null));
            }
        });
        this.listOfDevices = ((BluetoothActivity) this.getActivity()).getBluetoothService().getBTArrayAdapter();
        listViewDevices = (ListView)view.findViewById(android.R.id.list);
        listViewDevices.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewDevices.setAdapter(listOfDevices);
        listViewDevices.setOnItemClickListener(this);

        //actionButtonConnect.setEnabled(!((BluetoothActivity) this.getActivity()).getBluetoothService().isEnabledBluetooth());
        //actionButtonSearch.setEnabled(((BluetoothActivity)this.getActivity()).getBluetoothService().isEnabledBluetooth());
        return view;
    }

    /* @Subscribe
    public void getMessages(KdUinoMessageBusEvent event)
    {
        switch (event.getMessage()) {
            case KdUINOMessages.BLUETOOTH_ON:
                actionButtonConnect.setEnabled(false);
                actionButtonSearch.setEnabled(true);
                break;

            case KdUINOMessages.BLUETOOTH_OFF:
                actionButtonConnect.setEnabled(true);
                actionButtonSearch.setEnabled(false);
                break;
        }
    } */

    @Override
    public void onAttach(Activity activity) {
        activity.setTitle(getString(R.string.bluetooth_search_device_title));
        this.parentActivity = (BluetoothActivity)activity;
        this.listOfDevices = this.parentActivity.devices;
        this.bus = ((kdUINOApplication)getActivity().getApplication()).getBus();
        super.onAttach(activity);
        //bus.post(new KdUinoMessageBusEvent(KdUINOMessages.CONNECT_BLUETOOTH, null));
        //bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SEARCH_DEVICES, null));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {

        super.onStart();
        this.bus.register(this);
        //bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SEARCH_DEVICES, null));
        bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SEARCH_DEVICES, null));
       /* if (!parentActivity.getBluetoothService().isEnabled()) {
            parentActivity.getBluetoothService().enableBluetooth();
        }
        else {
            parentActivity.getBluetoothService().searchDevices();
        } */

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.bus = null;
        this.listOfDevices = null;
        this.parentActivity = null;
    }

    @Override
    public void onStop() {
        this.bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
       /* if (v.getId() == R.id.action_button_activate_bluetooth)
        {
            bus.post(new KdUinoMessageBusEvent(KdUINOMessages.CONNECT_BLUETOOTH, null));
        }
        else if (v.getId() == R.id.action_button_search_bluetooth_devices)
        {
            bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SEARCH_DEVICES, null));
        } */
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (getActivity() instanceof AdapterView.OnItemClickListener) {
            try {
                ((AdapterView.OnItemClickListener) getActivity()).onItemClick(parent, view, position, id);
            }

            catch (Exception ex) {
                DisplayUtilities.ShowLargeMessage(this.getString(R.string.bluetooth_cant_connect),
                        "",
                        view,
                        false,
                        null);
            }
        }

    }

    @Override
    public void clickView(View v) {

    }

}
