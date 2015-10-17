package io.bega.kduino.services.actions;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.Measurement;
import io.bega.kduino.datamodel.RawMeasurement;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.SensorTestBuoyAdapter;
import io.bega.kduino.datamodel.SensorTestResult;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.events.KdUinoAnalysisMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoDataFileMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ComputeKdService;
import io.bega.kduino.services.ProcessDataService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DateUtility;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 * TEST
 * Letter “Q”
 * Action: The KdUINO send one punctual measurement.
 * Example:
 * 2015/07/05_10:45:12 1 32424 2 1324134 3 3432432 4 34234 5 3124232
 * Final character(ACK): “+”
 * App action: Read all the data, analyse it to obtain kd and r2 and send t
 * he info to the DB when it will be possible. Print the info in the screen.
 * Correct the info if it is necessary.
 */
public class TestDataReadAction extends BluetoothAction {

    MaterialDialog dialog;

    KDUINOBuoy kduinoBuoy;

    DataSet dataset;

    Bus bus;

    public TestDataReadAction(Activity activity,
                              BluetoothService service,
                              StorageService fileutilities,
                              IFinishBluetoothActionCallBack callBack,
                              Bus bus,
                              String kduinoID)
    {
        super(activity, service, fileutilities, callBack, true, false);
        this.bus = bus;
        this.kduinoBuoy =  KDUINOBuoy.findById(KDUINOBuoy.class, Long.parseLong(kduinoID));
    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("Q");
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



            ProcessDataService processDataService = new ProcessDataService();
            String userId = "";
            Iterator<User> users = User.findAll(User.class);
            if (users.hasNext())
            {
                User user = users.next();
                userId = user.UserId;
            }

            BuoyDefinition buoy = new BuoyDefinition(
                    userId,
                    kduinoBuoy.getId(),
                    kduinoBuoy.Name,
                    kduinoBuoy.Maker,
                    kduinoBuoy.User,
                    kduinoBuoy.MacAddress,
                    kduinoBuoy.Lat,
                    kduinoBuoy.Lon,
                    kduinoBuoy.Sensors,
                    kduinoBuoy.getSensors()
            );

            Measurement measurement  =
                    processDataService.proceedData(buoy, datareceived.replace("+", ""));
            ComputeKdService service = new ComputeKdService();
            Analysis analysis = null;
            List<Sensor> list =  Sensor.find(Sensor.class, "buoy_id = ?", Long.toString(kduinoBuoy.getId()));
            try {
                analysis = service.ComputeLinearRegresion(list, measurement);


            }catch (Exception ex)
            {
                Log.e(kdUINOApplication.TAG, "Error", ex);
            }

            if (analysis == null) {
                analysis = new Analysis();
                analysis.R2 = -1;
                analysis.Kd = -1;
                analysis.date = DateUtility.getDate();
            }


            ArrayList<Analysis> analysises = new ArrayList<Analysis>();
            analysises.add(analysis);
            dataset = new DataSet(DataSet.TYPE_ONE, buoy);
            dataset.setAnalysises(analysises);



            DecimalFormat real_formater = new DecimalFormat("0.###");
            // + Character.toString((char)0xFD)
            DisplayUtilities.ShowLargeMessage("Values calculated: Kd: " + real_formater.format(analysis.Kd) + " r2" + ": " + real_formater.format(analysis.R2),
                    activity.getString(R.string.ok_dialog),
                    this.activity.findViewById(android.R.id.content),
                    true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

            if (analysis.R2 != -1 && analysis.Kd != -1)
            {
                String name = DateUtility.getDate() + DateUtility.getTime();
                fileutilities.saveData(datareceived,  "test" + name + ".txt", false);
                bus.post(new KdUinoDataFileMessageBusEvent("test" + name + ".txt"));
            }

           // bus.post(new KdUinoAnalysisMessageBusEvent(dataset));
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
