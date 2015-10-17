package io.bega.kduino.services;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.otto.Bus;

import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;

/**
 * Created by usuario on 14/08/15.
 */
public class ConnectivityUpdateReceiver  extends BroadcastReceiver {

    Bus bus;



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        bus =  ((kdUINOApplication)context.getApplicationContext()).getBus();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int pos = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            /*if (pos == BluetoothAdapter.STATE_OFF)
            {
                bus.post(
                        new KdUinoMessageBusEvent(
                        KdUINOMessages.BLUETOOTH_OFF, null));
                // Bluetooth is disconnected, do handling here
            }
            else if (pos == BluetoothAdapter.STATE_ON)
            {
                bus.post(
                        new KdUinoMessageBusEvent(
                        KdUINOMessages.BLUETOOTH_ON, null));
            } */

            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        NetworkInfo activeWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean isConnectedTelephone = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();

        boolean isConnectedWifi = activeWifiInfo != null && activeWifiInfo.isConnectedOrConnecting();

        if (isConnectedTelephone || isConnectedWifi)
        {
            bus.post(
                    new KdUinoMessageBusEvent(
                    KdUINOMessages.INTERNET_ON, null));
        }

        else{

            bus.post(new KdUinoMessageBusEvent(
                    KdUINOMessages.INTERNET_OFF, null));
        }

    }
}
