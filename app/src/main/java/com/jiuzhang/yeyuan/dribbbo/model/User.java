package com.jiuzhang.yeyuan.dribbbo.model;

import java.util.List;
import java.util.Map;

public class User {

    public String username;
    public String name;
    public String first_name;
    public String last_name;
    public String email;
    public String bio;
    public String twitter_username;
    public String instagram_username;
    public String portfolio_url;
    public String location;
    public Map<String, String> profile_image;
    public int total_likes;
    public int total_photos;
    public int total_collections;
    public boolean followed_by_user;

    public List<Shot> photos;

    public String getProfileImageURL() {
        if (profile_image == null) {
            return "";
        }
        String url = profile_image.containsKey("large") ? profile_image.get("large") : profile_image.get("medium");
        return url == null ? "" : url;
    }
}
