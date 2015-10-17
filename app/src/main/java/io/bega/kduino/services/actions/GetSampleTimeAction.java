package io.bega.kduino.services.actions;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.StorageService;

/**
 * Created by usuario on 24/07/15.
 * SAMPLETIME
 * Letter “N”
 * App action: After the Letter T, the app has to send mm with the seconds of measurement. After that, notify the action.
 * Action: Configure the measurement time.
 * Final character (ACK): “+”
 *
 */
public class GetSampleTimeAction extends BluetoothAction
{

    public GetSampleTimeAction(Activity activity,
                               BluetoothService service,
                               StorageService fileutilities,
                               IFinishBluetoothActionCallBack callBack
                               )
    {
        super(activity, service, fileutilities, callBack, true, false);
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("N");
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
