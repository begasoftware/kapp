package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by usuario on 15/07/15.
 */
public class CapturesDefinition {



    @SerializedName("kd")
    public double Kd;

    @SerializedName("r2")
    public double R2;

    @SerializedName("date")
    public String date;

    @SerializedName("sensors")
    public String Sensors;

    @SerializedName("captures")
    public ArrayList<MeasurementDefinition> measurementDefinitions;

    public CapturesDefinition(Captures captures, String buoyID)
    {
        this.Kd = captures.Kd;
        this.R2 = captures.R2;

        if (Double.isNaN(this.Kd))
        {
            String p = "";
        }

        if (Double.isNaN(this.R2))
        {
            String r2 = "";
        }


        ParsePosition parsePosition = new ParsePosition(0);
        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date datevalue = formatFrom.parse(captures.date, parsePosition);
        this.date = formatTo.format(datevalue);

        int i = 0;
        String previous = "";
        for (MeasurementDefinition measurementDefinition : captures.MeasurementDefinitions)
        {
            try {
                if (measurementDefinition.sensorNumber.length() == 0)
                {
                    measurementDefinition.sensorNumber =  Integer.toString(
                            Integer.parseInt(previous) + 1);
                }

                previous = measurementDefinition.sensorNumber;

                measurementDefinition.sensorNumber = buoyID + String.format("%02d",
                        Integer.parseInt(measurementDefinition.sensorNumber));
            }catch (Exception ex)
            {


                String p = ex.getMessage();
            }
            int rawData = 0;
            try {
                rawData = Integer.parseInt(measurementDefinition.rawValue);
            }
            catch (Exception ex) {
                measurementDefinition.rawValue = "0";
            }

            if (rawData != 0)
            {
                    i ++;
            }


        }

        this.Sensors = Integer.toString(i);
        this.measurementDefinitions = captures.MeasurementDefinitions;

    }

    public CapturesDefinition(Analysis analysis)
    {
        this.Kd = analysis.Kd;
        this.R2 = analysis.R2;


        if (Double.isNaN(this.Kd))
        {
            this.Kd = 0;
        }

        if (Double.isNaN(this.R2))
        {
            this.R2 = 0;
        }

        // 2015/9/2_16:58:1
        ParsePosition parsePosition = new ParsePosition(0);
        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date datevalue = formatFrom.parse(analysis.date, parsePosition);
        this.date = formatTo.format(datevalue);
        this.measurementDefinitions = new ArrayList<MeasurementDefinition>();
        for (RawMeasurement rawMeasurement: analysis.measurement.getRawMeasurements())
        {



            Date datevalueRaw = formatFrom.parse(rawMeasurement.date, parsePosition);
            this.measurementDefinitions.add(new MeasurementDefinition(
                    rawMeasurement.sensorNumber,
                    formatTo.format(datevalueRaw),
                    rawMeasurement.rawValue
            ));
        }
    }

    public CapturesDefinition(double kd, double r2, String date, ArrayList<MeasurementDefinition> measurement) {

        this.Kd = kd;
        this.R2 = r2;
        ParsePosition parsePosition = new ParsePosition(0);
        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date datevalue = formatFrom.parse(date, parsePosition);
        this.date = formatTo.format(datevalue);
        this.measurementDefinitions = measurement;
    }






}
