package lk.steps.breakdownassist;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import java.util.List;

import lk.steps.breakdownassist.FileExplorer.FileChooser;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final int READ_REQUEST_CODE_CUSTOMER_DATA = 42;
    private static final int READ_REQUEST_CODE_PREMISES_DATA = 52;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onStop() {
        Globals.initAreaCodes(getBaseContext());
        super.onStop();
    }
/*    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        if (header.id == R.id.Database_update) {
            Toast.makeText(this,"Database_update",Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(BreakDownAssist.getAppContext(), FileChooser.class);
            intent1.putExtra("fileTypeFilter",".db");
            startActivityForResult(intent1,READ_REQUEST_CODE_CUSTOMER_DATA);
        }
    }*/



    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {

        if (preference instanceof Preference) { //To avoid the fragments such as data & synch without any preferences object
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }


    // Added to fix up button issue in Android 6.0
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("area_name"));
            bindPreferenceSummaryToValue(findPreference("night_mode"));
            bindPreferenceSummaryToValue(findPreference("areacode1"));
            bindPreferenceSummaryToValue(findPreference("areacode2"));
            bindPreferenceSummaryToValue(findPreference("areacode3"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    private static Preference.OnPreferenceClickListener PreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("Import_Customer_Data")){
                Intent intent1 = new Intent(BreakdownAssist.getAppContext(), FileChooser.class);
                intent1.putExtra("fileTypeFilter",".db");
                getPrefActivity(preference).startActivityForResult(intent1,READ_REQUEST_CODE_CUSTOMER_DATA);
            }else if(preference.getKey().equals("Import_PremisesID_Data")){
                Intent intent1 = new Intent(BreakdownAssist.getAppContext(), FileChooser.class);
                intent1.putExtra("fileTypeFilter",".db");
                getPrefActivity(preference).startActivityForResult(intent1,READ_REQUEST_CODE_PREMISES_DATA);
            }
            return false;
        }
    };

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));

            findPreference("Import_Customer_Data").setOnPreferenceClickListener(PreferenceClickListener);
            findPreference("Import_PremisesID_Data").setOnPreferenceClickListener(PreferenceClickListener);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }
    //For Database update file browse
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent resultData) {
        DBHandler dbHandler;
        dbHandler = new DBHandler(this,null,null,1);

        if (requestCode == READ_REQUEST_CODE_CUSTOMER_DATA && resultCode == Activity.RESULT_OK) {
            String curFileName = resultData.getStringExtra("GetFullPathFileName") ;
            dbHandler.importGPSdata(curFileName);
            Toast.makeText(this, "Finished uploading Customer data from "+ curFileName, Toast.LENGTH_SHORT).show();
        }else if(requestCode == READ_REQUEST_CODE_PREMISES_DATA && resultCode == Activity.RESULT_OK) {
            String curFileName = resultData.getStringExtra("GetFullPathFileName") ;
            dbHandler.importPremisesID(curFileName);
            Toast.makeText(this, "Finished uploading Premises data from "+ curFileName, Toast.LENGTH_SHORT).show();
        }
        dbHandler.close();
    }

    @Nullable
    public static Activity getPrefActivity(Preference pref)
    {
        Context c = pref.getContext();
        if (c instanceof ContextThemeWrapper)
        {
            if (((ContextThemeWrapper) c).getBaseContext() instanceof Activity)
                return (Activity) ((ContextThemeWrapper) c).getBaseContext();
        }
        else if (c instanceof Activity)
            return (Activity) c;
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


