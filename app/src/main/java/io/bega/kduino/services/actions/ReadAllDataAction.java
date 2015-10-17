package io.bega.kduino.services.actions;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.Measurement;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.events.KdUinoAnalysisMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoDataFileMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoSendDataMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ComputeKdService;
import io.bega.kduino.services.ProcessDataService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DateUtility;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 24/07/15.
 * SENDDATA:
 * Letter “D”
 * Action: Send all the data stored into the SD memory.
 * Example:
 * 2015/07/05_10:45:12 1 32424 2 1324134 3 3432432 4 34234 5 3124234
 * 2015/07/05_10:46:12 1 32424 2 1324134 3 3432432 4 34234 5 3124234
 * 2015/07/05_10:47:12 1 32424 2 1324134 3 3432432 4 34234 5 3124234
 * Final character(ACK): “+”
 * App action: Read all the data, analyse it to obtain kd and r2 and send the info to the DB when it will be possible.
 *
 *
 */
public class ReadAllDataAction extends BluetoothAction {

    private String nameFile;

    KDUINOBuoy kduinoBuoy;

    Bus bus;

    DataSet dataset;

    private Handler handler = new Handler();

    public ReadAllDataAction(Activity activity,
                             BluetoothService service,
                             StorageService fileutilities,
                             IFinishBluetoothActionCallBack callBack,
                             String buoyID,
                             Bus bus
    )
    {
        super(activity, service, fileutilities, callBack, true, true);
        this.kduinoBuoy = KDUINOBuoy.findById(KDUINOBuoy.class, Long.parseLong(buoyID));
        this.nameFile =  "data_" + kduinoBuoy.getId().toString() + DateUtility.getDate();
        this.bus = bus;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if (datareceived!=null && datareceived.indexOf("+")!=-1){

            if (datareceived.equals("0+") || datareceived.equals("+"))
            {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }

                DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_not_found),
                        "",
                        this.activity.findViewById(android.R.id.content),
                        false,
                        null);
                return;
            }
            else {
                DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_has_been_read),
                        "",
                        this.activity.findViewById(android.R.id.content),
                        false,
                        null);
            }

        }else{
            DisplayUtilities.ShowLargeMessage(this.activity.getString(R.string.bluetooth_data_cant_read_data),
                    "",
                    this.activity.findViewById(android.R.id.content),
                    false,
                    null);
            return;
        }

        if (datareceived != null && fileutilities != null)
        {

            SettingsManager manager = new SettingsManager(this.activity);
            manager.setLastTotalFileName("all" + nameFile + ".txt");
            String fileName = manager.getLastTotalFileName();
            fileutilities.saveData(datareceived, fileName, false);
            bus.post(new KdUinoAnalysisMessageBusEvent(this.dataset));

        }
        
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        super.doInBackground(params);

        if (datareceived.equals("0+") || datareceived.equals("+") || datareceived.length() == 0)
        {
            return null;
        }


        ProcessDataService processDataService = new ProcessDataService();
        ComputeKdService service = new ComputeKdService();

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


        ArrayList<Measurement> measurements  =
                processDataService.proceeAllData(buoy, datareceived);
        final int max = measurements.size();



        handler.post(new Runnable() {
            public void run() {
                dialog.setProgress(0);
                dialog.setMaxProgress(max);
                dialog.setTitle(activity.getString(R.string.progress_calculating_kd_title));
                dialog.setContent(activity.getString(R.string.progress_calculating_kd_help));
            }
        });

        ArrayList<Analysis> analysises = new ArrayList<Analysis>();
        List<Sensor> list =  Sensor.find(Sensor.class, "buoy_id = ?", Long.toString(kduinoBuoy.getId()));
        for (int i=0; i<measurements.size(); i++)
        {
            Measurement measurement =  measurements.get(i);
            if (measurement != null)
            {
                Analysis analysis = service.ComputeLinearRegresion(list, measurement);


                analysises.add(analysis);
                final int counter = i;

                handler.post(new Runnable() {
                    public void run() {
                        dialog.setProgress(counter);
                    }
                });
            }

           // dialog.setProgress(i);
        }

        dataset = new DataSet(
                DataSet.TYPE_ALL,
                buoy);
        dataset.setAnalysises(analysises);

        return null;

    }

    @Override
    protected void onPreExecute() {
        try
        {
            this.bluetoothService.sendMessage("D");
        }
        catch(IOException ex)
        {
            Log.e(kdUINOApplication.TAG, "Error sending action command", ex);
        }

        super.onPreExecute();
        handler.post(new Runnable() {
            public void run() {
                dialog.setTitle(activity.getString(R.string.progress_retrieving_title));
                dialog.setContent(activity.getString(R.string.progress_retrieving_title));
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        });
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
