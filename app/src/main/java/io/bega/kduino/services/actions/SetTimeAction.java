package io.bega.kduino.services.actions;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DateUtility;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 * PROGRAMTIME:
 * Letter “T”
 * App action: After the Letter T, the app has to send YYYMMDDhhmmss with the time of the Android device. After that, notify the action.
 * Action: Configure the RTC.
 * Final character (ACK): “+”
 *
 */
public class SetTimeAction extends BluetoothAction {

    private String date;

    public SetTimeAction(
            Activity activity,
            BluetoothService service,
            StorageService fileutilities,
            IFinishBluetoothActionCallBack callBack,
            String date) {

        super(activity, service, fileutilities, callBack, false, false);

        this.date = date;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (datareceived!=null && datareceived.indexOf("+")!=-1){
            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_time_saved),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
        }else
        {
            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_cant_time_saved),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
        }

        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        try
        {
            String date = DateUtility.getDate() + DateUtility.getTime();
            this.bluetoothService.sendMessage("T"+date);
           // this.bluetoothService.sendMessage(date);
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
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
