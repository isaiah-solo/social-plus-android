package me.zayz.socialplus.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Handles session data and manages SharedPreferences.
 */
class InstagramSession {
    private Context mContext;
    private SharedPreferences mSharedPref;
    private DatabaseReference mFirebase;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String USERNAME = "username";
    private static final String PROFILE_PICTURE = "profile_picture";
    private static final String FULL_NAME = "full_name";
    private static final String BIO = "bio";
    private static final String WEBSITE = "website";
    private static final String IS_BUSINESS = "is_business";

    private static final String NUMBER_OF_MEDIA = "number_of_media";
    private static final String NUMBER_OF_FOLLOWS = "number_of_follows";
    private static final String NUMBER_OF_FOLLOWED_BY = "number_of_followed_by";

    private static final String MEDIA = "media";
    private static final String FOLLOWS = "follows";
    private static final String FOLLOWED_BY = "followed_by";

    InstagramSession(Context context) {
        mContext = context;

        mFirebase = FirebaseDatabase.getInstance().getReference("instagram");
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Save user data
     *
     * @param user User data
     */
    void store(InstagramUser user) {
        mFirebase.child(user.id).child("profile").child(ACCESS_TOKEN).setValue(user.accessToken);
        mFirebase.child(user.id).child("profile").child(USERNAME).setValue(user.username);
        mFirebase.child(user.id).child("profile").child(PROFILE_PICTURE).setValue(user.profilePicture);
        mFirebase.child(user.id).child("profile").child(FULL_NAME).setValue(user.fullName);
        mFirebase.child(user.id).child("profile").child(BIO).setValue(user.bio);
        mFirebase.child(user.id).child("profile").child(WEBSITE).setValue(user.website);
        mFirebase.child(user.id).child("profile").child(IS_BUSINESS).setValue(user.isBusiness);

        mFirebase.child(user.id).child("first_stats").child(NUMBER_OF_MEDIA).setValue(user.numberOfMedia);
        mFirebase.child(user.id).child("first_stats").child(NUMBER_OF_FOLLOWS).setValue(user.numberOfFollows);
        mFirebase.child(user.id).child("first_stats").child(NUMBER_OF_FOLLOWED_BY).setValue(user.numberOfFollowedBy);

        mFirebase.child(user.id).child("prev_stats").child(NUMBER_OF_MEDIA).setValue(user.numberOfMedia);
        mFirebase.child(user.id).child("prev_stats").child(NUMBER_OF_FOLLOWS).setValue(user.numberOfFollows);
        mFirebase.child(user.id).child("prev_stats").child(NUMBER_OF_FOLLOWED_BY).setValue(user.numberOfFollowedBy);

        mFirebase.child(user.id).child("curr_stats").child(NUMBER_OF_MEDIA).setValue(user.numberOfMedia);
        mFirebase.child(user.id).child("curr_stats").child(NUMBER_OF_FOLLOWS).setValue(user.numberOfFollows);
        mFirebase.child(user.id).child("curr_stats").child(NUMBER_OF_FOLLOWED_BY).setValue(user.numberOfFollowedBy);

        for (int i = 0; i < user.media.size(); i++) {
            String media = user.media.get(i);
            String index = String.valueOf(i);
            mFirebase.child(user.id).child("first_stats").child(MEDIA).child(index).setValue(media);
            mFirebase.child(user.id).child("prev_stats").child(MEDIA).child(index).setValue(media);
            mFirebase.child(user.id).child("curr_stats").child(MEDIA).child(index).setValue(media);
        }

        for (int i = 0; i < user.follows.size(); i++) {
            String follows = user.follows.get(i);
            String index = String.valueOf(i);
            mFirebase.child(user.id).child("first_stats").child(FOLLOWS).child(index).setValue(follows);
            mFirebase.child(user.id).child("prev_stats").child(FOLLOWS).child(index).setValue(follows);
            mFirebase.child(user.id).child("curr_stats").child(FOLLOWS).child(index).setValue(follows);
        }

        for (int i = 0; i < user.followedBy.size(); i++) {
            String followedBy = user.followedBy.get(i);
            String index = String.valueOf(i);
            mFirebase.child(user.id).child("first_stats").child(FOLLOWED_BY).child(index).setValue(followedBy);
            mFirebase.child(user.id).child("prev_stats").child(FOLLOWED_BY).child(index).setValue(followedBy);
            mFirebase.child(user.id).child("curr_stats").child(FOLLOWED_BY).child(index).setValue(followedBy);
        }

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(ACCESS_TOKEN, user.accessToken);

        editor.apply();
    }

    /**
     * Reset user data
     */
    void reset() {
        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.clear();

        editor.apply();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * Get user data
     */
    void getUser(ValueEventListener listener) {
        mFirebase.addValueEventListener(listener);
    }

    /**
     * Check if there is an active session.
     *
     * @return true if active and vice versa
     */
    boolean isActive() {
        return mSharedPref.contains(ACCESS_TOKEN);
    }
}
