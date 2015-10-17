package io.bega.kduino.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOApiManager;
import io.bega.kduino.api.KdUINOApiToken;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.BuoyPostDefinition;
import io.bega.kduino.datamodel.Captures;
import io.bega.kduino.datamodel.CapturesDefinition;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.MeasurementPostData;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoOperationMessageBusEvent;
import io.bega.kduino.fragments.AnalysisFragment;

// import io.bega.kduino.fragments.ALoginDialogFragment;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.ConnectivityService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.services.upload.UploadDataTask;
import io.bega.kduino.services.upload.UploadDataTaskQueue;
import io.bega.kduino.utils.DisplayUtilities;
import io.bega.phonetutorial.activities.TutorialActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
/*import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide; */

public class BaseActivity extends AppCompatActivity  {

    final int TUTORIALACTIVITY = 0x030;

    final int TUTORIAL_HELP_ACTIVITY = 0x040;

    protected Bus bus;

    private DrawerLayout mDrawer;

    LinearLayout linear;

    private NavigationView nvDrawer;

    // ImageView imageViewBackground;

    kdUINOApplication application;

    private Toolbar toolbar;

    UploadDataTaskQueue queue;

    String currentActivity = "";

    DataSet currentDataSet;

    Menu menu;

    String BuoyID = "";

    String userName = "";

    MaterialDialog sendingDialog;

    public static final int DIALOG_FRAGMENT = 1;
    //ALoginDialogFragment loginFragment;

    protected void clearStack()
    {
        this.currentActivity = "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //((kdUINOApplication)getApplication()).inject(this);
        this.application = ((kdUINOApplication) getApplication());
        this.bus = application.getBus();
        this.queue = application.getQueue();
        this.currentActivity = application.getLastActivity();

    }

    @Override
    protected void onDestroy() {

        this.destroyBackground(linear);
        this.application = null;
        this.bus = null;
        this.queue = null;
        this.currentActivity = "";
        super.onDestroy();
    }

