package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 21/09/15.
 */
public class RecoveryPassword {

    @SerializedName("email")
    public String Email;

    public RecoveryPassword(String email)
    {
        Email = email;
    }
}
