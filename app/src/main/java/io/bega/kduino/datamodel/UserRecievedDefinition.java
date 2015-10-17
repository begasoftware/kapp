package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 01/09/15.
 */
public class UserRecievedDefinition {

    @SerializedName("user_id")
    public String UserID;

    @SerializedName("username")
    public String UserName;

    @SerializedName("name")
    public String Name;

    @SerializedName("country")
    public String Country;

    @SerializedName("email")
    public String Email;


    public UserRecievedDefinition()
    {

    }

}
