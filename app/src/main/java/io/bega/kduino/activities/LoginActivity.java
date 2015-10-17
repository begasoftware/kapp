package io.bega.kduino.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import io.bega.kduino.datamodel.RecoveryPassword;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends BaseActivity implements IClickable, Callback<JSONObject> {

    Bus bus;

    boolean setBackground = false;

    KdUINOAPIService service;

    EditText etEmail;

    EditText etPassword;

    RelativeLayout rlRoot;

    String userName;

    String password;

    SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bus = ((kdUINOApplication)getApplication()).getBus();

        rlRoot = (RelativeLayout) findViewById( R.id.rlRoot );
        final LinearLayout llSignUp = (LinearLayout) findViewById( R.id.llSignUp );

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);

        rlRoot.setBackgroundResource(R.drawable.kduino_main_screentshot);
        setBackground = true;

        // Hack to detect keyboard appearance
        /*rlRoot.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = rlRoot.getRootView().getHeight() - rlRoot.getHeight();
                        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                            llSignUp.setVisibility(View.GONE);
                        } else {
                            llSignUp.setVisibility(View.VISIBLE);
                        }
                    }
                }); */

        this.settingsManager = new SettingsManager(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (rlRoot != null) {
                if (!setBackground) {
                    rlRoot.setBackgroundResource(R.drawable.kduino_main_screentshot);
                    setBackground = true;
                }
            }
        }
        catch (Exception ex)
        {
            String p = ex.getMessage();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (setBackground) {
            rlRoot.setBackgroundResource(0);
            setBackground = false;
        }
    }

    @Override
    protected void onDestroy() {
        this.bus = null;
        this.service = null;
        super.onDestroy();

    }

    public void send(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void signUp(View view){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickView(View v) {

        switch (v.getId())
        {
            case R.id.btnSignUp:
                signUp(v);
                break;

            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.btnRecoverPassword:
                this.launchTutorial();
                //recoveryDialog();
                break;
            case R.id.btnEnterWithoutlogin:
                Intent mainIntent =
                        new Intent().setClass(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;
        }

    }

    private void signIn()
    {
        this.service = ((kdUINOApplication)this.getApplication()).getAPIService();
        userName = etEmail.getText().toString();
        password = etPassword.getText().toString();
        password = etPassword.getText().toString();
        if (userName.length() == 0)
        {
            return;
        }

        if (password.length() == 0)
        {
            return;
        }


        service.login(userName, password, this);
    }

    private void recoveryDialog()
    {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_recover_title)
                .customView(R.layout.recover_email_dialog, false)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        MaterialDialog materialDialog = (MaterialDialog) dialog;
                        View view = materialDialog.getView();

                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MaterialDialog materialDialog = (MaterialDialog) dialog;
                        View view = materialDialog.getView();


                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        EditText editEmail =
                                (EditText) view.findViewById(R.id.recover_email);
                        String email = editEmail.getText().toString();
                        service = ((kdUINOApplication)getApplication()).getAPIService();
                        service.recoverPassword(new RecoveryPassword(email), new Callback<String>() {
                            @Override
                            public void success(String s, Response response) {
                                DisplayUtilities.ShowLargeMessage(getString(R.string.dialog_recover_email_sended),
                                        "",
                                        rlRoot,
                                        false,
                                        null);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                DisplayUtilities.ShowLargeMessage(getString(R.string.dialog_recover_email_error),
                                        "",
                                        rlRoot,
                                        false,
                                        null);
                            }
                        });

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();
        dialog.show();
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
        SettingsManager manager = new SettingsManager(this);
        if (manager.getUsername().equals(userName));
        {
            if (manager.getUserId() != 0)
            {
                Intent mainIntent =
                        new Intent().setClass(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
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
                            settingsManager.setUsername(userName);
                            settingsManager.setPassword(password);
                            settingsManager.setUserID(Integer.parseInt(
                                    userRecievedDefinition.UserID));
                           /* SharedPreferences settings = getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
                            settings.edit().putInt("user_id", Integer.parseInt(
                                    userRecievedDefinition.UserID)).commit();
                            settings.edit().putString("user_name", userName).commit();
                            settings.edit().putString("password", password).commit(); */

                            break;
                        }
                    }

                    Intent mainIntent =
                            new Intent().setClass(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } catch (Exception ex) {
                    String p = ex.getMessage();
                }


            }

            @Override
            public void failure(RetrofitError error) {

                finish();
            }
        });
    }


    @Override
    public void failure(RetrofitError error) {
        DisplayUtilities.ShowLargeMessage(this.getString(R.string.data_login_error),
                "",
                rlRoot,
                false,
                null);
    }
}