    public void destroyBackground(RelativeLayout relativeLayout)
    {
        if (relativeLayout != null)
        {
            Drawable drawable = relativeLayout.getBackground();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.recycle();
            }
        }

    }

    public void destroyBackground(LinearLayout linear)
    {
        if (linear != null)
        {
            Drawable drawable = linear.getBackground();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.recycle();
            }
        }

    }

    public void setBackgroundImage(RelativeLayout linearLayout, int resource)
    {

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackgroundDrawable(
                    getResources().getDrawable(resource)
            );
        }
        else
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    resource,
                    options);

            Drawable background = new BitmapDrawable(getResources(), bitmap);
            linearLayout.setBackground(background);
        }

        // linearLayout.setBackground(background);
        //imageView.setImageBitmap(bitmap);//        setBackground(background);
    }

    public void setBackgroundImage(LinearLayout linearLayout, int resource)
    {

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackgroundDrawable(
                    getResources().getDrawable(resource)
            );
        }
        else
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    resource,
                    options);

            Drawable background = new BitmapDrawable(getResources(), bitmap);
            linearLayout.setBackground(background);
        }

        // linearLayout.setBackground(background);
        //imageView.setImageBitmap(bitmap);//        setBackground(background);
    }

    protected void setOnlyToolbar()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void setActionNavigation()
    {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        linear = (LinearLayout)nvDrawer.findViewById(R.id.nav_header_root);
        //TextView textView = (TextView)nvDrawer.findViewById(R.id.nav_header_login_name);
        //imageViewBackground = (ImageView)nvDrawer.findViewById(R.id.nav_header_image_background);

        this.setupDrawerContent(nvDrawer);
        this.setBackgroundImage(linear, R.drawable.ciclops_header);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);



        //SettingsManager manager = new SettingsManager(this);

        /*if (manager.getUsername().length() > 0) {
            textView.setText(manager.getUsername());

        }
        else
        {
            textView.setText(getString(R.string.user_guest));
        }*/

    }



    public void sendDataToTask(DataSet dataset)
    {
        /*sendingDialog = new MaterialDialog.Builder(this)
                .title("Please Wait!")
                .content("Sending Kd Data To Server.")
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .progress(true, 0)
                .build();
        sendingDialog.show(); */
        currentDataSet = dataset;


        //KdUINOApiManager kdApiManager = new KdUINOApiManager(this);
        //kdApiManager.login();


        // KdUINOAPIService service = ((kdUINOApplication)getApplication()).getAPIService();




       //queue.add(new UploadDataTask(dataset, service));
    }

    protected void sendFileToTask(String filePath)
    {
        //File fileToSend = new File(filePath);
        StorageService storageService = new StorageService(this, linear);
        queue.add(new UploadDataTask(filePath));
    }

    protected void processNavigationMessage(int message)
    {
        String nextActivity = "";
        Intent mainActivity =
                new Intent();
        // isTaskRoot();
        switch (message)
        {
            case KdUINOMessages.LAUNCH_TUTORIAL:

                this.launchTutorial();
                return;

            case KdUINOMessages.PROMPT_LOGIN:
                nextActivity = "LoginActivity";
                if (currentActivity.equals(nextActivity))
                {
                    return;
                }


                // application.setLastActivity("LoginActivity");
                mainActivity.setClass(BaseActivity.this, LoginActivity.class);

                break;

            case KdUINOMessages.MAKE_KDUINO:

                if (!application.getAPIService().isAthenticated())
                {
                    this.openAuthenticationDialog(message);
                    return;
                }

                nextActivity = "MakeKdUINOActivity";
                if (currentActivity.equals(nextActivity))
                {
                    return;
                }

                // application.setLastActivity("MakeKdUINOActivity");
                mainActivity.setClass(BaseActivity.this, MakeKdUINOActivity.class);
            case KdUINOMessages.CONNECT_KDUINO:
                nextActivity = "BluetoothActivity";
                /*if (currentActivity.equals(nextActivity))
                {
                    return;
                } */
                // application.setLastActivity("BluetoothActivity");
                mainActivity.setClass(BaseActivity.this, BluetoothActivity.class);
                break;
            case KdUINOMessages.SHOW_DATA:
                nextActivity = "ShowDataActivity";
                /*if (currentActivity.equals((nextActivity)))
                {
                    return;
                } */

                bus.post(
                        new KdUinoMessageBusEvent(
                                KdUINOMessages.DISCONNECT_KDUINO, null));
                         // application.setLastActivity("ShowDataActivity");
                         mainActivity.setClass(BaseActivity.this, ShowDataActivity.class);

                break;
            case KdUINOMessages.CONNECT_BLUETOOTH_ACTIVATE:

                nextActivity = "BluetoothActivity";
               /* if (currentActivity.equals((nextActivity)))
                {
                    return;
                } */

                // application.setLastActivity("BluetoothActivity");
                mainActivity.setClass(BaseActivity.this, BluetoothActivity.class);
                break;
            case KdUINOMessages.HELP_VIDEO:

                nextActivity = "HelpVideoActivity";
                if (currentActivity.equals((nextActivity)))
                {
                    return;
                }


               // application.setLastActivity("HelpVideoActivity");
                mainActivity.setClass(BaseActivity.this, HelpVideoActivity.class);

                break;
            case KdUINOMessages.SETTINGS:


                nextActivity = "UserActivity";
                if (currentActivity.equals((nextActivity)))
                {
                    return;
                }

               // application.setLastActivity("UserActivity");
                mainActivity.setClass(BaseActivity.this, UserActivity.class);

                break;
            case KdUINOMessages.HELP_ABOUT:
                nextActivity = "HelpKduinoActivity";
                if (currentActivity.equals((nextActivity)))
                {
                    return;
                }

               // application.setLastActivity("HelpKduinoActivity");
                mainActivity.setClass(BaseActivity.this, HelpKduinoActivity.class);

                break;
            case KdUINOMessages.BLUETOOTH_OFF:
            case KdUINOMessages.BLUETOOTH_ON:
            case KdUINOMessages.INTERNET_OFF:
                this.invalidateOptionsMenu();
                return;
            case KdUINOMessages.INTERNET_ON:
                this.invalidateOptionsMenu();
                this.queue.start();
                return;

            default:
                return;
        }

      //  mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //mainActivity.setFlags(mainActivity.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(mainActivity);


        Log.d(kdUINOApplication.TAG, "Message: " + message);

        // Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }



    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (menu == null || menu.size() == 0)
        {
            return false;
        }



       /* Boolean enabled = BluetoothService.isEnabledBluetooth();
        if (enabled) {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_bluetooth_on));
        }
        else
        {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_bluetooth_white_off));
        }

        if (ConnectivityService.isOnline(this))
        {
            menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_wifi_on));
        }
        else
        {
            menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_wifi_white_off));
        } */


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {

        menuItem.setChecked(true);
        // setTitle(menuItem.getTitle());
        int message = KdUINOMessages.DEFAULT;
        switch(menuItem.getItemId()) {



            case R.id.nav_first_connect_kduino:
                message = KdUINOMessages.CONNECT_KDUINO;
                break;
          /*  case R.id.nav_first_make_kduino:
                message = KdUINOMessages.MAKE_KDUINO;
                break; */
            case R.id.nav_data_map:
                message = KdUINOMessages.SHOW_DATA;
                break;
            /*case R.id.nav_data_kduino:
                message = KdUINOMessages.SHOW_MY_DATA;
                break; */
            /*case R.id.nav_settings:
                message = KdUINOMessages.SETTINGS;
                break; */
            case R.id.nav_video_help:
                message = KdUINOMessages.HELP_VIDEO;
                break;
            //case R.id.nav_login:
            //    this.openAuthenticationDialog(KdUINOMessages.DEFAULT);
            //    return;
            case R.id.nav_tutorial:
                message = KdUINOMessages.LAUNCH_TUTORIAL;
                break;
            case R.id.nav_about:
                message = KdUINOMessages.HELP_ABOUT;
                break;
            case R.id.nav_third_fragment:

                break;

        }

        bus.post(new KdUinoMessageBusEvent(message, null));
        mDrawer.closeDrawers();
    }

    public void launchHelpTutorial()
    {
        System.gc();
        Intent intent = new Intent(this, IntroductionActivity.class);
        startActivityForResult(intent, TUTORIAL_HELP_ACTIVITY);

    }

    public void launchTutorial()
    {
        // try to ask to recollect all the memory because will use a lot of bitmaps.
        System.gc();
        String[] titles = getResources().getStringArray(R.array.titles);
        int[] images =  {
                R.drawable.screen0,
                R.drawable.screen1,
                R.drawable.screen2,
                R.drawable.screen3,
                R.drawable.screen4,
                R.drawable.screen5,
                R.drawable.screen6,
                R.drawable.screen7,
                R.drawable.screen8

        };

        int colorIndicator = Color.parseColor("#FFFFFF");
        int colorBackground = Color.parseColor("#1E88E5");
        int colorButton = Color.parseColor("#FFFFFF");
        int colorIcon = Color.parseColor("#1E88E5");
        Intent tutorialIntent = new Intent(this,TutorialActivity.class);
        tutorialIntent.putExtra(TutorialActivity.COLORBACKGROUND, colorBackground);
        tutorialIntent.putExtra(TutorialActivity.COLORBUTTON, colorButton);
        tutorialIntent.putExtra(TutorialActivity.COLORINDICATOR, colorIndicator);
        tutorialIntent.putExtra(TutorialActivity.COLORICON, colorIcon);
        tutorialIntent.putExtra(TutorialActivity.IMAGES, images);
        tutorialIntent.putExtra(TutorialActivity.TITLES, titles);

        startActivityForResult(tutorialIntent, TUTORIALACTIVITY);
    }


    private void openAuthenticationDialog(int messages)
    {

        this.openLoginDialog();

    }

    private void openLoginDialog()
    {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_login_title)
              //  .content(R.string.dialog_login_message)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .customView(R.layout.login_fragment, false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        EditText editTextUser = (EditText)view.findViewById(R.id.login_dialog_etEmail);
                        EditText editTextPassword = (EditText)view.findViewById(R.id.login_dialog_etPassword);


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

                        KdUINOAPIService service = ((kdUINOApplication) getApplication()).getAPIService();
                        service.login(userName, password, new Callback<JSONObject>() {
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
                                KdUINOAPIService service = ((kdUINOApplication) getApplication()).getAPIService();
                                service.setApiToken(token);


                                SharedPreferences settings = getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
                                if (settings.getString("user_name", "").equals(userName)) {

                                    if (settings.getInt("user_id", 0) != 0) {
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

                                                    SharedPreferences settings = getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
                                                    settings.edit().putInt("user_id", Integer.parseInt(
                                                            userRecievedDefinition.UserID)).commit();
                                                    settings.edit().putString("user_name", userName).commit();

                                                    break;
                                                }
                                            }
                                        } catch (Exception ex) {
                                            String p = ex.getMessage();
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .positiveText(R.string.login_dialog)
                .negativeText(R.string.cancel_dialog)
                .build();

        dialog.show();
    }

    /*
    private void sendMeasurements(String buoyID, DataSet dataset)
    {
        KdUINOAPIService service = ((kdUINOApplication)getApplication()).getAPIService();
        ArrayList<CapturesDefinition> list = new ArrayList<CapturesDefinition>();

        KDUINOBuoy buoy = KDUINOBuoy.findById(KDUINOBuoy.class, dataset.getBuoy().BuoyID);
        String lastDate = buoy.LastDataRecieved;

        for (Analysis analysis:  dataset.getAnalysises())
        {

            if (Double.isNaN(analysis.R2))
            {
                continue;
            }

            if (Double.isNaN(analysis.Kd))
            {
                continue;
            }

            if (analysis.date == null)
            {
                continue;
            }


            Captures captures = new Captures(analysis);

            if (Double.isNaN(captures.R2))
            {
                continue;
            }

            if (Double.isNaN(captures.Kd))
            {
                continue;
            }

            lastDate = captures.date;
            CapturesDefinition capturesDefinition = new CapturesDefinition(captures, buoyID);
            list.add(capturesDefinition);

        }

        buoy.LastDataRecieved = lastDate;
        buoy.save();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeSpecialFloatingPointValues();
        Gson gson = gsonBuilder.create();


        MeasurementPostData measurementPostData =
                new MeasurementPostData(buoyID,
                        list);
        String toTest = "";
        try
        {
            toTest = gson.toJson(measurementPostData);
            Log.i(kdUINOApplication.TAG, "Kduino data: " + toTest);
        }
        catch (Exception ex)
        {
            Log.e(kdUINOApplication.TAG, "Error generating gson", ex);
        }


        Log.i(kdUINOApplication.TAG, "TOSEND: " + measurementPostData);

        if (!service.isAthenticated())
        {
            this.openAuthenticationDialog(0);
        }


        service.createMeasures(measurementPostData, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //String p = "lucky";
                if (sendingDialog.isShowing()) {
                    sendingDialog.dismiss();
                }

                DisplayUtilities.ShowLargeMessage(getString(R.string.data_sended_correct),
                        "",
                        mDrawer,
                        false,
                        null);
            }

            @Override
            public void failure(RetrofitError error) {
                if (sendingDialog.isShowing()) {
                    sendingDialog.dismiss();
                }

                DisplayUtilities.ShowLargeMessage(getString(R.string.data_error_sending_correct),
                        "",
                        mDrawer,
                        false,
                        null);
            }
        });
    }
        */
}
