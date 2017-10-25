package me.zayz.socialplus.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.zayz.socialplus.R;
import me.zayz.socialplus.models.InstagramEngagement;
import me.zayz.socialplus.models.InstagramMedia;
import me.zayz.socialplus.models.InstagramPublicUser;
import me.zayz.socialplus.models.InstagramStats;
import me.zayz.socialplus.models.InstagramUser;
import me.zayz.socialplus.models.InstagramProfile;
import me.zayz.socialplus.models.SocialPlusUser;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Handles Firebase and SharedPreferences.
 */
class InstagramSession {

    private static final String INSTAGRAM = "instagram";

    private static final String STATS = "stats";
    private static final String PROFILE = "profile";
    private static final String PREV_USER = "prev_user";

    private static final String ID = "id";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USERNAME = "username";
    private static final String PROFILE_PICTURE = "profile_picture";
    private static final String FULL_NAME = "full_name";
    private static final String BIO = "bio";
    private static final String WEBSITE = "website";
    private static final String IS_BUSINESS = "is_business";

    private static final String MEDIA = "media";
    private static final String FOLLOWS = "follows";
    private static final String FOLLOWED_BY = "followed_by";

    private Context mContext;
    private SharedPreferences mSharedPref;
    private DatabaseReference mFirebase;

