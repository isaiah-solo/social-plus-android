package me.zayz.socialplus.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zayz on 11/25/17.
 * <p>
 * Instagram media post
 */

public class InstagramMedia implements Serializable {

    public String id;
    public boolean isVideo;
    public String image;
    public long likesCount;
    public long commentsCount;
    public String createdTime;
    public List<String> tags;
    public List<InstagramPublicUser> users;
    public String link;

    public InstagramMedia() {
    }

    public InstagramMedia(String id, boolean isVideo, String image, long likesCount,
                          long commentsCount, String createdTime, List<String> tags,
                          List<InstagramPublicUser> users, String link) {

        this.id = id;
        this.isVideo = isVideo;
        this.image = image;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.createdTime = createdTime;
        this.tags = tags;
        this.users = users;
        this.link = link;
    }

    /**
     * Creates string containing media tags.
     *
     * @return String containing media tags
     */
    public String getLineTags() {

        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (String tag : tags) {
            if (builder.toString().length() + tag.length() > 75) {
                builder.append("...");
                break;
            }

            if (!first) {
                builder.append(", ");
            }

            String hashtag = "#" + tag;
            builder.append(hashtag);
            first = false;
        }

        return builder.toString();
    }
}
