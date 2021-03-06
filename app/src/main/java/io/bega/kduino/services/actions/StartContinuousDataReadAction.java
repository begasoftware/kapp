package io.bega.kduino.services.actions;

import android.app.Activity;
import android.util.Log;

import com.squareup.otto.Bus;

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
 * START
 * Letter “S”
 * Action: Start the measurements.
 * Final character(ACK): “+”
 * App action: Just notify the action.
 *
 */
public class StartContinuousDataReadAction extends BluetoothAction {
    Bus bus;
    public StartContinuousDataReadAction(Activity activity,
                                         BluetoothService service,
                                         StorageService fileutilities,
                                         IFinishBluetoothActionCallBack callBack,
                                         Bus bus)
    {
        super(activity, service, fileutilities, callBack, false, false);
        this.bus = bus;
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("S");
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
            io.bega.kduino.datamodel.Status statusData =  ((kdUINOApplication)(this.activity).getApplication()).getStatus();

            statusData.StatusKduino = 1;

            bus.post(statusData);


            DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_start_kduino),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
        } else {
            DisplayUtilities.ShowLargeMessage(
                    this.activity.getString(R.string.bluetooth_data_cant_start_kduino),
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
