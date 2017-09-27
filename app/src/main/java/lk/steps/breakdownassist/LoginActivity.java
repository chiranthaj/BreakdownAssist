package lk.steps.breakdownassist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.URL;

import lk.steps.breakdownassist.Sync.SyncRESTService;
import lk.steps.breakdownassist.Sync.Token;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
  //  private View mLoginFormView;

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

      // mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        ShowLastCredential();

        TextView txtAppName = (TextView) findViewById(R.id.txt_app_name);
        try{
            txtAppName.setText("Breakdown Assist "+ this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName + "\nCeylon Electricity Board  ©  2017");
        }catch(Exception e){
            txtAppName.setText("Ceylon Electricity Board  ©  2017");
        }

        //GetIpAddress();

    }

    private void GetIpAddress(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    InetAddress address = InetAddress.getByName(new URL("http://meterasist.hopto.org").getHost());
                    String ip = address.getHostAddress();
                    Globals.serverUrl="http://"+ip+"";
                    Log.e("IP","="+ip);
                }catch(Exception e){
                    Log.e("IP","="+e.getMessage());
                }
            }
        });
        thread.start();
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
            performeLogin(username,password);
            /*Boolean usernameExists = false;
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
    private boolean ForceLocalLogin = false;

    private void performeLogin(final String username, final String password){

        long lastLoginTime = ReadLongPreferences("last_login_time", 0);
        final long currentTime = System.currentTimeMillis()/1000;
        long expiresIn = ReadLongPreferences("expires_in", 0);
        String lastUsername = ReadStringPreferences("last_username", "");
        String lastPassword = ReadStringPreferences("last_password", "");

        Log.e("lastLoginTime","="+lastLoginTime);
        Log.e("currentTime","="+currentTime);
        Log.e("expiresIn","="+expiresIn);
        Log.e("lastUsername","="+lastUsername);
        Log.e("lastPassword","="+lastPassword);
        long safeTimeMargin = 60*60*-1000;
        if(((lastLoginTime + expiresIn + safeTimeMargin > currentTime) | ForceLocalLogin ) & // token expired or will not expire in next hour and user/pass are correct
                (lastUsername.equals(username) & lastPassword.equals(password))){
            Log.e("Login","Local login");//Local login
            showProgress(false);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    //Log.d("TEST","3");
                    WriteLongPreferences("last_login_time",currentTime);
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                }
            });
            finish();
        }else if(!ForceLocalLogin){
            Log.e("Login","Remote login");//Remote login
            SyncRESTService syncAuthService = new SyncRESTService();
            Call<Token> call = syncAuthService.getService().GetJwt(username,password);
            call.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    if (response.isSuccessful()) {
                        Log.e("GetAuthToken","Authorized");
                        showProgress(false);
                        Token token = response.body();
                        //SaveToken(token);
                        WriteStringPreferences("user_id",token.user_id);
                        WriteStringPreferences("area_id",token.area_id);
                        WriteStringPreferences("team_id",token.team_id);
                        WriteLongPreferences("expires_in",token.expires_in);
                        WriteStringPreferences("access_token",token.access_token);
                        WriteStringPreferences("group_token",token.group_token);
                        WriteStringPreferences("last_username",username);
                        WriteStringPreferences("last_password",password);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                //Log.d("TEST","3");
                                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(myIntent);
                            }
                        });
                        Toast.makeText(getApplicationContext(),"Login successful.. ", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (response.errorBody() != null) {
                        showProgress(false);
                        Log.d("GetAuthToken","Fail"+response.errorBody());
                        Toast.makeText(getApplicationContext(),"Network failure..\nSwitch to local login"+response.errorBody(), Toast.LENGTH_LONG).show();
                        ForceLocalLogin = true;
                        performeLogin(username,password);

                        /*if(error.getResponse()==null){
                            Toast.makeText(getApplicationContext(),"Network failure.. "+error, Toast.LENGTH_LONG).show();
                        }else if (error.getResponse().getStatus()==401){
                            Log.e("GetAuthToken","Unauthorized:"+error);
                            mUsernameView.setError("Login fail..");
                            mUsernameView.requestFocus();
                        }else {
                            Toast.makeText(getApplicationContext(),"Network failure.. "+error, Toast.LENGTH_LONG).show();
                        }*/
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    Log.e("Login","Remote login onFailure"+t.getMessage());//Remote login
                    ForceLocalLogin = true;
                    performeLogin(username,password);
                }

            });
        }
    }



    private void WriteLongPreferences(String key, long value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putLong(key,value).apply();
    }
    private void WriteStringPreferences(String key, String value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString(key,value).apply();
    }
    private String ReadStringPreferences(String key, String defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }
    private long ReadLongPreferences(String key, long defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getLong(key, defaultValue);
    }

    private void ShowLastCredential(){
        String lastUsername = ReadStringPreferences("last_username", "");
        String lastPassword = ReadStringPreferences("last_password", "");
        mUsernameView.setText(lastUsername);
        mPasswordView.setText(lastPassword);
    }
}

