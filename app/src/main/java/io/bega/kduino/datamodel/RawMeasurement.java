package io.bega.kduino.datamodel;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by usuario on 25/07/15.
 */
public class RawMeasurement extends SugarRecord<RawMeasurement> {
    public String sensorNumber;
    public String rawValue;
    public String date;

    Measurement measurement;

    public RawMeasurement(){
    }

    public RawMeasurement(Measurement measurement, String sensor, String raw, String date){
        this.measurement = measurement;
        this.sensorNumber = sensor;
        this.rawValue = raw;
        this.date = date;

    }

}
