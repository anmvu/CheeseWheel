package cheesewheel.cheesewheel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by xflyter on 5/1/16.
 */
public class RestaurantViewer extends AppCompatActivity {
    ServerConnection serverConnection = new ServerConnection();
    String loginUsername;
    String parsedYelpData;
    String restaurantData;
    int rIndex;
    ArrayList<String> rArray;
    ProgressDialog progressDialog;

    private Button yesButton;
    private Button noButton;

    String currentFunction = "reject";

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

    public void reject() {
        System.out.println("restaurantData: " + restaurantData);
        String rname = restaurantData;
        String sendToServer = "rejectrest " + loginUsername + " " + rname;
        String success = serverConnection.send(sendToServer);
        System.out.println("Success: " + success);
        sendNew();
    }

    public String getAddress(String data) {
        System.out.println("data: " + data);
        return "";
    }

    public void sendNew() {
        String index = serverConnection.send(parsedYelpData);
        restaurantData = rArray.get(Integer.parseInt(index));
        // rest data should contain the location and all that
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loginUsername = extras.getString("loginUsername");
            parsedYelpData = extras.getString("parsedYelpData");
            restaurantData = extras.getString("restaurantData");
            rArray = extras.getStringArrayList("rArray");
            rIndex = extras.getInt("rIndex");
        }
        progressDialog = new ProgressDialog(RestaurantViewer.this,
                R.style.AppTheme_Dark_Dialog);

        setContentView(R.layout.restaurant_viewer);

        // Set label stuff
        TextView rNameLabel = (TextView)findViewById(R.id.restaurantName);
        rNameLabel.setText(restaurantData);
        TextView rAddressLabel = (TextView)findViewById(R.id.restaurantAddress);
        rAddressLabel.setText(getAddress(rArray.get(rIndex)));

        // Button stuff
        this.yesButton = (Button)this.findViewById(R.id.Yes);
        this.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapYes();
            }
        });

        this.noButton = (Button)this.findViewById(R.id.No);
        this.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapNo();
            }
        });
    }

    public void didTapYes() {

    }

    public void didTapNo() {
        new NewRestaurantCaller().execute();
    }

    private class NewRestaurantCaller extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            if (currentFunction == "reject") {
                progressDialog.setMessage("Determining New Restaurant...");
            } else {
                progressDialog.setMessage("stuff...");
            }
            progressDialog.show();
            // Loading screen or something
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (currentFunction.equals("reject")) {
                reject();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

        }
    }
}

