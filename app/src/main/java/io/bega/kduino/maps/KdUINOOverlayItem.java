package io.bega.kduino.maps;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.DecimalFormat;

/**
 * Created by usuario on 03/08/15.
 */
public class KdUINOOverlayItem extends OverlayItem {

    String plotKD;

    String description;

    String user;

    GeoPoint point;

    public KdUINOOverlayItem(String aTitle, String aSnippet, GeoPoint aGeoPoint, String plotKD, String user) {
        super(aTitle, aSnippet, aGeoPoint);
        this.plotKD = plotKD;
        this.user = user;
        this.point = aGeoPoint;
    }

    public String getPlotKD() {
        return plotKD;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUser()
    {
        return user;
    }

    public String getPosition()
    {
        String position = "";
        if (point != null)
        {
            DecimalFormat form = new DecimalFormat("0.0000");
            position = "Lat: " + form.format(this.point.getLatitude()) +
                    ", Lon: " + form.format(this.point.getLongitude());

        }

        return position;
    }


    public KdUINOOverlayItem(String aUid, String aTitle, String aDescription, GeoPoint aGeoPoint, String plotKD, String user) {
        super(aUid, aTitle, aDescription, aGeoPoint);
        this.description = aDescription;
        this.user =  user;
        this.plotKD = plotKD;
    }


}
