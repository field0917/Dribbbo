package com.jiuzhang.yeyuan.dribbbo.model;

import com.jiuzhang.yeyuan.dribbbo.dribbble.Dribbble;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.net.URL;
import java.util.Map;

public class Shot {

    private static final String IMAGE_RAW = "raw";
    private static final String IMAGE_FULL = "full";
    private static final String IMAGE_REGULAR = "regular";
    private static final String IMAGE_SMALL = "small";
    private static final String IMAGE_THUMB = "thumb";

    public String id;
    public String description;
//    public String title;

    public int width;
    public int height;

    public Map<String, String> urls;
//    public Map<String, String> location;

    public int views;
    public int likes;
    public int downloads;

    public User user;

//    public String imageURL;

    public String getImageUrl() {
        if (urls == null) {
            return "";
        }
        String url = urls.get(IMAGE_RAW) == null ?
                urls.get(IMAGE_FULL) : urls.get(IMAGE_RAW);
        return url == null ? urls.get(IMAGE_REGULAR) : url;
    }
}
