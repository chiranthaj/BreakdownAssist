package lk.steps.breakdownassist;

import android.*;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import lk.steps.breakdownassist.Fragments.GmapFragment;
import lk.steps.breakdownassist.Fragments.JobListFragment;
import lk.steps.breakdownassist.Modules.DirectionFinder;
import lk.steps.breakdownassist.Modules.DirectionFinderListener;

/**
 * Created by JagathPrasanga on 5/22/2017.
 */

public  class JobView {

    public static Dialog DialogInfo(final Fragment fragment, final Breakdown breakdown, final Marker marker, final Location currentLocation){

        if(breakdown == null){
            Toast.makeText(fragment.getActivity().getApplicationContext(),
                    "Breakdown details not available..",Toast.LENGTH_LONG).show();
            return null;
        }

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);

        TextView txtJobno = (TextView) dialog.findViewById(R.id.jobno);
        txtJobno.setText(breakdown.get_Job_No().trim());
        TextView txtRecTime = (TextView) dialog.findViewById(R.id.received_date_time);
        txtRecTime.setText("Received time : " + Globals.parseDate(breakdown.get_Received_Time().trim()));
        TextView txtAcctNum = (TextView) dialog.findViewById(R.id.acctnum);
        txtAcctNum.setText("Acc. No. : "+breakdown.get_Acct_Num().trim());
        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        if(breakdown.get_Name() != null)
            txtName.setText(breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());

        TextView txtPhoneNo = (TextView) dialog.findViewById(R.id.phoneno);
        if(breakdown.get_Contact_No() != null)
            txtPhoneNo.setText(breakdown.get_Contact_No().trim());

