package lk.steps.breakdownassist;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.UnsupportedEncodingException;

import lk.steps.breakdownassist.Fragments.JobListFragment;
import lk.steps.breakdownassist.Modules.DirectionFinder;
import lk.steps.breakdownassist.Modules.DirectionFinderListener;

/**
 * Created by JagathPrasanga on 5/22/2017.
 */

public  class JobView {

    public static void Dialog(final Fragment fragment, final Breakdown breakdown, final Marker marker, final Location lastLocation){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_dialog);
        //dialog.setTitle("Job Details");

        TextView txtJobno = (TextView) dialog.findViewById(R.id.jobno);
        txtJobno.setText(breakdown.get_Job_No().trim());
        TextView txtRecTime = (TextView) dialog.findViewById(R.id.received_date_time);
        txtRecTime.setText("Received time : " + breakdown.get_Received_Time().trim());
        TextView txtAcctNum = (TextView) dialog.findViewById(R.id.acctnum);
        txtAcctNum.setText("Acc. No. : "+breakdown.get_Acct_Num().trim());
        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        if(breakdown.get_Name() != null)
            txtName.setText(breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());

        TextView txtPhoneNo = (TextView) dialog.findViewById(R.id.phoneno);
        if(breakdown.get_Contact_No() != null)
            txtPhoneNo.setText(breakdown.get_Contact_No().trim());

        TextView txtFullDescription = (TextView) dialog.findViewById(R.id.fulldescription);
        //txtFullDescription.setText(selectedBreakdown.get_Full_Description().trim());
        txtFullDescription.setText("");

        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BreakdownFeedbackDialog(fragment,breakdown);
                // UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        ImageButton dialogButton_visited = (ImageButton) dialog.findViewById(R.id.dialogButtonVisited);
        dialogButton_visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UpdateBreakDown(fragment, breakdown,Breakdown.Status_JOB_VISITED);
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        ImageButton dialogButton_navigate = (ImageButton) dialog.findViewById(R.id.dialogButtonNavigate);
        if(marker == null)dialogButton_navigate.setEnabled(false);
        dialogButton_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
                Toast.makeText(fragment.getActivity().getApplicationContext(),"Press and Hold for Google Navigation !!",
                        Toast.LENGTH_SHORT).show();
                Location currentLocation = lastLocation;
                if (currentLocation!=null){
                    getDirections(fragment, new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),marker.getPosition() );
                }else{
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Current location is not available, Please try again",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        dialogButton_navigate.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),"Opening Google Navigation...",
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
        // if button is clicked, close the job_dialog dialog
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+breakdown.get_Contact_No().trim()));
                fragment.getActivity().startActivity(intent);
            }
        });

        Button btnVisted = (Button) dialog.findViewById(R.id.btnVisted);
        // if button is clicked, close the job_dialog dialog
        btnVisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobVisitedDialog(fragment,breakdown);
                //Toast.makeText(fragment.getActivity().getApplicationContext(),
                //        "btnVisted",Toast.LENGTH_LONG).show();
            }
        });
        Button btnAttending = (Button) dialog.findViewById(R.id.btnAttending);
        // if button is clicked, close the job_dialog dialog
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobAttendingDialog(fragment,breakdown);
                //Toast.makeText(fragment.getActivity().getApplicationContext(),
                //        "btnAttending",Toast.LENGTH_LONG).show();
            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        // if button is clicked, close the job_dialog dialog
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobDoneDialog(fragment,breakdown);
                //Toast.makeText(fragment.getActivity().getApplicationContext(),
                //        "btnDone",Toast.LENGTH_LONG).show();
            }
        });
        Button btnCompleted = (Button) dialog.findViewById(R.id.btnCompleted);
        // if button is clicked, close the job_dialog dialog
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobCompleteDialog(fragment,breakdown);
                //Toast.makeText(fragment.getActivity().getApplicationContext(),
                //        "btnCompleted",Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }


    private static void JobVisitedDialog(final Fragment fragment, final Breakdown breakdown){

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_visited_dialog);
        //TODO : Use date time picker
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        //Spinner


        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);
        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(spinner1.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure type");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner2.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure nature");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner3.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure cause");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else{
                    UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                    Log.d("Reason ",spinner1.getSelectedItem().toString());
                    dialog.dismiss();
                }*/
                UpdateBreakDown(fragment, breakdown,Breakdown.Status_JOB_COMPLETED);
                //Log.d("Reason ",spinner1.getSelectedItem().toString());
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
    }

    private static void JobAttendingDialog(final Fragment fragment, final Breakdown breakdown){

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_attending_dialog);
        //TODO : Use date time picker
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());


        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);
        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(spinner1.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure type");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner2.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure nature");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner3.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure cause");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else{
                    UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                    Log.d("Reason ",spinner1.getSelectedItem().toString());
                    dialog.dismiss();
                }*/
                UpdateBreakDown(fragment, breakdown,Breakdown.Status_JOB_COMPLETED);
                //Log.d("Reason ",spinner1.getSelectedItem().toString());
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
    }

    private static void JobDoneDialog(final Fragment fragment, final Breakdown breakdown){

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_done_dialog);
        //TODO : Use date time picker
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());


        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);
        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(spinner1.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure type");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner2.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure nature");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner3.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure cause");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else{
                    UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                    Log.d("Reason ",spinner1.getSelectedItem().toString());
                    dialog.dismiss();
                }*/
                UpdateBreakDown(fragment, breakdown,Breakdown.Status_JOB_COMPLETED);
                //Log.d("Reason ",spinner1.getSelectedItem().toString());
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
    }

    private static void JobCompleteDialog(final Fragment fragment, final Breakdown breakdown){

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_complete_dialog);
        //TODO : Use date time picker
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());

        //Failure Type Spinner
        final Spinner spinner_type = (Spinner) dialog.findViewById(R.id.spinner_failure_type);
        spinner_type.setAdapter( new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, Failure.Type));

        final Spinner spinner_cause = (Spinner) dialog.findViewById(R.id.spinner_failure_cause);
        spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, Failure.Cause1));

        final Spinner spinner_description = (Spinner) dialog.findViewById(R.id.spinner_failure_description);
        spinner_description.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, Failure.Description1));

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                SetSpinners(fragment,spinner_type,spinner_cause,spinner_description);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);


        DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);

        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(spinner1.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure type");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner2.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure nature");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner3.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure cause");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else{
                    UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                    Log.d("Reason ",spinner1.getSelectedItem().toString());
                    dialog.dismiss();
                }*/
                UpdateBreakDown(fragment, breakdown,Breakdown.Status_JOB_COMPLETED);
                //Log.d("Reason ",spinner1.getSelectedItem().toString());
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
    }

    private static void UpdateBreakDown(Fragment fragment, Breakdown breakdown,int iStatus) {
        DBHandler dbHandler = new DBHandler(fragment.getActivity().getApplicationContext(), null, null, 1);
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


    private static void SetSpinners(Fragment fragment,Spinner spinner_type,Spinner spinner_cause, Spinner spinner_description){
        int type = spinner_type.getSelectedItemPosition();
        if(type == 0 | type == 1 ){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, Failure.Cause1));
        }else if(type == 2){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, Failure.Cause2));
        }else if(type == 3){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, Failure.Cause3));
        }

        int cause = spinner_cause.getSelectedItemPosition();
        if(type == 0 | type == 1 ){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, Failure.Cause1));
        }else if(type == 2){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, Failure.Cause2));
        }else if(type == 3){
            spinner_cause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, Failure.Cause3));
        }

        int description= spinner_description.getSelectedItemPosition();

    }
}
