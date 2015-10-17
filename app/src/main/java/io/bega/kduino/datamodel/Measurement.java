package io.bega.kduino.datamodel;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by usuario on 15/07/15.
 */
public class Measurement extends SugarRecord<Measurement> {

    public String date;

    public BuoyDefinition buoy;

    ArrayList<RawMeasurement> list = new ArrayList<>();

    public Measurement(BuoyDefinition buoy, String date)
    {
        this.buoy = buoy;

        this.date = date;
    }

    public ArrayList<RawMeasurement> getRawMeasurements()
    {
        return  list;
    }

    public void addMeasurement(String id, String rawValue, String date)
    {
        RawMeasurement rawMeasurement = new RawMeasurement(this, id, rawValue, date);
        list.add(rawMeasurement);
    }







}
