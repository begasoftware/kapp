package io.bega.kduino.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Obtenemos la fecha y la hora del sistema
 */
public class DateUtility {


    //obtenemos la fecha del sistema
    public static String getDate(){

        String currentDate;

        DateFormat dateFormattotxt = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        currentDate=dateFormattotxt.format(cal.getTime());

        return currentDate;
    }

    //obtenemos la hora del sistema
    public static String getTime(){

        String currentTime;

        DateFormat dateFormattotxt = new SimpleDateFormat("hhmmss");
        Calendar cal = Calendar.getInstance();
        currentTime=dateFormattotxt.format(cal.getTime());

        return currentTime;
    }


}
