package cheesewheel.cheesewheel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Wheel extends AppCompatActivity implements Choice.FragmentListener {
    String loginUsername = "";
    ArrayList<String> rArray = new ArrayList<>();
    Bundle yelpBundle;

    private static String[] cuisines = new String[]{"Chinese","Fast Food","Japanese","BBQ","Pizza","Deli","Italian","Thai","Mediterranean",
            "Malaysian","Greek","Turkish","Moroccon","Chicken","Burgers","Bar Food","Mexican","Cafes","Seafood","Pizza","Sushi","Soul","Korean",
            "Vietnamese","Asian","Pastries","French","German","Vegetarian","Vegan","Jewish","Chinese-Islamic","Chinese-Mexican","Tex-Mex","Steak",
            "Hot Pot","Indian"
    };
    private static Random rand = new Random();

    private static Map<String,Float> choices;
    private static float[] angles;

    private static float ration;

    private static ArrayList<String> alreadyPlaced;

    private static ArrayList<String> rejected;

    private static String landed;
    private static int amount = 8;


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

    LocationManager lm;
    Location location;
    double latitude = 40.7128;
    double longitude = -74.0059;

    ProgressDialog progressDialog;

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
    String restaurantData = "chinese";
    String parsedYelpData;
    int rIndex;

    ServerConnection server = new ServerConnection();

    private GestureDetector detector;
    private boolean[] quadrantTouched;
    private boolean allowRotating;

    public void getChoices(){
        int left = cuisines.length - alreadyPlaced.size()-rejected.size();
        //System.out.println("left amount: " + left + " cuisines: " + cuisines.length + " already: " + alreadyPlaced.size() + " rejected: " + rejected.size() );
        while (left < amount ) amount /= 2;
        //System.out.println("amount: " + amount);
        ration = 360/amount;
        float angle = 0;
        angles = new float[amount];
//        for (int i = 0; i < amount; i++){
            int index = rand.nextInt(cuisines.length);
            while(alreadyPlaced.contains(cuisines[index]) || rejected.contains(cuisines[index])){index = rand.nextInt(cuisines.length);}
            alreadyPlaced.add(cuisines[index]);
            landed = cuisines[index];
            //System.out.println(angle);
            choices.put(cuisines[index], angle);
//            angles[i] = angle;
            angle += ration;
//        }
    }

    public void updateChoices(){
        alreadyPlaced.remove(landed);
        rejected.add(landed);
        int left = cuisines.length - alreadyPlaced.size()-rejected.size();
        choices.clear();
//        if(left < amount) angles = new float[amount];
//        while(left < amount) amount /=2;
        ration = 360/amount;
        float angle = 0;
        int index = rand.nextInt(cuisines.length);
        while(alreadyPlaced.contains(cuisines[index]) || rejected.contains(cuisines[index])){index = rand.nextInt(cuisines.length);}
        alreadyPlaced.add(cuisines[index]);
        landed = cuisines[index];
//        for(int i = 0; i < alreadyPlaced.size(); i++){
//            choices.put(alreadyPlaced.get(i),angle);
//            angles[i] = angle;
//            angle += ration;
//        }
    }

    public float getRotation(float angle, int amount){
        float rotate = angle;
        float weirdRotate = 90;
//        if(amount == 6) weirdRotate = ration*2;
//        else if(amount %2 != 0) weirdRotate = ration*2;
        if(amount % 2 == 0) {
            if (rotate < 90 && rotate > 0) rotate -= weirdRotate;
            else if (rotate < 180 && rotate > 90) if(amount == 6) rotate -= weirdRotate/2; else rotate-= weirdRotate;
            else if (rotate < 270 && rotate > 180) if(amount == 6) rotate += weirdRotate/2; else rotate+= weirdRotate;
            else if (rotate < 360 && rotate > 270) rotate += weirdRotate;
            else if (rotate == 180 || rotate == 90) rotate += 180;
        }
//        else if(amount == 7){
//            if(rotate < 90 && rotate > 0)rotate -= weirdRotate;
//            else if(rotate > 270) rotate+= weirdRotate;
//            else if(rotate < 152 && rotate > 90) rotate -= weirdRotate*2+180;
//            else if(rotate < 270 && rotate > 254) rotate += weirdRotate*2+180;
//            else if(rotate < 180 && rotate > 152) rotate -= weirdRotate;
//            else if(rotate < 255 && rotate > 180) rotate += weirdRotate/2+weirdRotate;
//        }
//        else if(amount == 5){
//            if(rotate < 90 && rotate > 0)rotate -= weirdRotate;
//            else if(rotate > 270) rotate+= weirdRotate;
//            else if(rotate < 180 && rotate > 90) rotate -= weirdRotate*2+180;
//            else if(rotate < 270 && rotate > 180) rotate += weirdRotate*2+180;
//        }
//        else if(amount == 3){
//            if(rotate < 180 && rotate > 90) rotate -= weirdRotate+180;
//            else if(rotate < 270 && rotate > 180) rotate += weirdRotate+180;
//        }
//        System.out.println("rotating: " + rotate);
        return rotate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loginUsername = extras.getString("loginUsername");
        }

        System.out.println("loginusername: " + loginUsername);

        setContentView(R.layout.activity_wheel);

        progressDialog = new ProgressDialog(Wheel.this,
                R.style.AppTheme_Dark_Dialog);

        // GPS Stuff

        lm =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (lm != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("permission has been granted");
                Criteria criteria = new Criteria();
                String bestProvider = lm.getBestProvider(criteria, false);
                lm.requestLocationUpdates(bestProvider, 100, 1, locationListener);
                location = lm.getLastKnownLocation(bestProvider);
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
                    resize.postScale((float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getWidth(), (float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                    imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
                    float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
                    float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
                    matrix.postTranslate(translateX, translateY);
                    dialer.setImageBitmap(imageScaled);
                    dialer.setImageMatrix(matrix);
                }
            }
        });
        choices = new HashMap<String,Float>();
        alreadyPlaced = new ArrayList<String>();
        rejected = new ArrayList<String>();
        getChoices();
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.screen);

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
            System.out.println(velocity);
            if (Math.abs(velocity) > 5 && allowRotating) {
                rotateDialer(velocity / 75);
                velocity /= 1.0666F;
                dialer.post(this);
            }
            else {
//                float[] vert = new float[9];
//                matrix.getValues(vert);
//                double bleh = Math.round(Math.atan2(vert[matrix.MSKEW_X], vert[matrix.MSCALE_X]) * (180 / Math.PI));
//                System.out.println("before: " + bleh);
//                bleh += 90;
//                if (bleh < 0) bleh += 360;
//                if (bleh > 360) bleh -= 360;
//                System.out.println("angle: " + bleh);
//                double landedAngle = bleh;
//                float range = 360 / choices.size() / 2;
//                for (int i = 0; i < angles.length; i++) {
//                    if (bleh >= angles[i] - range && bleh < angles[i] + range) {
//                        landedAngle = angles[i];
//                    }
//                }
//
//                for (String o : choices.keySet()) {
//                    if (choices.get(o).equals((float) landedAngle)) {
//                        landed = o;
//                    }
//                }
                //System.out.println("landed at: " + landedAngle + " on " + landed);
                restaurantData = landed;
                if (findViewById(R.id.fragment_container) != null) {
                    Choice choose = new Choice(landed,loginUsername);
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container, choose).commit();
                }
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
        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
        dialer.setImageMatrix(matrix);
    }

    public void getRestaurant() {
        if (location != null) {
            System.out.println("the location was not null");
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        new AsyncCaller().execute();
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Determining Restaurant...");
            progressDialog.show();
            // Loading screen or something
        }

        @Override
        protected Void doInBackground(Void...params) {
            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            String response = yelp.search(restaurantData, latitude, longitude);
            System.out.println("Yelp response:");
            System.out.println(response);
            // parse json to do something
            YelpParser yParser = new YelpParser();
            yParser.setResponse(response);
            try {
                yParser.parseBusiness();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            }
            yelpBundle = yParser.getYelpBundle();
            ArrayList<String> tempKeys = yParser.getBundleKeys();
            // Recreate necessary JSON String now
            JSONObject shorterResponse = new JSONObject();
            String spaceDelimitedString = "select " + loginUsername + " ";
            int position = 0;
            for (String key : tempKeys) {
                try {
                    String rData = key + " " + position + " " + restaurantData.replaceAll("\\s+", "");
                    rArray.add(position, rData);
                    if (position == tempKeys.size() - 1) {

                    } else {
                        spaceDelimitedString = spaceDelimitedString + rData + " ";
                    }
                    shorterResponse.put(key, JSONObject.wrap(yelpBundle.get(key)));
                } catch (JSONException e) {
                    // Some kind of error handling here
                }
                position = position + 1;
            }
            System.out.println("new stuff sent to server: " + spaceDelimitedString);
            System.out.print("Our server repsonse:");
            parsedYelpData = spaceDelimitedString;
            String index = server.send(spaceDelimitedString);
            System.out.println("index: " + index);
            rArray = tempKeys;
            restaurantData = tempKeys.get(Integer.parseInt(index));
            rIndex = Integer.parseInt(index);
            System.out.println("restaurant data: " + restaurantData);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), RestaurantViewer.class);
            intent.putExtra("parsedYelpData", parsedYelpData);
            intent.putExtra("restaurantData", restaurantData);
            intent.putExtra("loginUsername", loginUsername);
            intent.putExtra("rArray", rArray);
            intent.putExtra("rIndex", rIndex);
            intent.putExtra("bundle", yelpBundle);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        }
    }

    @Override
    public void onButtonSelect(boolean b, Choice c){
        if (b){
            getRestaurant();
        }
        else {
            getSupportFragmentManager().beginTransaction().remove(c).commit();
            updateChoices();
        }
    }
}