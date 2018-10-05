package com.jiuzhang.yeyuan.dribbbo.model;

import java.util.List;
import java.util.Map;

public class Shot {

    private static final String IMAGE_RAW = "raw";
    private static final String IMAGE_FULL = "full";
    private static final String IMAGE_REGULAR = "regular";
    private static final String IMAGE_SMALL = "small";
    private static final String IMAGE_THUMB = "thumb";

    public String id;
    public String description;
    public String title;

    public int width;
    public int height;

    public Map<String, String> urls;
    public Map<String, String> links;
    public List<Bucket> current_user_collections; // The *current user's* collections that this photo belongs to.

    public int views;
    public int likes;
    public int downloads;
    public boolean liked_by_user;

    public User user;
    public Location location;
    public Info exif;

//    public String imageURL;

    public String getImageUrl () {
        if (urls == null) {
            return "";
        }
        String url = urls.get(IMAGE_FULL) == null ?
                urls.get(IMAGE_REGULAR) : urls.get(IMAGE_FULL);
        return url == null ? urls.get(IMAGE_SMALL) : url;
    }

    public String getHtmlUrl () {
        if (links != null) {
            return links.get("html");
        }
        return "Error!";
    }

    public boolean isBucketed() {
        return !this.current_user_collections.isEmpty();
    }

}
