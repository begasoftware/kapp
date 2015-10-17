package io.bega.kduino.fragments.make;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bega.kduino.R;
import io.bega.kduino.fragments.KduinoFragmentAdapter;
import io.bega.kduino.fragments.KduinoMakeFragmentAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MakeKduinoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeKduinoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUOYID = "buoyID";


    // TODO: Rename and change types of parameters
    private String mBuoyID;

    ViewPager viewPager;

    TabLayout tabLayout;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MakeKduinoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeKduinoFragment newInstance(String buoyID) {
        MakeKduinoFragment fragment = new MakeKduinoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUOYID, buoyID);
        fragment.setArguments(args);
        return fragment;
    }

    public MakeKduinoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBuoyID = getArguments().getString(ARG_BUOYID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_kduino, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.kdunino_control_viewpager_main);
        viewPager.setAdapter(new KduinoMakeFragmentAdapter(
                this.getFragmentManager(),
                getActivity(), mBuoyID));

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
