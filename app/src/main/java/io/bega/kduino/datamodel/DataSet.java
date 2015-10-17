package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by usuario on 27/08/15.
 */
public class DataSet {

    public static String TYPE_ONE = "ONE";

    public static String TYPE_ALL = "ALL";

    @SerializedName("buoy_data")
    BuoyDefinition buoy;

    @SerializedName("analisys_data")
    ArrayList<Analysis> analysises;

    public BuoyDefinition getBuoy()
    {
        return buoy;
    }

    @SerializedName("dataset_type")
    public String Type = DataSet.TYPE_ONE;

    public DataSet()
    {

    }

    public DataSet(String type, BuoyDefinition buoy)
    {
        this.Type = type;
        this.buoy = buoy;
    }

    public ArrayList<Analysis> getAnalysises()
    {
        return analysises;
    }

    public void setAnalysises(ArrayList<Analysis> analysises)
    {
        this.analysises = analysises;
    }


}
