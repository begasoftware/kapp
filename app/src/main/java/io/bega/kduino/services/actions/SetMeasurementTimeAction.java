package io.bega.kduino.services.actions;

import android.app.Activity;
import android.util.Log;

import com.squareup.otto.Bus;

import java.io.IOException;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
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
public class SetMeasurementTimeAction extends BluetoothAction {

    private String measurementTime;

    Bus bus;

    public SetMeasurementTimeAction(Activity activity,
                                    BluetoothService service,
                                    StorageService fileutilities,
                                    IFinishBluetoothActionCallBack callback,
                                    String extData,
                                    Bus bus) {
        super(activity, service, fileutilities, callback, false, false);
        measurementTime = extData;
        this.bus = bus;

        int measure = Integer.parseInt(measurementTime);
        if (measure < 10)
        {
            measurementTime = "0" + measurementTime;
        }
    }

    @Override
    protected void onPreExecute() {
        try
        {
            Log.i(kdUINOApplication.TAG, "Send M" + measurementTime);
            //this.bluetoothService.sendMessage("M"+ measurementTime);
            this.bluetoothService.sendMessage("M" + measurementTime);
            //this.bluetoothService.sendMessage(measurementTime);
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
            Log.i(kdUINOApplication.TAG, "Error change measurement time data");
            super.onPostExecute(aVoid);
            return;
        }


        if (datareceived!=null && datareceived.indexOf("+")!=-1){

            Log.i(kdUINOApplication.TAG, "Executed measurement");
            io.bega.kduino.datamodel.Status statusData =  ((kdUINOApplication)(this.activity).getApplication()).getStatus();

            int measurementTime = Integer.parseInt(this.measurementTime);
            statusData.MeasurementTime = measurementTime;

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
