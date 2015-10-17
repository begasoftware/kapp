package io.bega.kduino.datamodel;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by usuario on 15/07/15.
 */
public class Captures extends SugarRecord<Captures> {

    public double Kd;

    public double R2;

    public String date;

    public ArrayList<MeasurementDefinition> MeasurementDefinitions;

    public boolean Sended;

    public Captures()
    {

    }

    public Captures(Analysis analysis)
    {
        this.Kd = analysis.Kd;
        this.R2 = analysis.R2;
        this.date = analysis.date;

        this.MeasurementDefinitions = new ArrayList<MeasurementDefinition>();
        for (RawMeasurement rawMeasurement: analysis.measurement.getRawMeasurements())
        {
            ParsePosition parsePosition = new ParsePosition(0);
            SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
            SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            String finalDate = "";
            try
            {
                Date datevalue = formatFrom.parse(rawMeasurement.date, parsePosition);
                finalDate = formatTo.format(datevalue);
            }
            catch (Exception ex)
            {
                String p = ex.getMessage();
                this.R2 = Double.NaN;
                this.Kd = Double.NaN;
                return;
            }

            this.MeasurementDefinitions.add(new MeasurementDefinition(
                    rawMeasurement.sensorNumber,
                    finalDate,
                    rawMeasurement.rawValue
            ));
        }
    }

    public Captures(double kd, double r2, String date, ArrayList<MeasurementDefinition> measurement) {

        this.Kd = kd;
        this.R2 = r2;
        this.date = date;
        this.MeasurementDefinitions = measurement;
    }
}
