package io.bega.kduino.api;


import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.BuoyPostDefinition;
import io.bega.kduino.datamodel.CapturesDefinition;
import io.bega.kduino.datamodel.GeoLocationServiceResponse;
import io.bega.kduino.datamodel.KdUINOErrorHandler;
import io.bega.kduino.datamodel.LastId;
import io.bega.kduino.datamodel.MeasurementPostData;
import io.bega.kduino.datamodel.RecoveryPassword;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.UserDefinition;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by usuario on 16/08/15.
 */
public class KdUINOAPIService {

    private RestAdapter.LogLevel LOG_LEVEL = RestAdapter.LogLevel.FULL;

    private Context ctx;

    private RestAdapter retrofit;

    private RestAdapter retrofitAuth;

    private RestAdapter retrofitCountryIPService;

    private OkHttpClient okHttpClient;

    private KdUINOApiToken auth_token;

    private long lastDateToken;

    public void refreshAuthToken()
    {

    }

    public void setApiToken(KdUINOApiToken token)
    {
        Time time = new Time();
        time.setToNow();
        lastDateToken = time.toMillis(false);
        this.auth_token = token;



        retrofitAuth = new RestAdapter.Builder()
                .setLogLevel(LOG_LEVEL)
                .setLog(new AndroidLog(kdUINOApplication.TAG))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {

                        if (auth_token != null) {
                            request.addHeader(
                            "Authorization", String.format("%s %s", auth_token.Type, auth_token.Token));

                        }
                    }
                })
                .setClient(new OkClient(okHttpClient))
                .setErrorHandler(new KdUINOErrorHandler())
                .setEndpoint(KdUINOApiServer.API_URL)
                .build();

    }

    public Boolean isAthenticated()
    {
        if (retrofitAuth == null)
        {
            return false;
        }



        return true;
    }

    public KdUINOAPIService(Context ctx)
    {
        this.ctx = ctx;
        okHttpClient = new OkHttpClient();
        retrofit = new RestAdapter.Builder()
                .setLogLevel(LOG_LEVEL)
                .setLog(new AndroidLog(kdUINOApplication.TAG))
                .setClient(new OkClient(okHttpClient))
                .setErrorHandler(new KdUINOErrorHandler())
                .setEndpoint(KdUINOApiServer.API_URL)
                .build();

        retrofitCountryIPService = new RestAdapter.Builder()
                .setLogLevel(LOG_LEVEL)
                .setLog(new AndroidLog(kdUINOApplication.TAG))
                .setClient(new OkClient(okHttpClient))
                .setErrorHandler(new KdUINOErrorHandler())
                .setEndpoint(KdUINOApiServer.API_GET_COUNTRY)
                .build();
    }

    public GeoLocationServiceResponse getLocationService()
    {
        final KdUINOApiServer kdUINOApiService = retrofitCountryIPService.create(KdUINOApiServer.class);

        return kdUINOApiService.getLocationSynchro();
    }

    public void getLocationService(Callback<GeoLocationServiceResponse> callback)
    {
        final KdUINOApiServer kdUINOApiService = retrofitCountryIPService.create(KdUINOApiServer.class);
        kdUINOApiService.getLocation(callback);
    }

    public void getAllUsers(Callback<String> userCallback)
    {
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);

        kdUINOApiService.getAllUser(userCallback);
    }

    public void getLastID(Callback<String> callback)
    {
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);

        kdUINOApiService.getLastId(callback);
    }


    public String getLastID()
    {
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);
        return kdUINOApiService.getLastIdSynchro();
    }


    public JsonObject createNewBuoy(BuoyPostDefinition buoy)
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        return kdUINOApiService.createBuoySynchro(buoy);

    }

    public void createNewBuoy(BuoyPostDefinition buoy, Callback<String> callback)
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        kdUINOApiService.createBuoy(buoy, callback);

    }

    public KdUINOApiToken authSynchro(String username, String password)
    {
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);
        Response response = kdUINOApiService.loginSinchro(username, password, "password", username);
        if (response != null)
        {
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
            return token;
        }

        return null;

    }

    public void recoverPassword(RecoveryPassword password, Callback<String> callback)
    {
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);
        kdUINOApiService.recoverPassword(password, callback);
    }

    public void createMeasures(MeasurementPostData measures, Callback<JsonObject> callback)
    {
        final KdUINOApiServer kdUINOAPIService = retrofitAuth.create(KdUINOApiServer.class);
        kdUINOAPIService.addMeasurement(measures, callback);
    }

    public JsonObject createMeasure(MeasurementPostData measures)
    {
        final KdUINOApiServer kdUINOAPIService = retrofitAuth.create(KdUINOApiServer.class);
        return kdUINOAPIService.addMeasurementSynchro(measures);
    }

    public void createMeasurement(CapturesDefinition definition, Callback<String> callback)
    {
        // final KdUINOAPIService kdUINOAPIService = retrofiAuth.create(
    }

    public void getAllUser(Callback<String> callback)
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        kdUINOApiService.getAllUser(callback);
    }

    public String getAllUser()
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        return kdUINOApiService.getAllUserSynchro();
    }

    public List<BuoyPostDefinition> getAllUserBuoy()
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        return kdUINOApiService.getBuoyUser();
    }


    public void login(String username, String password, Callback<JSONObject> callback)
    {
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);

        kdUINOApiService.login(username, password, "password", username, callback);
    }

    public void getAllBuoysByUser(Callback<List<BuoyDefinition>> buoys)
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        kdUINOApiService.getBuoyUser(buoys);
    }

    public List<BuoyPostDefinition> getAllBoysSynchro()
    {
        final KdUINOApiServer kdUINOApiService = retrofitAuth.create(KdUINOApiServer.class);
        return kdUINOApiService.getBuoyUser();

    }


    public void saveUser(UserDefinition user, Callback<String> callback) {

        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);
        kdUINOApiService.createUser(user, callback);
    }

    public String saveUser(UserDefinition user) {

        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);
        return kdUINOApiService.createUserSynchro(user);
    }
}
