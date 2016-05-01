package cheesewheel.cheesewheel;

/**
 * Created by xflyter on 4/17/16.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    String loginUsername;
    boolean isLogin = false;
    @InjectView(R.id.input_name) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;
    @InjectView(R.id.offline) TextView _wheelLink;

    ServerConnection serverConnection = new ServerConnection();
    Boolean loginSuccess = true;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                intent.putExtra("loginUsername", loginUsername);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _wheelLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Wheel.class);
                intent.putExtra("loginUsername", loginUsername);
                startActivity(intent);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        loginUsername = _emailText.getText().toString();

        new LoginAsync().execute();

        isLogin = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("isLogin",isLogin).commit();

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_SIGNUP) {
//            if (resultCode == RESULT_OK) {
//
//                // TODO: Implement successful signup logic here
//                // By default we just finish the Activity and log them in automatically
//                String loginStr = "login " + _emailText.getText().toString() + " " + _passwordText.getText().toString();
//                String success = serverConnection.send(loginStr);
//                if (success == "true") {
//                    loginSuccess = true;
//                } else {
//                    loginSuccess = false;
//                }
//                this.finish();
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), Wheel.class);
        intent.putExtra("loginUsername", loginUsername);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void loginCheck() {
        String loginStr = "login " + _emailText.getText().toString() + " " + _passwordText.getText().toString();
        String success = serverConnection.send(loginStr);
        System.out.println("success variable: " + success);
        if (success.equals("true")) {
            loginSuccess = true;
        } else {
            loginSuccess = false;
        }
    }

    private class LoginAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            // Loading screen or something
        }

        @Override
        protected Void doInBackground(Void...params) {
            loginCheck();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("onPostExecute:");
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (loginSuccess) {
                onLoginSuccess();
            } else {
                onLoginFailed();
            }
            //this method will be running on UI thread
            // some type of alert to show that the querying and what not is done
        }
    }
}

