package io.bega.kduino.services.actions;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Display;

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
 * DELETEDATA:
 * Letter “Z”
 * Action: Delete all data of the SD memory.
 * Final character(ACK): “+”
 * App action: Just notify the action.
 *
 */
public class DeleteDataAction extends BluetoothAction {
    public DeleteDataAction(Activity activity,
                            BluetoothService service,
                            StorageService fileutilities,
                            IFinishBluetoothActionCallBack callBack
                           ) {
        super(activity, service, fileutilities, callBack, false, false);
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("Z");
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if ((datareceived!=null && datareceived.indexOf("+")!=-1))
        {

            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_delete),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
        }else{
            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_cant_delete),
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
