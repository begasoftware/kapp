package io.bega.kduino;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import io.bega.kduino.api.KdUINOMessages;

/**
 * Created by usuario on 05/09/15.
 */
public class SettingsManager {

    private String LASTDATE = "last_Date";

    private String FIRSTIME = "my_first_time";

    private String USERNAME = "user_name";

    private String USERID = "user_id";

    private String PASSWORD = "password";

    private String TOTALFILE = "total_file";

    private String TESTFILE = "test_file";

    private SharedPreferences settings;

    private Context context;

    public SettingsManager(Activity activity)
    {
        context = activity;
        settings = this.context.getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
    }

    public SettingsManager(Context ctx)
    {
        context = ctx;
        settings = ctx.getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
    }

    public void setFirstTime()
    {
        SharedPreferences settings = this.context.getSharedPreferences(kdUINOApplication.PREFS_NAME, 0);
        if (settings.getBoolean(FIRSTIME, true)) {

            settings.edit().putBoolean(FIRSTIME, false).commit();

        }
    }

    public String getLastTestFileName()
    {
        return settings.getString(TESTFILE, "");
    }

    public void setLastTestFileName(String fileName)
    {
        settings.edit().putString(TESTFILE, fileName).commit();
    }


    public String getLastTotalFileName()
    {
        return settings.getString(TOTALFILE, "");
    }

    public void setLastTotalFileName(String fileName)
    {
        settings.edit().putString(TOTALFILE, fileName).commit();
    }

    public void setPassword(String password)
    {
        settings.edit().putString(PASSWORD, password).commit();
    }

    public String getUniqueID()
    {
         return Settings.Secure.getString(this.context.getContentResolver(),
                 Settings.Secure.ANDROID_ID);
    }

    public String getPassword() {
        return settings.getString(PASSWORD, "");
    }

    public boolean getFirstTime()
    {
        return settings.getBoolean(FIRSTIME, true);
    }

    public String getUsername()
    {
        return settings.getString(USERNAME, "");
    }

    public void setLastDateUploaded(String date)
    {

        settings.edit().putString(LASTDATE, date).commit();
    }

    public String getLastDateUploaded()
    {
        return settings.getString(LASTDATE, "");
    }

    public void setUsername(String userName)
    {
        settings.edit().putString(USERNAME, userName).commit();
    }

    public int getUserId()
    {
        return settings.getInt(USERID, 0);

    }

    public void setUserID(int userID)
    {
        settings.edit().putInt(USERID, userID).commit();
    }





}
