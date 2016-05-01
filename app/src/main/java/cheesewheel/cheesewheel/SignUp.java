package cheesewheel.cheesewheel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    String loginUsername;

    ServerConnection serverConnection = new ServerConnection();
    Boolean didSignUp = true;
    ProgressDialog progressDialog;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    @InjectView(R.id.offline) TextView _wheelLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(SignUp.this,
                R.style.AppTheme_Dark_Dialog);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        loginUsername = _nameText.getText().toString();

        new SignUpAsyncCaller().execute();
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), Wheel.class);
        intent.putExtra("loginUsername", loginUsername);
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign-up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void register() {
        String registerStr = "register " + _nameText.getText().toString() + " " + _passwordText.getText().toString() + " " + _emailText.getText().toString();
        String success = serverConnection.send(registerStr);
        System.out.println("success variable: " + success);
        if (success.equals("true")) {
            didSignUp = true;
        } else {
            didSignUp = false;
        }
    }

    private class SignUpAsyncCaller extends AsyncTask<Void, Void, Void> {
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            // Loading screen or something
        }

        @Override
        protected Void doInBackground(Void...params) {
            register();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("onPostExecute:");
            super.onPostExecute(result);
            System.out.println("didsignup variable: " + didSignUp);
            progressDialog.dismiss();
            if (didSignUp) {
                onSignupSuccess();
            } else {
                onSignupFailed();
            }
            //this method will be running on UI thread
            // some type of alert to show that the querying and what not is done
        }
    }
}