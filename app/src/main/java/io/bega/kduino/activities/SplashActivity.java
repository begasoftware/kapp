package io.bega.kduino.activities;

import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOApiManager;
import io.bega.kduino.api.KdUINOApiToken;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.ConnectivityService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends Activity implements Callback<JSONObject>, View.OnClickListener {

    /**
     *  Milliseconds of delay of the SplashScreen
     */
    private static long SPLASH_DELAY = 4000; //4 seconds

    Bitmap bitmap;

    KdUINOAPIService service;

    Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        this.timer = null;
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.kduino_logo,
                options);
        ImageView imageView = (ImageView)findViewById(R.id.activity_splash_relative_layout_image);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(this);

        /*if (ConnectivityService.isOnline(this))
        {
            // goToLogin();
            return;
        } */
        

        /*if (ConnectivityService.isOnline(this)) {
            SettingsManager manager = new SettingsManager(this);
            if (manager.getFirstTime()) {
                String test = manager.getUniqueID();

            }
        } */


        if (ConnectivityService.isOnline(this)) {

            KdUINOApiManager kdUINOApiManager = new KdUINOApiManager(this);
            kdUINOApiManager.registerOrGetUser();
            kdUINOApiManager.login();
        }

        goToMain();

    }

    @Override
    protected void onDestroy() {
        this.service = null;
        bitmap.recycle();
        bitmap = null;
        super.onDestroy();
        //System.gc();
    }

    @Override
    public void success(JSONObject jsonObject, Response response) {





    }

    private void goToMain() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d(kdUINOApplication.TAG, "goToMain -> Timer released");

                Intent mainIntent =
                        new Intent().setClass(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);

                finish();//Destruimos esta activity para prevenit que el usuario retorne aqui presionando el boton Atras.
            }
        };

        if (timer == null) {
            timer = new Timer();
            timer.schedule(task, SPLASH_DELAY);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        // goToLogin();

    }

   /*private void goToLogin() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d(kdUINOApplication.TAG, "goToLogin -> Timer released");

                Intent loginIntent =
                        new Intent().setClass(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        };

        if (timer == null) {

            timer = new Timer();
            timer.schedule(task, SPLASH_DELAY);
        }
    } */

    @Override
    public void onClick(View v) {
        if (timer != null)
        {
            timer.cancel();
        }

        Intent mainIntent =
                new Intent().setClass(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);

        finish();
    }
}
