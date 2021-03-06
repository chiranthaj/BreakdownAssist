package lk.steps.breakdownassist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "user:abc123", "user2:123456"
    };

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                    mPasswordView.clearFocus();
                    hideKeyboardFrom(getApplicationContext(),mPasswordView);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        FancyButton mUsernameSignInButton = (FancyButton) findViewById(R.id.sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        TextView txtAppName = (TextView) findViewById(R.id.txt_app_name);
        try{
            txtAppName.setText("Breakdown Assist "+ this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName + "\nCeylon Electricity Board  ©  2017");
        }catch(Exception e){
            txtAppName.setText("Ceylon Electricity Board  ©  2017");
        }

    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            Log.d("TEST","10");
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
            Log.d("TEST","11");
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
            Log.d("TEST","12");
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


            //mAuthTask = new UserLoginTask(username, password);
            //mAuthTask.execute((Void) null);
            Log.d("TEST","55");

            Boolean usernameExists = false;
            Boolean passwordMatches = false;
            for (String credential : DUMMY_CREDENTIALS) {
                String _username = credential.split(":")[0];
                String _password = credential.split(":")[1];
                if(_username.equals(username)){
                    usernameExists=true;
                    if(_password.equals(password))passwordMatches=true;
                }
            }

            if(usernameExists & passwordMatches){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("TEST","3");
                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(myIntent);
                    }
                });
                finish();
            }else if(usernameExists){
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }else{
                showProgress(false);
                mUsernameView.setError(getString(R.string.error_invalid_username));
                mUsernameView.requestFocus();
            }

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(username)) {
                    // Account exists, return true if the password matches.
                    if(pieces[1].equals(password)){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Log.d("TEST","3");
                                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(myIntent);
                            }
                        });
                        finish();
                    }else{
                        Log.d("TEST","1");
                        showProgress(false);
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }else{
                    Log.d("TEST","2");
                    showProgress(false);
                    mUsernameView.setError(getString(R.string.error_invalid_username));
                    mUsernameView.requestFocus();
                }
            }*/
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
       // return username.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

