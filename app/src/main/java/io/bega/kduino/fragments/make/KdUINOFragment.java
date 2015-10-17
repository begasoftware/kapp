package io.bega.kduino.fragments.make;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.BuoyAdapter;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.ConnectivityService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * interface.
 */
public class KdUINOFragment extends Fragment implements AbsListView.OnItemClickListener, View.OnClickListener, Callback<List<BuoyDefinition>> {


    private static final String ARG_KDUINO_ID = "kdUINO_ID";

    String UserID = "0";

    // TODO: Rename and change types of parameters
    private String kdUINOId;

    List<KDUINOBuoy> list;

    KdUINOAPIService service;

    SettingsManager settingsManager;

    Bus bus;


    AddFloatingActionButton addFloatingActionButton;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BaseAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static KdUINOFragment newInstance(String kdUINOId) {
        KdUINOFragment fragment = new KdUINOFragment();
        Bundle args = new Bundle();
        args.putString(ARG_KDUINO_ID, kdUINOId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public KdUINOFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            kdUINOId = getArguments().getString(ARG_KDUINO_ID);

        }

        service = ((kdUINOApplication)getActivity().getApplication()).getAPIService();
        bus = ((kdUINOApplication)getActivity().getApplication()).getBus();

        list =  KDUINOBuoy.listAll(KDUINOBuoy.class);
        // TODO: Change Adapter to display your content
        mAdapter = new BuoyAdapter(getActivity(), list, R.layout.kduino_row);
        settingsManager = new SettingsManager(getActivity());
        this.UserID = Integer.toString(settingsManager.getUserId());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kduino, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);

        addFloatingActionButton = (AddFloatingActionButton)view.findViewById(R.id.action_button_add_kduino);
        addFloatingActionButton.setOnClickListener(this);

        return view;
    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_buoy, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        KDUINOBuoy buoy = (KDUINOBuoy) list.get(info.position);
        switch (item.getItemId()) {
            case R.id.action_buoy_unasign:
                buoy.MacAddress = "";
                buoy.save();

                return true;
            case R.id.action_buoy_delete:

                buoy.delete();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }







    @Override
    public void onStart() {
        listData();
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onDestroy() {
        bus = null;
        service = null;
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private void listData()
    {
        list =  KDUINOBuoy.listAll(KDUINOBuoy.class);
        if (list.size() > 0) {
            if (ConnectivityService.isOnline(getActivity())) {
                if (service.isAthenticated()) {
                    service.getAllBuoysByUser(this);
                }
            }
            //  buoyID = list.get(0).getId().toString();
        }

        mAdapter.notifyDataSetChanged(); //.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        KDUINOBuoy kduino = list.get(position);
        bus.post(new KdUinoMessageBusEvent<KDUINOBuoy>(KdUINOMessages.EDIT_MAKE_KDUINO, kduino));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.action_button_add_kduino:
                bus.post(new KdUinoMessageBusEvent(KdUINOMessages.ADD_MAKE_KDUINO, null));
                break;
        }
    }

    @Override
    public void success(List<BuoyDefinition> buoyDefinitions, Response response) {

        List<BuoyDefinition> buoyDefinitionsList = buoyDefinitions;
        ArrayList<BuoyDefinition> selected = new ArrayList<BuoyDefinition>();
        for (BuoyDefinition buoyDefinition : buoyDefinitionsList)
        {
            if (Long.toString(buoyDefinition.BuoyID).startsWith(this.UserID))
            {
                selected.add(buoyDefinition);
            }
        }

        if (selected.size() > 0)
        {
            for (BuoyDefinition buoyDefinition : selected)
            {
                long test = buoyDefinition.BuoyID;
            }
        }





    }

    @Override
    public void failure(RetrofitError error) {
        String e = error.getMessage();
    }
}
