package lk.steps.breakdownassistpluss.BreakdownDialogs;

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

import java.util.ArrayList;
import java.util.GregorianCalendar;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.Models.FailureObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Strings;
import lk.steps.breakdownassistpluss.Sync.SyncService;

public class NotAttendingDialog extends AppCompatActivity {
    Breakdown breakdown;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_not_attending_dialog);

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
            final EditText etComment = (EditText) findViewById(R.id.etComment);

            ArrayList<FailureObject> list = new ArrayList<FailureObject>();
            for (String[] array : Strings.NotAttendingComments) {
                FailureObject obj = new FailureObject();
                obj.AreaCode=array[0];
                obj.Id=array[1];
                obj.ParentId=array[2];
                obj.English=array[3];
                obj.Sinhala=array[4];
                list.add(obj);
            }
            //Spinner
            final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
            spinner.setAdapter(new ArrayAdapter<FailureObject>(this,
                    R.layout.spinner_row, R.id.textView, list));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

            ImageButton btnNotAttending = (ImageButton) findViewById(R.id.btnNotAttending);
            btnNotAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(spinner.getSelectedItemPosition()==0){
                        Toast.makeText(getApplicationContext(), "Please select a reason", Toast.LENGTH_SHORT).show();
                    }else {
                        FailureObject obj = (FailureObject) spinner.getSelectedItem();
                        JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                                Breakdown.JOB_NOT_ATTENDING, GetSelectedDateTime(getWindow().getDecorView()), obj.English);
                        UpdateJobStatusChange(jobStatusChangeRec, breakdown, Breakdown.JOB_ACKNOWLEDGED);

                        finish();
                    }
                }
            });
            ImageButton btnCancel = (ImageButton) findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  RestoreCard(fragment, breakdown, position);
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
}
