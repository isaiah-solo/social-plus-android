package me.zayz.socialplus.instagram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Instagram user info and credentials object.
 */
public class InstagramUser {
    String id;
    String username;
    String profilePicture;
    String fullName;
    String bio;
    String website;
    Boolean isBusiness;
    String accessToken;

    int numberOfMedia;
    int numberOfFollows;
    int numberOfFollowedBy;

    List<String> media;
    List<String> follows;
    List<String> followedBy;

    InstagramUser() {
        media = new ArrayList<>();
        follows = new ArrayList<>();
        followedBy = new ArrayList<>();
    }
}
