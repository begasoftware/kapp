package io.bega.kduino.fragments.help;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.kdUINOApplication;

public class UserSettingsFragment extends PreferenceFragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);

        Preference button = (Preference)getPreferenceManager().findPreference("prefDownloadMap");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {


                    ((kdUINOApplication)  getActivity().getApplication()).getBus().post(KdUINOMessages.DOWNLOAD_MAP);

                    return true;
                }
            });
        }
 
    }
}
