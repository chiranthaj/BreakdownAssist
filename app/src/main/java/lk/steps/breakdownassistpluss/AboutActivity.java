package lk.steps.breakdownassistpluss;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lk.steps.breakdownassistpluss.HtmlTextView.HtmlTextView;



public class AboutActivity extends Activity {

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        String version = "0";
        int versionCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textview = (TextView) findViewById(R.id.vName);
        textview.setText("Version : "+ version);
      //  textview.setText("Version : "+ Global.VersionName.substring(0, Math.min(Global.VersionName.length(), 5)));

        textview = (TextView) findViewById(R.id.vCode);
        textview.setText("Build number : " + Integer.toString(versionCode));

        HtmlTextView text = (HtmlTextView) findViewById(R.id.html_text);
        text.setHtmlFromString(readEula(this), true);
       // Typeface font = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
      //  text.setTypeface(font);

    }

    private static String readEula(Activity activity) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(activity.getAssets().open("eula.txt")));
            String line;
            String eula="";
            while ((line = in.readLine()) != null) eula=eula+line;
            Log.d("eula ", eula);
            return eula;
        } catch (IOException e) {
            return "";
        }
    }

}
