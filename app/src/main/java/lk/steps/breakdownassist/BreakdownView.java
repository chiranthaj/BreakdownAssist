package lk.steps.breakdownassist;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public  class BreakdownView {

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
        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BreakdownFeedbackDialog(fragment,breakdown);
                // UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        ImageButton dialogButton_visited = (ImageButton) dialog.findViewById(R.id.dialogButtonVisited);
        // if button is clicked, close the job_dialog dialog
        dialogButton_visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateBreakDown(fragment, breakdown,Breakdown.Status_JOB_VISITED);
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        ImageButton dialogButton_navigate = (ImageButton) dialog.findViewById(R.id.dialogButtonNavigate);
        if(marker == null)dialogButton_navigate.setEnabled(false);
        // if button is clicked, close the job_dialog dialog
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
                Toast.makeText(fragment.getActivity().getApplicationContext(),
                        "btnVisted",Toast.LENGTH_LONG).show();
            }
        });
        Button btnAttending = (Button) dialog.findViewById(R.id.btnAttending);
        // if button is clicked, close the job_dialog dialog
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),
                        "btnAttending",Toast.LENGTH_LONG).show();
            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        // if button is clicked, close the job_dialog dialog
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),
                        "btnDone",Toast.LENGTH_LONG).show();
            }
        });
        Button btnCompleted = (Button) dialog.findViewById(R.id.btnVisted);
        // if button is clicked, close the job_dialog dialog
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),
                        "btnCompleted",Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    public static void BreakdownFeedbackDialog(final Fragment fragment, final Breakdown breakdown){

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_complete_dialog);
        //TODO : Use date time picker
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if(breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Name().trim()+"\n"+breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        //Spinner
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(fragment.getActivity(), R.array.failure_type, R.layout.spinner_row );
        final Spinner spinner1 = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner1.setAdapter(adapter1);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(fragment.getActivity(), R.array.failure_nature, R.layout.spinner_row );
        final Spinner spinner2 = (Spinner) dialog.findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(fragment.getActivity(), R.array.failure_cause, R.layout.spinner_row );
        final Spinner spinner3 = (Spinner) dialog.findViewById(R.id.spinner3);
        spinner3.setAdapter(adapter3);

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
                Log.d("Reason ",spinner1.getSelectedItem().toString());
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

    public static void UpdateBreakDown(Fragment fragment, Breakdown breakdown,int iStatus) {
        DBHandler dbHandler = new DBHandler(fragment.getActivity().getApplicationContext(), null, null, 1);
        dbHandler.UpdateBreakdownStatus(breakdown,iStatus);
        if (fragment instanceof JobListFragment) {
            JobListFragment.RefreshListView(fragment);
        }
    }

    public static void getDirections(final Fragment fragment, LatLng origin, LatLng destination) {
        //TODO : Exception when current location is not available
        try {
            String sOrigin=String.valueOf(origin.latitude) + ","+ String.valueOf(origin.longitude);
            String sDestination=String.valueOf(destination.latitude) + ","+ String.valueOf(destination.longitude);
            new DirectionFinder((DirectionFinderListener)fragment,sOrigin , sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
