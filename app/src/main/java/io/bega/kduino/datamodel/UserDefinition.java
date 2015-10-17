package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 01/09/15.
 */
public class UserDefinition {


    @SerializedName("name")
    public String Name;

    @SerializedName("country")
    public String Country;

    @SerializedName("email")
    public String Email;

    @SerializedName("password")
    public String Password;

    public UserDefinition()
    {

    }

    public UserDefinition(User user)
    {
        Name = user.Name;
        Country = user.Country;
        Email = user.Email;
        Password = user.Password;
    }
}
