package io.bega.kduino.datamodel;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by usuario on 15/07/15.
 */
public class User extends SugarRecord<User> {

    @SerializedName("user_id")
    public String UserId;

    @SerializedName("name")
    public String Name;

    @SerializedName("country")
    public String Country;

    @SerializedName("email")
    public String Email;

    @SerializedName("password")
    public String Password;

    public Boolean StoredInServer;

    public User()
    {

    }

    public User(String userId,
                String name,
                String country,
                String email,
                String password)
    {
        this.UserId = userId;
        this.Name = name;
        this.Country = country;
        this.Email = email;
        this.Password = password;
        this.StoredInServer = false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (UserId != user.UserId) return false;
        if (Name != null ? !Name.equals(user.Name) : user.Name != null) return false;
        if (Country != null ? !Country.equals(user.Country) : user.Country != null) return false;
        if (Email != null ? !Email.equals(user.Email) : user.Email != null) return false;
        return !(Password != null ? !Password.equals(user.Password) : user.Password != null);

    }

    @Override
    public int hashCode() {
        int result =  Integer.parseInt(UserId);
        result = 31 * result + (Name != null ? Name.hashCode() : 0);
        result = 31 * result + (Country != null ? Country.hashCode() : 0);
        result = 31 * result + (Email != null ? Email.hashCode() : 0);
        result = 31 * result + (Password != null ? Password.hashCode() : 0);
        return result;
    }
}
