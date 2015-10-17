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
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 * READTIME:
 * Letter “R”
 * Action: The KdUINO is going to send the date to the Android device with the format: YYYY/MM/DD_hh:mm:ss
 * App action: Print the info.
 * Final character (ACK): “+”
 *
 */
public class ReadTimeAction extends BluetoothAction {



    public ReadTimeAction(Activity activity,
                          BluetoothService service,
                          StorageService fileutilities,
                          IFinishBluetoothActionCallBack callBack)
    {
        super(activity, service, fileutilities, callBack, true, false);
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("R");
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (datareceived!=null && datareceived.indexOf("+")!=-1){
            DisplayUtilities.ShowLargeMessage("KdUINO Time: " + datareceived.replace("+", ""),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);

        }else{
            DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_cant_read_data) ,
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
        }
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
