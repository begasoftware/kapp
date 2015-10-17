package io.bega.kduino.fragments.bluetooth;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.osmdroid.util.GeoPoint;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.activities.BluetoothActivity;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.DistanceBuoy;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.Status;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoOperationMessageBusEvent;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.events.KdUinoSendDataMessageBusEvent;
import io.bega.kduino.fragments.CommandsFragmentAdapter;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.LocationService;
import io.bega.kduino.utils.DateUtility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link BluetoothManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothManagerFragment extends Fragment implements IClickable, MaterialDialog.InputCallback,
        LocationService.newBestLocationListener {

    private static final String BUOY_ID = "buoy_id";

    private static final String MAC_ADRESS = "mac_adress";

    private String mBuoyId;

    private String mMacAdress;

    private KDUINOBuoy buoy;

    BluetoothService communicationService;

    private LocationService locationService;

    Bus bus;

    GeoPoint bestPoint;

    String currentBuoyName = "";

    private EditText editBuoyName;

    private EditText editBuoyMarker;

    private EditText editBuoyUser;

    private EditText editGPSValue;

    private TextView txtStatus;

    private TextView txtSampleTime;

    private TextView txtMeasurementTime;

    private TextView txtError;

    private SettingsManager settingsManager;

    private MaterialDialog waitInfoDialog;



    public BluetoothManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuoyId = getArguments().getString(BUOY_ID);
            mMacAdress = getArguments().getString(MAC_ADRESS);

        }

        this.locationService = new LocationService(getActivity());
        this.locationService.setNewBestLocationListener(this);
        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
        this.settingsManager = new SettingsManager(getActivity());
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static BluetoothManagerFragment newInstance(String buoyID, String macAdress) {
        BluetoothManagerFragment fragment = new BluetoothManagerFragment();
        Bundle args = new Bundle();
        args.putString(BUOY_ID, buoyID);
        args.putString(MAC_ADRESS, macAdress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth_manager, container, false);

        txtError = (TextView)view.findViewById(R.id.register_buoy_txt_status_error_value);
        txtStatus = (TextView)view.findViewById(R.id.register_buoy_txt_status_value);
        txtSampleTime = (TextView)view.findViewById(R.id.register_buoy_txt_status_sampletime_value);
        txtMeasurementTime = (TextView)view.findViewById(R.id.register_buoy_txt_status_measurement_value);
        editGPSValue = (EditText)view.findViewById(R.id.register_buoy_gps_value);


        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new CommandsFragmentAdapter(this.getFragmentManager(),
                getActivity(), mBuoyId));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

      //  editBuoyName = (EditText)view.findViewById(R.id.register_buoy_edit_name);
        editBuoyMarker = (EditText)view.findViewById(R.id.register_buoy_edit_name_maker);
     /*   editBuoyMarker.postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (editBuoyMarker.getText().toString().length() == 0)
                    {

                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.BuoyErrorTitle)
                                .content(R.string.BuoyErrorContent)
                                .positiveColorRes(R.color.colorPrimaryDark)
                                .neutralColorRes(R.color.colorPrimary)
                                .negativeColorRes(R.color.colorPrimary)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        bus.post(
                                                new KdUinoMessageBusEvent(
                                                        KdUINOMessages.DISCONNECT_KDUINO, null)
                                        );
                                       getActivity().finish();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                    }
                                })
                                .positiveText(R.string.ok_dialog)
                                .build();

                        dialog.show();



                    }
            }
        }, 5000); */
      //  editBuoyUser = (EditText)view.findViewById(R.id.register_buoy_edit_name_user);
   //        editBuoyUser.setText(settingsManager.getUsername());
        //editMacAdress = (EditText)view.findViewById(R.id.register_buoy_txt_mac_value);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.bindData(mBuoyId, mMacAdress);

        this.bus.register(this);
        this.bus.post(new KdUinoMessageBusEvent(KdUINOMessages.REFRESH_INFO, null));

        if (!this.locationService.isLocationEnabled())
        {
            this.locationService.displayPromptForEnablingGPS(getActivity());
        }
        else
        {
            this.waitDialog();
        }

        this.locationService.StartListen();




    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.locationService.StopListen();
        this.bus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void newBestLocation(Location currentBestLocation) {
        GeoPoint point = new GeoPoint(
                currentBestLocation.getLatitude(),
                currentBestLocation.getLongitude());

        /*if (this.bestPoint == null)
        {
            this.map.getController().setCenter(point);
        }*/

        this.bestPoint = point;

        if (this.bestPoint != null) {
            Iterator<KDUINOBuoy> listOfKduinos =  KDUINOBuoy.findAll(KDUINOBuoy.class);
            while(listOfKduinos.hasNext())
            {
                KDUINOBuoy kduinoBuoy = listOfKduinos.next();
                if (kduinoBuoy.Name.equals(currentBuoyName)) {
                    float[] results = new float[]{0f, 0f, 0f};
                    Location.distanceBetween(this.bestPoint.getLatitude(),
                            this.bestPoint.getLongitude(), buoy.Lat, buoy.Lon, results);

                    float test = results[0];
                    if (results[0] < 100)
                    {
                       // kduinoBuoy.MacAddress = buoy.MacAddress;
                       // buoy = kduinoBuoy;
                        break;
                    }
                }
            }
        }

        if (buoy != null) {
            float[] results = new float[]{0f, 0f, 0f};
            Location.distanceBetween(buoy.Lat, buoy.Lon,
                    this.bestPoint.getLatitude(), this.bestPoint.getLongitude(), results);
            float meters = results[0];
            if (meters > 10) {
                // TODO if the best point is more than create a new buoy.
                buoy.setLatLong(this.bestPoint);
                buoy.save();
            }
        }

        DecimalFormat form = new DecimalFormat("0.0000");
        editGPSValue.setText("Lat: "

                + form.format(point.getLatitude())
                + ",\nLon: "
                + form.format((point.getLongitude())));

        //this.setPoint(point, false);
       // this.currentLocationPoint = point;


    }

    @Subscribe
    public void getMessage(Status status)
    {



        String error = "";
        switch (status.SDError)
        {
            case 0:
            {
                error = getString(R.string.status_error0);
                break;
            }
            case 1:
            {
                error = getString(R.string.status_error1);
                break;
            }
            case 2:
            {
                error = getString(R.string.status_error2);
                break;
            }
            case 3:
            {
                error = getString(R.string.status_error3);
                break;
            }
        }

        this.txtError.setText(error);
        this.txtMeasurementTime.setText(status.MeasurementTime + "s");
        this.txtSampleTime.setText(status.SampleTime + "m");
        String statusValue = getString(R.string.status_run);
        if (status.StatusKduino == 0)
        {
            statusValue = getString(R.string.status_stop);
        }


        this.txtStatus.setText(statusValue);
        if (waitInfoDialog != null && waitInfoDialog.isShowing())
        {
            waitInfoDialog.dismiss();
        }

    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent message)
    {
        if(message.getMessage() == KdUINOMessages.RECIEVE_INFO)
        {
            Status status = (Status)message.getData();
            String error = "";
            switch (status.SDError)
            {
                case 0:
                {
                    error = getString(R.string.status_error0);
                    break;
                }
                case 1:
                {
                    error = getString(R.string.status_error1);
                    break;
                }
                case 2:
                {
                    error = getString(R.string.status_error2);
                    break;
                }
                case 3:
                {
                    error = getString(R.string.status_error3);
                    break;
                }
            }

            if (waitInfoDialog != null && waitInfoDialog.isShowing())
            {
                waitInfoDialog.dismiss();
            }

            /*if (this.bestPoint != null) {
                List<KDUINOBuoy> listOfKduinos = (List<KDUINOBuoy>) KDUINOBuoy.findAll(KDUINOBuoy.class);
                for (KDUINOBuoy buoy : listOfKduinos) {
                    if (buoy.Name.equals(status.Name)) {
                        float[] results = new float[]{0f, 0f, 0f};
                        Location.distanceBetween(this.bestPoint.getLatitude(),
                                this.bestPoint.getLongitude(), buoy.Lat, buoy.Lon, results);

                        float test = results[0];
                    }
                }
            } */

            currentBuoyName = status.Name;
            if (buoy != null)
            {
                buoy.Name = status.Name;
                buoy.Maker = status.Maker;
                buoy.Sensors = status.SensorNumber;
                buoy.save();
            }

            if (settingsManager != null)
            {
                buoy.User = settingsManager.getUsername();
                buoy.save();
            }

            this.editBuoyMarker.setText(status.Maker);
            //this.editBuoyName.setText(status.Name);
            this.txtError.setText(error);
            this.txtMeasurementTime.setText(status.MeasurementTime + "s");
            this.txtSampleTime.setText(status.SampleTime + "m");
            String statusValue = getString(R.string.status_run);
            if (status.StatusKduino == 0)
            {
                statusValue = getString(R.string.status_stop);
            }
            this.txtStatus.setText(statusValue);
        }
    }

    @Subscribe
    public void getMessage(GeoPoint geoPoint) {

        this.bestPoint = geoPoint;
       /* if (this.bestPoint != null) {
            List<KDUINOBuoy> listOfKduinos = (List<KDUINOBuoy>) KDUINOBuoy.findAll(KDUINOBuoy.class);
            for (KDUINOBuoy buoy : listOfKduinos) {
                if (buoy.Name.equals(currentBuoyName)) {
                    float[] results = new float[]{0f, 0f, 0f};
                    Location.distanceBetween(this.bestPoint.getLatitude(),
                            this.bestPoint.getLongitude(), buoy.Lat, buoy.Lon, results);

                    float test = results[0];
                }
            }
        } */

        buoy.setLatLong(geoPoint);
        buoy.save();
        if (((BluetoothActivity) getActivity()).getData() != null) {
            ((BluetoothActivity) getActivity()).getData().getBuoy().Lat = geoPoint.getLatitude();
            ((BluetoothActivity) getActivity()).getData().getBuoy().Lon = geoPoint.getLongitude();
        }
    }

    @Override
    public void clickView(View v) {
        KdUINOOperations operation = KdUINOOperations.TEST_SENSORS;
        switch (v.getId())
        {
           /* case R.id.btnSendToServerData:
                DataSet dataSet =  ((BluetoothActivity)getActivity()).getData();
                ((BluetoothActivity)getActivity()).sendDataToTask(dataSet);
                return; */
            case R.id.btnManagerTestSensors:
                operation = KdUINOOperations.TEST_SENSORS;
                bus.post(new KdUinoOperationMessageBusEvent<String>(operation, mBuoyId));
                return;
            case R.id.btnMeasurmentTime:
                operation = KdUINOOperations.SET_TIME;
                break;
            case R.id.btnSampleTime:
                operation = KdUINOOperations.READ_TIME;
                break;
            case R.id.btnManagerCommandReadAll:
                operation = KdUINOOperations.GET_DATA;
                bus.post(new KdUinoOperationMessageBusEvent<String>(operation, mBuoyId));
                return;
            case R.id.btnManagerCommandDeleteAll:
                this.confirmDeleteDialog();
                return;
            case R.id.btnManagerEndReadDataAction:
                operation = KdUINOOperations.END_KdUINO;
                break;
            case R.id.btnManagerCalibrationSensor:
                operation = KdUINOOperations.CALIBRATION;
                break;
            case R.id.btnManagerStartReadDataAction:
                operation = KdUINOOperations.START_KdUINO;
                break;
            case R.id.btnManagerSetInterval:
                this.setSampleTimeDialog();
                return;
            case R.id.btnManagerSetMeasurement:
                this.setMeasurementTimeDialog();
                return;
          //  case R.id.btnManagerGetInterval:
          //      operation = KdUINOOperations.READ_INTERVAL;
          //      break;

        }

        bus.post(new KdUinoOperationMessageBusEvent<String>(operation, ""));
    }

    private void waitDialog()
    {
        waitInfoDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_wait_title)
                .content(R.string.dialog_wait_message)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .progress(true, 0)
                .build();
        waitInfoDialog.show();
    }

    private void confirmDeleteDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_delete_title)
                .content(R.string.dialog_delete_message)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        bus.post(new KdUinoOperationMessageBusEvent<String>(KdUINOOperations.DELETE_DATA, ""));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();
        dialog.show();

    }

    private void setMeasurementTimeDialog()
    {

        io.bega.kduino.datamodel.Status statusData =
                ((kdUINOApplication)(this.getActivity()).getApplication()).getStatus();
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_set_measurement_time_title)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .customView(R.layout.set_measurement_time_dialog, false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        DiscreteSeekBar discreteSeekBar =
                                (DiscreteSeekBar) view.findViewById(R.id.measurement_time_dsb);
                        int interval = discreteSeekBar.getProgress();
                        bus.post(new KdUinoOperationMessageBusEvent<String>(KdUINOOperations.SET_MEASUREMENT,
                                Integer.toString(interval)));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();

        View view = dialog.getCustomView();
        DiscreteSeekBar discreteSeekBar =
                (DiscreteSeekBar)view.findViewById(R.id.measurement_time_dsb);
        discreteSeekBar.setMin(1);
        discreteSeekBar.setMax(59);
        discreteSeekBar.setProgress(statusData.MeasurementTime);
        dialog.show();

    }

    private void setSampleTimeDialog()
    {
        io.bega.kduino.datamodel.Status statusData =
                ((kdUINOApplication)(this.getActivity()).getApplication()).getStatus();

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_set_sample_time_title)
                .customView(R.layout.set_sample_time_dialog, false)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        DiscreteSeekBar discreteSeekBar =
                                (DiscreteSeekBar) view.findViewById(R.id.sample_time_dsb);
                        int interval = discreteSeekBar.getProgress();

                        bus.post(new KdUinoOperationMessageBusEvent<String>(KdUINOOperations.SET_SAMPLE,
                                Integer.toString(interval)));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();

        View view = dialog.getCustomView();
        DiscreteSeekBar discreteSeekBar =
                (DiscreteSeekBar)view.findViewById(R.id.sample_time_dsb);
        discreteSeekBar.setMin(1);
        discreteSeekBar.setMax(59);

       /* int sampleTime = 0;
        if (statusData.SampleTime > 1000)
        {
            sampleTime = statusData.SampleTime / 1000;
        } */

        discreteSeekBar.setProgress(statusData.SampleTime);
        dialog.show();

    }

    @Override
    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
        String data = charSequence.toString();
        KdUINOOperations operation = KdUINOOperations.SET_SAMPLE;
        bus.post(new KdUinoOperationMessageBusEvent<String>(operation, data));
    }



    private void bindData(String buoyID, String macAdress)
    {
       // editMacAdress.setText(macAdress);
        long id = Long.parseLong(buoyID);
        if (id > 0) {
            buoy = KDUINOBuoy.findById(KDUINOBuoy.class, id);
            /* if (buoy != null) {
               // editBuoyMarker.setText(buoy.Maker);
                editBuoyName.setText(buoy.Name);
               // editBuoyUser.setText(buoy.User);
            } */

            // bus.post(new KdUinoMessageBusEvent(KdUINOMessages.BLUETOOTH_CONNECT_TO_KDUINO, buoy));
        }
        else
        {
            // btnSaveSensor.setVisibility(View.INVISIBLE);
        }




    }



}
