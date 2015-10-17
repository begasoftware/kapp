// Copyright 2012 Square, Inc.
package io.bega.kduino.services.upload;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

// import javax.inject.Inject;

import io.bega.kduino.kdUINOApplication;

public class UploadDataTaskService extends Service implements UploadDataTask.Callback {
  private static final String TAG = "Tape:UploadDataTaskService";

  UploadDataTaskQueue queue;

  kdUINOApplication application;

  Bus bus;

  private boolean running;

  @Override public void onCreate() {
    super.onCreate();
    application = ((kdUINOApplication) getApplication());
    bus = ((kdUINOApplication) getApplication()).getBus();
    queue = ((kdUINOApplication)getApplication()).getQueue();

    Log.i(kdUINOApplication.TAG, "Tape:UploadDataTaskService Service starting!");
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    executeNext();
    return START_STICKY;
  }

  private void executeNext() {
    if (running) return; // Only one task at a time.

    UploadDataTask task = queue.peek();
    if (task != null) {
      running = true;

      task.setApplication(application);
      task.execute(this);
    } else {
      Log.i(kdUINOApplication.TAG, "Service stopping!");
      stopSelf(); // No more tasks are present. Stop.
    }
  }

  @Override public void onSuccess(final String url) {
    running = false;
    queue.remove();
    bus.post(new UploadDataSuccessEvent(url));
    executeNext();
  }

  @Override public void onFailure() {
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
