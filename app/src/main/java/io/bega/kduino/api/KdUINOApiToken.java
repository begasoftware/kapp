package io.bega.kduino.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by usuario on 02/09/15.
 */
public class KdUINOApiToken {

    @SerializedName("access_token")
    public String Token;

    @SerializedName("token_type")
    public String Type;

    @SerializedName("expires_in")
    public String Expire;

    @SerializedName("scope")
    public String Scope;
}
