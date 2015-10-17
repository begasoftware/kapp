package io.bega.kduino.fragments.bluetooth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.Status;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DataCommandsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataCommandsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button btnStart;

    private Button btnStop;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Bus bus;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataCommandsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataCommandsFragment newInstance(String param1, String param2) {
        DataCommandsFragment fragment = new DataCommandsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DataCommandsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_commands, container, false);
        btnStart = (Button)view.findViewById(R.id.btnManagerStartReadDataAction);
        btnStop = (Button)view.findViewById(R.id.btnManagerEndReadDataAction);
        return view;
    }

    @Subscribe
    public void getMessage(Status status) {
        if (status.StatusKduino == 0) {
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        } else {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        }
    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent message)
    {
        if (message.getMessage() == KdUINOMessages.RECIEVE_INFO)
        {
            Status status = (Status)message.getData();
            if (status.StatusKduino == 0) {
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            } else {
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        }



    }


    @Override
    public void onStart() {
        super.onStart();
        this.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.bus.unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.bus = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
