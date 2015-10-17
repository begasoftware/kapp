package io.bega.kduino.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.bega.kduino.SettingsManager;
import io.bega.kduino.activities.MainActivity;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.BuoyPostDefinition;
import io.bega.kduino.datamodel.Captures;
import io.bega.kduino.datamodel.CapturesDefinition;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.DistanceBuoy;
import io.bega.kduino.datamodel.GeoLocationServiceResponse;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.MeasurementPostData;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.UserDefinition;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.LocationService;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by usuario on 23/09/15.
 */
public class KdUINOApiManager {

    private String fakeID = "";

    String countryIP = "http://ip-api.com/json";

    Activity activity;

    SettingsManager settingsManager;

    KdUINOAPIService service;

    private String userName;

    private String lastId;

    private boolean IsUserRegister;

    private User user = null;

    private Bus bus = null;

    private float METERS_SAME_BUOY = 20;

    public KdUINOApiManager(Activity ctx)
    {
        settingsManager = new SettingsManager(ctx);
        activity = ctx;
        this.service = ((kdUINOApplication)this.activity.getApplication()).getAPIService();
    }

    public KdUINOApiManager(kdUINOApplication application)
    {
        settingsManager = new SettingsManager(application.getApplicationContext());
        this.service = application.getAPIService();
        this.bus = application.getBus();
    }

    public void destroy()
    {
        this.service = null;
    }

    public String getIdUser()
    {
        return settingsManager.getUserId() + "";
    }

