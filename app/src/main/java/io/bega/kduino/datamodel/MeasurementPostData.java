package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by usuario on 07/09/15.
 */
public class MeasurementPostData {

    @SerializedName("buoy_id")
    public String BuoyID;

    @SerializedName("measurements")
    public ArrayList<CapturesDefinition> Captures;

    public MeasurementPostData(String buoyID,
                               ArrayList<CapturesDefinition> captureDefinition)
    {
        this.BuoyID = buoyID;
        this.Captures = captureDefinition;
    }
}
