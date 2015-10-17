package io.bega.kduino;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orm.SugarApp;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/*import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.ObjectGraph;
import dagger.Provides;
import dagger.internal.Modules; */
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.datamodel.Status;
import io.bega.kduino.services.upload.UploadDataTaskQueue;
//import io.bega.kduino.api.ApiServer;


public class kdUINOApplication extends SugarApp {

  //  private ObjectGraph objectGraph;

    Status status = new Status();

    Bus bus;

    private String lastActivity;

    private static kdUINOApplication sInstance;

    KdUINOAPIService service;

    public static String TAG = "KdUINOApplication";

    public static String PREFS_NAME = "Kduino_preference_file";

    public String getLastActivity()
    {
        return this.lastActivity;
    }

    public Status getStatus()
    {
        return this.status;
    }



    public void setLastActivity(String activity)
    {
        this.lastActivity = activity;
    }

    UploadDataTaskQueue queue;

    public KdUINOAPIService getAPIService()
    {
        return this.service;
    }

    // public ApiServer apiServer;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sInstance = this;
        lastActivity = "";
       // buildObjectGraphAndInject();
        this.bus = new Bus(ThreadEnforcer.MAIN);
        this.queue = UploadDataTaskQueue.create(this, new GsonBuilder().create(), this.bus);
        this.service = new KdUINOAPIService(this);
     //   sInstance.initializeInstance();
    }


  /*  private void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(new AppModule("", this));
        objectGraph.inject(this);
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

     private void createApiRequestHandler() {
        bus.register(new ApiRequestHandler(bus, apiService));
    } */



    /*public ObjectGraph createScopedGraph(Object module) {
        return objectGraph.plus(module);
    } */

    public static kdUINOApplication get(Context context) {
        return (kdUINOApplication) context.getApplicationContext();
    }

    public Bus getBus()
    {
        return this.bus;
    }

    public UploadDataTaskQueue getQueue()
    {
        return this.queue;
    }

}
