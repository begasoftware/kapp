package io.bega.kduino.services;

import java.io.IOException;
import java.util.List;

/**
 * Created by usuario on 18/07/15.
 */
public interface IBluetoothServiceManager {

    void sendMessage(String message) throws IOException;

    String recieveMessage(ICounter counter, boolean needsCounter) throws IOException;

    Boolean isConnected();

    void connect(String address);

    void disconnect();

    void registerReciever();

    void unregisterReciever();
}
