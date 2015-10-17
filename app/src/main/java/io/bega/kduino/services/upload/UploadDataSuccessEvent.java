// Copyright 2012 Square, Inc.
package io.bega.kduino.services.upload;

public class UploadDataSuccessEvent {
  public final String url;

  public UploadDataSuccessEvent(String url) {
    this.url = url;
  }
}
