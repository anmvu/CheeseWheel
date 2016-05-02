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
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by xflyter on 5/1/16.
 */
public class RestaurantViewer extends AppCompatActivity {
    ServerConnection serverConnection = new ServerConnection();
    String loginUsername;
    String parsedYelpData;
    String restaurantData;
    int rIndex;
    double latitude;
    double longitude;
    ArrayList<String> rArray;
    ProgressDialog progressDialog;
    Bundle bundle;

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

    public String getSpacedRName(String data) {
        String temp = data.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
        return temp;
    }

    public String getPhoneNumber(String data) {
        String phoneNum = bundle.getString(data);
        phoneNum = phoneNum.substring(0, phoneNum.indexOf(","));
        return phoneNum;
    }

    public void reject() {
        System.out.println("restaurantData: " + restaurantData);
        String rname = restaurantData;
        String sendToServer = "rejectrest " + loginUsername + " " + rname;
        String success = serverConnection.send(sendToServer);
        System.out.println("Success: " + success);
        sendNew();
    }

    public String getAddress(String data) {
        String temp = bundle.getString(data);
        System.out.println("string got from bundle:" + temp);
        temp = temp.substring(temp.indexOf("[") + 1);
        temp = temp.substring(0, temp.indexOf("]"));
        return temp;
    }

    public void sendNew() {
        String index = serverConnection.send(parsedYelpData);
        rIndex = Integer.parseInt(index);
        restaurantData = rArray.get(Integer.parseInt(index));
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
            bundle = extras.getBundle("bundle");
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
        }
        progressDialog = new ProgressDialog(RestaurantViewer.this,
                R.style.AppTheme_Dark_Dialog);

        setContentView(R.layout.restaurant_viewer);

        // Set label stuff
        TextView rNameLabel = (TextView)findViewById(R.id.restaurantName);
        rNameLabel.setText(getSpacedRName(restaurantData));
        TextView rAddressLabel = (TextView)findViewById(R.id.restaurantAddress);
        rAddressLabel.setText(getAddress(rArray.get(rIndex)));
        final TextView rPhoneLabel = (TextView)findViewById(R.id.rPhone);
        rPhoneLabel.setText(getPhoneNumber(rArray.get(rIndex)));

        rPhoneLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = rPhoneLabel.getText().toString().replaceAll("-", "");
                number = number.replace("+", "");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+number));
                startActivity(callIntent);
            }
        });

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
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + restaurantData + "+,+" + (getAddress(rArray.get(rIndex))).replace("\\s+", "+"));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
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

            TextView rNameLabel = (TextView)findViewById(R.id.restaurantName);
            rNameLabel.setText(getSpacedRName(restaurantData));
            TextView rAddressLabel = (TextView)findViewById(R.id.restaurantAddress);
            rAddressLabel.setText(getAddress(rArray.get(rIndex)));
            TextView rPhoneLabel = (TextView)findViewById(R.id.rPhone);
            rPhoneLabel.setText(getPhoneNumber(rArray.get(rIndex)));
        }
    }
}

