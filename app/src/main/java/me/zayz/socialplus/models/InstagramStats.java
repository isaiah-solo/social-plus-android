package me.zayz.socialplus.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zayz on 11/5/17.
 * <p>
 * Handles instagram stats calculations
 */
public class InstagramStats {

    public static final String NEW_UNFOLLOWERS = "new_unfollowers";
    public static final String NEW_FOLLOWERS = "new_followers";
    public static final String UNFOLLOWERS = "unfollowers";
    public static final String FOLLOWERS = "followers";
    public static final String IDOLS = "idols";
    public static final String FANS = "fans";
    public static final String MUTUAL = "mutual";
    public static final String ALL = "all";

    private Comparator<InstagramPublicUser> comparator = new Comparator<InstagramPublicUser>() {
        @Override
        public int compare(InstagramPublicUser o1, InstagramPublicUser o2) {
            long difference = Long.parseLong(o1.time) - Long.parseLong(o2.time);

            return difference > 0 ? -1 : 1;
        }
    };

    public List<InstagramPublicUser> newUnfollowers;
    public List<InstagramPublicUser> newFollowers;

    public List<InstagramPublicUser> unfollowers;
    public List<InstagramPublicUser> followers;
    public List<InstagramPublicUser> idols;
    public List<InstagramPublicUser> fans;
    public List<InstagramPublicUser> mutual;
    //public List<InstagramPublicUser> blocked;
    public List<InstagramPublicUser> all;

    public InstagramStats() {

        this.newUnfollowers = new ArrayList<>();
        this.newFollowers = new ArrayList<>();
        this.unfollowers = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.idols = new ArrayList<>();
        this.fans = new ArrayList<>();
        this.mutual = new ArrayList<>();
        //this.blocked = new ArrayList<>();
        this.all = new ArrayList<>();
    }

    public InstagramStats(DataSnapshot stats) {

        this.newUnfollowers = new ArrayList<>();
        this.newFollowers = new ArrayList<>();

        GenericTypeIndicator<List<InstagramPublicUser>> typePublicUser =
                new GenericTypeIndicator<List<InstagramPublicUser>>() {
                };

        this.unfollowers = stats.child(UNFOLLOWERS).getValue(typePublicUser);
        this.followers = stats.child(FOLLOWERS).getValue(typePublicUser);
        this.idols = stats.child(IDOLS).getValue(typePublicUser);
        this.fans = stats.child(FANS).getValue(typePublicUser);
        this.mutual = stats.child(MUTUAL).getValue(typePublicUser);
        this.all = stats.child(ALL).getValue(typePublicUser);

        if (this.unfollowers == null) {
            this.unfollowers = new ArrayList<>();
        }

        if (this.followers == null) {
            this.followers = new ArrayList<>();
        }

        if (this.idols == null) {
            this.idols = new ArrayList<>();
        }

        if (this.fans == null) {
            this.fans = new ArrayList<>();
        }

        if (this.mutual == null) {
            this.mutual = new ArrayList<>();
        }

        /*
        if (this.blocked == null) {
            this.blocked = new ArrayList<>();
        }
        */

        if (this.all == null) {
            this.all = new ArrayList<>();
        }
    }

    /**
     * Calculates stats
     *
     * @param currentUser Current user
     * @param prevUser    Previous user
     */
    public void calculateStats(InstagramUser currentUser, InstagramUser prevUser) {

        appendFromArray(all, currentUser.follows);
        appendFromArray(all, currentUser.followedBy);
        appendFromArray(all, prevUser.follows);
        appendFromArray(all, prevUser.followedBy);

        newUnfollowers = new ArrayList<>(prevUser.followedBy);
        removeFromArray(newUnfollowers, currentUser.followedBy);

        newFollowers = new ArrayList<>(currentUser.followedBy);
        removeFromArray(newFollowers, prevUser.followedBy);

        long currentTime = System.currentTimeMillis();

        for (InstagramPublicUser newUnfollower : newUnfollowers) {
            newUnfollower.time = String.valueOf(currentTime);
        }

        for (InstagramPublicUser newFollower : newFollowers) {
            newFollower.time = String.valueOf(currentTime);
        }

        List<InstagramPublicUser> newIdols = new ArrayList<>(currentUser.follows);
        removeFromArray(newIdols, currentUser.followedBy);

        List<InstagramPublicUser> newFans = new ArrayList<>(currentUser.followedBy);
        removeFromArray(newFans, currentUser.follows);

        List<InstagramPublicUser> newMutual = new ArrayList<>(currentUser.follows);
        retainFromArray(newMutual, currentUser.followedBy);

        addMoreAndUpdateFromArray(unfollowers, newUnfollowers);
        addMoreAndUpdateFromArray(followers, newFollowers);

        addMoreFromArray(idols, newIdols);
        addMoreFromArray(fans, newFans);
        addMoreFromArray(mutual, newMutual);

        removeFromArray(idols, currentUser.followedBy);
        removeFromArray(fans, currentUser.follows);

        removeFromArray(unfollowers, currentUser.followedBy);
        retainFromArray(followers, currentUser.followedBy);

        Collections.sort(unfollowers, comparator);
        Collections.sort(followers, comparator);
        Collections.sort(idols, comparator);
        Collections.sort(fans, comparator);
        Collections.sort(mutual, comparator);
        Collections.sort(all, comparator);
        //Collections.sort(blocked, comparator);
    }

