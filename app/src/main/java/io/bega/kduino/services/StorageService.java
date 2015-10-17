package io.bega.kduino.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import io.bega.kduino.R;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.Profile;
import io.bega.kduino.kdUINOApplication;
import io.bega.kduino.utils.DisplayUtilities;


/**
 * Utilidades generales, sobre los archivos txt
 */
public class StorageService extends Activity{

    private final static String STORAGE_PATH = "/kduino_data_files";

    private final static String PROFILE_FILE = "kduino_user_data";

    private Context context;

    private View view;

    public StorageService(Context ctx)
    {
        this.context = ctx;
        this.checkDirectory();
    }

    public StorageService(Context ctx, View view){

        this.context = ctx;
        this.view=view;
        this.checkDirectory();
    }

    private void checkDirectory()
    {

        File dir = new File(Environment.getExternalStorageDirectory() + STORAGE_PATH);
        if(!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
    }

    public String getApplicationDataDirectory()
    {
        File dir = new File(Environment.getExternalStorageDirectory() + STORAGE_PATH );
        return dir.getAbsolutePath() + "/";
    }

    public void deleteOfflineMapFile()
    {
        String filepath = "";
        File folder = new File(
                Environment.getExternalStorageDirectory() + "/osmdroid");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
            filepath = Environment.getExternalStorageDirectory()
                    .getPath() + "/osmdroid";

            // Deleting if zip file exists
            File folder2 = Environment.getExternalStorageDirectory();
            String fileName = folder2.getPath() + "/osmdroid/world_2015-07-31_182626.zip";

            File myFile = new File(fileName);
            if(myFile.exists())
                myFile.delete();

        }
    }

    public String getOffLineMapPath() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

