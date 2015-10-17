// Copyright 2012 Square, Inc.
package io.bega.kduino.services.upload;

import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.FileObjectQueue.Converter;
import com.squareup.tape.ObjectQueue;
import com.squareup.tape.TaskQueue;

import java.io.File;
import java.io.IOException;

import io.bega.kduino.services.ConnectivityService;
import io.bega.kduino.services.StorageService;

public class UploadDataTaskQueue extends TaskQueue<UploadDataTask> {
  private static final String FILENAME = "upload_data_task_queue";

  private final Context context;
  private final Bus bus;

  private UploadDataTaskQueue(ObjectQueue<UploadDataTask> delegate, Context context, Bus bus) {
    super(delegate);
    this.context = context;
    this.bus = bus;
    bus.register(this);

    if (size() > 0) {
      if (ConnectivityService.isOnline(context)) {


        startService();
      }
    }
  }

  public void start()
  {
    if (size() > 0) {
      if (ConnectivityService.isOnline(context)) {
        startService();
      }
    }
  }

  private void startService() {
    if (ConnectivityService.isOnline(context)) {
      context.startService(new Intent(context, UploadDataTaskService.class));
    }
  }

  @Override public void add(UploadDataTask entry) {
    super.add(entry);
    bus.post(produceSizeChanged());
    startService();
  }

  @Override public void remove() {
    super.remove();
    bus.post(produceSizeChanged());
  }

  @SuppressWarnings("UnusedDeclaration") // Used by event bus.
  public UploadDataQueueSizeEvent produceSizeChanged() {
    return new UploadDataQueueSizeEvent(size());
  }

  public static UploadDataTaskQueue create(Context context, Gson gson, Bus bus) {
    Converter<UploadDataTask> converter = new GsonConverter<UploadDataTask>(gson, UploadDataTask.class);
    String path = new StorageService(context, null).getApplicationDataDirectory();

    File queueFile = new File(path, FILENAME);
    FileObjectQueue<UploadDataTask> delegate;
    try {
      delegate = new FileObjectQueue<UploadDataTask>(queueFile, converter);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create file queue.", e);
    }
    return new UploadDataTaskQueue(delegate, context, bus);
  }
}
