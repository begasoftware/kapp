/*
package io.bega.kduino.fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOApiToken;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.UserDefinition;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

*/
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ALoginDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *//*

public class ALoginDialogFragment extends DialogFragment implements View.OnClickListener, Callback<JSONObject> {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MESSAGE = "arg_message";

    private int mMessage;

    KdUINOAPIService service;

    EditText editTextUser;

    EditText editTextPassword;

    Button btnSignup;

    Button btnRecoverPassword;

    Bus bus;

    String userName = "";

    Activity currentActivity;


    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginDialogFragment.
     *//*

    // TODO: Rename and change types and number of parameters
    public static ALoginDialogFragment newInstance(int message) {
        ALoginDialogFragment fragment = new ALoginDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public ALoginDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = getArguments().getInt(ARG_MESSAGE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.service = ((kdUINOApplication) getActivity().getApplication()).getAPIService();
        this.bus = ((kdUINOApplication) getActivity().getApplication()).getBus();
        currentActivity = this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        editTextUser = (EditText)view.findViewById(R.id.login_dialog_etEmail);
        editTextPassword = (EditText)view.findViewById(R.id.login_dialog_etPassword);
        //btnSignup = (Button)view.findViewById(R.id.login_dialog_btnSignIn);
        //btnRecoverPassword = (Button)view.findViewById(R.id.login_dialog_btn_recover_password);
        btnSignup.setOnClickListener(this);
        btnRecoverPassword.setOnClickListener(this);
        editTextUser.setText("manolo@gmail.com");
        editTextPassword.setText("1234");
        setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        return view;
    }


    @Override
    public void onClick(View v) {
        */
/*if (v.getId() == R.id.login_dialog_btnSignIn)
        {
            userName = editTextUser.getText().toString();
            String password = editTextPassword.getText().toString();
            if (userName.length() == 0)
            {
                return;
            }

            if (password.length() == 0)
            {
                return;
            }


            service.login(userName, password, this);

        } *//*

    }

    @Override
    public void success(JSONObject jsonObject, Response response) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String result = sb.toString();
        Gson gson = new Gson();
        KdUINOApiToken token =
                gson.fromJson(result, KdUINOApiToken.class);
        service.setApiToken(token);


        SharedPreferences settings = getActivity().getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
        if (settings.getString("user_name", "").equals(userName)) {

            if (settings.getInt("user_id", 0) != 0) {
                if (mMessage != 0)
                {
                    bus.post(new KdUinoMessageBusEvent(mMessage, null));
                }

                dismiss();
                return;
            }
        }

        service.getAllUser(new Callback<String>() {
            @Override
            public void success(String usersData, Response response) {
                Gson gson1 = new Gson();
                Type listType = new TypeToken<List<UserRecievedDefinition>>() {
                }.getType();
                try {
                    List<UserRecievedDefinition> users = gson1.fromJson(usersData, listType);
                    for (UserRecievedDefinition userRecievedDefinition : users) {
                        if (userRecievedDefinition.UserName.equals(userName)) {

                            SharedPreferences settings = currentActivity.getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
                            settings.edit().putInt("user_id", Integer.parseInt(
                                    userRecievedDefinition.UserID)).commit();
                            settings.edit().putString("user_name", userName).commit();

                            break;
                        }
                    }

                    if (mMessage != 0) {
                        bus.post(new KdUinoMessageBusEvent(mMessage, null));
                    }

                    dismiss();

                } catch (Exception ex) {
                    String p = ex.getMessage();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                if (mMessage != 0) {
                    bus.post(new KdUinoMessageBusEvent(mMessage, null));
                }

                dismiss();
            }
        });





    }

    @Override
    public void failure(RetrofitError error) {
        // Show auth error.
    }
}
*/