            return Environment.getExternalStorageDirectory() + "/osmdroid/";
        }

        return "";
    }

    //crea el txt del profile
    public  void createProfileFile (Profile profile) {


        try
        {
            File data_user = Environment.getExternalStorageDirectory();
            File file = new File(data_user.getAbsolutePath() + STORAGE_PATH, PROFILE_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(context.openFileOutput(PROFILE_FILE, Context.MODE_PRIVATE));
            osw.write(profile.toString());
            osw.flush();
            osw.close();
            if (view != null) {
                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_profile_saved),
                        "",
                        view,
                        false,
                        null);
            }


        }
        catch (IOException e) {
            Log.e(kdUINOApplication.TAG, "Can't save profile file", e);
            if (view != null) {
                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_profile_cant_saved),
                        "",
                        view,
                        false,
                        null);
            }
        }
    }


    public String retrieveData(String name)
    {
        File data_user = Environment.getExternalStorageDirectory();
        File file = new File(data_user.getAbsolutePath() + STORAGE_PATH, name);
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis) ;
            BufferedReader buffreader = new BufferedReader(isr) ;

            String readString = buffreader.readLine() ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine() ;
            }

            isr.close() ;
        } catch ( IOException ioe ) {
            ioe.printStackTrace() ;
            return "";
        }
        return datax.toString() ;
    }


    public static DataSet getData(String name)
    {
        File data_user = Environment.getExternalStorageDirectory();
        File file = new File(data_user.getAbsolutePath() + STORAGE_PATH, name);
        DataSet dataSet = null;
        try
        {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            Gson gson = new Gson();
            dataSet = (DataSet)gson.fromJson(json, DataSet.class);
        }
        catch (IOException ioEx)
        {
            Log.e(kdUINOApplication.TAG, "Error recovering data", ioEx);
        }


        return dataSet;

    }

    public  String setData(String name, DataSet data)
    {
        Gson gson = new Gson();
        String toSaveBuoy = gson.toJson(data.getBuoy());
        String toSaveData = gson.toJson(data.getAnalysises());
        String toSave = toSaveBuoy + "|" + toSaveData;

        File data_user = Environment.getExternalStorageDirectory();
        File file = new File(data_user.getAbsolutePath() + STORAGE_PATH, name);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(toSave.getBytes());
            fileOutputStream.close();
        }
        catch (IOException ioEx)
        {
            Log.e(kdUINOApplication.TAG, "Error saving data", ioEx);
            return "";
        }

        return file.getAbsolutePath();
    }

    //inserta los datos en el archivo txt de trabajo
    public String saveData (String data,String name, Boolean display)
    {
        try
        {
            File data_user = Environment.getExternalStorageDirectory();
            File file = new File(data_user.getAbsolutePath() + STORAGE_PATH, name);
            FileOutputStream stream = new FileOutputStream(file);

            //OutputStreamWriter osw = new OutputStreamWriter(context.openFileOutput(name, Context.));
            stream.write(data.getBytes());
            stream.flush();
            stream.close();

            if (display) {
                if (view != null) {
                    DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_data_saved),
                            "",
                            view,
                            false,
                            null);
                }
            }

            Log.d(kdUINOApplication.TAG, "fileSave Application Data: " + file.getAbsolutePath());

            return file.getAbsolutePath();

        }
        catch (IOException e1)
        {
            Log.e(kdUINOApplication.TAG, "can't save data in the storage file", e1);
            if (view != null) {
                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_data_cant_saved),
                        "",
                        view,
                        false,
                        null);
            }
            return new String();
        }
    }

    //inserta los datos en el archivo txt de backup
    public void insertardatatobackup (String data,String name){

        try {
            File data_user = Environment.getExternalStorageDirectory();
            File file = new File(data_user.getAbsolutePath() +  STORAGE_PATH, name);
            OutputStreamWriter osw = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_APPEND));
            osw.write(data);
            osw.flush();
            osw.close();


        }
        catch (IOException e2)
        {
            Log.e(kdUINOApplication.TAG, "can't save data in the storage file", e2);
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_data_cant_saved),
                    "",
                    view,
                    false,
                    null);
        }


    }

    //recupera los datos del archivo txt con el profile
    public Profile recuperarprofile() {

            Profile profilerecupered = new Profile();
            InputStreamReader fis = null;
            BufferedReader br = null;


            try {
                //creamos un objeto InputStreamReader, que será el que nos permita leer el contenido del archivo de texto
                fis = new InputStreamReader(context.openFileInput(PROFILE_FILE));
                //creamos un objeto buffer, en el que iremos almacenando el contenido del archivo
                br = new BufferedReader(fis);


                //por cada EditText leemos una línea y escribimos el contenido en el
                profilerecupered.setKduinoname(br.readLine());

                profilerecupered.setNumberofsensor(Integer.valueOf(br.readLine()));

                profilerecupered.setDepth_1(br.readLine());

                profilerecupered.setDepth_2(br.readLine());

                profilerecupered.setDepth_3(br.readLine());

                profilerecupered.setDepth_4(br.readLine());

                profilerecupered.setDepth_5(br.readLine());

                profilerecupered.setDepth_6(br.readLine());

                profilerecupered.setLat(br.readLine());

                profilerecupered.setLongi(br.readLine());

                profilerecupered.setMarkername(br.readLine());

                profilerecupered.setDate(br.readLine());

                profilerecupered.setTime(br.readLine());

                //cerramos el flujo de lectura del archivo.
                br.close();
                fis.close();


                return profilerecupered;

            }
            catch (Exception e3)
            {
                Log.e(kdUINOApplication.TAG, "Can't recover data profile", e3);

                DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_data_cant_saved),
                        "",
                        view,
                        false,
                        null);
                return null;

            }
        }

    //recupera los datos del archivo txt con los datos que ha enviado el kduino
    public String recuperardata(String nameoffile){

        InputStreamReader fis2 = null;
        BufferedReader br2=null;
        final String data;
        int i=0;
        final char endata='+';
        StringBuilder sbdata=new StringBuilder();

        try {
            // Creamos un objeto InputStreamReader, que será el que nos permita
            // leer el contenido del archivo de texto.

            fis2 = new InputStreamReader(context.openFileInput(nameoffile));
            // Creamos un objeto buffer, en el que iremos almacenando el contenido
            // del archivo.
            br2 = new BufferedReader(fis2);

            //leemos la información
            data=br2.readLine();

            // Cerramos el flujo de lectura del archivo.
            br2.close();
            fis2.close();

            //Mandamos solo los datos sin el ack="+"
            while ((char)data.charAt(i) != endata){

                sbdata.append(String.valueOf(data.charAt(i)));
                i++;
            }

            return sbdata.toString();

        }
        catch (Exception e4)
        {
            Log.e(kdUINOApplication.TAG, "Data file can't read", e4);
            DisplayUtilities.ShowLargeMessage(this.context.getString(R.string.storage_data_cant_recupered),
                    "",
                    view,
                    false,
                    null);
            e4.printStackTrace();
            return null;

        }
    }

	
		 
}
        