package me.zayz.socialplus.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zayz on 11/25/17.
 * <p>
 * Handles engagement calculation
 */

public class InstagramEngagement {

    public List<InstagramMedia> bestPhoto;
    public List<InstagramMedia> bestVideo;
    public List<InstagramMedia> leastComments;

    public InstagramEngagement() {

        this.bestPhoto = new ArrayList<>();
        this.bestVideo = new ArrayList<>();
        this.leastComments = new ArrayList<>();
    }

    public void calculateEngagement(InstagramUser user) {

        for (InstagramMedia media : user.media) {
            if (media.isVideo) {
                bestVideo.add(media);
            } else {
                bestPhoto.add(media);
            }
        }

        leastComments = user.media;

        Collections.sort(bestPhoto, bestMediaComparator);
        Collections.sort(bestVideo, bestMediaComparator);
        Collections.sort(leastComments, leastCommentsComparator);
    }

    private Comparator<InstagramMedia> bestMediaComparator = new Comparator<InstagramMedia>() {

        @Override
        public int compare(InstagramMedia o1, InstagramMedia o2) {

            long difference = o1.likesCount - o2.likesCount;

            if (difference == 0) {
                difference = o1.commentsCount - o2.commentsCount;
            }

            return difference > 0 ? -1 : 1;
        }
    };

    private Comparator<InstagramMedia> leastCommentsComparator = new Comparator<InstagramMedia>() {

        @Override
        public int compare(InstagramMedia o1, InstagramMedia o2) {

            long difference = o2.commentsCount - o1.commentsCount;

            if (difference == 0) {
                difference = o2.likesCount - o1.likesCount;
            }

            return difference > 0 ? -1 : 1;
        }
    };
}
