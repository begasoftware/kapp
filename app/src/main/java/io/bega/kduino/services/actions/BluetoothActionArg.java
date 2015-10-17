package io.bega.kduino.services.actions;

import android.app.Activity;

import io.bega.kduino.IFinishBluetoothActionCallBack;
import io.bega.kduino.services.BluetoothService;
import io.bega.kduino.services.StorageService;

/**
 * Created by usuario on 16/08/15.
 */
public class BluetoothActionArg {

    private Activity activity;

    private BluetoothService service;

    private StorageService fileutilities;

    private IFinishBluetoothActionCallBack callBack;

    public BluetoothActionArg()
    {

    }
}
