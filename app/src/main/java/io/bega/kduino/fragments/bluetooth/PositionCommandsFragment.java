package io.bega.kduino.fragments.bluetooth;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Bus;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.GroundOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import java.util.ArrayList;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.LocationService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PositionCommandsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PositionCommandsFragment extends Fragment
        implements LocationService.newBestLocationListener, IClickable, View.OnClickListener, MapEventsReceiver {

    private static final String ARG_BUOYID = "buoyID";

    private String mBuoyID;

    GeoPoint bestPoint;

    private MapView map;

    KDUINOBuoy buoy;

    private Bus bus;

    private LocationService locationService;

    private SimpleLocationOverlay locationOverlay;

    private GeoPoint setSelectedPoint;

    private GeoPoint currentLocationPoint;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param buoyID Parameter 1.
     * @return A new instance of fragment TestCommandsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PositionCommandsFragment newInstance(String buoyID) {
        PositionCommandsFragment fragment = new PositionCommandsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUOYID, buoyID);
        fragment.setArguments(args);
        return fragment;
    }

    public PositionCommandsFragment() {
        // Required empty public constructor
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
    public void onStart() {
        super.onStart();
        this.locationService.StartListen();
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
        map.getOverlays().add(0, mapEventsOverlay);
        setKduinoModel();
    }

    private void setKduinoModel() {
        long id = Long.parseLong(mBuoyID);
        if (id > 0) {
            //   btnSaveSensor.setVisibility(View.VISIBLE);
            buoy = KDUINOBuoy.findById(KDUINOBuoy.class, id);
            if (buoy.Lat != 0 && buoy.Lon != 0)
            {
                GeoPoint geoPoint = new GeoPoint(buoy.Lat, buoy.Lon, 0);
                this.setSelectedPoint = geoPoint;
                this.setPoint(geoPoint, true);
                // map.getController().setCenter(geoPoint);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.locationService.StopListen();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_commands, container, false);
        this.map = (MapView)view.findViewById(R.id.map);
        this.map.setTileSource(new XYTileSource("MapQuest",
                ResourceProxy.string.mapquest_osm, 0, 18, 256, ".jpg", new String[] {
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));


        //map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
      //  map.setUseDataConnection(false);
        map.setMaxZoomLevel(16);
        IMapController mapController = map.getController();
        mapController.setZoom(9);

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.action_button_setLocation);
        floatingActionButton.setOnClickListener(this);
        FloatingActionButton floatingActionButtonBuoyLocation = (FloatingActionButton)view.findViewById(R.id.action_button_search_location_buoy);
        floatingActionButtonBuoyLocation.setOnClickListener(this);
        return view;
    }

    @Override
    public void newBestLocation(Location currentBestLocation) {
        GeoPoint point = new GeoPoint(
                currentBestLocation.getLatitude(),
                currentBestLocation.getLongitude());

        // fix problem with the set center controller
        /* if (this.bestPoint == null)
        {
            this.map.getController().setCenter(point);
        } */

        this.bestPoint = point;

        this.setPoint(point, false);
        this.currentLocationPoint = point;


    }


    @Override
    public void clickView(View v) {
        if (v.getId() == R.id.btn_selectPosition_Map)
        {
            bus.post(new GeoPoint(
                    this.map.getMapCenter().getLatitude(),
                    this.map.getMapCenter().getLongitude()
            ));
        }
        else if (v.getId() == R.id.action_button_search_location_buoy)
        {
            if (this.setSelectedPoint != null)
            {
                this.map.getController().setCenter(this.setSelectedPoint);
            }
        }
    }


    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.action_button_setLocation)
        {
            if (this.bestPoint != null) {
                map.getController().setCenter(this.bestPoint);
                bus.post(new GeoPoint(
                        this.map.getMapCenter().getLatitude(),
                        this.map.getMapCenter().getLongitude()
                ));
            }
        }
        else if (v.getId() == R.id.action_button_search_location_buoy)
        {
            if (this.setSelectedPoint != null)
            {
                this.map.getController().setCenter(this.setSelectedPoint);
            }
        }
    }


    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {

        /* GroundOverlay myGroundOverlay = new GroundOverlay(getActivity());
        myGroundOverlay.setPosition(geoPoint);
        myGroundOverlay.setImage(getResources().getDrawable(R.drawable.kduino_marker).mutate());
        myGroundOverlay.setDimensions(2000.0f); */


        setSelectedPoint = geoPoint;

        setPoint(geoPoint, true);

        bus.post(geoPoint);
        return false;
    }

    private void setPoint(GeoPoint geoPoint, boolean isPoint) {



        final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        if (isPoint)
        {
            OverlayItem kduinoLocationSelectedPoint = new OverlayItem("Here", "Current Position", geoPoint);
            Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.kduino_marker);
            kduinoLocationSelectedPoint.setMarker(myCurrentLocationMarker);
            items.add(kduinoLocationSelectedPoint);
        }
        else
        {
            if (setSelectedPoint != null)
            {
                OverlayItem kduinoLocationSelectedPoint = new OverlayItem("Here", "Current Position", setSelectedPoint);
                Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.kduino_marker);
                kduinoLocationSelectedPoint.setMarker(myCurrentLocationMarker);
                items.add(kduinoLocationSelectedPoint);
            }
        }

        if (this.bestPoint != null)
        {
            OverlayItem kduinoLocationPoint = new OverlayItem("Here", "Current Position", this.bestPoint);
            Drawable myCurrentLocationMarkerBest = this.getResources().getDrawable(R.drawable.ic_action_position_light);
            kduinoLocationPoint.setMarker(myCurrentLocationMarkerBest);
            items.add(kduinoLocationPoint);

        }
        else
        {
            OverlayItem kduinoLocationPoint = new OverlayItem("Here", "Current Position", new GeoPoint(0,0));
            Drawable myCurrentLocationMarkerBest = this.getResources().getDrawable(R.drawable.ic_action_position_light);
            kduinoLocationPoint.setMarker(myCurrentLocationMarkerBest);
            items.add(kduinoLocationPoint);
        }


        //kduinoLocationSelectedPoint
        ItemizedOverlayWithFocus overlay = new ItemizedOverlayWithFocus<OverlayItem>(getActivity().getApplicationContext(), items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });







       /* Polygon circle = new Polygon(getActivity());
        circle.setPoints(Polygon.pointsAsCircle(geoPoint, 20.0));
        circle.setPoints(Polygon.pointsAsCircle(geoPoint, 200.0));
        circle.setFillColor(0x12121212);
        circle.setStrokeColor(Color.RED);
        circle.setStrokeWidth(3); */
        //circle.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
        //circle.setTitle("KdUINO pos: " + geoPoint.getLatitude() + "," + geoPoint.getLongitude());
        if (map.getOverlays().size() > 1) {
            map.getOverlays().remove(1);
        }
        map.getOverlays().add(overlay);
        map.invalidate();
    }
}
