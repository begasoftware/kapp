package io.bega.kduino.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.Status;
import io.bega.kduino.datamodel.events.KdUinoAnalysisMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoDataFileMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoOperationMessageBusEvent;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.events.KdUinoSendAnalysisDataMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoSendDataMessageBusEvent;
import io.bega.kduino.fragments.AnalysisFragment;
import io.bega.kduino.fragments.KdUINOControlFragment;
//import io.bega.kduino.fragments.ALoginDialogFragment;
import io.bega.kduino.fragments.MapDialogFragment;
import io.bega.kduino.fragments.bluetooth.BluetoothActivityFragment;
import io.bega.kduino.fragments.bluetooth.BluetoothManagerFragment;
import io.bega.kduino.fragments.bluetooth.RegisterBuoyFragment;
import io.bega.kduino.fragments.bluetooth.SelectKdUINOModelFragment;
import io.bega.kduino.fragments.make.DefineBuoyFragment;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothManager;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ConnectBluetooth;
import io.bega.kduino.services.IDeviceDiscoveryNameService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DisplayUtilities;

public class BluetoothActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        IClickable, IDeviceDiscoveryNameService {

    public static final int DIALOG_FRAGMENT = 1;

    protected MaterialDialog connectingDialog;

    private BluetoothService bluetoothService;

    private BluetoothManager bluetoothManager;

    private Context ctx;

    private LineChart mChart;

    ConnectBluetooth connectBluetooth;

    DataSet dataset;

    private final String stackName = "bluetooth";

    ListView listViewDevices;

    public ArrayAdapter<String> devices;

    TextView tv;

    private String buoyID = "0";

    private String macAddress;

    Menu mMenu;

    Bus bus;

    private ProgressDialog progressDialog;

    AnalysisFragment analysisFragment;

    private IClickable fragment;

    View view;


    public BluetoothService getBluetoothService()
    {
        return this.bluetoothService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        this.ctx = this;
        setActionNavigation();
        this.bus = ((kdUINOApplication)  this.getApplication()).getBus();
        this.devices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        this.setTitle(getString(R.string.title_activity_bluetooth));


        view = findViewById(R.id.main_layout_linear);
        this.bluetoothService = new BluetoothService(this, this.bus, this.devices, this, view);
        this.bluetoothManager = new BluetoothManager(this, this.bluetoothService,
                new StorageService(this, view), this.bus);
        this.setTitle("");
        this.moveToSearch();
    }

