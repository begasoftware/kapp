package io.bega.kduino.datamodel;



/**
 * Clase profile que guarda el perfil del usuario y los datos del menu_makeakaduino
 */
public class Profile {


    //declaramos las variables
    private String depth_1="0.0";
    private String depth_2="0.0";
    private String depth_3="0.0";
    private String depth_4="0.0";
    private String depth_5="0.0";
    private String depth_6="0.0";
    private String lat;
    private String longi;
    private int numberofsensor;
    private String date;
    private  String time;
    private String markername;
    private String kduinoname;




    public String getLat() {
        return lat;

    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getDepth_1() {
        return depth_1;
    }

    public void setDepth_1(String depth_1) {
        this.depth_1 = depth_1;
    }

    public String getDepth_2() {
        return depth_2;
    }

    public void setDepth_2(String depth_2) {
        this.depth_2 = depth_2;
    }

    public String getDepth_3() {
        return depth_3;
    }

    public void setDepth_3(String depth_3) {
        this.depth_3 = depth_3;
    }

    public String getDepth_4() {
        return depth_4;
    }

    public void setDepth_4(String depth_4) {
        this.depth_4 = depth_4;
    }

    public String getDepth_5() {
        return depth_5;
    }

    public void setDepth_5(String depth_5) {
        this.depth_5 = depth_5;
    }

    public String getDepth_6() {
        return depth_6;
    }

    public void setDepth_6(String depth_6) {
        this.depth_6 = depth_6;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberofsensor() {
        return numberofsensor;
    }

    public void setNumberofsensor(int numberofsensor) {
        this.numberofsensor = numberofsensor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMarkername() {
        return markername;
    }

    public void setMarkername(String markername) {
        this.markername = markername;
    }

    public String getKduinoname() {
        return kduinoname;
    }

    public void setKduinoname(String kduinoname) {
        this.kduinoname = kduinoname;
    }


    //pasamos los datos a una string
    @Override
    public String toString() {
        return
                kduinoname+
                "\n" + numberofsensor+
                "\n" + depth_1 +
                "\n" + depth_2 +
                "\n" + depth_3 +
                "\n" + depth_4 +
                "\n" + depth_5 +
                "\n" + depth_6 +
                "\n" + lat +
                "\n" + longi +
                "\n" + markername +
                "\n" + date+
                "\n" + time+
                "\n";

    }
}
