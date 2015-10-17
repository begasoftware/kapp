package io.bega.kduino.datamodel;



import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 15/07/15.
 */
public class BuoyPostDefinition {

    @SerializedName("buoy_id")
    public String BuoyID;

    @SerializedName("name")
    public String Name;

    @SerializedName("description")
    public String Description;


    @SerializedName("lat")
    public double Lat;

    @SerializedName("lon")
    public double Lon;

    @SerializedName("user")
    public String User;

    @SerializedName("maker")
    public String Maker;

    @SerializedName("sensors")
    public int Sensors;

    @SerializedName("sensor_definitions")
    List<SensorDefinition> SensorsList;

    public BuoyPostDefinition()
    {

    }

    public BuoyPostDefinition(KDUINOBuoy buoy)
    {
        this.BuoyID = buoy.KduinoID;
        this.Name = buoy.Name;
        this.Maker = buoy.Maker;
        this.User = buoy.User;
        this.Lat = buoy.Lat;
        this.Lon = buoy.Lon;
        this.Description = "";
        this.Sensors = buoy.Sensors;
        this.SensorsList = new ArrayList<SensorDefinition>();
        for (Sensor sensor: buoy.getSensors())
        {
            SensorDefinition sensorDefinition =
                    new SensorDefinition(sensor);
            this.SensorsList.add(sensorDefinition);
        }

    }

    public BuoyPostDefinition(
            BuoyDefinition definition
    )
    {
        this.BuoyID = Long.toString(definition.BuoyID);
        this.Name = definition.Name;
        this.Lat = definition.Lat;
        this.Lon = definition.Lon;
        this.Description = "";
        this.Sensors = definition.Sensors;
        this.SensorsList = new ArrayList<SensorDefinition>();
        for (SensorDefinition sensor: definition.SensorsList)
        {
            this.SensorsList.add(sensor);
        }
    }
}
