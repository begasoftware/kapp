package io.bega.kduino.datamodel;

import android.location.Location;

/**
 * Created by usuario on 02/10/15.
 */
public class DistanceBuoy {

    public String IDBuoy = "";

    public int ID = 0;

    public float meters = 0;

    BuoyPostDefinition buoyDefinition = null;

    public DistanceBuoy(BuoyPostDefinition buoyDefinition, BuoyDefinition currentDefinition)
    {
        this.buoyDefinition = buoyDefinition;
        this.ID = Integer.parseInt(buoyDefinition.BuoyID.substring(2));
        this.IDBuoy = buoyDefinition.BuoyID;
        float[] results = new float[]{0f, 0f, 0f};
        Location.distanceBetween(buoyDefinition.Lat, buoyDefinition.Lon,
                currentDefinition.Lat, currentDefinition.Lon, results);
        meters = results[0];
    }

    public BuoyPostDefinition getBuoyDefinition()
    {
        return buoyDefinition;
    }


}
