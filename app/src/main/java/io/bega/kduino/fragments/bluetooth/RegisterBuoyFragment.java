package io.bega.kduino.fragments.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.SensorBuoyAdapter;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.fragments.MapDialogFragment;
import io.bega.kduino.kdUINOApplication;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link RegisterBuoyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterBuoyFragment extends Fragment implements IClickable, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BUOY_ID = "buoy_id";
    private static final String MAC_ADRESS = "mac_adress";

    // TODO: Rename and change types of parameters
    private String mBuoyId;

    private String mMacAdress;

    private EditText editBuoyName;

    private EditText editBuoyMarker;

    private EditText editBuoyUser;

    private EditText editNumberOfSensors;

    private TextView txtMacAdress;

    private TextView txtLatitud;

    private TextView txtLonguitud;

    private Button btnSaveSensor;

    private FloatingActionButton actionButtonSendCommand;

    private FloatingActionButton actionButtonSetPosition;

    Integer[] items = new Integer[]{ 0, 1,2,3,4, 5, 6, 7, 8, 9, 10, 11, 12 };

    private Spinner spinner;

    private Bus bus;

    private BuoyDefinition buoy;

    List<Sensor> listSensorsData;

    private ListView listSensors;

    IClickable clickableFragment;

    SensorBuoyAdapter sensorBuoyAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param buoyID The buoyID.
     * @param macAdress the Mac Adress of the buoy to configure
     * @return A new instance of fragment RegisterBuoyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterBuoyFragment newInstance(String buoyID, String macAdress) {
        RegisterBuoyFragment fragment = new RegisterBuoyFragment();
        Bundle args = new Bundle();
        args.putString(BUOY_ID, buoyID);
        args.putString(MAC_ADRESS, macAdress);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterBuoyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuoyId = getArguments().getString(BUOY_ID);
            mMacAdress = getArguments().getString(MAC_ADRESS);
        }

        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();


    }

    @Subscribe
    public void getMessage(GeoPoint geoPoint)
    {
        GeoPoint selectedPoint = geoPoint;
        this.txtLatitud.setText(selectedPoint.getLatitude() + "");
        this.txtLonguitud.setText(selectedPoint.getLongitude() + "");
    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent<KDUINOBuoy> busEvent)
    {
        KDUINOBuoy buoyModel = busEvent.getData();
        this.editBuoyMarker.setText(buoyModel.Maker);
        this.editBuoyName.setText(buoyModel.Name);
        this.editBuoyUser.setText(buoyModel.User);

        listSensorsData.clear();
        List<Sensor> sensorList = Sensor.find(
                Sensor.class, "buoy_id=?", Double.toString(buoyModel.getId()));
        listSensorsData.addAll(sensorList);
        sensorBuoyAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        this.bus.register(this);
        this.launchSelectionDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_buoy, container, false);
        editBuoyName = (EditText)view.findViewById(R.id.register_buoy_edit_name);
        editBuoyMarker = (EditText)view.findViewById(R.id.register_buoy_edit_name_maker);
        editBuoyUser = (EditText)view.findViewById(R.id.register_buoy_edit_name_user);
        txtMacAdress = (TextView)view.findViewById(R.id.register_buoy_txt_mac_value);

        actionButtonSetPosition = (FloatingActionButton)view.findViewById(R.id.action_button_command_map_position);
        // actionButtonGetData = (FloatingActionButton)view.findViewById(R.id.action_button_command_download_data);
        actionButtonSendCommand = (FloatingActionButton)view.findViewById(R.id.action_button_command_send_kduino);
        // this.actionButtonGetData.setOnClickListener(this);
        this.spinner = (Spinner)view.findViewById(R.id.register_buoy_spinner_number_of_sensors);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(),android.R.layout.simple_spinner_item, items);
        this.spinner.setAdapter(adapter);
        this.actionButtonSendCommand.setOnClickListener(this);
        this.actionButtonSetPosition.setOnClickListener(this);
        this.listSensors = (ListView)view.findViewById(R.id.register_list_sensors);
        this.listSensorsData = new ArrayList<Sensor>();
        sensorBuoyAdapter =
                new SensorBuoyAdapter(
                        this.getActivity(),
                        listSensorsData,
                        R.layout.sensors_definition_row, false, 0);
        this.listSensors.setAdapter(sensorBuoyAdapter);
        return view;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(getString(R.string.bluetooth_search_device_title));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void launchMapDialog()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        MapDialogFragment mapDialogFragment = MapDialogFragment.newInstance();
        clickableFragment = mapDialogFragment;
        mapDialogFragment.show(fm, "map fragment");
    }


    private void launchSelectionDialog()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        KdUINOModelsDialogFragment sensorDialogFragment = KdUINOModelsDialogFragment.newInstance();

        sensorDialogFragment.show(fm, "sensor fragment");
    }


   /* private void bindData(String buoyID, String macAdress)
    {
        txtMacAdress.setText(macAdress);
        long id = Long.parseLong(buoyID);
        if (id > 0) {
            btnSaveSensor.setVisibility(View.VISIBLE);
            buoy = KDUINOBuoy.findById(KDUINOBuoy.class, id);
            if (buoy != null) {
                editBuoyMarker.setText(buoy.Maker);
                editBuoyName.setText(buoy.Name);
                editBuoyUser.setText(buoy.User);
            }

            }
        else
        {
            btnSaveSensor.setVisibility(View.INVISIBLE);
        }
    } */


    @Override
    public void clickView(View v) {
        switch (v.getId())
        {
        }
    }


   /* private void saveBuoyEntity() {
        if (buoy == null)
        {
            // String name, String marker, String user, String macAdress
            buoy = new Buoy(editBuoyName.getText().toString(),
                    editBuoyMarker.getText().toString(),
                    editBuoyUser.getText().toString(),
                    txtMacAdress.getText().toString(),
                    0, 0)

            ;

        }
        else
        {
            buoy.Name = editBuoyName.getText().toString();
            buoy.Maker = editBuoyMarker.getText().toString();
            buoy.User = editBuoyUser.getText().toString();

        }

        buoy.save();
    } */


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.action_button_command_map_position:
                bus.post(
                        new KdUinoMessageBusEvent(
                        KdUINOMessages.POSITION_KDUINO, null)
                );
                break;
            case R.id.action_button_command_send_kduino:
                bus.post(
                        new KdUinoMessageBusEvent(
                        KdUINOMessages.SEND_COMMAND_KDUINO,
                                null));
                break;
        }
    }
}
