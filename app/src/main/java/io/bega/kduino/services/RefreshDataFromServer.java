package io.bega.kduino.services;

import android.app.Activity;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;

import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;

/**
 * Created by usuario on 08/09/15.
 */
public class RefreshDataFromServer extends AsyncTask<String, Integer, Void> {

    BluetoothService bluetoothService;

    MaterialDialog connectingDialog;

    Activity activity;

    Bus bus;

    KDUINOBuoy buoy;

    public RefreshDataFromServer(Bus bus, Activity activity, BluetoothService service, KDUINOBuoy buoy)
    {
        this.buoy = buoy;
        this.bus = bus;
        this.activity = activity;
        this.bluetoothService = service;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (connectingDialog == null)
        {
            connectingDialog = new MaterialDialog.Builder(this.activity)
                    .title(R.string.dialog_connecting_title)
                    .content(R.string.dialog_connecting_message)
                    .positiveColorRes(R.color.colorPrimaryDark)
                    .neutralColorRes(R.color.colorPrimary)
                    .negativeColorRes(R.color.colorPrimary)
                  //  .progress(true, 0)
                    .build();
            connectingDialog.show();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        try {
            bluetoothService.connect(params[0]);
        }
        catch(Exception ex)
        {
            String message = ex.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (connectingDialog != null && connectingDialog.isShowing()) {
            connectingDialog.dismiss();
        }

        if (bluetoothService.isConnected()) {

            bus.post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_CONNECT_TO_KDUINO, buoy));
        }
        else
        {
            bus.post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_ERROR, buoy));
        }
    }



}
