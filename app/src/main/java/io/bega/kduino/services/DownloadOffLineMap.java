package io.bega.kduino.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.FileNotFoundException;

//import dagger.internal.Binding;
import io.bega.kduino.R;
import io.bega.kduino.activities.MainActivity;
import io.bega.kduino.api.KdUINOApiServer;
import io.bega.kduino.api.KdUINOMessages;
import io.bega.kduino.datamodel.events.KdUinoMessageBusEvent;
import io.bega.kduino.kdUINOApplication;

public class DownloadOffLineMap extends Service {

    Bus bus;

    SharedPreferences preferenceManager;

    DownloadManager downloadManager;

    String Download_ID = "DOWNLOAD_ID";

    public DownloadOffLineMap() {
    }


    @Override
    public void onCreate() {
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        this.bus = ((kdUINOApplication)getApplication()).getBus();
        this.bus.register(this);
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(downloadReceiver);
        this.bus.unregister(this);
        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent intent) {
      return mBinder;
    }

    @Subscribe
    public void getMessage(KdUinoMessageBusEvent event) {

        if (event.getMessage() == KdUINOMessages.DOWNLOAD_MAP) {

            downloadOffLineMap((int)event.getData());
            return;
        }
    }



    private final IBinder mBinder = new LocalBinder();

    private void downloadOffLineMap(int idmap)
    {
        StorageService storage = new StorageService(this, null);

        storage.deleteOfflineMapFile();
        String url = "";
        if (idmap == 7) {
            url = KdUINOApiServer.API_URL + "/static/map/KDUINO_2015-08-16_194850.zip";
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getString(R.string.dm_description));
        request.setTitle(getString(R.string.dm_title));
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long download_id =  downloadManager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir("/osmdroid", "kduinomap.zip"));

        //Save the download id
        SharedPreferences.Editor PrefEdit = preferenceManager.edit();
        PrefEdit.putLong(Download_ID, download_id);
        PrefEdit.commit();


    }

    public BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(preferenceManager.getLong(Download_ID, 0));
            Cursor cursor = downloadManager.query(query);
            return;
//            if (cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                int status = cursor.getInt(columnIndex);
//                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
//                int reason = cursor.getInt(columnReason);
//
//                if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                    //Retrieve the saved download id
//                    long downloadID = preferenceManager.getLong(Download_ID, 0);
//
//                    ParcelFileDescriptor file;
//                    try {
//                        file = downloadManager.openDownloadedFile(downloadID);
//                        Toast.makeText(getApplicationContext(),
//                                "File Downloaded: " + file.toString(),
//                               Toast.LENGTH_LONG).show();
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        /*Toast.makeText(MainActivity.this,
//                                e.toString(),
//                                Toast.LENGTH_LONG).show(); */
//                    }
//
//                } else if (status == DownloadManager.STATUS_FAILED) {
//                    /*Toast.makeText(MainActivity.this,
//                            "FAILED!\n" + "reason of " + reason,
//                            Toast.LENGTH_LONG).show(); */
//                } else if (status == DownloadManager.STATUS_PAUSED) {
//                   /* Toast.makeText(MainActivity.this,
//                            "PAUSED!\n" + "reason of " + reason,
//                            Toast.LENGTH_LONG).show(); */
//                } else if (status == DownloadManager.STATUS_PENDING) {
//                 /*    Toast.makeText(MainActivity.this,
//                            "PENDING!",
//                            Toast.LENGTH_LONG).show();*/
//                } else if (status == DownloadManager.STATUS_RUNNING) {
//                   /* Toast.makeText(MainActivity.this,
//                            "RUNNING!",
//                            Toast.LENGTH_LONG).show(); */
//                }
//            }
        }

    };
}
