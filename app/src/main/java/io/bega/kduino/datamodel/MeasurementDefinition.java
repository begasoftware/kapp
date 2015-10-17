package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 01/09/15.
 */
public class MeasurementDefinition {

    @SerializedName("sensor_id")
    public String sensorNumber;

    @SerializedName("date")
    public String date;

    @SerializedName("raw")
    public String rawValue;

    public MeasurementDefinition(String sensor, String date, String raw){

        this.sensorNumber = sensor;
        this.date = date;
        this.rawValue = raw;
    }
}
