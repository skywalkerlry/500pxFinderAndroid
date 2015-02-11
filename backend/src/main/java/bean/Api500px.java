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

    public static int getPhotoHoldingDays() {return PHOTO_HOLDING_DAYS; }

    public static int getPhotosPerPage() {return PHOTOS_PER_PAGE;}

    public static int getTotalPage() {return TOTAL_PAGE;}

    private static final String BASIC_URL = "https://api.500px" +
            ".com/v1/photos?feature=popular&exclude=People&sort=rating";
    private static final int PHOTOS_PER_PAGE = 100;
    private static final int TOTAL_PAGE = 50;
    private static final String CONSUMER_KEY = "m7DfdVZZJA85VGRL6jF7O58LRFV03pKBjstmMfRY";
    private static final long UPDATE_INTERVAL = 300*1000;
    private static final int PHOTO_HOLDING_DAYS = 3;
}
