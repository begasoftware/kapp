package io.bega.kduino.fragments.bluetooth;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.SensorBuoyAdapter;
import io.bega.kduino.datamodel.SensorType;
import io.bega.kduino.datamodel.Status;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorTabFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUOYID = "buoyID";

    // TODO: Rename and change types of parameters
    private String mBuoyID;

    Bus bus;

    List<Sensor> listSensorsData = new ArrayList<Sensor>();

    private ListView listSensors;

    SensorBuoyAdapter sensorBuoyAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param buoyID Parameter 1.
     * @return A new instance of fragment SensorTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorTabFragment newInstance(String buoyID) {
        SensorTabFragment fragment = new SensorTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUOYID, buoyID);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorTabFragment() {
        // Required empty public constructor
    }

    public void refreshList()
    {
        if (this.listSensorsData.size() > 0)
        {
            this.sensorBuoyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.listSensorsData.size() > 0)
        {
            this.sensorBuoyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.bus.register(this);

       // this.bindData(mBuoyID);
    }

    @Override
    public void onStop() {
        this.bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuoyID = getArguments().getString(ARG_BUOYID);
        }

        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_tab, container, false);

        this.listSensors = (ListView)view.findViewById(R.id.sensor_list);
        this.sensorBuoyAdapter = new SensorBuoyAdapter(this.getActivity(),
                listSensorsData,
                R.layout.sensors_definition_row,
                true,
                0
        );

        this.listSensors.setOnItemClickListener(this);
        this.listSensors.setAdapter(sensorBuoyAdapter);
        return view;
    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent message)
    {
        if (message.getMessage() == KdUINOMessages.RECIEVE_INFO)
        {

            Status status = (Status)message.getData();
            SettingsManager manager = new SettingsManager(getActivity());
            KDUINOBuoy buoy = KDUINOBuoy.findById(KDUINOBuoy.class, Long.parseLong(mBuoyID));
            buoy.deleteSensors();
            if (status.SensorNumber != 0) {


                if (buoy.getSensors().size() != status.SensorNumber)
                {
                    int counter = 1;
                    int max = 0;
                    for (int deep : status.SensorList) {
                        Sensor sensor = new Sensor(buoy,
                                String.format("%02d", manager.getUserId())
                                        + String.format("%03d", buoy.getId()) +
                                        String.format("%02d", counter),
                                deep  / 100,
                                SensorType.Light,
                                true);
                        sensor.save();
                        max = Math.max(max, deep);
                        counter++;
                    }


                }
                else
                {
                    List<Sensor> sensors = buoy.getSensors();

                    for (Sensor sensor : sensors)
                    {
                        int index = Integer.parseInt(sensor.SensorID.substring(sensor.SensorID.length() - 2));

                        int indexes = 1;
                        for (int deep : status.SensorList)
                        {
                            if (indexes == index)
                            {
                                if (sensor.Deep != deep)
                                {
                                    sensor.Deep = deep / 100;
                                    sensor.save();
                                }

                                break;
                            }

                            indexes++;
                        }
                    }

                   // sensorBuoyAdapter.refreshMaxDeep();
                }

                this.bindData(buoy);

            }
        }
    }

    public void bindData(KDUINOBuoy buoy)
    {
        if (listSensorsData.size() > 0)
        {
            return;
        }

        List<Sensor> sensorList = buoy.getSensors();
        if (sensorList.size() == 0)
        {
            return;
        }

        listSensorsData.addAll(sensorList);
        sensorBuoyAdapter.refreshMaxDeep();
       // sensorBuoyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sensor sensor = this.listSensorsData.get(position);
        this.setDeepDialog(sensor);
    }

    private void setDeepDialog(Sensor sensor)
    {
        final Sensor sensorValue = sensor;
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_set_sample_deep_title)
                .customView(R.layout.set_deep_dialog, false)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        MaterialDialog materialDialog = (MaterialDialog) dialog;
                        View view = materialDialog.getView();
                        EditText editDeep =
                                (EditText) view.findViewById(R.id.sensor_definition_deep_value);
                        editDeep.selectAll();

                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        try {

                            inputMethodManager.showSoftInput(editDeep, InputMethodManager.SHOW_IMPLICIT);
                            // inputMethodManager.toggleSoftInputFromWindow(editDeep, InputMethodManager.SHOW_FORCED, 0);
                        } catch (Exception ex) {
                            String p = ex.getMessage();
                        }
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MaterialDialog materialDialog = (MaterialDialog) dialog;
                        View view = materialDialog.getView();
                        EditText editDeep =
                                (EditText) view.findViewById(R.id.sensor_definition_deep_value);
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        //inputMethodManager.hideSoftInputFromWindow(editDeep.getWindowToken(), 0);
                        //showSoftInput(editDeep, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        EditText editDeep =
                                (EditText) view.findViewById(R.id.sensor_definition_deep_value);
                        String deep = editDeep.getText().toString();

                        if (deep.length() == 0) {
                            return;
                        }

                        if (deep.endsWith(".")) {
                            deep = deep.replace(".", "");
                        }

                        if (deep.endsWith(",")) {
                            deep = deep.replace(",", "");
                        }

                        deep = deep.replace(",", ".");
                        float value = Float.parseFloat(deep);
                        /*if (value > 40) {
                            value = 40;
                        }*/

                        // sensorBuoyAdapter.setMaxDeep(value);
                        sensorValue.Deep = value;
                        sensorBuoyAdapter.refreshMaxDeep();
                        //sensorBuoyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();

        View view = dialog.getCustomView();
        EditText editText =
                (EditText)view.findViewById(R.id.sensor_definition_deep_value);

        if (Double.isNaN(sensor.Deep))
        {
            sensor.Deep = 0d;
        }

        if (sensor.Deep != 0) {
            DecimalFormat form = new DecimalFormat("0.00");
            editText.setText(form.format(sensor.Deep));
        }
        else {
            editText.setHint("0.00");
        }

        dialog.show();

    }


}
