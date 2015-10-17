package io.bega.kduino.datamodel;



import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 15/07/15.
 */
public class BuoyDefinition  {

    @SerializedName("buoy_id")
    public long BuoyID;

    @SerializedName("name")
    public String Name;

    @SerializedName("description")
    public String Description;

    @SerializedName("maker")
    public  String Maker;

    @SerializedName("user")
    public String User;

    public transient String MacAdress;

    @SerializedName("lat")
    public double Lat;

    @SerializedName("lon")
    public double Lon;

    @SerializedName("sensors")
    public int Sensors;

    @SerializedName("plotkd")
    public String plotkd;


    public String filePlotName;

    @SerializedName("sensor_definitions")
    List<io.bega.kduino.datamodel.SensorDefinition> SensorsList;

    public BuoyDefinition()
    {

    }

    public BuoyDefinition(String userID, long id, String name, String marker, String user, String macAdress, double lat, double lon, int sensors,
                List<io.bega.kduino.datamodel.Sensor> sensorsList)
    {
        this.BuoyID = id;
        this.Name = name;
        this.Maker = marker;
        this.User = user;
        this.MacAdress = macAdress;
        this.Lat = lat;
        this.Lon = lon;
        this.Sensors = sensors;
        this.SensorsList = new ArrayList<SensorDefinition>();
        for (io.bega.kduino.datamodel.Sensor sensor: sensorsList)
        {
            if (sensor.SensorID.startsWith("00"))
            {
                sensor.SensorID = userID + sensor.SensorID.substring(2);
            }

            this.SensorsList.add(new SensorDefinition(sensor));
        }
    }


    public void setLatLong(double lat, double lon)
    {
        this.Lat = lat;
        this.Lon = lon;
    }

}
