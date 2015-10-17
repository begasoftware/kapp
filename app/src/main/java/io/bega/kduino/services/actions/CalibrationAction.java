package io.bega.kduino.services.actions;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.SensorTestBuoyAdapter;
import io.bega.kduino.datamodel.SensorTestResult;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ProcessDataService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 * CALIBRATION
 * Letter “C”
 * Action: Send the calibration of the sensors.
 * Example:
 * 1 32424 2 1324134 3 3432432 4 34234 5 3124232
 * Final character(ACK): “+”
 * App action: Read all the data and send the info to the DB when it will be possible.
 */
public class CalibrationAction extends BluetoothAction
{
    MaterialDialog dialog;

    public CalibrationAction(Activity activity, BluetoothService service,
                             StorageService fileutilities,
                             IFinishBluetoothActionCallBack callBack) {
        super(activity, service, fileutilities, callBack, true, false);
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("C");
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

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        if (datareceived!=null && datareceived.indexOf("+")!=-1){

            boolean wrapInScrollView = false;
            dialog = new MaterialDialog.Builder(this.activity)
                    .title(R.string.dialog_sensor_adjustment_title)
                    .customView(R.layout.test_sensorsresult, wrapInScrollView)
                    .positiveText(R.string.yes)
                    .build();

            View view = dialog.getCustomView();

            ProcessDataService processDataService = new ProcessDataService();
            SensorTestResult result =  processDataService.proceedTestSensorsCalibration(datareceived);

            ListView listView = (ListView)view.findViewById(R.id.test_sensors_listview);
            SensorTestBuoyAdapter sensorTestBuoyAdapter = new SensorTestBuoyAdapter(this.activity,
                    R.layout.sensors_test_definition_row, result.getSensors());
            listView.setAdapter(sensorTestBuoyAdapter);
            dialog.show();

        }
        else
        {
            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_sensor_cant_tested),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
        }

        /* if (datareceived!=null && datareceived.indexOf("+")!=-1){
            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_has_been_calibrated),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);

            /* if (fileutilities != null)
            {
                //Lo grabamos en dos txt, uno para trabajar con el y el que nos piden de backup
                fileutilities.insertardatatofile(datareceived, "cal_boia");
                fileutilities.insertardatatobackup(datareceived, "dades_backup");
            }
         }
        else
        {
            DisplayUtilities.ShowLargeMessage(activity.getString(R.string.bluetooth_data_cant_calibrated),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);

        } */


    }
}
