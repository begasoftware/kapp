package io.bega.kduino.services;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.Measurement;
import io.bega.kduino.datamodel.RawMeasurement;
import io.bega.kduino.datamodel.SensorTest;
import io.bega.kduino.datamodel.SensorTestResult;
import io.bega.kduino.utils.DateUtility;

/**
 * Created by usuario on 25/07/15.
 */
public class ProcessDataService {

    private String separator = " ";

   // private Activity activity;

    public ProcessDataService() {
       // this.activity = activity;
    }

    public SensorTestResult proceedTestSensorsCalibration(String data)
    {
        ArrayList<SensorTest> sensorTests = new ArrayList<>();

        data = data.replace("+","");
        StringTokenizer messageTokens = new StringTokenizer(data, " ");
        //String date = messageTokens.nextToken();

        while(messageTokens.hasMoreTokens())
        {
            SensorTest test = new SensorTest();
            String id = messageTokens.nextToken();
            String value = messageTokens.nextToken();
            test.SensorNumber = Integer.parseInt(id);
            test.SensorCalibration = Long.parseLong(value);
            sensorTests.add(test);
        }

        return new SensorTestResult(sensorTests);
    }


    public ArrayList<Measurement> proceeAllData(BuoyDefinition buoy,  String data) {
        if (data == null || data.length() == 0) {
            return null;
        }



        data = data.replace("+", "");
        String[] blocks = data.split("\\r?\\n");
        ArrayList<Measurement> measurements = new ArrayList<Measurement>();
        for (int i= 0; i<blocks.length; i++)
        {
            measurements.add(this.proceedData(buoy, blocks[i]));
        }

        return measurements;
    }



    public Measurement proceedData(BuoyDefinition buoy, String data) {
        if (data == null || data.length() == 0) {
            return null;
        }



        int startindex = 0;
        data = data.replace("+", "");

        String[] tokens =  data.split(" ");

        if (tokens.length <= startindex)
        {
            return null;
        }

        if (tokens.length == 1)
        {
            return null;
        }

        String date = tokens[startindex];
        if (!date.contains("/"))
        {
            startindex++;
            date = tokens[startindex];
        }

        startindex++;
        Measurement measurement = new Measurement(buoy, date);

        for (int i = startindex; i < tokens.length; i++) {
            String id = "-1";
            try {
                id = tokens[i];
            }
            catch (Exception ex)
            {

            }

            String rawValue = "0";
            try
            {
                rawValue = tokens[i+1];
            }
            catch (Exception ex)
            {

            }

            measurement.addMeasurement(id, rawValue, date);
            i ++;
        }

        return measurement;

//
//
//
//
//
//        }
//        String data  data.split(" ");
//
//        ArrayList<RawMeasurement> list = new ArrayList<>();
//        StringTokenizer tokens = new StringTokenizer(data, separator);
//
//        Buoy buoy = new Buoy("test name", "test marker", "test user", mac, 0, 0);
//        buoy.save();
//
//
//        do {
//            String token = tokens.nextToken();
//            // must start with a date, take care in the year 3000 ...
//            if (!token.startsWith("20")) {
//                continue;
//            }
//
//            token
//
//            StringTokenizer messageTokens = new StringTokenizer(token, " ");
//            String date = messageTokens.nextElement().toString();
//
//
//
//            Measurement measurement = new Measurement(buoy, date);
//            measurement.save();
//            while (tokens.hasMoreTokens()) {
//                try {
//                    String idnumber = messageTokens.nextElement().toString();
//                    String rawValue = messageTokens.nextElement().toString();
//                    RawMeasurement rawMeasurement = new RawMeasurement(measurement, idnumber, rawValue, date);
//                    list.add(rawMeasurement);
//                }catch (Exception ex)
//                {
//                    String test = ex.getMessage();
//                }
//              //  rawMeasurement.save();
//            }
//
//
//        } while (tokens.hasMoreTokens());
//
//        return list;
    }
}
