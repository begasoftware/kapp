package io.bega.kduino.fragments.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import io.bega.kduino.R;

public final class PlaceTextSlideFragment extends Fragment {

    int index;

    public PlaceTextSlideFragment() {
    }

    private static final String ARG_INDEX = "index_image";

    private static final String ARG_INDEX_TEXT = "index";

    public static PlaceTextSlideFragment newInstance(int index) {
        PlaceTextSlideFragment fragment = new PlaceTextSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX_TEXT, index);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_INDEX_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.content_text_panel, container, false);
        CharSequence[] titles = getResources().getStringArray(R.array.intro_titles);
        List<CharSequence> titleList = Arrays.asList(titles);

        CharSequence[] messages = getResources().getStringArray(R.array.intro_messages);
        List<CharSequence> messageList = Arrays.asList(messages);


        TextView textViewTitle = (TextView)view.findViewById(R.id.content_panel_title);
        textViewTitle.setText(titleList.get(index));

        TextView textViewMessage = (TextView)view.findViewById(R.id.content_panel_message);
        textViewMessage.setText(messageList.get(index));

        return view;
    }
}
