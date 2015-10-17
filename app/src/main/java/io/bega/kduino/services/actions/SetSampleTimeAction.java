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
 * SAMPLETIME
 * Letter “M”
 * App action: After the Letter T, the app has to send mm with the seconds of measurement. After that, notify the action.
 * Action: Configure the measurement time.
 * Final character (ACK): “+”
 *
 */
public class SetSampleTimeAction extends BluetoothAction
{

    private String sampleTime;

    Bus bus;

    public SetSampleTimeAction(Activity activity,
                            BluetoothService service,
                            StorageService fileutilities,
                            IFinishBluetoothActionCallBack callBack,
                            String interval,
                               Bus bus)
    {
        super(activity, service, fileutilities, callBack, false, false);
        this.sampleTime = interval;
        this.bus = bus;
        int measure = Integer.parseInt(sampleTime);
        if (measure < 10)
        {
            sampleTime = "0" + sampleTime;
        }
    }

    @Override
    protected void onPreExecute() {
        try
        {
            //this.bluetoothService.sendMessage("N"+sampleTime);
            Log.i(kdUINOApplication.TAG, "Send N" + sampleTime);
            this.bluetoothService.sendMessage("N" + sampleTime);
            //this.bluetoothService.sendMessage(sampleTime);
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if (datareceived == null)
        {
            super.onPostExecute(aVoid);
            return;
        }

        if (datareceived.indexOf("-") > -1)
        {
            super.onPostExecute(aVoid);
            Log.i(kdUINOApplication.TAG, "Error change smaple time data");
            return;
        }

        if (datareceived!=null && datareceived.indexOf("+")!=-1){
            // TODO Change correct message
            Log.i(kdUINOApplication.TAG, "N Executed");
            io.bega.kduino.datamodel.Status statusData =  ((kdUINOApplication)(this.activity).getApplication()).getStatus();

            int sampleTime = Integer.parseInt(this.sampleTime);
            statusData.SampleTime = sampleTime;

            bus.post(statusData);

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
