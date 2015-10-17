package io.bega.kduino.fragments.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import io.bega.kduino.R;

public final class PlaceSlideFragment extends Fragment {

    int imageResourceId;


    public PlaceSlideFragment() {
    }

    private static final String ARG_INDEX = "index_image";


    public static PlaceSlideFragment newInstance(int imageIndex) {
        PlaceSlideFragment fragment = new PlaceSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, imageIndex);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageResourceId = getArguments().getInt(ARG_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.content_panel, container, false);

        ImageView image = (ImageView)view.findViewById(R.id.content_panel_image);
        image.setImageResource(imageResourceId);



        return view;
    }
}