    /**
     * Remove from array helper function.
     *
     * @param baseList     Base list
     * @param toRemoveList List to remove from
     */
    private void removeFromArray(List<InstagramPublicUser> baseList,
                                 List<InstagramPublicUser> toRemoveList) {

        List<InstagramPublicUser> foundList = new ArrayList<>();

        for (InstagramPublicUser baseUser : baseList) {
            for (InstagramPublicUser toRemoveUser : toRemoveList) {
                if (baseUser.id.equals(toRemoveUser.id)) {
                    foundList.add(baseUser);
                }
            }
        }

        baseList.removeAll(foundList);
    }

    /**
     * Retain from array helper function.
     *
     * @param baseList     Base list
     * @param toRetainList List to retain
     */
    private void retainFromArray(List<InstagramPublicUser> baseList,
                                 List<InstagramPublicUser> toRetainList) {

        List<InstagramPublicUser> foundList = new ArrayList<>();

        for (InstagramPublicUser baseUser : baseList) {
            for (InstagramPublicUser toRetainUser : toRetainList) {
                if (baseUser.id.equals(toRetainUser.id)) {
                    foundList.add(baseUser);
                }
            }
        }

        baseList.retainAll(foundList);
    }

    /**
     * Add more and update from array helper function.
     *
     * @param baseList          Base list
     * @param toAddMoreFromList List to add more from and retain
     */
    private void addMoreAndUpdateFromArray(List<InstagramPublicUser> baseList,
                                           List<InstagramPublicUser> toAddMoreFromList) {

        List<InstagramPublicUser> foundList = new ArrayList<>();

        for (InstagramPublicUser baseUser : baseList) {
            for (InstagramPublicUser toAddMoreFromUser : toAddMoreFromList) {
                if (baseUser.id.equals(toAddMoreFromUser.id)) {
                    foundList.add(baseUser);
                }
            }
        }

        baseList.removeAll(foundList);
        baseList.addAll(toAddMoreFromList);
    }

    /**
     * Add more from array helper function.
     *
     * @param baseList          Base list
     * @param toAddMoreFromList List to add more from
     */
    private void addMoreFromArray(List<InstagramPublicUser> baseList,
                                  List<InstagramPublicUser> toAddMoreFromList) {

        List<InstagramPublicUser> foundList = new ArrayList<>();

        for (InstagramPublicUser baseUser : baseList) {
            for (InstagramPublicUser toRetainUser : toAddMoreFromList) {
                if (baseUser.id.equals(toRetainUser.id)) {
                    foundList.add(baseUser);
                }
            }
        }

        baseList.retainAll(foundList);

        for (InstagramPublicUser toAddMoreFromUser : toAddMoreFromList) {
            boolean found = false;

            for (InstagramPublicUser baseUser : baseList) {
                if (baseUser.id.equals(toAddMoreFromUser.id)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                baseList.add(toAddMoreFromUser);
            }
        }
    }

    /**
     * Append from array helper function.
     *
     * @param baseList          Base list
     * @param toAddMoreFromList List to append from
     */
    private void appendFromArray(List<InstagramPublicUser> baseList,
                                 List<InstagramPublicUser> toAddMoreFromList) {

        for (InstagramPublicUser toAddMoreFromUser : toAddMoreFromList) {
            boolean found = false;

            for (InstagramPublicUser baseUser : baseList) {
                if (baseUser.id.equals(toAddMoreFromUser.id)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                baseList.add(toAddMoreFromUser);
            }
        }
    }

    /**
     * Callback interface to return user after request.
     */
    public interface Callback {

        void onFinish();

        void onError();
    }
}
