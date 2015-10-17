package io.bega.kduino;

public interface IApiListener {

    public void onRequestSuccess(String tag, Object object);

    public void onRequestError(String tag, Object object);

}
