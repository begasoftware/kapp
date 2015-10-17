package io.bega.kduino.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KdUINOControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KdUINOControlFragment extends Fragment implements IClickable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUOYID = "buoyID";
    private static final String ARG_MACADRESS = "macAdress";

    // TODO: Rename and change types of parameters
    private String mBuoyID;
    private String mMacAdress;


    ViewPager viewPager;

    TabLayout tabLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param buoyID Parameter 1.
     * @param macAdress Parameter 2.
     * @return A new instance of fragment KdUINOControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KdUINOControlFragment newInstance(String buoyID, String macAdress) {
        KdUINOControlFragment fragment = new KdUINOControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUOYID, buoyID);
        args.putString(ARG_MACADRESS, macAdress);
        fragment.setArguments(args);
        return fragment;
    }

    public KdUINOControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuoyID = getArguments().getString(ARG_BUOYID);
            mMacAdress = getArguments().getString(ARG_MACADRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kd_uinocontrol, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.kdunino_control_viewpager_main);
        viewPager.setAdapter(new KduinoFragmentAdapter(this.getFragmentManager(),
                getActivity(), mBuoyID, mMacAdress));

        // Give the TabLayout the ViewPagerkdUINOControlFragment
        tabLayout = (TabLayout) view.findViewById(R.id.kdunino_control_sliding_main_tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return view;
    }


    @Override
    public void clickView(View v) {

        KduinoFragmentAdapter adapter = (KduinoFragmentAdapter)viewPager.getAdapter();
        adapter.clickView(v);
    }
}
