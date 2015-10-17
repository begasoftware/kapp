package io.bega.kduino.services.actions;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 * MEASUREMENTTIME
 * Letter “M”
 * App action: After the Letter T, the app has to send ss with the seconds of measurement. After that, notify the action.
 * Action: Configure the measurement time.
 * Final character (ACK): “+”
 *
 */
public class MeasurementTimeAction extends BluetoothAction {
    public MeasurementTimeAction(Activity activity,
                                 BluetoothService service,
                                 StorageService fileutilities,
                                 IFinishBluetoothActionCallBack callback) {
        super(activity, service, fileutilities, callback, false, false);
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("M");
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if (datareceived!=null && datareceived.indexOf("+")!=-1){

            DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_time_has_been_read),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
         }
        else{

            DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_time_cant_read),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);

        }

        super.onPostExecute(aVoid);
    }

    @Override
    public boolean equals(Object o) {

        if (this.getClass().equals(o.getClass()))
        {
            return true;
        }

        return super.equals(o);
    }




}
