package cheesewheel.cheesewheel;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Wheel extends AppCompatActivity {

    private static final String IP_ADDRESS = "172.16.21.188";

    private static String[] cuisines = new String[]{"Chinese","Fast Food","Japanese","BBQ","Pizza","Deli","Italian","Thai","Mediterranean"};

    private static Random rand = new Random();

    private static Map<String,Float> choices;
    private static float[] angles;

    private static ArrayList<String> alreadyPlaced;

    private static ArrayList<String> rejected;

    private static String landed;

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

    private GestureDetector detector;
    private boolean[] quadrantTouched;
    private boolean allowRotating;

    public void getChoices(){
        float angle = 0;
        int amount = 8;
        int left = cuisines.length - alreadyPlaced.size()-rejected.size();

        //System.out.println("left amount: " + left + " cuisines: " + cuisines.length + " already: " + alreadyPlaced.size() + " rejected: " + rejected.size() );
        if (left < amount ) amount = left;
        //System.out.println("amount: " + amount);
        angles = new float[amount];
        for (int i = 0; i < amount; i++){
            int index = rand.nextInt(cuisines.length);
            while(alreadyPlaced.contains(cuisines[index])){index = rand.nextInt(cuisines.length);}
            alreadyPlaced.add(cuisines[index]);
            choices.put(cuisines[index],angle);
            angles[i] = angle;
            angle += (360.0/amount);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

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

        //System.out.println("amount " + alreadyPlaced.size());

        for (int i= 0; i < alreadyPlaced.size(); i++) {
            float angle = choices.get(alreadyPlaced.get(i));
            TextView text = new TextView(this);
            text.setText(alreadyPlaced.get(i));
            text.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            text.setGravity(Gravity.CENTER);
            int id = View.generateViewId();
            float rotate = angle;
            if (rotate > 90 && rotate < 270) rotate -= 180;
            text.setRotation(rotate);

            text.setTranslationX(300*(float)Math.cos(Math.toRadians(angle)));
            text.setTranslationY(300*(float)Math.sin(Math.toRadians(angle)));
            //System.out.println(alreadyPlaced.get(i) + " before: " + angle + " X: " + text.getX() + " Y: " + text.getY());

            //System.out.println(alreadyPlaced.get(i) + " after: " + angle);
            myLayout.addView(text);

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
            System.out.println("angle: " + startAngle);
            double landedAngle = startAngle;
            float range = 360/choices.size();
            for(int i = 0; i < angles.length; i++){
                if (startAngle >= angles[i] && startAngle < angles[i]+range){
                    landedAngle = angles[i];
                }
            }
            System.out.println("landed: "+ landedAngle);
            for(String o : choices.keySet()){
//                System.out.println(choices.get(o));
                if(choices.get(o).equals((float)landedAngle)) {
                    System.out.println("found");
                    landed = o;
                }
            }
            System.out.println("text: " + landed);
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
        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
        dialer.setImageMatrix(matrix);
    }

}