package io.bega.kduino.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Bus;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.activities.BaseActivity;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.KdUINOErrorHandler;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.LocationService;
import retrofit.Callback;
import retrofit.RestAdapter;
import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOApiServer;
import io.bega.kduino.datamodel.BuoyDefinition;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    Bus bus;

    boolean isBackground = true;

    LocationService locationService;

    RelativeLayout rlRoot;

    private interface Listener
    {
        void onItemSelected(int position);
    }

    private Listener listener;

    public MainActivityFragment() {

    }

    @Override
    public void onStop() {
        super.onStop();
   //     bus.unregister(this);
       // if (isBackground)
       // {
       //     rlRoot.setBackgroundResource(0);
       //     isBackground = false;
       // }
    }

    @Override
    public void onStart() {

        this.bus = ((kdUINOApplication)getActivity().getApplication()).getBus();
        this.locationService = new LocationService(getActivity());
        super.onStart();


        //if (!isBackground)
        //{
        //    rlRoot.setBackgroundResource(R.drawable.kduino_main_screentshot);
        //    isBackground = true;
        //}
//        bus.register(this);
    }

    @Override
    public void onDestroy() {

        ((BaseActivity)this.getActivity()).destroyBackground(rlRoot);
        this.bus = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);




        final FloatingActionButton actionMakeKduino = (FloatingActionButton) view.findViewById(R.id.action_button_howto_kduino);
        actionMakeKduino.setOnClickListener(this);
        final FloatingActionButton actionConnectKduino = (FloatingActionButton) view.findViewById(R.id.action_button_connect_kduino);
        actionConnectKduino.setOnClickListener(this);
        rlRoot = (RelativeLayout)view.findViewById(R.id.rlRoot);
        ((BaseActivity)this.getActivity()).setBackgroundImage(rlRoot, R.drawable.kduino_bg_main);
        //final FloatingActionButton actionHowToMakeKduino = (FloatingActionButton) view.findViewById(R.id.action_button_howto_kduino);
        //actionHowToMakeKduino.setOnClickListener(this);
        FloatingActionsMenu menu = (FloatingActionsMenu)view.findViewById(R.id.multiple_actions);
        menu.expand();
        final FloatingActionButton actionCheckData = (FloatingActionButton) view.findViewById(R.id.action_button_data);
        actionCheckData.setOnClickListener(this);



      /*
     final FloatingActionButton actionSettings = (FloatingActionButton) view.findViewById(R.id.action_button_settings);
        actionSettings.setOnClickListener(this);
       */




        /* TourGuide mTourGuideHandler = TourGuide.init(getActivity()).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setTitle("Welcome!").setDescription("Click on Get Started to begin..."))
                .setOverlay(new Overlay())
                .playOn(actionConnectKduino); */



        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            //case R.id.action_button_howto_kduino:
            //    bus.post(KdUINOMessages.HELP_VIDEO);
            //    Log.d(kdUINOApplication.TAG, "Help Video");
            //    break;

            /*case R.id.action_button_make_kduino:
                bus.post( new KdUinoMessageBusEvent(
                        KdUINOMessages.MAKE_KDUINO,
                        null));
                Log.d(kdUINOApplication.TAG, "Make a kaduino");
                break; */
            case R.id.action_button_howto_kduino:
                ((BaseActivity)this.getActivity()).launchTutorial();

                break;

            case R.id.action_button_data:
                bus.post(new KdUinoMessageBusEvent(
                        KdUINOMessages.SHOW_DATA,
                        null));
                Log.d(kdUINOApplication.TAG, "Data");
                break;

            case R.id.action_button_connect_kduino:
                bus.post(
                        new KdUinoMessageBusEvent(
                        KdUINOMessages.CONNECT_BLUETOOTH_ACTIVATE,
                                null
                        )
                );
                Log.d(kdUINOApplication.TAG, "Connect");
                break;

           /* case R.id.action_button_settings:
                bus.post(KdUINOMessages.SETTINGS);
                Log.d(kdUINOApplication.TAG, "Settings");
                break; */
        }
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       // this.listener = (Listener)activity;
    }

}
