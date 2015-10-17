package io.bega.kduino.fragments.bluetooth;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.otto.Bus;

import java.util.List;

import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.BuoyAdapter;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.kdUINOApplication;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KdUINOModelsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KdUINOModelsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BUOY_ID = "buoyID";

    private static final String SENSOR_ID = "sensorID";

    private String buoyID;

    private String sensorID;

    private Bus bus;

    ListView listView;

    List<KDUINOBuoy> list;

    BuoyAdapter listAdapter;


    BuoyDefinition buoy;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment KdUINOModelsDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KdUINOModelsDialogFragment newInstance() {
        KdUINOModelsDialogFragment fragment = new KdUINOModelsDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public KdUINOModelsDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


        }

        // set the bus
        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_kduino_model_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.listView = (ListView)view.findViewById(R.id.fragment_kduino_model_listview);
        return view;
    }

    @Override
    public void onStart() {
        this.bindData();
        super.onStart();
    }

    private void bindData()
    {
        this.list =  KDUINOBuoy.listAll(KDUINOBuoy.class);
        this.listAdapter = new BuoyAdapter(
                this.getActivity(),
                list,
                R.layout.kduino_row);
        this.listView.setOnItemClickListener(this);
        this.listView.setAdapter(listAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        KDUINOBuoy buoy = this.list.get(position);

        this.bus.post(new KdUinoMessageBusEvent<KDUINOBuoy>(KdUINOMessages.CONNECT_KDUINO, buoy));
        dismiss();
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
