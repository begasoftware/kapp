package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by usuario on 17/07/15.
 */
public class SensorDefinition {

    @SerializedName("id")
    public String SensorID;

    @SerializedName("deep")
    public double Deep;

    @SerializedName("description")
    public String Description;

    @SerializedName("sensor_type")
    public SensorType SensorType;

    @SerializedName("sensor_enabled")
    public boolean Enabled;

    public SensorDefinition()
    {

    }

    public SensorDefinition(Sensor sensor)
    {
        this.Description = "";
        this.SensorID = sensor.SensorID;
        this.Deep = sensor.Deep;
        this.SensorType = sensor.SensorType;
        this.Enabled = sensor.Enabled;
    }


    @Override
    public String toString() {
        return "Sensor{" +
                ", Deep=" + Deep +
                ", SensorType=" + SensorType +
                '}';
    }
}
