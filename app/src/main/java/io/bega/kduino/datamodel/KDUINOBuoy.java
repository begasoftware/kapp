package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by usuario on 15/07/15.
 */
public class KDUINOBuoy extends SugarRecord<KDUINOBuoy> {

    public String KduinoID;

    public String Name;

    public String Maker;

    public String User;

    public String MacAddress;

    public int MeasurementTime;

    public int SampleTime;

    public double Lat;

    public double Lon;

    public int Sensors;

    public String LastDataRecieved;


    public KDUINOBuoy()
    {

    }

    public KDUINOBuoy(String id, String name, String marker,
                  String user, String macAddress,
                  double lat, double lon,
                  int sensors)
    {
        this.KduinoID = id;
        this.SampleTime = 0;
        this.MeasurementTime = 0;
        this.Name = name;
        this.Maker = marker;
        this.User = user;
        this.MacAddress = macAddress;
        this.Lat = lat;
        this.Lon = lon;
        this.Sensors = sensors;
    }

    public void updateID(String newID) {
        long id = 0;
        try {
            id = Long.parseLong(newID);
        }
        catch (Exception ex)
        {
            return;
        }
    }

    public void setLatLong(GeoPoint point)
    {
        Lat = point.getLatitude();
        Lon = point.getLongitude();
    }

    public void deleteSensors() {
        if (getSensors().size() > 0) {
            Sensor.deleteAll(Sensor.class, "buoy_id=?", this.getId().toString());
        }
    }

    public List<Sensor> getSensors()
    {
        return Sensor.find(Sensor.class, "buoy_id=?", this.getId().toString());
    }
    

    @Override
    public String toString() {
        return "KdUINO{" +
                "Name='" + Name + '\'' +
                ", Maker='" + Maker + '\'' +
                ", User='" + User + '\'' +
                ", MacAdress='" + MacAddress + '\'' +
                ", Lat=" + Lat +
                ", Lon=" + Lon +
                ", Sensors=" + Sensors +
                '}';
    }
}
