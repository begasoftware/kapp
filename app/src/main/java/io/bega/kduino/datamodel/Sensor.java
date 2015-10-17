package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by usuario on 17/07/15.
 */
public class Sensor extends SugarRecord<Sensor> {

    @SerializedName("id")
    public String SensorID;

    @SerializedName("deep")
    public double Deep;

    @SerializedName("description")
    public String Description;

    public KDUINOBuoy BuoyID;

    public SensorType SensorType;

    public boolean Enabled;

    public Sensor()
    {

    }

    public Sensor(KDUINOBuoy buoy, String sensorID,  double deep, SensorType sensorType, boolean enabled)
    {
        this.Description = "";
        this.BuoyID = buoy;
        this.SensorID = sensorID;
        this.Deep = deep;
        this.SensorType = sensorType;
        this.Enabled = enabled;
    }


    @Override
    public String toString() {
        return "Sensor{" +
                ", Deep=" + Deep +
                ", SensorType=" + SensorType +
                '}';
    }
}
