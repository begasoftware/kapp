package io.bega.kduino.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bega.kduino.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowDataActivityFragment extends Fragment {

    public ShowDataActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_data, container, false);
    }
}
