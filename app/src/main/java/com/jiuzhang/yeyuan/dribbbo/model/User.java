package com.jiuzhang.yeyuan.dribbbo.model;

import java.util.Map;

public class User {

    public String username;
    public String name;
    public Map<String, String> profile_image;

    public String getProfileImageURL() {
        if (profile_image == null) {
            return "";
        }
        String url = profile_image.containsKey("small") ? profile_image.get("small") : profile_image.get("medium");
        return url == null ? "" : url;
    }
}
