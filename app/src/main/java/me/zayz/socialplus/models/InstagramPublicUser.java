package me.zayz.socialplus.models;

import java.io.Serializable;

/**
 * Created by zayz on 11/9/17.
 * <p>
 * Instagram public user information
 */
public class InstagramPublicUser implements Serializable {

    public String id;
    public String fullName;
    public String profilePicture;
    public String username;
    public String time;

    public InstagramPublicUser() {

        this.id = "";
        this.fullName = "";
        this.profilePicture = "";
        this.username = "";
        this.time = String.valueOf(System.currentTimeMillis());
    }

    public InstagramPublicUser(String id, String fullName, String profilePicture, String username) {

        this.id = id;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.username = username;
        this.time = String.valueOf(System.currentTimeMillis());
    }

}
