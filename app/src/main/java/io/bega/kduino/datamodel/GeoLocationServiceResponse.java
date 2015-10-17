package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 12/10/15.
 */
public class GeoLocationServiceResponse {

    @SerializedName("as")
    public String TelephoneServiceName;

    @SerializedName("city")
    public String City;

    @SerializedName("country")
    public String Country;

    @SerializedName("countryCode")
    public String CountryCode;

    @SerializedName("isp")
    public String ISP;

    @SerializedName("lat")
    public String Lat;

    @SerializedName("lon")
    public String Lon;

    @SerializedName("org")
    public String Organization;

    @SerializedName("query")
    public String IP;

    @SerializedName("region")
    public String Region;

    @SerializedName("regionName")
    public String RegionName;

    @SerializedName("status")
    public String Status;

    @SerializedName("timezone")
    public String TimeZone;

    @SerializedName("zip")
    public String Zip;
}
