package cheesewheel.cheesewheel;
import org.scribe.model.Token;
import org.scribe.builder.api.DefaultApi10a;

/**
 * Created by xflyter on 4/20/16.
 */
public class YelpAPI2 extends DefaultApi10a{
    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token arg0) {
        return null;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }
}