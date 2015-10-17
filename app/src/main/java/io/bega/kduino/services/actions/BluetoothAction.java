package io.bega.kduino.services.actions;




import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.io.IOException;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ICounter;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DisplayUtilities;


/**
 * Definimos las acciones en función de la opción escogida por el usuario en el menu control y lanzamos el dialogo de espera
 */
public class BluetoothAction extends AsyncTask<Void,Integer,Void> implements
        ICounter, IFinishBluetoothActionCallBack {

    //creamos las variables
    protected MaterialDialog dialog;
    protected String datareceived;
    protected Activity activity;
    protected BluetoothService bluetoothService;
    protected StorageService fileutilities;
    protected char commandRecieved;
    private  KdUINOOperations operation;
    private BluetoothAction asyncObject;
    private CountDownTimer timer;
    private boolean finishTask = false;
    private boolean showDialog = false;
    private boolean getTotal = false;
    private IFinishBluetoothActionCallBack callback;

    public BluetoothAction(final Activity activity,
                           BluetoothService service,
                           StorageService fileutilities,
                           IFinishBluetoothActionCallBack callback,
                           boolean showDialog,
                           boolean getTotal) {
        super();
        this.bluetoothService = service;
        this.activity=activity;
        this.fileutilities=fileutilities;
        this.callback = callback;
        this.showDialog = showDialog;
        this.getTotal = getTotal;
        //if (showDialog) {
            dialog = new MaterialDialog.Builder(this.activity)
                    .positiveColorRes(R.color.colorPrimaryDark)
                    .neutralColorRes(R.color.colorPrimary)
                    .negativeColorRes(R.color.colorPrimary)
                    .cancelable(false)
                    .negativeText(R.string.cancel_dialog)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNegative(MaterialDialog dialog) {

                            super.onNegative(dialog);
                        }
                    })
                    .title(R.string.progress_dialog)
                    .content(R.string.please_wait)
                    .progress(false, 0, true)
                    .build();
        //}

        asyncObject = this;
       /* timer = new CountDownTimer(60000, 10000) {
            public void onTick(long millisUntilFinished) {

                // Watch Dog Timer

            }
            public void onFinish() {

                if (finishTask)
                {
                    return;
                }


                // stop async task if not in progress
                if (asyncObject.getStatus() == AsyncTask.Status.RUNNING) {
                    if (bluetoothService.isReceiving())
                    {
                        return;
                    }


                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    asyncObject.cancel(true);
                    DisplayUtilities.ShowLargeMessage("Error: Bluetooth command timeout","",
                            activity.findViewById(android.R.id.content),
                            false,
                            null);
                }
            }
        }.start(); */

        /* dialog = new ProgressDialog(this.activity);
        dialog.setMessage("Wait!");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true); */
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            if (bluetoothService != null)
            {

                datareceived = bluetoothService.recieveMessage(this, this.getTotal);
            }
        } catch (IOException ex) {
            DisplayUtilities.ShowLargeMessage("Error: Connection lost from device.", "",
                    activity.findViewById(android.R.id.content),
                    false,
                    null);
            Log.e(kdUINOApplication.TAG, "Error recieveing data from device" , ex);
        }

            return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (dialog != null && !dialog.isShowing()) {

            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {

        finishTask = true;
        //timer.cancel();
        //timer = null;
        super.onPostExecute(aVoid);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        this.finishAction(this);
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        if (dialog != null)
        {
            dialog.setProgress(values[0]);
        }

        super.onProgressUpdate(values);
    }


    @Override
    public void receivedCommand(char command) {
        Log.i(kdUINOApplication.TAG, "Received command" + command);
        if (dialog.isShowing())
        {
            dialog.dismiss();
        }

        this.commandRecieved = command;
    }

    @Override
    public void updateData(int counter) {
        publishProgress(counter);
    }

    @Override
    public void totalCounter(final int total) {

        if (total == 0)
        {
           return;
        }
        else {
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                /*if (showDialog) {
                     dialog = new MaterialDialog.Builder(activity)
                    .title(R.string.progress_dialog)
                    .content(R.string.please_wait)
                    .progress(false, total)
                    .build();
                    dialog.show();
                }*/

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }

                    if (dialog != null) {
                        dialog.setMaxProgress(total);

                    }

                }
            });
        }
    }

    @Override
    public void finishAction(BluetoothAction action) {
        if (callback != null)
        {
           // if (dialog!=dialog.isShowing())
            callback.finishAction(action);
        }
    }
}
