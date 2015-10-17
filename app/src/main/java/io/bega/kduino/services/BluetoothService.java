package io.bega.kduino.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Created by usuario on 18/07/15.
 */
public class BluetoothService implements  IBluetoothServiceManager {

    public static int REQUEST_ENABLE_BT = 0x001;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private IDisconnectCallback disconnectCallback;

    private BluetoothAdapter currentBluetoothAdapter;

    private ArrayList<BluetoothDevice> devices = new ArrayList();

    private ArrayAdapter<String> BTArrayAdapter;

    protected static BluetoothSocket btSocket = null;

    protected static OutputStream outputStream = null;

    protected static InputStream inputStream= null;

    private boolean recieving = false;

    private MaterialDialog dialog;

    private Context context;

    private View view;

    private Bus bus;

    private IDeviceDiscoveryNameService discoveryNameService;

    public ArrayAdapter<String> getBTArrayAdapter()
    {
        return this.BTArrayAdapter;
    }

    public ArrayList<BluetoothDevice> getBTDevices()
    {
        return this.devices;
    }

    public BluetoothService(Context ctx,
                            Bus bus,
                            ArrayAdapter<String> btArrayAdapter,
                            IDeviceDiscoveryNameService service,
                            View viewContext)
    {
        this.view = viewContext;
        this.bus = bus;
        this.BTArrayAdapter = btArrayAdapter;
        this.context = ctx;
        this.discoveryNameService = service;
    }

