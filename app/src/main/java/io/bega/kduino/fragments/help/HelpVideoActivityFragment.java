package io.bega.kduino.fragments.help;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bega.kduino.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class HelpVideoActivityFragment extends Fragment {

    public HelpVideoActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_video, container, false);
    }
}