        TextView txtFullDescription = (TextView) dialog.findViewById(R.id.fulldescription);
        txtFullDescription.setText("");

        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageButton btnNavigate = (ImageButton) dialog.findViewById(R.id.btnNavigate);
        if(marker == null)btnNavigate.setEnabled(false);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),"Press and Hold for Google Navigation !!",
                        Toast.LENGTH_SHORT).show();
                if (currentLocation!=null){
                    getDirections(fragment, new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),marker.getPosition() );
                }else{
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Current location is not available, Please try again",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        btnNavigate.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),"Opening Google Map Navigation...",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," +
                                marker.getPosition().longitude ));
                fragment.getActivity().startActivity(intent);
                dialog.dismiss();
                return true;
            }

        });


        ImageButton btnCall = (ImageButton) dialog.findViewById(R.id.btnMakeCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+breakdown.get_Contact_No().trim()));
                fragment.getActivity().startActivity(intent);
            }
        });

        Button btnVisted = (Button) dialog.findViewById(R.id.btnVisted);
        btnVisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobVisitedDialog(fragment,breakdown);
                dialog.dismiss();
            }
        });
        Button btnAttending = (Button) dialog.findViewById(R.id.btnAttending);
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobAttendingDialog(fragment,breakdown);
                dialog.dismiss();
            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobDoneDialog(fragment,breakdown);
                dialog.dismiss();
            }
        });
        Button btnCompleted = (Button) dialog.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobCompleteDialog(fragment,breakdown);
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }


    private static void JobVisitedDialog(final Fragment fragment, final Breakdown breakdown){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_visited_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No()+"\n"+breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter( new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row,R.id.textView, Failure.VisitedComments));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                TextView textView = (TextView) view.findViewById(R.id.textView);//Spinner textbox
                if(position==0){
                    etComment.setText("", TextView.BufferType.EDITABLE);
                }else {
                    etComment.setText(textView.getText().toString(), TextView.BufferType.EDITABLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ImageButton btnVisited = (ImageButton) dialog.findViewById(R.id.btnVisited);
        btnVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec=new JobChangeStatus(breakdown.get_Job_No(),
                        "V",GetSelectedDateTime(dialog),etComment.getText().toString());
                UpdateJobStatusChange(fragment,jobStatusChangeRec, breakdown,Breakdown.Status_JOB_VISITED );
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobAttendingDialog(final Fragment fragment, final Breakdown breakdown){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_attending_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No()+"\n"+breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter( new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row,R.id.textView, Failure.AttendingComments));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                TextView textView = (TextView) view.findViewById(R.id.textView);//Spinner textbox
                if(position==0){
                    etComment.setText("", TextView.BufferType.EDITABLE);
                }else {
                    etComment.setText(textView.getText().toString(), TextView.BufferType.EDITABLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        ImageButton btnAttending = (ImageButton) dialog.findViewById(R.id.btnAttending);
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec=new JobChangeStatus(breakdown.get_Job_No(),
                        "A",GetSelectedDateTime(dialog),etComment.getText().toString());
                UpdateJobStatusChange(fragment,jobStatusChangeRec, breakdown,Breakdown.Status_JOB_ATTENDING );
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobDoneDialog(final Fragment fragment, final Breakdown breakdown){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_done_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No()+"\n"+breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());

        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter( new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row,R.id.textView, Failure.DoneComments));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                TextView textView = (TextView) view.findViewById(R.id.textView);//Spinner textbox
                if(position==0){
                    etComment.setText("", TextView.BufferType.EDITABLE);
                }else {
                    etComment.setText(textView.getText().toString(), TextView.BufferType.EDITABLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ImageButton btnDone = (ImageButton) dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                        "D",GetSelectedDateTime(dialog),etComment.getText().toString());
                UpdateJobStatusChange(fragment,jobStatusChangeRec, breakdown,Breakdown.Status_JOB_DONE );
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }




    public static Dialog JobCompleteDialog(final Fragment fragment, final Breakdown breakdown){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_complete_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);

        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No()+"\n"+breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        
        //Failure Type Spinner
        final Spinner spinner_type = (Spinner) dialog.findViewById(R.id.spinner_failure_type);
        spinner_type.setAdapter( new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row,R.id.textView, Failure.Type));

        final Spinner spinner_cause = (Spinner) dialog.findViewById(R.id.spinner_failure_cause);
        spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row,R.id.textView, Failure.Cause1));

        final Spinner spinner_description = (Spinner) dialog.findViewById(R.id.spinner_failure_description);
        spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row,R.id.textView, Failure.Description1));

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                SetCauseSpinners(fragment,spinner_type,spinner_cause,spinner_description);
                if(view != null){
                    TextView textView = (TextView) view.findViewById(R.id.textView);
                    if(position>0)textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
                    else textView.setTextColor(Color.RED);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        spinner_cause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                SetDescriptionSpinners(fragment,spinner_type,spinner_cause,spinner_description);
                if(view != null){
                    TextView textView = (TextView) view.findViewById(R.id.textView);
                    if(position>0)textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
                    else textView.setTextColor(Color.RED);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        spinner_description.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                //SetDescriptionSpinners(fragment,spinner_type,spinner_cause,spinner_description);
                if(view != null){
                    TextView textView = (TextView) view.findViewById(R.id.textView);
                    if(position>0)textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
                    else textView.setTextColor(Color.RED);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ImageButton btnCompleted = (ImageButton) dialog.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String fault_type = spinner_type.getSelectedItem().toString();
                String fault_type = spinner_type.getSelectedItem().toString();
                String fault_type = spinner_type.getSelectedItem().toString();

                Toast.makeText(fragment.getActivity().getApplicationContext(),
                        "DateTime="+GetSelectedDateTime(dialog),Toast.LENGTH_LONG).show();
                JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                        "C",GetSelectedDateTime(dialog),"reason");
                UpdateJobStatusChange(fragment,jobStatusChangeRec, breakdown,Breakdown.Status_JOB_COMPLETED);
                ChangeMarker(fragment);//Change Maker as completed*/
                dialog.dismiss();
                //TODO : Use an Undo option
            }
        });
        ImageButton dialogButton_visited = (ImageButton) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the job_dialog dialog
        dialogButton_visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
    
    private static void UpdateJobStatusChange(Fragment fragment, JobChangeStatus jobchangestatus, Breakdown breakdown,int iStatus) {
        DBHandler dbHandler = new DBHandler(fragment.getActivity().getApplicationContext(), null, null, 1);
        dbHandler.addJobStatusChangeRec(jobchangestatus);
        dbHandler.UpdateBreakdownStatus(breakdown,iStatus);
        if (fragment instanceof JobListFragment) {
            JobListFragment.RefreshListView(fragment);
        }
    }

    private static void getDirections(final Fragment fragment, LatLng origin, LatLng destination) {
        //TODO : Exception when current location is not available
        try {
            String sOrigin=String.valueOf(origin.latitude) + ","+ String.valueOf(origin.longitude);
            String sDestination=String.valueOf(destination.latitude) + ","+ String.valueOf(destination.longitude);
            new DirectionFinder((DirectionFinderListener)fragment,sOrigin , sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    
    private static void SetCauseSpinners(Fragment fragment,Spinner spinner_type,Spinner spinner_cause, Spinner spinner_description){
        int type = spinner_type.getSelectedItemPosition();
        if(type == 0 | type == 1 ){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row,R.id.textView, Failure.Cause1));
            spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row,R.id.textView, Failure.Description1));
        }else if(type == 2){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row,R.id.textView, Failure.Cause2));
            spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row,R.id.textView, Failure.Description4));
        }else if(type == 3){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row,R.id.textView, Failure.Cause3));
            spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row,R.id.textView, Failure.Description6));
        }
        spinner_cause.setSelection(0);
        spinner_description.setSelection(0);

        if(type == 0){
            spinner_cause.setEnabled(false);
            spinner_description.setEnabled(false);
        }else{
            spinner_cause.setEnabled(true);
            spinner_description.setEnabled(true);
        }
    }

    private static void SetDescriptionSpinners(Fragment fragment,Spinner spinner_type,Spinner spinner_cause, Spinner spinner_description){
        int type = spinner_type.getSelectedItemPosition();
        int cause = spinner_cause.getSelectedItemPosition();

        if((type == 0 | type == 1 ) ){
            if(cause == 1){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description1));
            }else if(cause == 2){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description2));
            }else if(cause == 3){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description3));
            }
        }else if(type == 2){
            if(cause == 1){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description4));
            }else if(cause == 2){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description5));
            }
        }else if(type == 3){
            if(cause == 1){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description6));
            }else if(cause == 2){
                spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                        R.layout.spinner_row,R.id.textView, Failure.Description7));
            }
        }
        spinner_description.setSelection(0);
        if(cause == 0){
            spinner_description.setEnabled(false);
        }else{
            spinner_description.setEnabled(true);
        }
    }


    private static String GetSelectedDateTime(Dialog view){
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        GregorianCalendar calendar=new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),timePicker.getCurrentMinute());

        String text = ""+ Globals.timeFormat.format(calendar.getTime());
        return  text;
    }


    private static void ChangeMarker(Fragment fragment){
        final FragmentManager fm;
        fm = fragment.getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(), MainActivity.MAP_FRAGMENT_TAG).commit();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Fragment fragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                if (fragment instanceof GmapFragment) {
                    GmapFragment GmapFrag = (GmapFragment) fragment;
                    GmapFrag.RefreshJobsFromDB();
                }
            }
        }, 2000);
    }
}