    public void login()
    {

        if (settingsManager.getUsername().length() > 0 && settingsManager.getPassword().length() > 0)
        {
            //if (!this.service.isAthenticated())
            //{

            this.userName = settingsManager.getUsername();
            this.service.login(settingsManager.getUsername(), settingsManager.getPassword(), new Callback<JSONObject>() {
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

                                        settingsManager.setUserID(Integer.parseInt(
                                                userRecievedDefinition.UserID));


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

                    service.getLastID(new Callback<String>() {
                        @Override
                        public void success(String lastIdData, Response response) {
                            lastId = lastIdData;
                            String email = settingsManager.getUniqueID() + "@kduino.org";
                            String password = "A12345B";
                            user = new User(lastIdData,
                                    email,
                                    "Unknown",
                                    email,
                                    password);
                            user.save();

                            settingsManager.setUsername(user.Name);
                            settingsManager.setPassword(user.Password);

                            UserDefinition userDefinition =
                                    new UserDefinition(user);

                            Gson gson = new Gson();
                            String test = gson.toJson(userDefinition);
                            service.saveUser(userDefinition, new Callback<String>() {
                                        @Override
                                        public void success(String value, Response response) {
                                            user.UserId = value;
                                            user.StoredInServer = true;
                                            user.save();
                                            settingsManager.setUserID(Integer.parseInt(value));
                                            settingsManager.setPassword(user.Password);
                                            settingsManager.setUsername(user.Name);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {

                                        }
                                    }
                            );

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                }
            });

        }

    }

    // this call will be done in another thread
    public JsonObject sendDataToServer(DataSet dataset, String userID)
    {
        if (!service.isAthenticated())
        {
            KdUINOApiToken token = service.authSynchro(settingsManager.getUsername(), settingsManager.getPassword());
            service.setApiToken(token);
        }

        String BuoyID = "";


        List<BuoyPostDefinition> buoys =  service.getAllUserBuoy();
        ArrayList<DistanceBuoy> distances = new ArrayList<DistanceBuoy>();
        for (BuoyPostDefinition buoy : buoys)
        {
            if (buoy.BuoyID.startsWith(userID))
            {
                DistanceBuoy distanceBuoy = new DistanceBuoy(buoy, dataset.getBuoy());
                distances.add(distanceBuoy);
            }
        }

        BuoyPostDefinition buoyPostDefinition = null;
        for (DistanceBuoy distanceBuoy : distances)
        {
            if (distanceBuoy.meters < METERS_SAME_BUOY)
            {
                if(distanceBuoy.getBuoyDefinition().Name
                        .equals(dataset.getBuoy().Name))
                   buoyPostDefinition = distanceBuoy.getBuoyDefinition();
            }
        }



        //SettingsManager manager =  new SettingsManager(this.activity);
        ArrayList<CapturesDefinition> list = new ArrayList<CapturesDefinition>();


        if (buoyPostDefinition == null) {
            BuoyID = userID + String.format("%03d", dataset.getBuoy().BuoyID);
            buoyPostDefinition =
                    new BuoyPostDefinition(dataset.getBuoy());
            buoyPostDefinition.BuoyID = BuoyID;
            JsonObject result = service.createNewBuoy(buoyPostDefinition);
        }


        KDUINOBuoy buoy = KDUINOBuoy.findById(KDUINOBuoy.class, dataset.getBuoy().BuoyID);
        String lastDate = buoy.LastDataRecieved;
        ParsePosition parsePosition = new ParsePosition(0);
        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        //SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date finalDate = null;

        try
        {
            finalDate = formatFrom.parse(buoy.LastDataRecieved, parsePosition);
            //finalDate = formatTo.format(datevalue);
        }
        catch(Exception ex)
        {

        }



        for (Analysis analysis:  dataset.getAnalysises())
        {
            Date currentAnalysisDate = formatFrom.parse(analysis.date, new ParsePosition(0));
            if (currentAnalysisDate == null)
            {
                continue;
            }

            if (finalDate != null) {
                if (currentAnalysisDate.before(finalDate)) {
                    continue;
                }

                if (currentAnalysisDate.equals(finalDate)) {
                    continue;
                }
            }


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
            CapturesDefinition capturesDefinition = new CapturesDefinition(captures, BuoyID);
            list.add(capturesDefinition);

        }
        if (list.size() > 1) {
            buoy.LastDataRecieved = lastDate;
            buoy.save();
        }



        MeasurementPostData measurementPostData =
                new MeasurementPostData(BuoyID,
                        list);


        if (list.size() > 0) {
            return service.createMeasure(measurementPostData);
        }

        return null;


        //bus.post(new KdUINOMessages(KdUINOMessages.DATA_SEND_OK));
        /*service.createNewBuoy(buoyPostDefinition, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                sendMeasurements(BuoyID, currentDataSet);
                sendingDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                sendingDialog.dismiss();
            }
        }); */

    }

    public boolean checkIfAuthFailAndLogin()
    {
        boolean result = true;
        String email = settingsManager.getUniqueID() + fakeID + "@kduino.org";
        String password = "A12345B";
        try {
            KdUINOApiToken token = service.authSynchro(email, password);
            if (token == null)
            {
                result = false;
            }
            else
            {
                this.service.setApiToken(token);
            }
        }
        catch (Exception ex)
        {
            String message = ex.getMessage();
            return false;
        }


        return result;

    }

    public void saveUserSynchro()
    {

        String lastId =  service.getLastID();
        String email = settingsManager.getUniqueID() + fakeID + "@kduino.org";
        String password = "A12345B";

        GeoLocationServiceResponse geoLocationServiceResponse =
                service.getLocationService();
        User.deleteAll(User.class);
        user = new User(lastId,
                email,
                geoLocationServiceResponse.Country,
                email,
                password);
        user.save();

        settingsManager.setUsername(user.Name);
        settingsManager.setPassword(user.Password);
        UserDefinition userDefinition =
                new UserDefinition(user);
        Gson gson = new Gson();
        String test = gson.toJson(userDefinition);
        String id = "";
        try {
            id = service.saveUser(userDefinition);
        }
        catch(Exception ex)
        {
            String message = ex.getMessage();
            return;
        }

        user.UserId = id;
        user.StoredInServer = true;
        user.save();
        settingsManager.setUserID(Integer.parseInt(id));
        settingsManager.setPassword(user.Password);
        settingsManager.setUsername(user.Name);
        KdUINOApiToken response = this.service.authSynchro(email, password);
        this.service.setApiToken(response);
    }

    public void checkAllbuoyFromUser()
    {
        int userID = settingsManager.getUserId();
        String userIDData = Integer.toString(userID);
        List<BuoyPostDefinition> list = service.getAllBoysSynchro();
        for (BuoyPostDefinition buoy : list)
        {

                if (buoy.BuoyID.startsWith(userIDData)) {

                    List<KDUINOBuoy> listOfKduinos =
                            KDUINOBuoy.find(KDUINOBuoy.class,
                                    "kduino_id=?", buoy.BuoyID);
                    if (listOfKduinos.size() == 0) {
                        KDUINOBuoy newBuoy = new KDUINOBuoy(buoy.BuoyID,
                                buoy.Name,
                                buoy.Maker,
                                buoy.User,
                                "",
                                buoy.Lat,
                                buoy.Lon,
                                buoy.Sensors);
                        newBuoy.save();
                    }
                }
        }
    }

    public void registerOrGetUserSynchro() {
        if (!checkIfAuthFailAndLogin())
        {
            this.saveUserSynchro();

        }
        else {
            this.checkAllbuoyFromUser();
        }
    }

    public void registerOrGetUser() {
        Iterator<User> users = User.findAll(User.class);
        if (users.hasNext()) {
            user =  users.next();

            if (settingsManager.getUsername().length() == 0)
            {
                settingsManager.setUsername(user.Name);

            }

            if (settingsManager.getPassword().length() == 0)
            {
                settingsManager.setPassword(user.Password);
            }

            return;
        }

        String email = settingsManager.getUniqueID() + "@kduino.org";
        String password = "A12345B";
        settingsManager.setUsername(email);
        settingsManager.setPassword(password);

        this.service.login(settingsManager.getUsername(), settingsManager.getPassword(), new Callback<JSONObject>() {
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

                                    settingsManager.setUserID(Integer.parseInt(
                                            userRecievedDefinition.UserID));

                                    List<User> usersLocal =
                                            User.find(User.class, "user_id= ?",
                                                    userRecievedDefinition.UserID);
                                    if (usersLocal.size() == 0) {
                                        User user = new User(userRecievedDefinition.UserID,
                                                userRecievedDefinition.UserName,
                                                "Unknown",
                                                userRecievedDefinition.UserName,
                                                "A12345B");
                                        user.save();
                                        user.StoredInServer = true;
                                    }
                                    service.getAllBuoysByUser(new Callback<List<BuoyDefinition>>() {
                                        @Override
                                        public void success(List<BuoyDefinition> buoyDefinitions, Response response) {
                                            int userID = settingsManager.getUserId();
                                            String userIDData = Integer.toString(userID);
                                            for (BuoyDefinition buoy : buoyDefinitions)
                                            {
                                                String id = Long.toString(buoy.BuoyID);
                                                if (id.startsWith(userIDData))
                                                {
                                                    try {
                                                        List<KDUINOBuoy> listOfKduinos =
                                                                KDUINOBuoy.find(KDUINOBuoy.class,
                                                                        "kduino_id=?", id);
                                                        if (listOfKduinos.size() == 0) {
                                                            KDUINOBuoy newBuoy = new KDUINOBuoy(id,
                                                                    buoy.Name,
                                                                    buoy.Maker,
                                                                    buoy.User,
                                                                    "",
                                                                    buoy.Lat,
                                                                    buoy.Lon,
                                                                    buoy.Sensors);
                                                            newBuoy.save();
                                                            // TODO Add new kduino.
                                                        }
                                                    }
                                                    catch(Exception ex)
                                                    {
                                                        Log.e(kdUINOApplication.TAG, "error seraching buoys", ex);
                                                    }
                                                }

                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {

                                        }
                                    });



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

                service.getLastID(new Callback<String>() {
                    @Override
                    public void success(String lastIdData, Response response) {
                        lastId = lastIdData;
                        String email = settingsManager.getUniqueID() + "@kduino.org";
                        String password = "A12345B";
                        user = new User(lastIdData,
                                email,
                                "Unknown",
                                email,
                                password);
                        user.save();

                        settingsManager.setUsername(user.Name);
                        settingsManager.setPassword(user.Password);

                        UserDefinition userDefinition =
                                new UserDefinition(user);

                        Gson gson = new Gson();
                        String test = gson.toJson(userDefinition);
                        service.saveUser(userDefinition, new Callback<String>() {
                                    @Override
                                    public void success(String value, Response response) {
                                        user.UserId = value;
                                        user.StoredInServer = true;
                                        user.save();
                                        settingsManager.setUserID(Integer.parseInt(value));
                                        settingsManager.setPassword(user.Password);
                                        settingsManager.setUsername(user.Name);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                }
                        );

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            }
        });




        /*service.getLastID(new Callback<String>() {
            @Override
            public void success(String lastIdData, Response response) {
                lastId = lastIdData;
                String email = settingsManager.getUniqueID() + "@kduino.org";
                String password = "A12345B";
                user = new User(lastIdData,
                        email,
                        "Unknown",
                        email,
                        password);
                user.save();

                settingsManager.setUsername(user.Name);
                settingsManager.setPassword(user.Password);

                UserDefinition userDefinition =
                        new UserDefinition(user);

                Gson gson = new Gson();
                String test = gson.toJson(userDefinition);
                service.saveUser(userDefinition, new Callback<String>() {
                            @Override
                            public void success(String value, Response response) {
                                user.UserId = value;
                                user.StoredInServer = true;
                                user.save();
                                settingsManager.setUserID(Integer.parseInt(value));
                                settingsManager.setPassword(user.Password);
                                settingsManager.setUsername(user.Name);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        }
                );

            }

            @Override
            public void failure(RetrofitError error) {

            }
        }); */
    }



    public void signup()
    {

    }
}
