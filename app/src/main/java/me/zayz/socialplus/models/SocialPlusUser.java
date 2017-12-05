package me.zayz.socialplus.models;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Instagram user info and credentials object.
 */
public class SocialPlusUser {

    public static final String STATS = "stats";
    public static final String PROFILE = "profile";
    public static final String PREV_USER = "previous_user";
    public static final String ENGAGEMENT = "engagement";

    public InstagramProfile profile;
    public InstagramUser previousUser;
    public InstagramStats stats;
    public InstagramEngagement engagement;

    public SocialPlusUser() {

        this.profile = new InstagramProfile();
        this.previousUser = new InstagramUser();
        this.stats = new InstagramStats();
        this.engagement = new InstagramEngagement();
    }

    public SocialPlusUser(InstagramProfile profile, InstagramUser previousUser,
                          InstagramStats stats, InstagramEngagement engagement) {

        this.profile = profile;
        this.previousUser = previousUser;
        this.stats = stats;
        this.engagement = engagement;
    }
}
