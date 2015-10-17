package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by usuario on 15/07/15.
 */
public class Analysis extends SugarRecord<Analysis> {

    public  int Id;

    public double Kd;

    public double R2;

    Measurement measurement;

    public String date;

    public boolean IsInServer;

    public Analysis()
    {

    }

    public Analysis(int id, double kd, double r2, Measurement measurement) {
        this.Id = id;
        this.Kd = kd;
        this.R2 = r2;
        this.date = measurement.date;
        this.measurement = measurement;
        this.IsInServer = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Analysis analysis = (Analysis) o;

        if (Id != analysis.Id) return false;
        if (Double.compare(analysis.Kd, Kd) != 0) return false;
        return Double.compare(analysis.R2, R2) == 0;

    }

    @Override
    public int hashCode() {
        int result = Id;
        result = 31 * result + (Kd != +0.0d ? (int)Double.doubleToLongBits(Kd) : 0);
        result = 31 * result + (R2 != +0.0d ? (int)Double.doubleToLongBits(R2) : 0);
        return result;
    }


}
