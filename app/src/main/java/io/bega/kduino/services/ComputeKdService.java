package io.bega.kduino.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.KDUINOBuoy;
import io.bega.kduino.datamodel.Measurement;
import io.bega.kduino.datamodel.RawMeasurement;
import io.bega.kduino.datamodel.Sensor;
import io.bega.kduino.kdUINOApplication;

/**
 * Created by usuario on 15/07/15.
 */
public class ComputeKdService {


    List<Sensor> sensorList;


    public Analysis ComputeLinearRegresion(
            List<Sensor> currentSensorList, Measurement measurement)
    {

        sensorList = currentSensorList;
        int MAXN = 15;
        int n = 0;
        double[] x = new double[MAXN];
        double[] y = new double[MAXN];

        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        if (measurement == null)
        {
            return null;
        }

        if (measurement.getRawMeasurements() == null)
        {
            return null;
        }

        for (int i=0; i< measurement.getRawMeasurements().size(); i++)
        {

            double rawMeasurement = 0d;
            try {

                rawMeasurement =  Double.parseDouble(measurement.getRawMeasurements().get(i).rawValue);
            }
            catch (Exception ex)
            {
                Log.d(kdUINOApplication.TAG, "Error parsing double", ex);
                continue;
            }

            String sensorNumber =  measurement.getRawMeasurements().get(i).sensorNumber;
            try {
                x[n] = this.searchDeep(Integer.parseInt(sensorNumber));
            }
            catch(Exception ex)
            {
                Log.d(kdUINOApplication.TAG, "Error parsing double", ex);
                continue;
            }

            y[n] =  Math.log(rawMeasurement);
            sumx += x[n];
            sumx2 += x[n] * x[n];
            sumy += y[n];
            n++;
        }

      /*  while(!StdIn.isEmpty()) {
            x[n] = StdIn.readDouble();
            y[n] = StdIn.readDouble();
            sumx  += x[n];
            sumx2 += x[n] * x[n];
            sumy  += y[n];
            n++;
        } */

        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }

        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        Log.d(kdUINOApplication.TAG,  "y   = " + beta1 + " * x + " + beta0);

        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        double R2    = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/n + xbar*xbar*svar1;

        Log.d(kdUINOApplication.TAG, "R^2                 = " + R2);
        Log.d(kdUINOApplication.TAG, "std error of beta_1 = " + Math.sqrt(svar1));
        Log.d(kdUINOApplication.TAG, "std error of beta_0 = " + Math.sqrt(svar0));
        svar0 = svar * sumx2 / (n * xxbar);
        Log.d(kdUINOApplication.TAG, "std error of beta_0 = " + Math.sqrt(svar0));

        Log.d(kdUINOApplication.TAG, "SSTO = " + yybar);
        Log.d(kdUINOApplication.TAG, "SSE  = " + rss);
        Log.d(kdUINOApplication.TAG, "SSR  = " + ssr);

        Analysis analysis = new Analysis(0, (-1) * beta1, R2, measurement);
        return analysis;
    }

    private double searchDeep(int sensorNumber)
    {
       // int finalSensorNumber = sensorNumber - 1;
        if (sensorList == null)
        {
            return 0;
        }

        for(int i=0; i < sensorList.size(); i++)
        {

            String sensorIDValue = sensorList.get(i).SensorID;
            if (sensorIDValue.endsWith(String.format("%02d", sensorNumber)) )
            {
                return sensorList.get(i).Deep;
            }
        }

        return 0;

    }

}