    InstagramSession(Context context) {

        this.mContext = context;
        this.mFirebase = FirebaseDatabase.getInstance().getReference(INSTAGRAM);
        this.mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Gets user data from Firebase.
     *
     * @param callback Response callback
     */
    void getUser(final Callback callback) {

        mFirebase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataSnapshot userSnapshot = dataSnapshot.child(getUserId());

                if (!userSnapshot.exists()) {
                    callback.onNotExists();
                    return;
                }

                InstagramProfile profile = new InstagramProfile(userSnapshot.child(PROFILE));
                InstagramUser previousUser = new InstagramUser(userSnapshot.child(PREV_USER));
                InstagramStats userStats = new InstagramStats(userSnapshot.child(STATS));
                InstagramEngagement engagement = new InstagramEngagement();

                callback.onFinish(profile, previousUser, userStats, engagement);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Stores processed user as previous user for reference during next processing.
     *
     * @param socialPlusUser User to store
     */
    void storeUser(final SocialPlusUser socialPlusUser, final InstagramUser instagramUser) {

        final String id = socialPlusUser.profile.id;
        InstagramProfile profile = socialPlusUser.profile;

        // InstagramProfile stats
        mFirebase.child(id).child(PROFILE).child(ID)
                .setValue(id);
        mFirebase.child(id).child(PROFILE).child(ACCESS_TOKEN)
                .setValue(profile.accessToken);
        mFirebase.child(id).child(PROFILE).child(USERNAME)
                .setValue(profile.username);
        mFirebase.child(id).child(PROFILE).child(PROFILE_PICTURE)
                .setValue(profile.profilePicture);
        mFirebase.child(id).child(PROFILE).child(FULL_NAME)
                .setValue(profile.fullName);
        mFirebase.child(id).child(PROFILE).child(BIO)
                .setValue(profile.bio);
        mFirebase.child(id).child(PROFILE).child(WEBSITE)
                .setValue(profile.website);
        mFirebase.child(id).child(PROFILE).child(IS_BUSINESS)
                .setValue(profile.isBusiness);

        mFirebase.child(id).child(PREV_USER).removeValue(
                new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {

                        // Media ids
                        for (int i = 0; i < instagramUser.media.size(); i++) {
                            InstagramMedia media = instagramUser.media.get(i);
                            String index = String.valueOf(i);
                            mFirebase.child(socialPlusUser.profile.id).child(PREV_USER)
                                    .child(MEDIA).child(index).setValue(media);
                        }

                        // InstagramPublicUser follows ids
                        for (int i = 0; i < instagramUser.follows.size(); i++) {
                            InstagramPublicUser follows = instagramUser.follows.get(i);
                            String index = String.valueOf(i);
                            mFirebase.child(socialPlusUser.profile.id).child(PREV_USER)
                                    .child(FOLLOWS).child(index).setValue(follows);
                        }

                        // Users followed by ids
                        for (int i = 0; i < instagramUser.followedBy.size(); i++) {
                            InstagramPublicUser followedBy = instagramUser.followedBy.get(i);
                            String index = String.valueOf(i);
                            mFirebase.child(socialPlusUser.profile.id).child(PREV_USER)
                                    .child(FOLLOWED_BY).child(index).setValue(followedBy);
                        }
                    }
                });
    }

    /**
     * Updates Firebase with stats calculations.
     *
     * @param stats Instagram user stats
     * @param socialPlusUser User
     */
    void updateStats(InstagramProfile profile, InstagramStats stats,
                     final SocialPlusUser socialPlusUser, final InstagramUser instagramUser) {

        final CountDownLatch latch = new CountDownLatch(8);

        socialPlusUser.profile = profile;

        updateStatsFromList(socialPlusUser, stats.newUnfollowers,
                InstagramStats.NEW_UNFOLLOWERS, latch);
        updateStatsFromList(socialPlusUser, stats.newFollowers,
                InstagramStats.NEW_FOLLOWERS, latch);
        updateStatsFromList(socialPlusUser, stats.unfollowers,
                InstagramStats.UNFOLLOWERS, latch);
        updateStatsFromList(socialPlusUser, stats.followers,
                InstagramStats.FOLLOWERS, latch);
        updateStatsFromList(socialPlusUser, stats.idols,
                InstagramStats.IDOLS, latch);
        updateStatsFromList(socialPlusUser, stats.fans,
                InstagramStats.FANS, latch);
        updateStatsFromList(socialPlusUser, stats.mutual,
                InstagramStats.MUTUAL, latch);
        updateStatsFromList(socialPlusUser, stats.all,
                InstagramStats.ALL, latch);

        final Handler mainThread = new Handler(mContext.getMainLooper());

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    latch.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Log.e(mContext.getString(R.string.app_name), e.getMessage());
                    return;
                }

                mainThread.post(new Runnable() {

                    @Override
                    public void run() {

                        storeUser(socialPlusUser, instagramUser);
                    }
                });
            }
        }).start();
    }

    /**
     * Store user credentials to access Firebase.
     *
     * @param socialPlusUser User
     */
    void storeLocal(SocialPlusUser socialPlusUser) {

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(ID, socialPlusUser.profile.id);
        editor.apply();
    }

    /**
     * Resets local user data.
     */
    void resetLocal() {

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.clear();
        editor.apply();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * Checks if there is an active session.
     *
     * @return If active
     */
    boolean isActive() {

        return mSharedPref.contains(ID);
    }

    /**
     * Updates Firebase with stats calculations per list.
     *
     * @param socialPlusUser Instagram user
     * @param list Instagram user stats list
     * @param latch Latch for asynchronous countdown
     */
    private void updateStatsFromList(final SocialPlusUser socialPlusUser,
                                     final List<InstagramPublicUser> list, final String title,
                                     final CountDownLatch latch) {

        mFirebase.child(socialPlusUser.profile.id).child(STATS).child(title).removeValue(
                new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {

                        for (int i = 0; i < list.size(); i++) {
                            final InstagramPublicUser item = list.get(i);
                            final String index = String.valueOf(i);
                            mFirebase.child(socialPlusUser.profile.id).child(STATS).child(title)
                                    .child(index).setValue(item);
                        }

                        latch.countDown();
                    }
                });
    }

    /**
     * Checks if there is an active session.
     *
     * @return If active
     */
    private String getUserId() {

        return mSharedPref.getString(ID, "");
    }

    /**
     * Callback interface to return user after request.
     */
    interface Callback {

        void onFinish(InstagramProfile profile, InstagramUser prevUser, InstagramStats userStats,
                      InstagramEngagement engagement);

        void onNotExists();
    }
}
