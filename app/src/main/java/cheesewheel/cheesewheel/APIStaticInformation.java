package cheesewheel.cheesewheel;

/**
 * Created by xflyter on 4/20/16.
 */

public class APIStaticInformation {
    private final String YELP_CONSUMER_KEY ="Jcxok9clExtwJHQ6sH_t_g";
    private final String YELP_CONSUMER_SECRET = "1kcL0AbqGnB-u-qlUbrZnrlRheM";
    private final String YELP_TOKEN = "DibVSEEczXLy45xUKbvd_q6VgCgymp2p";
    private final String YELP_TOKEN_SECRET = "eYbmcqVaqT-OxpVraCX0sbHSKkM";


    public String getYelpConsumerKey(){
        return YELP_CONSUMER_KEY;
    }

    public String getYelpConsumerSecret(){
        return YELP_CONSUMER_SECRET;
    }

    public String getYelpToken(){
        return YELP_TOKEN;
    }

    public String getYelpTokenSecret(){
        return YELP_TOKEN_SECRET;
    }

}