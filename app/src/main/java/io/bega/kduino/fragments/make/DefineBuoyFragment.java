package io.bega.kduino.fragments.make;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.osmdroid.util.GeoPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.BuoyPostDefinition;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.SensorBuoyAdapter;
import io.bega.kduino.datamodel.SensorType;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoOperationMessageBusEvent;
import io.bega.kduino.fragments.SensorDialogFragment;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link DefineBuoyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefineBuoyFragment extends Fragment implements IClickable,
        AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BUOY_ID = "buoy_id";
    private static final String MAC_ADRESS = "mac_adress";


    // TODO: Rename and change types of parameters
    private String mBuoyId;

    private String mMacAdress;

    private EditText editBuoyName;

    private EditText editBuoyMarker;

    private EditText editBuoyUser;

    private Spinner spinnerNumberOfSensors;

    private TextView txtMacAdress;

    private Button btnSaveSensor;

    private Bus bus;

    private int sensor_number;

    private KDUINOBuoy buoy;

    private KdUINOAPIService service;

    List<Sensor> sensorList;

    AddFloatingActionButton btn_add_buoy;

    Integer[] items = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    ListView listView;

    SensorBuoyAdapter sensorArrayAdapter;

    View view;

    GeoPoint geoPoint;

    LinearLayout rootLayout;

    int UserID = 0;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param buoyID The buoyID.
     * @return A new instance of fragment RegisterBuoyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DefineBuoyFragment newInstance(String buoyID) {
        DefineBuoyFragment fragment = new DefineBuoyFragment();
        Bundle args = new Bundle();
        args.putString(BUOY_ID, buoyID);
        fragment.setArguments(args);
        return fragment;
    }

    public DefineBuoyFragment() {
        // Required empty public constructor
    }

    @Subscribe
    public void getMessage(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
        if (buoy != null) {
            buoy.setLatLong(geoPoint);
            buoy.save();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuoyId = getArguments().getString(BUOY_ID);
        }

        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
        this.service = ((kdUINOApplication) getActivity().getApplication()).getAPIService();


    }

    boolean isOpened = false;

    public void setListenerToRootView(){
        final View activityRootView = this.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.


                    if (isOpened == false) {

                        btn_add_buoy.setVisibility(View.GONE);
                        rootLayout.setVisibility(View.GONE);


                        //Do two things, make the view top visible and the editText smaller
                    }
                    isOpened = true;
                } else if (isOpened == true) {
                    btn_add_buoy.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.VISIBLE);
                    isOpened = false;

                }
            }
        });
    }


    @Override
    public void onStart() {
        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
        this.bindData(mBuoyId);
        super.onStart();
        this.bus.register(this);
        //this.setListenerToRootView();
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
        view = inflater.inflate(R.layout.fragment_define_buoy, container, false);
        rootLayout = (LinearLayout)view.findViewById(R.id.fragment_define_buoy_number_sensors_layout);
        editBuoyName = (EditText) view.findViewById(R.id.register_buoy_edit_name);
        editBuoyMarker = (EditText) view.findViewById(R.id.register_buoy_edit_name_maker);
        editBuoyUser = (EditText) view.findViewById(R.id.register_buoy_edit_name_user);
        spinnerNumberOfSensors = (Spinner) view.findViewById(R.id.register_buoy_spinner_number_of_sensors);
        this.btn_add_buoy = (AddFloatingActionButton)view.findViewById(R.id.action_button_save_kduino);
       // this.getActivity().setTitle(getString(R.string.main_menu_make_a_device));
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, items);
        spinnerNumberOfSensors.setAdapter(adapter);
        spinnerNumberOfSensors.setOnItemSelectedListener(this);
        this.sensorList = new ArrayList<Sensor>();
        sensorArrayAdapter = new SensorBuoyAdapter(
                getActivity(),
                this.sensorList,
                R.layout.sensors_definition_row,
                false,
                0);

        this.listView = (ListView) view.findViewById(R.id.register_list_sensors);
        this.listView.setAdapter(sensorArrayAdapter);
        this.listView.setOnItemClickListener(this);
        this.btn_add_buoy = (AddFloatingActionButton) view.findViewById(R.id.action_button_save_kduino);
        this.btn_add_buoy.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void bindData(String buoyID) {
        long id = Long.parseLong(buoyID);
        if (id > 0) {
            //   btnSaveSensor.setVisibility(View.VISIBLE);
            buoy = KDUINOBuoy.findById(KDUINOBuoy.class, id);
            if (buoy != null) {
                editBuoyMarker.setText(buoy.Maker);
                editBuoyName.setText(buoy.Name);
                editBuoyUser.setText(buoy.User);
                spinnerNumberOfSensors.setOnItemSelectedListener(null);
                spinnerNumberOfSensors.setSelection(buoy.Sensors);
                spinnerNumberOfSensors.setOnItemSelectedListener(this);
                List<Sensor> sensorList = Sensor.find(Sensor.class, "buoy_id=?", buoy.getId().toString());
                this.sensorList.addAll(sensorList);
            }
        } else {
            this.btn_add_buoy.setEnabled(false);
        }

        SettingsManager manager = new SettingsManager(getActivity());
        this.UserID = manager.getUserId();
    }

    public void confirmDialog()
    {
        if (buoy.Lat == 0 || buoy.Lon == 0)
        {
            /* DisplayUtilities.ShowLargeMessage(
                    "Latitude and Longuitud not set in the kduino definition.",
                    "KO",
                    view, false, null); */
            getActivity().onBackPressed();
            return;
        }

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Store kduino Model in the Database")
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        /* if (buoy.KdUINOID.equals("0"))
                        {
                            buoy.KdUINOID = UserID + String.format("%03d", buoy.getId());
                            buoy.save();
                        } */

                        Gson gson = new Gson();
                        BuoyPostDefinition postBuoy = new BuoyPostDefinition(buoy);
                        String test = gson.toJson(postBuoy);

                        service.createNewBuoy(postBuoy, new Callback<String>() {
                            @Override
                            public void success(String s, Response response) {
                                DisplayUtilities.ShowLargeMessage("Kduino Model Save", "OK", view, false, null);
                                getActivity().onBackPressed();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                DisplayUtilities.ShowLargeMessage("Error saving Kduino Model", "OK", view, false, null);
                            }
                        });

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();

        // View view = dialog.getCustomView();
        dialog.show();
    }


    @Override
    public void clickView(View v) {
        switch (v.getId()) {
            /* case R.id.register_buoy_btn_add_sensor: {
                this.showEditDialog(buoy.getId().toString(), "0");
                break;
            } */

            /*case R.id.register_buoy_btn_save: {
                saveBuoyEntity();
                break;
            } */
        }
    }

    private void saveBuoyEntity() {
        String name = editBuoyName.getText().toString().trim();
        if (name.length() == 0)
        {
            editBuoyName.setError("Buoy name is required!" );
            editBuoyName.setHint("please enter buoy name");
            return;
        }

        String maker = editBuoyMarker.getText().toString().trim();
        if (maker.length() == 0)
        {
            editBuoyMarker.setError("Maker name is required!" );
            editBuoyMarker.setHint("please enter maker name");
            return;
        }

        String user = editBuoyUser.getText().toString().trim();
        if (user.length() == 0)
        {
            editBuoyUser.setError("User name is required!");
            editBuoyUser.setHint("please enter user name");
            return;
        }


        if (buoy == null) {
            // String name, String marker, String user, String macAdress
            buoy = new KDUINOBuoy(
                    "0",
                    name,
                    maker,
                    user,
                    "",
                    0,
                    0,
                    sensor_number);
        }
        else
        {
            buoy.Name = name;
            buoy.Maker = maker;
            buoy.User = user;
            buoy.Sensors = sensor_number;
        }

        if (geoPoint != null)
        {
            buoy.setLatLong(geoPoint);
        }

        try {

            double lastvalue = 0;
            // first check
            /*for (int i = 0; i < sensor_number; i++) {
                Sensor sensor = this.sensorList.get(i);

                if (sensor.Deep < lastvalue)
                {
                    DisplayUtilities.ShowLargeMessage("There is a deep value not correct. Check deep values", "OK", view, false, null);
                    return;
                }
            }*/


            buoy.save();
            buoy.KduinoID = UserID + String.format("%03d", buoy.getId());
            buoy.save();

          // Sensor.deleteAll(Sensor.class, "buoy_id=?", buoy.getId().toString());

            if (buoy.getSensors().size() == 0)
            {
                int number = 1;
                for (int i = 0; i < sensor_number; i++) {
                    Sensor sensor = this.sensorList.get(i);
                    sensor.BuoyID = buoy;
                    sensor.SensorID = UserID + String.format("%03d", buoy.getId()) + String.format("%02d", number);
                    sensor.save();
                    number++;
                }
            }
            else
            {
                for (Sensor sensor : buoy.getSensors())
                {
                    sensor.save();
                }
            }

            this.confirmDialog();

        } catch (Exception ex) {
            Log.e(kdUINOApplication.TAG, "Error saving kduino model", ex);
            DisplayUtilities.ShowLargeMessage("Error saving Kduino Model", "OK", view, false, null);
        }
    }

    private void showEditDialog(String buoyId, String sensorID) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        SensorDialogFragment sensorDialogFragment = SensorDialogFragment.newInstance(buoyId, sensorID);
        sensorDialogFragment.show(fm, "sensor fragment");
        //editNameDialog.show(fm, "fragment_edit_name");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sensor_number = items[position];

        if (sensor_number > 0 && sensor_number != this.sensorList.size()) {
            this.sensorList.clear();
            this.btn_add_buoy.setEnabled(true);
            int topnumber = sensor_number + 1;
            for (int i = 1; i < topnumber; i++) {
                Sensor sensor = new Sensor(buoy, i + "", 0, SensorType.Light, true);
                sensor.SensorID = i + "";
                this.sensorList.add(sensor);
            }

            this.sensorArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_button_save_kduino: {
                this.saveBuoyEntity();
                break;
            }

            case R.id.action_button_command_map_position:
                bus.post(
                        new KdUinoMessageBusEvent(

                        KdUINOMessages.POSITION_KDUINO, null));
                break;
            case R.id.action_button_command_send_kduino:

                bus.post(
                        new KdUinoMessageBusEvent(
                        KdUINOMessages.SEND_COMMAND_KDUINO,
                                null));
                break;
          /*  case R.id.action_button_command_download_data:
                bus.post(KdUINOMessages.SEND_COMMAND_DATA_KDUINO);
                break; */
        }
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

                        if (deep.length() == 0)
                        {
                            return;
                        }

                        if (deep.endsWith(".")) {
                            deep = deep.replace(".", "");
                        }

                        if (deep.endsWith(",")) {
                            deep = deep.replace(",", "");
                        }

                        deep = deep.replace(",", ".");
                        double value = Double.parseDouble(deep);
                        if (value > 40) {
                            value = 40;
                        }

                        sensorValue.Deep = value;
                        sensorArrayAdapter.notifyDataSetChanged();
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sensor sensor = this.sensorList.get(position);
        this.setDeepDialog(sensor);
    }
}
