package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by usuario on 01/09/15.
 */
public class Measurements {

    @SerializedName("idboya")
    public int BuoyID;

    @SerializedName("sensors")
    public int SensorNumber;

    @SerializedName("captures")
    public List<CapturesDefinition> Captures;

    public Measurements(int idboya,  int sensorNumber, List<CapturesDefinition> captures)
    {
        this.BuoyID = idboya;
        this.SensorNumber = sensorNumber;
        this.Captures = captures;
    }
}
