package io.bega.kduino.fragments.bluetooth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Bus;

import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.BuoyAdapter;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;

/**
 * Use the {@link SelectKdUINOModelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectKdUINOModelFragment extends Fragment implements
        AdapterView.OnItemClickListener, IClickable, View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MAC = "MAC";

    // TODO: Rename and change types of parameters
    private String mMac;

    MaterialDialog dialog;

    Bus bus;

    ListView listView;

    List<KDUINOBuoy> list;

    BuoyAdapter listAdapter;





    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mac Parameter 1.
     * @return A new instance of fragment SelectKdUINOModelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectKdUINOModelFragment newInstance(String mac) {
        SelectKdUINOModelFragment fragment = new SelectKdUINOModelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MAC, mac);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectKdUINOModelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMac = getArguments().getString(ARG_MAC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_select_kd_uinomodel, container, false);
        this.listView = (ListView)view.findViewById(R.id.fragment_kduino_model_listview);
        return view;
    }

    private void bindData()
    {

        List<KDUINOBuoy> list =  KDUINOBuoy.find(KDUINOBuoy.class, "mac_address= ?", mMac);
        if (list.size() > 0) {
            bus.post(new KdUinoMessageBusEvent<KDUINOBuoy>(KdUINOMessages.CONNECT_KDUINO, list.get(0)));
        }
        this.list =  KDUINOBuoy.listAll(KDUINOBuoy.class);
        this.listAdapter = new BuoyAdapter(
                this.getActivity(),
                this.list,
                R.layout.kduino_row);
        this.listView.setOnItemClickListener(this);
        this.listView.setAdapter(listAdapter);
        if (this.list.size() == 0)
        {
            DisplayUtilities.ShowLargeMessage("No KdUINO Models in the local database.",
                    "Create KdUINO Model",
                    this.listView,
                    true,
                    this
                    );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.bus = ((kdUINOApplication)getActivity().getApplication()).getBus();
        this.bindData();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        KDUINOBuoy buoy = this.list.get(position);
        checkIfMacIsRegistered(buoy);


    }

    private void checkIfMacIsRegistered(final KDUINOBuoy kduinoBuoy)
    {

        List<KDUINOBuoy> list =  KDUINOBuoy.find(KDUINOBuoy.class, "mac_address= ?", mMac);
        if (list.size() == 0) {
            dialog = new MaterialDialog.Builder(this.getActivity())
                    .title(R.string.alert_associated_kduino_title)
                    .content(R.string.alert_associated_kduino_message)
                    .positiveText(R.string.positive_dialog)
                    .negativeText(R.string.negative_dialog)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                                kduinoBuoy.MacAddress = mMac;
                                kduinoBuoy.save();
                                bus.post(new KdUinoMessageBusEvent<KDUINOBuoy>(KdUINOMessages.CONNECT_KDUINO, kduinoBuoy));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                                bus.post(new KdUinoMessageBusEvent<KDUINOBuoy>(KdUINOMessages.CONNECT_KDUINO, kduinoBuoy));

                        }
                    })
                    .build();
            dialog.show();
        }



    }

    @Override
    public void clickView(View v) {

    }


    @Override
    public void onClick(View v) {

        bus.post(new KdUinoMessageBusEvent(KdUINOMessages.DISCONNECT_KDUINO, null));

        bus.post(new KdUinoMessageBusEvent(KdUINOMessages.MAKE_KDUINO, null));
       // NavUtils.navigateUpFromSameTask(getActivity());

    }
}
