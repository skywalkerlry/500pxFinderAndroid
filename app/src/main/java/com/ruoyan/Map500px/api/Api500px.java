package com.ruoyan.map500px.api;

/**
 * Created by ruoyan on 2/16/15.
 */
public class Api500px {
    public static final String HOST_SEARCH = "https://api.500px.com/v1/photos/search?";
    public static final String HOST_BASIC = "https://api.500px.com/v1/photos/";

    private static final String CONSUMER_KEY = "m7DfdVZZJA85VGRL6jF7O58LRFV03pKBjstmMfRY";

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }
}