    @Override
    protected void onStart() {

        this.bluetoothService.registerReciever();
        this.bus.register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {

        this.bluetoothService.unregisterReciever();
        this.bus.unregister(this);
        super.onStop();
    }


    @Subscribe
    public void getMessage(KdUinoAnalysisMessageBusEvent message) {

        this.setData(message.getData());
        if (message.getData().Type.equals(DataSet.TYPE_ALL)) {

            this.displayData(message.getData());
            // this.launchAnalysisFragment();
        } else {
            this.sendDataToTask(message.getData());
        }
    }

    @Subscribe
    public void getMessage(KdUinoSendAnalysisDataMessageBusEvent event)
    {
        this.sendDataToTask(event.getData());
    }

    @Subscribe
    public void getMessage(KdUinoDataFileMessageBusEvent event)
    {
        this.sendFileToTask(event.getPath());
    }


    @Subscribe
    public void getMessage(KdUinoMessageBusEvent event)
    {
        switch (event.getMessage())
        {
            case KdUINOMessages.RECIEVE_INFO:
                Status status = (Status)event.getData();
                this.setTitle(status.Name);
                break;
            case KdUINOMessages.REFRESH_INFO:
                this.sendInfoMessage();
                break;
            case KdUINOMessages.BLUETOOTH_CONNECT_TO_KDUINO:
                this.sendInfoMessage();
                this.invalidateOptionsMenu();
                if (event.getData() != null) {
                    String id = Long.toString(((KDUINOBuoy) event.getData()).getId());
                    this.moveToControl(id, this.macAddress);
                }
                break;
            case KdUINOMessages.CONNECT_BLUETOOTH:
                if (!this.bluetoothService.isEnabled()) {
                    this.bluetoothService.enableBluetooth(this);
                }
                else
                {
                    this.connectToDevice((KDUINOBuoy) event.getData());
                }

                this.invalidateOptionsMenu();

                break;
            case KdUINOMessages.SEARCH_DEVICES:

                if (event.getData() != null) {
                    String code = event.getData().toString();
                    if (code.equals("MENU"))
                    {
                        return;
                    }
                }

                if (bluetoothService.isConnected())
                {
                    return;
                }

                if (!bluetoothService.isEnabled())
                {

                    bluetoothService.enableBluetooth((Activity)ctx);
                }
                else
                {
                    if (bluetoothService.isConnected())
                    {
                        bluetoothService.disconnect();
                        bluetoothService.searchDevices();
                    }
                    else
                    {
                        bluetoothService.searchDevices();
                    }

                }

                break;
            case KdUINOMessages.DISCONNECT_KDUINO:
                if (this.bluetoothService.isConnected()) {
                    this.bluetoothService.disconnect();
                }
                this.invalidateOptionsMenu();
                break;
            case KdUINOMessages.SEND_COMMAND_KDUINO:
                break;
            case KdUINOMessages.BLUETOOTH_ON:
                this.invalidateOptionsMenu();
                break;
            case KdUINOMessages.CONNECT_KDUINO:
                this.connectToDevice((KDUINOBuoy) event.getData());
                break;
            case KdUINOMessages.BLUETOOTH_ERROR:

                KDUINOBuoy buoy = (KDUINOBuoy) event.getData();
                if (buoy != null) {
                    if (buoy.Name != null && buoy.Maker != null) {
                        if (buoy.Name.length() == 0 && buoy.Maker.length() == 0) {
                            buoy.delete();
                        }
                    }
                }

                DisplayUtilities.ShowLargeMessage("Error connecting to Bluetooth device.",
                        "", this.getWindow().getDecorView(), false,
                        null);
                break;
            case KdUINOMessages.ADD_MAKE_KDUINO:
                finish();
                break;
            default:
                this.processNavigationMessage(event.getMessage());
        }
    }

    private void sendInfoMessage() {
        if (this.bluetoothService.isConnected()) {
            this.bluetoothManager.ExecuteOperation(KdUINOOperations.INFO_KdUINO, "");
        }

        if (connectingDialog != null) {
            connectingDialog.dismiss();
        }
    }


    @Subscribe
    public void getMessage(KdUinoOperationMessageBusEvent message) {

        if (message.getData() != null &&  message.getData() instanceof String)
        {
            String extracommand = new String();
            extracommand = (String)message.getData();
            this.bluetoothManager.ExecuteOperation(message.getMessage(), extracommand);
        }



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!this.bluetoothService.isConnected()) {

            //for ()
            //parent.getChildAt()

            // ((TextView)   view).setTextColor(getResources().getColor(R.color.white_pressed));



            BluetoothDevice device = this.bluetoothService.getBTDevices().get(position);
            this.macAddress =  device.getAddress();
            List<KDUINOBuoy> list =  KDUINOBuoy.find(KDUINOBuoy.class, "mac_address= ?", this.macAddress);
            if (list.size() > 0) {
                buoyID = list.get(0).getId().toString();
                try {
                    this.connectToDevice(list.get(0));
                }
                catch(Exception ex)
                {

                    return;
                }

               // this.moveToControl(buoyID, this.macAddress);


            }
            else {
                SettingsManager manager = new SettingsManager(this);
                KDUINOBuoy buoy = new KDUINOBuoy(
                        "0",
                        "",
                        "",
                        manager.getUsername(),
                        this.macAddress,
                        0,0, 0);
                buoy.save();
                try {
                    this.connectToDevice(buoy);
                }
                catch(Exception ex)
                {

                    return;
                }

               // this.moveToControl(Long.toString(buoy.getId()), this.macAddress);
                //this.moveToKduinoModelSelection();
            }

        }

    }

