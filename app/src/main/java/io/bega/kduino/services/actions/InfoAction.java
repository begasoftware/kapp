package io.bega.kduino.services.actions;

import android.app.Activity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.ArrayList;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.RawMeasurement;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ComputeKdService;
import io.bega.kduino.services.ProcessDataService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 *
 * - INFO
 * Letter “I”
 * Action: Send the configuration of the  KdUINO with an specific format.
 * Format of info:
 * [START/STOP] [MEASUREMENTTIME] [SAMPLETIME] [ERROR]
 * Where:
 * START = 1
 * STOP = 0
 * MEASUREMENTTIME: number in seconds.
 * SAMPLETIME: number in minutes.
 * ERROR: 0, 1, 2, 3 or 4.
 * 0: No error.
 * 1: Cannot open SD card.
 * 2: Cannot open the data file.
 * 3: Cannot open the configuration file.
 * 4: Cannot know the real number of measurements.
 * Example: 1 10 3 0
 * The KdUINO is measuring during 10 seconds every 3 minutes and there is no errors.
 * Final character (ACK): “+”
 *
 */


public class InfoAction extends BluetoothAction {

    MaterialDialog dialog;

    Bus bus;


    public InfoAction(Activity activity,
                      BluetoothService service,
                      StorageService fileutilities,
                      IFinishBluetoothActionCallBack callBack,
                      Bus bus
                      )
    {

        super(activity, service, fileutilities, callBack, false, false);
        this.bus = bus;
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("I");
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (datareceived!=null && datareceived.indexOf("+")!=-1) {


            datareceived =  datareceived.replace("+", "");
            String[] dataMessage = datareceived.split(" ");
            boolean hasError = false;

            int status = 0;
            try {
                status = Integer.parseInt(dataMessage[0]);
            }
            catch (Exception ex)
            {
                hasError = true;
            }


            int sampleTime = 0;
            try {
                sampleTime = Integer.parseInt(dataMessage[1]);
            }
            catch (Exception ex)
            {
                hasError = true;
            }

            int measurementTime = 0;
            try {
                measurementTime = Integer.parseInt(dataMessage[2]);
            }
            catch (Exception ex)
            {
                hasError = true;
            }

            int error = 0;
            try {
                error = Integer.parseInt(dataMessage[3]);
            }
            catch (Exception ex)
            {
                hasError = true;
            }

            String name = "";
            try
            {
                name = dataMessage[4];
            }
            catch (Exception ex)
            {
                hasError = true;
            }

            String maker = "";
            try
            {
                maker = dataMessage[5];
            }
            catch (Exception ex)
            {
                hasError = true;
            }

            int numSensors = 0;
            try
            {
                numSensors = Integer.parseInt(dataMessage[6]);
            }
            catch (Exception ex)
            {
                hasError = true;
            }

            ArrayList<Integer> sensorDeeps = new ArrayList<Integer>();
            int pointer = 7;
            int top = pointer + numSensors;
            if (numSensors != 0)
            {
                for (int i= pointer; i < top; i++)
                {
                    int deep = 0;
                    try
                    {
                        deep = Integer.parseInt(dataMessage[i]);
                    }
                    catch (Exception ex)
                    {
                        hasError = true;
                    }

                    sensorDeeps.add(deep);
                }
            }


            if (!hasError)
            {
                io.bega.kduino.datamodel.Status statusData =  ((kdUINOApplication)(this.activity).getApplication()).getStatus();
                statusData.MeasurementTime = measurementTime;
                statusData.SampleTime = sampleTime;
                statusData.StatusKduino = status;
                statusData.SDError = error;
                statusData.Maker = maker.replace("_"," ");
                statusData.Name = name.replace("_", " ");
                statusData.SensorNumber = numSensors;
                statusData.SensorList = sensorDeeps;
                bus.post(new KdUinoMessageBusEvent(KdUINOMessages.RECIEVE_INFO, statusData));
            }

         /*   DisplayUtilities.ShowLargeMessage("Values calculated: ",
                    "kd: " + Double.toString(analysis.Kd) + " R2" + Double.toString(analysis.R2),
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null); */

        }


        super.onPostExecute(aVoid);

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
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
