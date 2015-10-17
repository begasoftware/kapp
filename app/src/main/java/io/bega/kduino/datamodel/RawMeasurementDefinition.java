package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 07/09/15.
 */
public class RawMeasurementDefinition {

    @SerializedName("sensor_id")
    public String SensorID;

    @SerializedName("date")
    public String Date;

    @SerializedName("raw")
    public String RawValue;

    public RawMeasurementDefinition(RawMeasurement raw)
    {
        this.SensorID = raw.sensorNumber;
        this.Date = raw.date;
        this.RawValue = raw.rawValue;
    }
}
