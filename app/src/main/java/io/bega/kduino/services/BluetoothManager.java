package io.bega.kduino.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.otto.Bus;

import java.util.concurrent.ConcurrentLinkedQueue;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.activities.BluetoothActivity;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.actions.BluetoothAction;
import io.bega.kduino.services.actions.CalibrationAction;
import io.bega.kduino.services.actions.DeleteDataAction;
import io.bega.kduino.services.actions.GetSampleTimeAction;
import io.bega.kduino.services.actions.InfoAction;
import io.bega.kduino.services.actions.ReadAllDataAction;
import io.bega.kduino.services.actions.ReadTimeAction;
import io.bega.kduino.services.actions.SetMeasurementTimeAction;
import io.bega.kduino.services.actions.SetSampleTimeAction;
import io.bega.kduino.services.actions.SetTimeAction;
import io.bega.kduino.services.actions.StartContinuousDataReadAction;
import io.bega.kduino.services.actions.StopContinuousDataReadAction;
import io.bega.kduino.services.actions.TestDataReadAction;

/**
 * Created by usuario on 24/07/15.
 */
public class BluetoothManager implements IFinishBluetoothActionCallBack, IDisconnectCallback {

    private ConcurrentLinkedQueue<BluetoothAction> actions;

    private BluetoothService service;

    private Activity activity;

    private StorageService storageService;

    private Bus bus;

    public BluetoothManager(Activity activity,
                            BluetoothService service,
                            StorageService storageService,
                            Bus bus)
    {
        this.storageService = storageService;
        this.activity = activity;
        this.actions = new ConcurrentLinkedQueue<BluetoothAction>();
        this.service = service;
        this.service.setDisconnectCallback(this);
        this.bus = bus;
    }

    public void ExecuteOperation(KdUINOOperations operations, String extData)
    {
        BluetoothAction action = null;
        switch (operations)
        {
            case INFO_KdUINO:
                action = new InfoAction(activity, service, storageService, this, this.bus);
                break;
            case READ_INTERVAL:
                action = new GetSampleTimeAction(activity, service, storageService, this);
                break;
            case SET_SAMPLE:
                action = new SetSampleTimeAction(activity, service, storageService, this, extData, this.bus);
                break;
            case SET_MEASUREMENT:
                action = new SetMeasurementTimeAction(activity, service, storageService, this, extData, this.bus);
                break;
            case SET_TIME:
                action = new SetTimeAction(activity, service, storageService, this, extData);
                break;
            case START_KdUINO:
                action = new StartContinuousDataReadAction(activity, service, storageService, this, this.bus);
                break;
            case END_KdUINO:
                action = new StopContinuousDataReadAction(activity, service, storageService, this, this.bus);
                break;
            case GET_DATA:

                BluetoothActivity bluetoothActivity = (BluetoothActivity)this.activity;
                if (bluetoothActivity != null)
                {
                    bluetoothActivity.setData(null);
                    System.gc();
                }

                action = new ReadAllDataAction(activity, service, storageService, this, extData, this.bus);
                break;
            case DELETE_DATA:
                action = new DeleteDataAction(activity, service, storageService, this);
                break;
            case CALIBRATION:
                action = new CalibrationAction(activity, service, storageService, this);
                break;
            case READ_TIME:
                action = new ReadTimeAction(activity, service, storageService, this);
                break;
            case TEST_SENSORS:
                action = new TestDataReadAction(activity, service, storageService, this, this.bus, extData);
                break;
        }

        Log.i(kdUINOApplication.TAG, "New action: " + action.toString());

        if (actions != null && !actions.contains(action))
        {
            Log.i(kdUINOApplication.TAG, "Insert action in execution queue: " + action.toString());
            actions.add(action);
            Log.i(kdUINOApplication.TAG, "Add action in the stack" + action.toString());

        }
        else
        {
            Log.i(kdUINOApplication.TAG, "No action put in the stack and cancel" + action.toString());
            action.cancel(true);
            return;
           // action = null;

        }

        if (actions.size() > 0)
        {
            Log.i(kdUINOApplication.TAG, "Queue size > 0");
            if (actions.peek().getStatus() != AsyncTask.Status.RUNNING)
            {
                Log.i(kdUINOApplication.TAG, "Execute: " + action.toString());
                action.execute();
            }

        }
    }


    @Override
    public void finishAction(BluetoothAction action) {
        if (actions.contains(action)) {
            Log.i(kdUINOApplication.TAG, "Remove action: " + action.toString());
            actions.remove(action);
        }


        while (actions.size() > 0) {
            BluetoothAction bluetoothAction = actions.poll();
            if (bluetoothAction.getStatus() == AsyncTask.Status.RUNNING)
            {
                Log.i(kdUINOApplication.TAG, "Action in running state: " + bluetoothAction.toString());
                return;
            }

            if (bluetoothAction.getStatus() != AsyncTask.Status.FINISHED) {
                Log.i(kdUINOApplication.TAG, "Execute in queue list: " + bluetoothAction.toString());
                bluetoothAction.execute();
                return;
            }
        }
    }

    @Override
    public void disconnect() {

        if (actions.size() > 0)
        {
            while (actions.size() > 0) {
                BluetoothAction bluetoothAction = actions.poll();
                bluetoothAction.cancel(true);
            }
        }

    }
}
