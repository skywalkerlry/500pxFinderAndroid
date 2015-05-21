package com.ruoyan.map500px.data;

import android.content.Context;

import com.ruoyan.map500px.api.Api500px;

/**
 * Created by ruoyan on 3/9/15.
 */
public class RequestUrl {
    private LocalDataSource dataSource;
    private String feature;
    private String only;
    private String exclude;
    private String sort;
    private int photoNum;

    public String getUrlOfCurrentSettings(Context context, String userLatitude,
                                          String userLongitude,
                                          double searchRadius, int imageSize) {
        dataSource = new LocalDataSource(context);
        if (dataSource.hasSettings("stream")) {
            feature = "feature="+dataSource.getSettings("stream");
            if (feature.equals("feature=default")) {
                feature = "";
            }
        }
        else
            feature = "";

        if (dataSource.hasSettings("category")) {
            String result = dataSource.getSettings("category");
            switch (result) {
                case "all" :
                    only = "";
                    exclude = "";
                    break;
                case "no people" :
                    only = "";
                    exclude = "&exclude=People";
                    break;
                case "people" :
                    only = "&only=People";
                    exclude = "";
                    break;
            }
        }
        else {
            only = "";
            exclude = "";
        }

        if (dataSource.hasSettings("sorting")) {
            sort = "&sort="+dataSource.getSettings("sorting");
            if (sort.equals("&sort=none")) {
                sort = "";
            }
        }
        else {
            sort = "";
        }

        photoNum = dataSource.getSettingsOrder("photo_number");
        if (photoNum==-1)
            photoNum = 20;

        return Api500px.HOST_SEARCH + feature
                + only + exclude + sort + "&rpp=" + Integer.toString(photoNum) + "&geo=" +
                userLatitude + "%2C" + userLongitude
                + "%2C" + Double.toString(searchRadius) + "mi" + "&image_size=" + Integer.toString
                (imageSize) +
                "&consumer_key=" + Api500px
                .getConsumerKey();
    }
}