    private void moveToKduinoModelSelection() {
        SelectKdUINOModelFragment buoyFragment = SelectKdUINOModelFragment.newInstance(this.macAddress);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_container_bluetooth, buoyFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.addToBackStack(stackName);
        ft.commit();
        fragment =  buoyFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == BluetoothService.REQUEST_ENABLE_BT) {
            /*bus.post(new KdUinoMessageBusEvent(
                            KdUINOMessages.BLUETOOTH_ON, null)
            ); */

            //this.moveToSearch();
            if (resultCode == 0)
            {
                finish();
                return;
            }

            bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SEARCH_DEVICES, null));
            this.invalidateOptionsMenu();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (bluetoothService.isConnected())
                {
                    closeDialog();
                }
                return false;
            }
        });
        return true;
    }

    public boolean onPrepareOptionsMenu (Menu menu)
    {
            if (this.bluetoothService.isConnected()) {
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_connect_white));

            }
            else
            {
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_disconnect_white_off));
            }




        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_menu_connect_device)
        {
            if (this.bluetoothService.isConnected())
            {
                this.closeDialog();
            }
        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_bluetooth_search) {

        }
        else if (id == R.id.action_bluetooth_activate)
        {
            this.bluetoothService.enableBluetooth();
            supportInvalidateOptionsMenu();
        }
        else if (id == R.id.action_bluetooth_disconnect)
        {
            this.bluetoothService.disconnect();
            this.tv.setText("Not Connected");
            supportInvalidateOptionsMenu();

        }*/

        return super.onOptionsItemSelected(item);
    }

    private void connectToDescription(String id)
    {
        BluetoothManagerFragment bluetoothManagerFragment =
                BluetoothManagerFragment.newInstance(id, this.macAddress);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_container_bluetooth, bluetoothManagerFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.addToBackStack(stackName);
        ft.commit();
        fragment =  bluetoothManagerFragment;
    }

    private void moveToSearch()
    {
        BluetoothActivityFragment fragment = new BluetoothActivityFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_container_bluetooth, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    private void moveToControl(String id, String mac)
    {
        BluetoothManagerFragment bluetoothManagerFragment
                = BluetoothManagerFragment.newInstance(id, mac);
        fragment = bluetoothManagerFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        ft.replace(R.id.fragment_container_bluetooth, bluetoothManagerFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.addToBackStack(stackName);
        ft.commit();
    }

    private void connectToDevice(KDUINOBuoy buoy)
    {
        if (buoy == null)
        {
            return;
        }

        if (!this.bluetoothService.isConnected()) {
            if (connectBluetooth == null) {
                connectBluetooth = new ConnectBluetooth(this.bus, this, this.bluetoothService, buoy);
                connectBluetooth.execute(this.macAddress);

                // this.bluetoothService.connect(this.macAddress);
            }
            else
            {
                if (connectBluetooth.getStatus() == AsyncTask.Status.FINISHED)
                {
                    connectBluetooth = new ConnectBluetooth(this.bus, this, this.bluetoothService, buoy);
                    connectBluetooth.execute(this.macAddress);
                }
            }
        }

        this.invalidateOptionsMenu();

    }


    @Override
    public void clickView(View v) {
        if (fragment != null)
        {
            fragment.clickView(v);
        }
    }


    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        }
        else {
            if (this.bluetoothService.isConnected()) {
                this.closeDialog();


               /* new AlertDialog.Builder(this)
                        .setMessage("Are you sure to disconnect?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("No", null)
                        .show(); */

            }
            else
            {
                getSupportFragmentManager().popBackStack(stackName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                super.onBackPressed();
            }
        }
    }

    private void closeDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Confirm disconnect")
                .content("You will disconnect from the kduino buoy.")
               .positiveColorRes(R.color.colorPrimaryDark)
               .neutralColorRes(R.color.colorPrimary)
               .negativeColorRes(R.color.colorPrimary)
               .positiveText(R.string.ok_dialog)
               .negativeText(R.string.cancel_dialog)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ((BaseActivity) ctx).clearStack();
                        if (analysisFragment != null && analysisFragment.isVisible()) {

                            analysisFragment.dismissAllowingStateLoss();
                        }

                        bus.post(
                                new KdUinoMessageBusEvent(
                                        KdUINOMessages.DISCONNECT_KDUINO, null)
                        );


                        finish();
                    }
                }).build();
        dialog.show();
    }

    @Override
    public String foundDevice(String mac, String name) {
        List<KDUINOBuoy> list = KDUINOBuoy.find(KDUINOBuoy.class, "mac_address= ?", mac);
        if (list.size() > 0)
        {
            return list.get(0).Name;
        }

        return name;
    }

    private void displayData(DataSet dataset)
    {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_kd_r2_title)
                .customView(R.layout.fragment_analysis, false)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog)
                    {
                        SettingsManager manager = new SettingsManager(dialog.getContext());
                        String nameFile = manager.getLastTotalFileName();
                        bus.post(new KdUinoDataFileMessageBusEvent(nameFile));
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .neutralText(R.string.action_send_data_to_server)
                .positiveText(R.string.ok_dialog)
               // .negativeText(R.string.cancel_dialog)
                .build();

        View view = dialog.getCustomView();

        mChart = (LineChart) view.findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(null);
        mChart.setOnChartValueSelectedListener(null);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        /*DiscreteSeekBar discreteSeekBar  = (DiscreteSeekBar)view.findViewById(R.id.sample_r2_data);
        discreteSeekBar.setMin(2);
        discreteSeekBar.setMax(dataset.getAnalysises().size());
        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                setData(mChart, value, getData());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        }); */
        /*DiscreteSeekBar discreteSeekBar =
                (DiscreteSeekBar)view.findViewById(R.id.sample_time_dsb);
        discreteSeekBar.setMin(1);
        discreteSeekBar.setMax(59); */

       /* int sampleTime = 0;
        if (statusData.SampleTime > 1000)
        {
            sampleTime = statusData.SampleTime / 1000;
        } */

        //discreteSeekBar.setProgress(statusData.SampleTime);
        dialog.show();
        setData(mChart,  dataset);
    }

    private void setData(LineChart mChart,  DataSet dataSet)
    {

        ArrayList<Analysis> analysises = dataSet.getAnalysises();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> kdVals = new ArrayList<Entry>();
        ArrayList<Entry> r2Vals = new ArrayList<Entry>();

        int i = 0;
        for (Analysis analysis : analysises)
        {
            if (Double.isNaN(analysis.R2))
            {
                continue;
            }

            if (Double.isNaN(analysis.Kd))
            {
                continue;
            }

            xVals.add(analysis.date.replace("_", " "));
            kdVals.add(new Entry((float)analysis.Kd, i));
            r2Vals.add(new Entry((float)analysis.R2, i));
            i++;
        }

        LineDataSet set1 = new LineDataSet(kdVals, "Kd data");
        LineDataSet set2 = new LineDataSet(r2Vals, "R2 data");
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.RED);


        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLUE);
        set2.setLineWidth(1f);
        set2.setCircleSize(3f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.BLUE);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

        mChart.setVisibleXRange(4); // allow 20 values to be displayed at once on the x-axis, not more
        mChart.moveViewToX(kdVals.size() - 5); // se
        //mChart.setVisibleYRange(3, null);
        //mChart.moveViewToY(kdVals.get(kdVals.size() - 1).getVal(), null);

    }


    private void launchAnalysisFragment()
    {
        System.gc();
        //Mater

        FragmentManager fm = getSupportFragmentManager();
        analysisFragment = AnalysisFragment.newInstance("", "");
        analysisFragment.show(fm, "map fragment");
    }




    public void setData(DataSet dataset)
    {

        this.dataset = dataset;
    }

    public DataSet getData()
    {
        return dataset;
    }
}
