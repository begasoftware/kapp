// Copyright 2012 Square, Inc.
package io.bega.kduino.services.upload;

public class UploadDataQueueSizeEvent {
  public final int size;

  public UploadDataQueueSizeEvent(int size) {
    this.size = size;
  }
}
