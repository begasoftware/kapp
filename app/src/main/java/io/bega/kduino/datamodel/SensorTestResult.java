package io.bega.kduino.datamodel;

import java.util.ArrayList;

/**
 * Created by usuario on 15/08/15.
 */
public class SensorTestResult {

    //String date;

    ArrayList<SensorTest> listOfSensors;

   /* public String getDate()
    {
        return this.date;
    } */

    public ArrayList<SensorTest> getSensors()
    {
        return this.listOfSensors;
    }

    public SensorTestResult(ArrayList<SensorTest> list)
    {
        //this.date = date;
        this.listOfSensors  = list;
    }
}
