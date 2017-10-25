package me.zayz.socialplus.models;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Instagram user info and credentials object.
 */
public class SocialPlusUser {

    public InstagramProfile profile;

    public SocialPlusUser() {

        this.profile = new InstagramProfile();
    }
}
