package me.zayz.socialplus.models;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;

/**
 * Created by zayz on 11/9/17.
 * <p>
 * Instagram profile
 */

public class InstagramProfile implements Serializable {

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String FULL_NAME = "full_name";
    public static final String BIO = "bio";
    public static final String WEBSITE = "website";
    public static final String IS_BUSINESS = "is_business";
    public static final String ACCESS_TOKEN = "access_token";

    public String id;
    public String username;
    public String profilePicture;
    public String fullName;
    public String bio;
    public String website;
    public Boolean isBusiness;
    public String accessToken;

    public InstagramProfile() {
    }

    public InstagramProfile(DataSnapshot profileData) {

        this.id = profileData.child(ID).getValue(String.class);
        this.username = profileData.child(USERNAME).getValue(String.class);
        this.profilePicture = profileData.child(PROFILE_PICTURE).getValue(String.class);
        this.fullName = profileData.child(FULL_NAME).getValue(String.class);
        this.bio = profileData.child(BIO).getValue(String.class);
        this.website = profileData.child(WEBSITE).getValue(String.class);
        this.isBusiness = profileData.child(IS_BUSINESS).getValue(Boolean.class);
        this.accessToken = profileData.child(ACCESS_TOKEN).getValue(String.class);
    }
}
