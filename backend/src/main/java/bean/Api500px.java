package bean;

/**
 * Created by ruoyan on 2/7/15.
 */
public class Api500px {
    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }

    public static String getBasicUrl() {
        return BASIC_URL;
    }

    public static long getUpateInterval() {
        return UPDATE_INTERVAL;
    }



    private static final String BASIC_URL = "https://api.500px.com/v1/photos?feature=fresh_today&exclude=People&sort=rating&rpp=46";
    private static final String CONSUMER_KEY = "m7DfdVZZJA85VGRL6jF7O58LRFV03pKBjstmMfRY";
    private static final long UPDATE_INTERVAL = 60*1000;
}
