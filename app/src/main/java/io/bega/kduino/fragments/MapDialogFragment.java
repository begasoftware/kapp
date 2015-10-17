package io.bega.kduino.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.squareup.otto.Bus;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.LocationService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapDialogFragment extends DialogFragment
        implements LocationService.newBestLocationListener, IClickable
         {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BUOY_ID = "buoyID";

    private static final String SENSOR_ID = "sensorID";

    private String buoyID;

    private String sensorID;

    private Bus bus;

    MapView map;

    BuoyDefinition buoy;

    LocationService locationService;

    Button btnSavePosition;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SensorDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapDialogFragment newInstance() {
        MapDialogFragment fragment = new MapDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MapDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the bus
        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.map = (MapView)view.findViewById(R.id.map);
        map.setTileSource(new XYTileSource("MapQuest",
                ResourceProxy.string.mapquest_osm, 0, 18, 256, ".jpg", new String[] {
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));
        // map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMaxZoomLevel(17);
        map.setMultiTouchControls(true);
        //map.setUseDataConnection(false);

        return view;
    }

    @Override
    public void onStart() {


        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        this.locationService = new LocationService(activity);
        this.locationService.setNewBestLocationListener(this);
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.locationService = null;
        // mListener = null;
    }

    @Override
    public void newBestLocation(Location currentBestLocation) {

        this.map.getController().setCenter( new GeoPoint(
                currentBestLocation.getLatitude(),
                currentBestLocation.getLongitude()));

    }


             @Override
             public void clickView(View v) {
                 if (v.getId() == R.id.btn_selectPosition_Map)
                 {
                     bus.post( new GeoPoint(
                             this.map.getMapCenter().getLatitude(),
                             this.map.getMapCenter().getLongitude()
                     ));
                     dismiss();
                 }
             }

}
