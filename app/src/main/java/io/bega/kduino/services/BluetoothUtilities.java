package io.bega.kduino.services;




import java.io.IOException;
import android.content.Context;
import android.view.View;

import io.bega.kduino.utils.DisplayUtilities;


/**
 * Utilidades sobre el Bluetooth
 */
public class BluetoothUtilities  {

    //creamos las variables
    private Context mcontext;
    private View view;


    public BluetoothUtilities(Context context,View view) {

        mcontext = context;
        this.view=view;

    }


    //leemos la información que nos envia el dispositivo emarejado hasta que nos llega el ack=


    //enviamos la información
    void sendData(String message) {

        // TODO Auto-generated method stub
        byte[] msgBuffer = message.getBytes();


   /*     try {
//            Connect_bluetooth.outStream.write(msgBuffer);

        } catch (IOException e) {
            e.printStackTrace();

            General_utilities.Mostrarmensajelargo(mcontext,view,"Data can't be send,an exception occurred during write");


            //getmacaddress() des de connect
  /*          if (addressboia.equals("00:00:00:00:00:00")) {

                General_utilities.Mostrarmensajelargo(mcontext, view, "No IP adress");

            } */
     //   }


    }
}
