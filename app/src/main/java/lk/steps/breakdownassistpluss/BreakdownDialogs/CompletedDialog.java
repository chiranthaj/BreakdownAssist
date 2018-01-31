package lk.steps.breakdownassistpluss.BreakdownDialogs;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.GregorianCalendar;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Fragments.GmapFragment;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.Models.FailureObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.Models.JobCompletion;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Strings;
import lk.steps.breakdownassistpluss.Sync.SyncService;

public class CompletedDialog extends AppCompatActivity {
    private static String[][] FailureTypeList;
    private static String[][] FailureCauseList;
    private static String[][] FailureNatureList;

    private static ArrayList<FailureObject> FailureTypeList2 = new  ArrayList<FailureObject>();
    private static ArrayList<FailureObject> FailureCauseList2 = new  ArrayList<FailureObject>();
    private static ArrayList<FailureObject> FailureNatureList2 = new  ArrayList<FailureObject>();

    Breakdown breakdown;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_complete_dialog);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String json  = bundle.getString("breakdown");
            breakdown = new Gson().fromJson(json, new TypeToken<Breakdown>() {}.getType());
            position = bundle.getInt("position");

            LoadFailureTypeList();
            LoadFailureCauseList("1");
            LoadFailureNatureList("1");

            TextView txtView = (TextView) findViewById(R.id.jobInfo);
            if (breakdown.NAME != null)
                txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
            else
                txtView.setText(breakdown.get_Job_No());

            final EditText sin = (EditText) findViewById(R.id.etSin);
            sin.setText(breakdown.get_SUB());

            //Strings Type Spinner
            final Spinner spinnerType = (Spinner) findViewById(R.id.spinner_failure_type);
            spinnerType.setAdapter(new ArrayAdapter<FailureObject>(this,
                    R.layout.spinner_row, R.id.textView, FailureTypeList2));

            final Spinner spinnerNature = (Spinner) findViewById(R.id.spinner_failure_nature);
            spinnerNature.setAdapter(new ArrayAdapter<FailureObject>(this,
                    R.layout.spinner_row, R.id.textView, FailureNatureList2));


            final Spinner spinnerCause = (Spinner) findViewById(R.id.spinner_failure_cause);
            spinnerCause.setAdapter(new ArrayAdapter<FailureObject>(this,
                    R.layout.spinner_row, R.id.textView, FailureCauseList2));

            if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
                JobCompletion obj = Globals.dbHandler.getJobCompletionRec(breakdown.get_Job_No());
                int type = Integer.parseInt(obj.type_failure);//2

                spinnerType.setSelection(type);

                LoadFailureNatureList(obj.type_failure);
                spinnerNature.setSelection(GetSubIndex(FailureNatureList2,obj.detail_reason_code));


                LoadFailureCauseList(obj.detail_reason_code);
                spinnerCause.setSelection(GetSubIndex(FailureCauseList2, obj.cause));

                spinnerType.setEnabled(false);
                spinnerNature.setEnabled(false);
                spinnerCause.setEnabled(false);

            } else {
                spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                        FailureObject obj = (FailureObject) parent.getItemAtPosition(position);
                        SetNatureSpinners( spinnerType, spinnerNature, spinnerCause, obj);

                        if (view != null) {
                            TextView textView = (TextView) view.findViewById(R.id.textView);
                            if (position > 0)
                                textView.setTextColor(getResources().getColor(R.color.darkGreen));
                            else textView.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
                spinnerNature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                        FailureObject obj = (FailureObject) parent.getItemAtPosition(position);
                        SetCauseSpinners( spinnerType, spinnerNature, spinnerCause,obj);

                        if (view != null) {
                            TextView textView = (TextView) view.findViewById(R.id.textView);
                            if (position > 0)
                                textView.setTextColor(getResources().getColor(R.color.darkGreen));
                            else textView.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
                spinnerCause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                        //SetCauseSpinners(fragment,spinnerType,spinnerNature,spinnerCause);
                        if (view != null) {
                            TextView textView = (TextView) view.findViewById(R.id.textView);
                            if(textView != null){
                                if (position > 0)
                                    textView.setTextColor(getResources().getColor(R.color.darkGreen));
                                else textView.setTextColor(Color.RED);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }


            final EditText etComment = (EditText) findViewById(R.id.etComment);
            //Spinner
            final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
            spinner.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.spinner_row, R.id.textView, Strings.CompletedComments));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                    TextView textView = (TextView) view.findViewById(R.id.textView);//Spinner textbox
                    if (position == 0) {
                        etComment.setText("", TextView.BufferType.EDITABLE);
                    } else {
                        etComment.setText(textView.getText().toString(), TextView.BufferType.EDITABLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            ImageButton btnCompleted = (ImageButton) findViewById(R.id.btnCompleted);
            btnCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
                       // JobMaterialDialog(fragment, breakdown, position);
                        finish();
                    }else if(sin.getText().toString().length()<4 | sin.getText().toString().equals("")){
                        // }else if(sin.getText().toString().length()<4 & !sin.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),
                                "Erroneous SIN", Toast.LENGTH_LONG).show();
                    } else if (spinnerType.getSelectedItemPosition() > 0 &
                            spinnerNature.getSelectedItemPosition() > 0 &
                            spinnerCause.getSelectedItemPosition() > 0) {
                        JobCompletion jobCompletionRec = new JobCompletion();
                        jobCompletionRec.JOB_NO = breakdown.get_Job_No();
                        jobCompletionRec.job_completed_datetime = GetSelectedDateTime(getWindow().getDecorView());
                        FailureObject typeObj = (FailureObject) spinnerType.getSelectedItem();
                        FailureObject natureObj = (FailureObject) spinnerNature.getSelectedItem();
                        FailureObject causeObj = (FailureObject) spinnerCause.getSelectedItem();
                        jobCompletionRec.type_failure = typeObj.Id;
                        jobCompletionRec.detail_reason_code = natureObj.Id;
                        jobCompletionRec.cause = causeObj.Id;
                        breakdown.set_SUB(sin.getText().toString());
                        UpdateCompletedJob(jobCompletionRec, breakdown);
                        //JobListFragment.CreateListView(fragment);
                        Globals.AverageTime = Globals.dbHandler.getAttendedTime();
                        //JobMaterialDialog(fragment, breakdown, position);
                        String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                        Intent i = new Intent(getApplication(), MaterialDialog.class);
                        i.putExtra("breakdown", json);
                        startActivity(i);

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please provide feedback information..", Toast.LENGTH_LONG).show();
                    }
                }
            });


            ImageButton btnCancel = (ImageButton) findViewById(R.id.btnCancel);
            // if button is clicked, close the job_dialog dialog
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //RestoreCard(fragment, breakdown, position);
                    finish();
                }
            });

        }
        this.setFinishOnTouchOutside(false);
    }

    private void UpdateCompletedJob(JobCompletion jobcompletion, Breakdown breakdown) {
        Globals.dbHandler.addJobCompletionRec(jobcompletion);
        Globals.dbHandler.UpdateBreakdownStatus(breakdown, Breakdown.JOB_COMPLETED);
        SyncService.PostBreakdownCompletion(getApplicationContext());

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

    private  void LoadFailureTypeList(){

        FailureTypeList = Strings.GetFailureTypeList(getApplicationContext());

        FailureTypeList2.clear();
        for (String[] array : FailureTypeList) {
            FailureObject obj = new FailureObject();
            obj.AreaCode=array[0];
            obj.Id=array[1];
            obj.ParentId=array[2];
            obj.English=array[3];
            obj.Sinhala=array[4];
            FailureTypeList2.add(obj);
        }
    }
    private void LoadFailureCauseList(String parentId){
        FailureCauseList = Strings.GetFailureCauseList(getApplicationContext());

        FailureCauseList2.clear();
        for (String[] array : FailureCauseList) {

            if(array[2].equals(parentId)) {
                FailureObject obj = new FailureObject();
                obj.AreaCode=array[0];
                obj.Id=array[1];
                obj.ParentId=array[2];
                obj.English=array[3];
                obj.Sinhala=array[4];
                FailureCauseList2.add(obj);
            }
        }
    }
    private  void LoadFailureNatureList(String parentId){

        FailureNatureList = Strings.GetFailureNatureList(getApplicationContext());
        FailureNatureList2.clear();
        for (String[] array : FailureNatureList) {

            if(array[2].equals(parentId)){
                FailureObject obj = new FailureObject();
                obj.AreaCode=array[0];
                obj.Id=array[1];
                obj.ParentId=array[2];
                obj.English=array[3];
                obj.Sinhala=array[4];
                FailureNatureList2.add(obj);
            }


        }
    }



    private void SetNatureSpinners(Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause, FailureObject obj) {

        String type =obj.Id;
        Log.e("TEST","Nature="+obj.Id+","+obj.Sinhala);
        if (type.equals("0")) {
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);
        } else {
            LoadFailureNatureList( type);
            spinnerNature.setEnabled(true);
            spinnerCause.setEnabled(true);
            spinnerNature.setAdapter(new ArrayAdapter<FailureObject>(getApplication(),
                    R.layout.spinner_row, R.id.textView, FailureNatureList2));
            Log.e("TEST","FailureNatureList2 size="+FailureNatureList2.size());
        }
        spinnerNature.setSelection(0);
        spinnerCause.setSelection(0);
    }

    private void SetCauseSpinners(Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause, FailureObject obj) {
        String nature =obj.Id;
        Log.e("TEST","Cause="+obj.Sinhala);
        if (nature.equals("0")) {
            spinnerCause.setEnabled(false);
        } else {
            spinnerCause.setEnabled(true);
            LoadFailureCauseList(nature);
            spinnerCause.setAdapter(new ArrayAdapter<FailureObject>(getApplication(),
                    R.layout.spinner_row, R.id.textView, FailureCauseList2));

        }
        spinnerCause.setSelection(0);
    }
    private static int GetSubIndex(ArrayList<FailureObject> list, String Id) {
        int i = 0;
        for (FailureObject obj : list) {
            if(obj.Id.equals(Id)){
                Log.e("TEST FOUND", " -> "+i);
                return i;
            }
            i++;
        }
        // Log.e("NOT FOUND", " in "+Array.toString());
        return 0;
    }
}
