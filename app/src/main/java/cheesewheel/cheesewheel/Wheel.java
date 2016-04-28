package cheesewheel.cheesewheel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import java.util.ArrayList;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Wheel extends AppCompatActivity {

    private static final String IP_ADDRESS = "172.16.22.91";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private static Bitmap imageOriginal, imageScaled;
    private static Matrix matrix;

    private ImageView dialer;
    private int dialerHeight, dialerWidth;

    private Button getRestaurantsButton;
    private String foodType;

    LocationManager lm;
    Location location;
    double longitude;
    double latitude;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    APIStaticInformation apiKeys = new APIStaticInformation();
    Yelp yelp = new Yelp(apiKeys.getYelpConsumerKey(), apiKeys.getYelpConsumerSecret(), apiKeys.getYelpToken(), apiKeys.getYelpTokenSecret());
    String restaurantData;

    private GestureDetector detector;
    private boolean[] quadrantTouched;
    private boolean allowRotating;

    ServerConnection server = new ServerConnection();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        this.getRestaurantsButton = (Button)this.findViewById(R.id.getRestaurants);
        this.getRestaurantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRestaurant();
            }
        });

        // GPS STUFF
        lm =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("permission has been granted");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

        // load the image only once
        if (imageOriginal == null) {
            imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.fork);
        }

        detector = new GestureDetector(this, new MyGestureDetector());

        quadrantTouched = new boolean[] {false, false, false, false, false};
        allowRotating = true;

        // initialize the matrix only once
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            // not needed, you can also post the matrix immediately to restore the old state
            matrix.reset();
        }

        dialer = (ImageView) findViewById(R.id.imageView_ring);
        dialer.setOnTouchListener(new MyOnTouchListener());
        dialer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (dialerHeight == 0 || dialerWidth == 0) {
                    dialerHeight = dialer.getHeight();
                    dialerWidth = dialer.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getHeight());
                    imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
                    float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
                    float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
                    matrix.postTranslate(translateX, translateY);
                    dialer.setImageBitmap(imageScaled);
                    dialer.setImageMatrix(matrix);
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do something map related
                    System.out.println("needed to request the permissions");
                    try {
                        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    } catch (SecurityException e) {
                        System.out.println("it failed");
                    }
                } else {

                }
                return;
            }
        }
    }

    private void getRestaurant() {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        new AsyncCaller().execute();
    }

    private class MyOnTouchListener implements View.OnTouchListener {

        private double startAngle;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    double currentAngle = getAngle(event.getX(), event.getY());
                    rotateDialer((float) (startAngle - currentAngle));
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:
                    allowRotating = true;
                    break;
            }

            quadrantTouched[getQuadrant(event.getX() - (dialerWidth / 2), dialerHeight - event.getY() - (dialerHeight / 2))] = true;
            detector.onTouchEvent(event);

            return true;
        }

    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int q1 = getQuadrant(e1.getX() - (dialerWidth / 2), dialerHeight - e1.getY() - (dialerHeight / 2));
            int q2 = getQuadrant(e2.getX() - (dialerWidth / 2), dialerHeight - e2.getY() - (dialerHeight / 2));

            if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math.abs(velocityY))
                    || (q1 == 3 && q2 == 3)
                    || (q1 == 1 && q2 == 3)
                    || (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math.abs(velocityY))
                    || ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
                    || ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
                    || (q1 == 2 && q2 == 4 && quadrantTouched[3])
                    || (q1 == 4 && q2 == 2 && quadrantTouched[3])) {
                dialer.post(new FlingRunnable(-1 * (velocityX + velocityY)));
            } else {
                dialer.post(new FlingRunnable(velocityX + velocityY));
            }
            return true;
        }
    }

    private class FlingRunnable implements Runnable {
        private float velocity;
        public FlingRunnable(float velocity) {
            this.velocity = velocity;
        }

        @Override
        public void run() {
            if (Math.abs(velocity) > 5 && allowRotating) {
                rotateDialer(velocity / 75);
                velocity /= 1.0666F;
                dialer.post(this);
            }
        }
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - (dialerWidth / 2d);
        double y = dialerHeight - yTouch - (dialerHeight / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    /**
     * @return The selected quadrant.
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    private void rotateDialer(float degrees) {
        matrix.postRotate(degrees, dialerWidth/2, dialerHeight/2);
        dialer.setImageMatrix(matrix);
    }

    public class Async extends Activity {
        @Override
        protected  void onCreate(Bundle savedInstance) {
            super.onCreate(savedInstance);
        }

        @Override
        protected  void onResume() {
            super.onResume();
            new AsyncCaller().execute();
        }
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();

            // Loading screen or something
        }

        @Override
        protected Void doInBackground(Void...params) {
            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            String response = yelp.search("chinese", latitude, longitude);
            System.out.print("Yelp response:");
            System.out.print(response + "\n");
            // parse json to do something
            YelpParser yParser = new YelpParser();
            yParser.setResponse(response);
            try {
                yParser.parseBusiness();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                //Do whatever you want with the error, like throw a Toast error report
            }
            Bundle tempBundle = yParser.getYelpBundle();
            ArrayList<String> tempKeys = yParser.getBundleKeys();
            // Recreate necessary JSON String now
            JSONObject shorterResponse = new JSONObject();
            for (String key : tempKeys) {
                try {
                    System.out.println("key:" + key + tempBundle.get(key));
                    shorterResponse.put(key, JSONObject.wrap(tempBundle.get(key)));
                } catch (JSONException e) {
                    // Some kind of error handling here
                }
            }
            String strShorterResponse = shorterResponse.toString();
            System.out.println("stringifed parsed json: " + strShorterResponse);
            System.out.print("Our server repsonse:");
            restaurantData = server.send(strShorterResponse);
            System.out.print(restaurantData);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            // some type of alert to show that the querying and what not is done
        }
    }
}
