package io.bega.kduino.api;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Call;

import org.json.JSONObject;

import java.util.List;

import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.BuoyPostDefinition;
import io.bega.kduino.datamodel.CapturesDefinition;
import io.bega.kduino.datamodel.GeoLocationServiceResponse;
import io.bega.kduino.datamodel.LastId;
import io.bega.kduino.datamodel.MeasurementPostData;
import io.bega.kduino.datamodel.Measurements;
import io.bega.kduino.datamodel.RecoveryPassword;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.datamodel.UserDefinition;
import io.bega.kduino.datamodel.UserRecievedDefinition;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by usuario on 30/07/15.
 */
public interface KdUINOApiServer {

    public static final String API_GET_COUNTRY = "http://ip-api.com";

    public static final String API_URL = "http://52.28.184.220";

    @GET("/buoy?format=json")
    void getBuoy(Callback<List<BuoyDefinition>> buoys);

    @GET("/buoy/user")
    void getBuoyUser(Callback<List<BuoyDefinition>> buoys);

    @GET("/buoy/user")
    List<BuoyPostDefinition> getBuoyUser();

    @GET("/user/all")
    void getAllUser(Callback<String> cb);

    @GET("/user/all")
    String getAllUserSynchro();

    @GET("/user/lastid")
    void getLastId(Callback<String> cb);

    @GET("/user/lastid")
    String getLastIdSynchro();

    @POST("/user/recover")
    void recoverPassword(@Body RecoveryPassword password, Callback<String> cb);

    @POST("/user/recover")
    String recoverPasswordSynchro(@Body RecoveryPassword password);

    @GET("/json")
    GeoLocationServiceResponse getLocationSynchro();

    @GET("/json")
    void getLocation(Callback<GeoLocationServiceResponse> callback);



    @POST("/buoy/")
    void createBuoy(@Body BuoyPostDefinition buoy, Callback<String> cb);

    @POST("/buoy/")
    JsonObject createBuoySynchro(@Body BuoyPostDefinition buoy);

    @PUT("/user/")
    void createUser(@Body UserDefinition user, Callback<String> cb);

    @PUT("/user/")
    String createUserSynchro(@Body UserDefinition user);

    @POST("/measurements/")
    void addMeasurement(@Body MeasurementPostData measurements, Callback<JsonObject> callback);

    @POST("/measurements/")
    JsonObject addMeasurementSynchro(@Body MeasurementPostData measurements);

    @FormUrlEncoded
    @POST("/oauth2/access_token")
    void login(@Field("username") String username,
               @Field("password") String password,
               @Field("grant_type") String grant_type,
               @Field("client_id") String clientID,
               @Field("callback") Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/oauth2/access_token")
    Response loginSinchro(@Field("username") String username,
               @Field("password") String password,
               @Field("grant_type") String grant_type,
               @Field("client_id") String clientID
    );




}
