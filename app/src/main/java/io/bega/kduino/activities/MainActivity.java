package io.bega.kduino.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/*import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget; */
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.ConnectivityService;
import io.bega.phonetutorial.activities.TutorialActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.fragments.MainActivityFragment;
import io.bega.kduino.services.DownloadOffLineMap;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements IClickable, Callback<String> {



    SettingsManager settingsManager;

    KdUINOAPIService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionNavigation();
        this.setTitle(getString(R.string.activity_title_main));
        this.settingsManager = new SettingsManager(this);
        //Intent downloadFileManager = new Intent(this, DownloadOffLineMap.class);
       // startService(downloadFileManager);
        this.service = ((kdUINOApplication)this.getApplication()).getAPIService();
        MainActivityFragment fragment = new MainActivityFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }



    @Override
    protected void onDestroy() {
        this.bus = null;
        this.service = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        this.bus.register(this);
        this.checkFirstTime();
        super.onStart();

    }

    @Override
    protected void onStop() {
        this.bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent message) {
        if (message.getMessage() == KdUINOMessages.BLUETOOTH_ON)
        {
            this.processNavigationMessage(KdUINOMessages.CONNECT_KDUINO);
            return;
        }
         else {
            this.processNavigationMessage(message.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void clickView(View v) {


    }

    private void checkFirstTime()
    {
        if (settingsManager.getFirstTime())
        {
            this.launchTutorial();
        }


        List<User> list =  User.listAll(User.class);
        if (list.size() == 0) {
            //if (settingsManager.getPassword().length() > 0) {
                if (ConnectivityService.isOnline(this)) {
                    if (service.isAthenticated()) {
                        service.getAllUser(this);
                    }
                }
                //service.login(userName, settingsManager.getPassword(), this);
                // launchUserData();
            //}
        }

    }



    private void launchUserData()
    {
        bus.post(new KdUinoMessageBusEvent(KdUINOMessages.SETTINGS, null));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == TUTORIALACTIVITY || requestCode == TUTORIAL_HELP_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                // CLick in next button
                //Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();

            }

            settingsManager.setFirstTime();


           /* SharedPreferences settings = getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
            if (settings.getBoolean("my_first_time", true)) {

                settings.edit().putBoolean("my_first_time", false).commit();
               // this.processNavigationMessage(KdUINOMessages.SETTINGS);
            } */
        }

    }

    @Override
    public void success(String usersData, Response response) {
        String userName = settingsManager.getUsername();
        Gson gson1 = new Gson();
        Type listType = new TypeToken<List<UserRecievedDefinition>>() {
        }.getType();
        try {
            List<UserRecievedDefinition> users = gson1.fromJson(usersData, listType);
            for (UserRecievedDefinition userRecievedDefinition : users) {
                if (userRecievedDefinition.UserName.equals(userName)) {
                    User user = null;
                    if (user == null) {

                        user = new User(
                                userRecievedDefinition.UserID,
                                userName, userRecievedDefinition.Country, userRecievedDefinition.Email, settingsManager.getPassword());

                    }
                    else
                    {
                        user.Name = userName;
                        user.Country = userRecievedDefinition.Country;
                        user.Email = userRecievedDefinition.Email;
                        user.Password = settingsManager.getPassword();

                    }

                    user.StoredInServer = true;
                    user.save();

                    //settingsManager.setUsername(userName);
                    //settingsManager.setPassword(password);
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

            /* Intent mainIntent =
                    new Intent().setClass(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish(); */

        } catch (Exception ex) {
            String p = ex.getMessage();
        }
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
