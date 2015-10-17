package io.bega.kduino.fragments;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.activities.MainActivity;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOApiServer;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.LastId;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.UserDefinition;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserActivityFragment extends Fragment implements IClickable {

    boolean insert;

    String temporalId;

    EditText editUsername;

  //  EditText editNickName;

    EditText editEmail;

    EditText editPassword;

    Spinner spinnerCountryList;

    FloatingActionButton saveActionButton;

    // Button btnDownloadMap;

    User user;

    View view;

    String lastId;

    public UserActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
  //      editNickName = (EditText)view.findViewById(R.id.fragment_user_nickname_value);
        editUsername = (EditText)view.findViewById(R.id.fragment_user_name_value);
        editPassword = (EditText)view.findViewById(R.id.fragment_user_password_value);
        editEmail = (EditText)view.findViewById(R.id.fragment_user_email_value);
        spinnerCountryList = (Spinner)view.findViewById(R.id.fragment_user_country_value);
        saveActionButton = (FloatingActionButton)view.findViewById(R.id.action_button_save_user);
        //btnDownloadMap = (Button)view.findViewById(R.id.btnDownloadMap);
        this.bindSpinner(spinnerCountryList);
        this.findUser();
        return view;
    }

    private void bindSpinner(Spinner countrySpinner)
    {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        for (String country : countries) {
            System.out.println(country);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, countries);
        // set the view for the Drop down list
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set the ArrayAdapter to the spinner
        countrySpinner.setAdapter(dataAdapter);

    }

    private void findUser()
    {
        Iterator<User> users = User.findAll(User.class);
        if (users.hasNext())
        {
            user = users.next();
         //   editNickName.setText(user.NickName);
            editUsername.setText(user.Name);
            editEmail.setText(user.Email);
            editPassword.setText(user.Password);

            for (int i= 0; i < spinnerCountryList.getAdapter().getCount(); i++)
            {
                if (spinnerCountryList.getAdapter().getItem(i).toString().equals(user.Country))
                {
                    spinnerCountryList.setSelection(i);
                    break;
                }
            }
        }

        try {
            KdUINOAPIService service = new KdUINOAPIService(this.getActivity());
            service.getLastID(new Callback<String>() {
                @Override
                public void success(String lastIdData, Response response) {
                    lastId = lastIdData;
                }

                @Override
                public void failure(RetrofitError error) {
                    lastId = "9000";
                }
            });



        }catch (Exception ex)
        {
            String exception = ex.getMessage();
        }

    }

    private void saveUser()
    {
      /*  String nickName = editNickName.getText().toString();
        if (nickName.trim().length() == 0)
        {
            editNickName.setError("Enter nickname");
            editNickName.setHint("Enter nickname");
            return;
        }*/

        String userName = editUsername.getText().toString();
        if (userName.trim().length() == 0)
        {
            editUsername.setError("Enter username");
            editUsername.setHint("Enter username");
            return;
        }

        String email = editEmail.getText().toString();
        if (email.trim().length() == 0)
        {
            editEmail.setError("Enter email");
            editEmail.setHint("Enter email");
            return;
        }

        String password = editPassword.getText().toString();
        if (password.trim().length() == 0)
        {
            editPassword.setError("Enter password");
            editPassword.setHint("Enter password");
            return;
        }

        String country = spinnerCountryList.getSelectedItem().toString();

        if (user == null) {

            user = new User(lastId,
                    userName, country, email, password);
            insert = true;
        }
        else
        {
            user.Name = userName;
            user.Country = country;
            user.Email = email;
            user.Password = password;
            insert = false;
        }

        user.save();
       /* if (user.getId() != -1)
        {

            DisplayUtilities.ShowLargeMessage("User Save Data", "OK",
                    view,
                    false, null);

        }
        else
        {
            DisplayUtilities.ShowLargeMessage("User Save Data", "OK",
                    view,
                    false, null);

        } */

        try {
            KdUINOAPIService service = new KdUINOAPIService(this.getActivity());

            UserDefinition userDefinition =
                    new UserDefinition(user);

            Gson gson = new Gson();
            String test = gson.toJson(userDefinition);
            service.saveUser(userDefinition, new Callback<String>() {
                        @Override
                        public void success(String value, Response response)
                        {
                            user.UserId = value;
                            user.StoredInServer = true;
                            user.save();
                            SettingsManager settingsManager = new SettingsManager(getActivity());
                            settingsManager.setUserID(Integer.parseInt(value));
                            settingsManager.setPassword(user.Password);
                            settingsManager.setUsername(user.Name);
                            DisplayUtilities.ShowLargeMessage("Correct saving User Data", "OK",
                                    view,
                                    false, null);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();

                        }

                        @Override
                        public void failure(RetrofitError error)
                        {
                            DisplayUtilities.ShowLargeMessage("Error saving  User Data", "KO",
                                    view,
                                    false, null);
                        }
                    }

            );

        }catch (Exception ex)
        {
            String exception = ex.getMessage();
        }


    }

    @Override
    public void clickView(View v) {
        switch (v.getId())
        {
            /* case R.id.btnDownloadMap:
                int maxZoom = 7;
                ((kdUINOApplication)  getActivity().getApplication()).getBus().post(
                        new KdUinoMessageBusEvent<Integer>(
                        KdUINOMessages.DOWNLOAD_MAP, maxZoom));
                break; */
            case R.id.action_button_save_user:
                this.saveUser();
                break;
        }
    }
}
