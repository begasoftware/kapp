// Copyright 2012 Square, Inc.
package io.bega.kduino.services.upload;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
//import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.tape.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.api.KdUINOAPIService;
import io.bega.kduino.api.KdUINOApiManager;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.BuoyPostDefinition;
import io.bega.kduino.datamodel.Captures;
import io.bega.kduino.datamodel.CapturesDefinition;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.Measurement;
import io.bega.kduino.datamodel.MeasurementDefinition;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.datamodel.User;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.services.ComputeKdService;
import io.bega.kduino.services.ProcessDataService;
import io.bega.kduino.services.StorageService;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;

import static com.github.kevinsawicki.http.HttpRequest.post;

/** Uploads the specified file to imgur.com. */
public class UploadDataTask implements Task<UploadDataTask.Callback> {
  private static final long serialVersionUID = 126142781146165256L;
    private static final String EXT_FILE = ".txt";
  private static final String TAG = "Tape:kUINOApplication";
  private static final String IMGUR_API_KEY = "74e20e836f0307a90683c4643a2b656e";
  private static final String DATA_UPLOAD_URL = "http://posttestserver.com/post.php?dir=jordi";
  private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    private final static String STORAGE_PATH = "/kduino_data_files";

  public interface Callback {
    void onSuccess(String url);
    void onFailure();
  }

  private String fileName = null;



    private transient kdUINOApplication application;


    public void setApplication(kdUINOApplication application)
    {
        this.application = application;
    }



  /*public UploadDataTask(DataSet dataSet, KdUINOAPIService currentService)
  {
      Gson gson = new Gson();

      this.service = currentService;
      String buoy =  "";
      String measurements = "";
      ArrayList<CapturesDefinition> capturesList = new ArrayList<CapturesDefinition>();
      try
      {
          buoyPostDefinition = new BuoyPostDefinition(dataSet.getBuoy());

          buoy = gson.toJson(dataSet.getBuoy());
          /*StringBuilder sb = new StringBuilder();
          for (Analysis analysis : dataSet.getAnalysises())
          {
              if (Double.isNaN(analysis.Kd))
              {
                  continue;
              }
              if (Double.isNaN(analysis.R2))
              {
                  continue;
              }

              Captures captures = new Captures(analysis);
              CapturesDefinition capturesDefinition = new CapturesDefinition(analysis);
              capturesList.add(capturesDefinition);
              try {
                  String data = gson.toJson(capturesDefinition);
                  sb.append(data);
              }
              catch (Exception ex)
              {
                  String test = ex.getMessage();
              }
          }

          measurements = sb.toString();
          // measurements = gson.toJson(dataSet.getAnalysises());

      } catch (Exception ex)
      {
          Log.i(kdUINOApplication.TAG, "Error parsing", ex);
      }

      Log.i(kdUINOApplication.TAG, buoy);
      Log.i(kdUINOApplication.TAG, measurements);
  } */

  public UploadDataTask(String fileName)

  {
      this.fileName = fileName;
  }


    public String recoverdata(File file){

        InputStreamReader fis2 = null;
        BufferedReader br2=null;
        String data;
        int i=0;
        final char endata='+';
        StringBuilder sbdata=new StringBuilder();

        try {
            // Creamos un objeto InputStreamReader, que será el que nos permita
            // leer el contenido del archivo de texto.
            FileInputStream fileInputStream = new FileInputStream(file);
            fis2 = new InputStreamReader(fileInputStream);
            // Creamos un objeto buffer, en el que iremos almacenando el contenido
            // del archivo.
            br2 = new BufferedReader(fis2);
            StringBuilder sb = new StringBuilder();
            do {
                 data =br2.readLine();
                if (data == null)
                {
                    break;
                }
                sb.append(data + "\r\n");

            } while (data != null);
            //leemos la información


            // Cerramos el flujo de lectura del archivo.
            br2.close();
            fis2.close();

            //Mandamos solo los datos sin el ack="+"


            return sb.toString();

        }
        catch (Exception e4)
        {

            return "";

        }
    }

