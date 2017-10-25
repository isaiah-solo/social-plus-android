package me.zayz.socialplus.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zayz on 11/9/17.
 * <p>
 * Instagram personal user information
 */
public class InstagramUser implements Serializable {

    private static final String MEDIA = "media";
    private static final String FOLLOWS = "follows";
    private static final String FOLLOWED_BY = "followed_by";

    public List<InstagramMedia> media;
    public List<InstagramPublicUser> follows;
    public List<InstagramPublicUser> followedBy;

    public InstagramUser() {

        this.media = new ArrayList<>();
        this.follows = new ArrayList<>();
        this.followedBy = new ArrayList<>();
    }

    public InstagramUser(DataSnapshot userData) {

        this.media = new ArrayList<>();
        this.follows = new ArrayList<>();
        this.followedBy = new ArrayList<>();

        GenericTypeIndicator<List<InstagramMedia>> typeMedia =
                new GenericTypeIndicator<List<InstagramMedia>>() {
                };

        GenericTypeIndicator<List<InstagramPublicUser>> typeUser =
                new GenericTypeIndicator<List<InstagramPublicUser>>() {
                };

        List<InstagramMedia> mediaData = userData.child(MEDIA).getValue(typeMedia);
        if (mediaData != null) this.media.addAll(mediaData);

        List<InstagramPublicUser> followsData = userData.child(FOLLOWS).getValue(typeUser);
        if (followsData != null) this.follows.addAll(followsData);

        List<InstagramPublicUser> followedByData = userData.child(FOLLOWED_BY).getValue(typeUser);
        if (followedByData != null) this.followedBy.addAll(followedByData);
    }
}
