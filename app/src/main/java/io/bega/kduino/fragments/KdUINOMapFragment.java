package io.bega.kduino.fragments;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Bus;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Iterator;

import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOMapAPIService;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.KdUINOOperations;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.events.KdUinoOperationMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.maps.LocationOverlayItem;
import io.bega.kduino.services.ConnectivityService;
import io.bega.kduino.services.LocationService;


public class KdUINOMapFragment extends Fragment implements LocationService.newBestLocationListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Bus bus;

    DefaultResourceProxyImpl mResourceProxy;

    ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

    ArrayList<LocationOverlayItem> centerPoints = new ArrayList<LocationOverlayItem>();

    ItemizedIconOverlay itemizedIconOverlay;

    ItemizedIconOverlay<LocationOverlayItem> centerIconOverlay;

    MapView map;

    boolean isFirstPosition = true;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocationService locationService;

    GeoPoint lastBestPosition = null;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KdUINOMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KdUINOMapFragment newInstance(String param1, String param2) {
        KdUINOMapFragment fragment = new KdUINOMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public KdUINOMapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_kd_uinomap, container, false);
        map  = (MapView) view.findViewById(R.id.map);
        map.setTileSource(new XYTileSource("MapQuest",
                ResourceProxy.string.mapquest_osm, 0, 18, 256, ".jpg", new String[] {
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));

        // map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMaxZoomLevel(14);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        FloatingActionsMenu menu = (FloatingActionsMenu)view.findViewById(R.id.multiple_actions);
        menu.expand();
        // map.setUseDataConnection(false);

        final FloatingActionButton actionAllBuoy = (FloatingActionButton) view.findViewById(R.id.action_button_show_all_buoy);
        actionAllBuoy.setOnClickListener(this);
        final FloatingActionButton actionMyBuoy = (FloatingActionButton) view.findViewById(R.id.action_button_show_my_buoy);
        actionMyBuoy.setOnClickListener(this);
        final FloatingActionButton actionPosition = (FloatingActionButton) view.findViewById(R.id.action_button_get_my_position);
        actionPosition.setOnClickListener(this);

        IMapController mapController = map.getController();
        mapController.setZoom(8);

        return view;
    }

    @Override
    public void onStart()
    {
        this.bus = ((kdUINOApplication)getActivity().getApplication()).getBus();
        this.mResourceProxy = new DefaultResourceProxyImpl(getActivity().getApplicationContext());
        this.locationService.StartListen();
        super.onStart();
        if (ConnectivityService.isOnline(getActivity())) {
            this.bindData("");
        }
        else {
            showInternetDialog();
        }



    }

    private void bindData(String id)
    {
        KdUINOMapAPIService service = new KdUINOMapAPIService(this.getActivity(),
                this.map,  this.mResourceProxy, this.items, this.itemizedIconOverlay, this.centerPoints, this.centerIconOverlay);
        service.getAllBuoys(id);
    }

    @Override
    public void onDestroy() {
        this.map.getTileProvider().clearTileCache();
        System.gc();
        this.bus = null;
        super.onDestroy();
    }

    @Override
    public void onStop()
    {
        this.mResourceProxy = null;
        this.locationService.StopListen();
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

        this.lastBestPosition = new GeoPoint(
                currentBestLocation.getLatitude(),
                currentBestLocation.getLongitude());
        if (isFirstPosition)
        {
            this.map.getController().setCenter(this.lastBestPosition);
            isFirstPosition = false;
        }

        KdUINOMapAPIService service = new KdUINOMapAPIService(this.getActivity(),
                this.map,  this.mResourceProxy, this.items, this.itemizedIconOverlay, this.centerPoints, this.centerIconOverlay);
        service.setCenter(this.lastBestPosition);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.action_button_show_all_buoy:

                this.bindData("");

                Log.d(kdUINOApplication.TAG, "Make a kaduino");
                break;

            case R.id.action_button_show_my_buoy:
                SettingsManager manager = new SettingsManager(getActivity());
                if (manager.getUserId() == 0)
                {
                    String userid = "0";
                    Iterator<User> users = User.findAll(User.class);
                    if (users.hasNext())
                    {
                        User user = users.next();
                        userid = user.UserId;
                        this.bindData(userid);
                        return;
                    }
                }





                String id = Integer.toString(manager.getUserId());

                this.bindData(id);

                // TODO to implement

                break;

            case R.id.action_button_get_my_position:

                if (this.lastBestPosition != null) {
                    this.map.getController().setZoom(12);
                    this.map.getController().setCenter(this.lastBestPosition);
                }
                break;


        }
    }

    private void showInternetDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.internet_alert_title)
                .content(R.string.internet_alert_message)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
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
