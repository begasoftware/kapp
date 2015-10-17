//package io.bega.kduino.api;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//// import com.tapformenu.app.BuildConfig;
////import com.tapformenu.app.TFMApp;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.bega.kduino.BuildConfig;
//import io.bega.kduino.IApiListener;
//import io.bega.kduino.KdUINOApp;
//import io.bega.kduino.datamodel.Buoy;
//import io.bega.kduino.datamodel.User;
//
//public class ApiServer {
//
//    public static final String API_HOST = "http://http://52.28.184.220/";
//
//    public static final String API_RESOURCE_BUOYS = "buoys";
//
//    public static final String API_RESOURCE_USERS = "users";
//
//    public static final String API_REQUEST_BUOYS                 = "buoys";
//    public static final String API_REQUEST_USERS                 = "users";
//
//
//    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//
//
//    public static RequestQueue requestQueue;
//
//    public ApiServer( Context context )
//    {
//        requestQueue = Volley.newRequestQueue(context);
//    }
//
//
//    public void setMeasurements(final IApiListener listener)
//    {
//        String url =  API_HOST + API_RESOURCE_BUOYS;
//
//        JsonObjectRequest request;
//        request = new JsonObjectRequest(Request.Method.POST, url,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response)
//                    {
//                        String listOfBuoys="";
//                        try {
//                            listOfBuoys = response.getString("results");
//                        }
//                        catch(Exception e){
//                            e.printStackTrace();
//                        }
//
//                        if( BuildConfig.DEBUG ) Log.d(KdUINOApp.TAG, "response = " + listOfBuoys);
//                        Gson gson = new GsonBuilder().setDateFormat( DATE_FORMAT ).create();
//                        ArrayList<Buoy> buoys =  gson.fromJson( listOfBuoys, new TypeToken<List<Buoy>>() {
//                        }.getType());
//
//                        listener.onRequestSuccess( API_REQUEST_BUOYS, buoys);
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//
//                        if( BuildConfig.DEBUG ) Log.d(KdUINOApp.TAG, "error = " + error.getMessage());
//                        // TODO: Parse the returned error
//                    }
//                }
//        );
//        requestQueue.add(request);
//
//
//    }
//
//
//    public void buoys(final IApiListener listener){
//
//        String url =  API_HOST + API_RESOURCE_BUOYS;
//
//       // String url = BuildConfig.API_HOST + API_RESOURCE_RESTAURANTS + "(?lon=0)"; // TODO: Remove the query at the end, which was for Apiary's sample server.
//       // if( BuildConfig.DEBUG ) Log.d(TFMApp.TAG, API_REQUEST_RESTAURANTS + " (" + url + ")...");
//
//
//
//        JsonObjectRequest request;
//        request = new JsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response)
//                    {
//                        String listOfBuoys="";
//                        try {
//                            listOfBuoys = response.getString("results");
//                        }
//                        catch(Exception e){
//                            e.printStackTrace();
//                        }
//
//                        if( BuildConfig.DEBUG ) Log.d(KdUINOApp.TAG, "response = " + listOfBuoys);
//                        Gson gson = new GsonBuilder().setDateFormat( DATE_FORMAT ).create();
//                        ArrayList<Buoy> buoys =  gson.fromJson( listOfBuoys, new TypeToken<List<Buoy>>() {
//                        }.getType());
//
//                        listener.onRequestSuccess( API_REQUEST_BUOYS, buoys);
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//
//                        if( BuildConfig.DEBUG ) Log.d(KdUINOApp.TAG, "error = " + error.getMessage());
//                        // TODO: Parse the returned error
//                    }
//                }
//        );
//        requestQueue.add(request);
//
//
//    }
//
//
//    public void users(final IApiListener listener){
//
//        //String url = BuildConfig.API_HOST + API_RESOURCE_RESERVATIONS;
//        // if( BuildConfig.DEBUG ) Log.d(TFMApp.TAG, API_REQUEST_RESERVATIONS);
//
//        String url = API_HOST + API_REQUEST_USERS;
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response)
//                    {
//                        String listReservations="";
//                        try {
//                            listReservations = response.getString("results");
//                        }
//                        catch(Exception e){
//                            e.printStackTrace();
//                        }
//                        if( BuildConfig.DEBUG ) Log.d(KdUINOApp.TAG, "response = " + listReservations);
//                        Gson gson = new GsonBuilder().setDateFormat( DATE_FORMAT ).create();
//                        ArrayList<User> reservations =  gson.fromJson( listReservations, new TypeToken<List<User>>() {
//                        }.getType());
//
//                        listener.onRequestSuccess( API_REQUEST_USERS, reservations );
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//                        if( BuildConfig.DEBUG ) Log.d(KdUINOApp.TAG, "error = " + error.getMessage());
//                        // TODO: Parse the returned error
//                    }
//                });
//        requestQueue.add(request);
//
//
//    }
//
//}