  @Override public void execute(final Callback callback) {
    // Image uploading is slow. Execute HTTP POST on a background thread.
    new Thread(new Runnable() {
      @Override public void run() {
        try {

            String typeDataSet = DataSet.TYPE_ALL;
            KdUINOApiManager managerApi = new KdUINOApiManager(application);
            File file = getFile(callback);
            if (file == null) return;

            if (fileName.startsWith("test"))
            {
                typeDataSet = DataSet.TYPE_ONE;
            }

            String datareceived = recoverdata(file);
            ProcessDataService processDataService = new ProcessDataService();
            ComputeKdService service = new ComputeKdService();
            managerApi.registerOrGetUserSynchro();
            String userId = getUser();
            KDUINOBuoy kduinoBuoy = null;

            // testear si devuelven todas las boyas.
            // Arreglar el tema da la boya escoger la ultima y recalcular el gps
            Iterator<KDUINOBuoy> buoys = KDUINOBuoy.findAll(KDUINOBuoy.class);
            if (buoys.hasNext()) {
                kduinoBuoy = buoys.next();
            }


            BuoyDefinition buoy = new BuoyDefinition(
                    userId,
                    kduinoBuoy.getId(),
                    kduinoBuoy.Name,
                    kduinoBuoy.Maker,
                    kduinoBuoy.User,
                    kduinoBuoy.MacAddress,
                    kduinoBuoy.Lat,
                    kduinoBuoy.Lon,
                    kduinoBuoy.Sensors,
                    kduinoBuoy.getSensors()
                );



            ArrayList<Measurement> measurements = processDataService.proceeAllData(buoy, datareceived);
            ArrayList<Analysis> analysises = new ArrayList<Analysis>();
            List<Sensor> list =  kduinoBuoy.getSensors();
                //Sensor.find(Sensor.class, "buoy_id = ?", Long.toString(kduinoBuoy.getId()));
            for (int i = 0; i < measurements.size(); i++) {
                Measurement measurement = measurements.get(i);
                if (measurement != null) {
                    Analysis analysis = service.ComputeLinearRegresion(list, measurement);
                    analysises.add(analysis);
                    final int counter = i;
                }
            }

            DataSet dataset = new DataSet(
                    typeDataSet,
                    buoy);
            dataset.setAnalysises(analysises);
            JsonObject object = managerApi.sendDataToServer(dataset, userId);
            if (object == null)
            {
                Log.i(kdUINOApplication.TAG,  "error");
            }

            deleteFile(file);

            // Get back to the main thread before invoking a callback.
            MAIN_THREAD.post(new Runnable() {
                  @Override
                  public void run() {
                      callback.onSuccess(fileName);
                  }
            });
        } catch (RuntimeException e) {
          Log.e(kdUINOApplication.TAG, "error: " + e.getMessage(), e);

        }
      }
    }).start();
  }

    @Nullable
    private File getFile(final Callback callback) {
        if (fileName == null) {
            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess("none_null");
                }
            });
            return null;
        }

        File file = checkFile(callback);
        if (file == null) return null;

        if (!fileName.endsWith(".txt"))
        {
            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess("none_ext");
                }
            });
            return null;
        }
        return file;
    }

    private void deleteFile(File file) {
        try {
            boolean result = file.delete();
        }
        catch(Exception ex)
        {
            Log.e(kdUINOApplication.TAG, "Error deleting temporal file", ex);
        }
    }

    private String getUser() {
        String userId = "";
        Iterator<User> users = User.findAll(User.class);
        User user = null;
        if (users.hasNext())
        {
            user = users.next();
        }

        userId = user.UserId;
        return userId;
    }

    @Nullable
    private File checkFile(final Callback callback) {
        File data_user = Environment.getExternalStorageDirectory();
        File file = new File(data_user.getAbsolutePath() + STORAGE_PATH, fileName);
        if (file == null || !file.exists()) {
            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess("none");
                }
            });
            return null;
        }
        return file;
    }
}
