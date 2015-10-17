package io.bega.kduino.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.squareup.otto.Bus;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.SensorType;
import io.bega.kduino.kdUINOApplication;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorDialogFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BUOY_ID = "buoyID";

    private static final String SENSOR_ID = "sensorID";

    private String buoyID;

    private String sensorID;

    private Bus bus;

    Spinner spinner_sensor_type;

    EditText sensor_deep;

    EditText sensor_description;

    KDUINOBuoy buoy;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param buoyID The ID Of the buoy associate
     * @return A new instance of fragment SensorDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorDialogFragment newInstance(String buoyID, String sensorID) {
        SensorDialogFragment fragment = new SensorDialogFragment();
        Bundle args = new Bundle();
        args.putString(BUOY_ID, buoyID);
        args.putString(SENSOR_ID, sensorID);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buoyID = getArguments().getString(BUOY_ID);
            long id = Long.parseLong(buoyID);
            buoy = KDUINOBuoy.findById(KDUINOBuoy.class, id);
        }

        // set the bus
        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sensor_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.spinner_sensor_type = (Spinner)view.findViewById(R.id.sensor_dialog_spinner_sensor_type);
        this.sensor_deep = (EditText)view.findViewById(R.id.sensor_dialog_edit_deep);
        this.sensor_description = (EditText)view.findViewById(R.id.sensor_dialog_edit_description);
        this.spinner_sensor_type.setAdapter(new ArrayAdapter<SensorType>(getActivity(), android.R.layout.simple_list_item_1, SensorType.values()));
        Button button =  (Button)view.findViewById(R.id.btnNewSensor);
        //button.setOnClickListener(this);

        return view;
    }


    /* @Override
    public void clickView(View v) {
        if (v.getId() == R.id.btnNewSensor) {

            String description = this.sensor_description.getText().toString();
            double deep = Double.parseDouble(this.sensor_deep.getText().toString());
            SensorType sensorType = (SensorType)this.spinner_sensor_type.getSelectedItem();
            Sensor sensor = new Sensor(buoy, buoyID + "1",  description, deep, sensorType);
            sensor.save();
        }
    } */
}
