package lk.steps.breakdownassistpluss.BreakdownDialogs;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Common;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.Models.FailureObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.Models.Team;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Strings;
import lk.steps.breakdownassistpluss.Sync.SyncService;

public class ReturnDialog extends AppCompatActivity {
    Breakdown breakdown;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_return_dialog);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String json  = bundle.getString("breakdown");
            breakdown = new Gson().fromJson(json, new TypeToken<Breakdown>() {}.getType());
            position = bundle.getInt("position");


            //
            TextView txtView = (TextView) findViewById(R.id.jobInfo);
            if (breakdown.NAME != null)
                txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
            else
                txtView.setText(breakdown.get_Job_No());

            ArrayList<FailureObject> list = new ArrayList<FailureObject>();
            for (String[] array : Strings.ReturnComments) {
                FailureObject obj = new FailureObject();
                obj.AreaCode=array[0];
                obj.Id=array[1];
                obj.ParentId=array[2];
                obj.English=array[3];
                obj.Sinhala=array[4];
                list.add(obj);
            }

            final EditText etComment = (EditText) findViewById(R.id.etComment);
            final Spinner teamSpinner = (Spinner) findViewById(R.id.spinner2);

            final List<Team> teams = GetTeamsList();

            teamSpinner.setAdapter(new ArrayAdapter<Team>(this,
                    R.layout.spinner_row, R.id.textView, teams));

            teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                    TextView textView = (TextView) view.findViewById(R.id.textView);//Spinner textbox
                    if (position == 0) {
                        textView.setTextColor(Color.RED);
                        etComment.setText("", TextView.BufferType.EDITABLE);
                    } else {
                        etComment.setText(textView.getText().toString(), TextView.BufferType.EDITABLE);
                        textView.setTextColor(getResources().getColor(R.color.darkGreen));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            //Spinner
            final Spinner reasonSpinner = (Spinner) findViewById(R.id.spinner1);
            reasonSpinner.setAdapter(new ArrayAdapter<FailureObject>(this,
                    R.layout.spinner_row, R.id.textView, list));
            reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                    TextView textView = (TextView) view.findViewById(R.id.textView);//Spinner textbox
                    if (position == 0) {
                        textView.setTextColor(Color.RED);
                        etComment.setText("", TextView.BufferType.EDITABLE);
                    } else {
                        etComment.setText(textView.getText().toString(), TextView.BufferType.EDITABLE);
                        textView.setTextColor(getResources().getColor(R.color.darkGreen));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

            ImageButton btnReject = (ImageButton) findViewById(R.id.btnReject);
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(teamSpinner.getSelectedItemPosition()==0){
                        Toast.makeText(getApplication(), "Please select a team to forward", Toast.LENGTH_SHORT).show();
                    }else if(reasonSpinner.getSelectedItemPosition()==0){
                        Toast.makeText(getApplication(), "Please select a reason", Toast.LENGTH_SHORT).show();
                    }else {
                        String teamId = Globals.mToken.team_id;
                        if(teamSpinner.getSelectedItemPosition()>1){
                            teamId = teams.get(teamSpinner.getSelectedItemPosition()).teamId;
                            FailureObject obj = (FailureObject) reasonSpinner.getSelectedItem();
                            JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                                    Breakdown.JOB_FORWARDED, GetSelectedDateTime(getWindow().getDecorView()), obj.English);
                            breakdown.set_TeamId(teamId);

                            UpdateJobStatusChange( jobStatusChangeRec, breakdown, Breakdown.JOB_FORWARDED);
                        }else{
                            FailureObject obj = (FailureObject) reasonSpinner.getSelectedItem();
                            JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                                    Breakdown.JOB_RETURNED, GetSelectedDateTime(getWindow().getDecorView()), obj.English);
                            breakdown.set_TeamId(teamId);

                            UpdateJobStatusChange( jobStatusChangeRec, breakdown, Breakdown.JOB_RETURNED);
                        }

                       // JobListFragment.CreateListView(fragment);
                        finish();
                    }
                }
            });

            ImageButton btnCancel = (ImageButton) findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // RestoreCard(fragment, breakdown, position);
                    finish();
                }
            });
        }
        this.setFinishOnTouchOutside(false);
    }

    private void UpdateJobStatusChange(JobChangeStatus jobchangestatus, Breakdown breakdown, int iStatus) {
        Globals.dbHandler.addJobStatusChangeRec(jobchangestatus);
        Globals.dbHandler.UpdateBreakdownStatus(breakdown, iStatus);
        SyncService.PostBreakdownStatusChange(getApplicationContext());
        /*if (fragment instanceof JobListFragment) {
            JobListFragment JobFrag = (JobListFragment) fragment;
            //JobFrag.CreateListView(fragment);
            JobFrag.mAdapter.notifyDataSetChanged();
        } else if (fragment instanceof GmapFragment) {
            GmapFragment GmapFrag = (GmapFragment) fragment;
            GmapFrag.RefreshJobsFromDB();
        }*/
    }


    private static String GetSelectedDateTime(View view) {
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        GregorianCalendar calendar = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute());

        String text = "" + Globals.timeFormat.format(calendar.getTime());
        return text;
    }

    private List<Team> GetTeamsList(){

        final List<Team> teams = new ArrayList<Team>();

        String json = Common.ReadStringPreferences(getApplication(),"teams_in_area",null);
        if(json!=null){
            Type listType = new TypeToken<List<Team>>() {}.getType();
            List<Team> _teams = new Gson().fromJson(json, listType);
            if(_teams!=null)teams.addAll(_teams);
        }

        Team pleaseSelect = new Team();
        pleaseSelect.teamId="0";
        pleaseSelect.teamName="කරුණාකර තෝරන්න";

        Team backOffice = new Team();
        backOffice.teamId="1";
        backOffice.teamName="Back Office";

        teams.add(0,pleaseSelect);
        teams.add(1,backOffice);

        return teams;
    }
}