    private void displayDiscoveryDialog()
    {
        dialog = new MaterialDialog.Builder(this.context)
                .title(R.string.dialog_search_blueetooth_title)
                .content(R.string.dialog_search_blueetooth_message)
                .progress(true, 0)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        currentBluetoothAdapter.cancelDiscovery();
                    }
                })
                .build();
        dialog.show();
    }

    public Boolean isReceiving()
    {
        return this.recieving;
    }

    public static Boolean isEnabledBluetooth()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
            // Device does not support Bluetooth
        } else {

            return mBluetoothAdapter.isEnabled();
        }
    }

    public boolean isEnabled()
    {
        currentBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return currentBluetoothAdapter.isEnabled();
    }

    public void enableBluetooth(Activity activity)
    {
        currentBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!currentBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //activity.startActivity(enableBtIntent);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }

            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {


                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);


                String discoveryName  = device.getName();
               /* if (discoveryNameService != null)
                {
                    discoveryName = discoveryNameService.foundDevice(device.getAddress(), device.getName());
                }*/
                BTArrayAdapter.add(discoveryName + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }

        }
    };

    public void searchDevices()
    {
        currentBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (currentBluetoothAdapter.isDiscovering())
        {

            currentBluetoothAdapter.cancelDiscovery();
        }
        else {
            //limpiamos el adaptador y empezamos a buscar dispositivos
            BTArrayAdapter.clear();
            this.devices.clear();

            /*for(BluetoothDevice device :  currentBluetoothAdapter.getBondedDevices()) {
                this.devices.add(device);


                String discoveryName  = device.getName();
                if (discoveryNameService != null)
                {
                    discoveryName = discoveryNameService.foundDevice(device.getAddress(), device.getName());
                }

                BTArrayAdapter.add(discoveryName + "\n" + device.getAddress() );
            } */
            this.displayDiscoveryDialog();
            currentBluetoothAdapter.startDiscovery();
        }
    }


    @Override
    public void connect(String address)
    {

        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }

        currentBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(currentBluetoothAdapter==null) {
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_not_supported),
                    "",
                    view,
                    true,
                    null);
            return;
        }

        if (currentBluetoothAdapter.isEnabled())
        {
            createConnection(address);
        }
        else
        {
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                    "",
                    view,
                    false,
                    null);
        }
    }

    public void createConnection(String address) {

        BluetoothDevice device = currentBluetoothAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);

        } catch (IOException e1) {

            Log.e(kdUINOApplication.TAG, "Fatal Error,In onResume() and socket create failed,TRY TO CONNECT AGAIN!", e1);
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                    "",
                    view,
                    false,
                    null);
        }


        //cancelamos la búsqueda
        if (currentBluetoothAdapter.isDiscovering()) {
            currentBluetoothAdapter.cancelDiscovery();
        }

        //establecemos la conexión
        try {

            btSocket.connect();


        }

        catch (IOException exception) {

            try {

                Log.e(kdUINOApplication.TAG, "Fatal Error,In onResume() and socket create failed, TRY AGAIN!", exception);
                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                        "",
                        view,
                        false,
                        null);

                btSocket.close();
                return;

            } catch (IOException e3) {

                Log.e(kdUINOApplication.TAG, "Fatal Error,In onResume() and unable to close socket during connection failure", e3);
                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                        "",
                        view,
                        false,
                        null);
                return;
            }

        }
        catch (Exception ex)
        {
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                    "",
                    view,
                    false,
                    null);
             return;
            //btSocket.close();
        }
        //una vez creada la conexión creamos un outputStrem i inputStream para operar con el posteriormente
        try {
            outputStream = btSocket.getOutputStream();
            inputStream = btSocket.getInputStream();


        } catch (IOException exception) {
            Log.e(kdUINOApplication.TAG, "Fatal Error,In onResume() and output/input stream creation failed", exception);
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                    "",
                    view,
                    false,
                    null);

        }
        catch (Exception ex)
        {
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                    "",
                    view,
                    false,
                    null);
            return;
            //btSocket.close();
        }

       //  bus.post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_CONNECT_TO_KDUINO, address));

        //una vez conectados vamos al menu control


    }

    //creamos el socket para conectarnos
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e5) {
                Log.e(kdUINOApplication.TAG, "Could not create Insecure RFComm connection", e5);
                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.bluetooth_cant_connect),
                        "",
                        view,
                        false,
                        null);

            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }



    public void setDisconnectCallback(IDisconnectCallback callback)
    {
        this.disconnectCallback = callback;
    }

    @Override
        public void disconnect() {

            if (!this.isConnected())
            {
                return;
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
                inputStream = null;
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
                outputStream = null;
            }

            if (btSocket != null) {
                try {
                    btSocket.close();
                } catch (Exception e) {
                }
                btSocket = null;
            }

        if (disconnectCallback != null)
        {
            disconnectCallback.disconnect();
        }

    }

    @Override
    public void registerReciever() {
        context.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        context.registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    public void unregisterReciever() {

        context.unregisterReceiver(bReceiver);
    }


    @Override
    public void sendMessage(String message) throws IOException {
        if (outputStream != null) {
                outputStream.write(message.getBytes());
                outputStream.flush();
        }
    }


    @Override
    public String recieveMessage(ICounter callback, boolean useCounter) throws IOException
    {
        this.recieving = false;
        final char endata = '+';
        char[] buffer = new char[1024];
        int byteread = 0; // bytes devueltos del read()
        int pos = 0;

        // update last firmware, the last firware send the message recieved
        if (BluetoothService.inputStream != null) {
            //if (BluetoothService.btSocket.isConnected()) {
            int comandRead = BluetoothService.inputStream.read();
            Log.i(kdUINOApplication.TAG, "Command received from kduino: "+ (char)comandRead);
            callback.receivedCommand((char) comandRead);
        }


        StringBuilder sb = new StringBuilder();
        int counter = 0;
        int total = 0;
        boolean firstRead = true;
        do {


            try {
                if (BluetoothService.inputStream != null) {
                    //if (BluetoothService.btSocket.isConnected()) {
                    byteread = BluetoothService.inputStream.read();
                }
                else
                {
                    return "";
                }
                //}
                /*else
                {
                    new Handler(Looper.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    bus.post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_ERROR, null));
                                }
                            });
                }*/
            }
            catch (IOException io)
            {
                this.view.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((kdUINOApplication) context.getApplicationContext()).getBus()
                                    .post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_ERROR, null));
                        }catch (Exception ex)
                        {
                            Log.e(kdUINOApplication.TAG, "Error sending message", ex);
                        }
                    }
                });

                /*new Handler(Looper.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                  bus.post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_ERROR, null));
                            }
                        }); */

                Log.e(kdUINOApplication.TAG, "Error inputstrem bluetooth", io);
                return "";
            }

            recieving = true;
            if (pos < 1024) {
                buffer[pos++] = (char) byteread;
                if (useCounter) {
                    if (byteread == 0x0D) {
                            if (callback != null) {
                                callback.updateData(counter);
                            }
                            counter++;

                    }
                    else if (byteread == 32) {
                        if (firstRead) {
                            String value = new String(buffer).trim();
                            if (value.matches("\\d+")) {
                                total = Integer.parseInt(value);
                                if (total == 0)
                                {
                                    recieving = false;
                                    return "0+";
                                }

                                callback.totalCounter(total);

                            }

                            firstRead = false;
                        }

                    }
                }

            }
            else
            {
                String message = new String(buffer);
                sb.append(message);
                buffer = new char[1024];
                pos = 0;
                buffer[pos] = (char)byteread;
            }

        } while ((char) byteread != endata);

        recieving = false;
        String message = new String(buffer);
        sb.append(message);
        return sb.toString().trim();

    }


  /*  @Override
    public String recieveMessage(ICounter callback) throws IOException {
        final char endata = '+';
        char[] buffer = new char[128];
        int byteread; // bytes devueltos del read()
        int counter = 0;
        int pos = 0;
        StringBuilder sb = new StringBuilder();
        do {
            byteread = BluetoothService.inputStream.read();
            if (byteread == 0x0A)
            {
                counter++;
                continue;
            }


            if (pos < 128 && byteread != 0x0D) {
                buffer[pos++] = (char) byteread;
            }
            else

            {
                String message = new String(buffer).trim();
                if (callback != null)
                {
                    callback.updateData(message, counter);
                }


                sb.append(message).append("|");
                buffer = new char[128];
                pos = 0;
            }

        } while ((char) byteread != endata);

        String message = new String(buffer).trim();
        sb.append(message);
        return sb.toString();
    } */

    @Override
    public Boolean isConnected() {
        if (btSocket == null)
        {
            return false;
        }

        return btSocket.isConnected();
    }

}
