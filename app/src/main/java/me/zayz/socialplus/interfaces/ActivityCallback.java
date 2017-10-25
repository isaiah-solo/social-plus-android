package me.zayz.socialplus.interfaces;

import me.zayz.socialplus.instagram.Instagram;

/**
 * Created by zayz on 11/7/17.
 *
 * Interface for Fragments to contact Activity.
 */
public interface ActivityCallback {

    /**
     * Gets instance of Instagram
     *
     * @return Instance of Instagram
     */
    Instagram getInstagram();

    /**
     * Sets top bar title
     *
     * @param title Title
     */
    void setTitle(String title);

    /**
     * Sets bottom bar navigation selection
     *
     * @param menuItem Menu item to select
     */
    void setNavigation(int menuItem);
}
