package lk.steps.breakdownassistpluss;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import lk.steps.breakdownassistpluss.Fragments.GmapFragment;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinder;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassistpluss.Sync.BackgroundService;
import lk.steps.breakdownassistpluss.Sync.SyncObject;
import lk.steps.breakdownassistpluss.Sync.SyncRESTService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by JagathPrasanga on 5/22/2017.
 */

public class JobView {

    public static Dialog DialogInfo(final Fragment fragment, final Breakdown breakdown, final Marker marker, final Location currentLocation, final int position) {

        if (breakdown == null) {
            Toast.makeText(fragment.getActivity().getApplicationContext(),
                    "Breakdown details not available..", Toast.LENGTH_LONG).show();
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

        if(breakdown.get_Acct_Num() != null) txtAcctNum.setText("Acc. No. : " + breakdown.get_Acct_Num().trim());
        else  txtAcctNum.setText("Acc. No. : Not available");
        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        if (breakdown.get_Name() != null)
            txtName.setText(breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());

        TextView txtPhoneNo = (TextView) dialog.findViewById(R.id.phoneno);
        if (breakdown.get_Contact_No() != null)
            txtPhoneNo.setText(breakdown.get_Contact_No().trim());

        TextView txtFullDescription = (TextView) dialog.findViewById(R.id.fulldescription);
        txtFullDescription.setText(breakdown.get_Full_Description());

        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof JobListFragment) {
                    JobListFragment jobListFragment = (JobListFragment) fragment;
                    jobListFragment.RestoreItem(breakdown, position);
                }
                dialog.dismiss();
            }
        });

        ImageButton btnNavigate = (ImageButton) dialog.findViewById(R.id.btnNavigate);
        if (marker == null) btnNavigate.setEnabled(false);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(), "Press and Hold for Google Navigation !!",
                        Toast.LENGTH_SHORT).show();
                if (currentLocation != null) {
                    getDirections(fragment, new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker.getPosition());
                } else {
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Current location is not available, Please try again", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        btnNavigate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(fragment.getActivity().getApplicationContext(), "Opening Google Map Navigation...",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," +
                                marker.getPosition().longitude));
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
                intent.setData(Uri.parse("tel:" + breakdown.get_Contact_No().trim()));
                fragment.getActivity().startActivity(intent);
            }
        });

        Button btnVisted = (Button) dialog.findViewById(R.id.btnVisted);
        btnVisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobVisitedDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        Button btnAttending = (Button) dialog.findViewById(R.id.btnAttending);
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobAttendingDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobDoneDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        Button btnCompleted = (Button) dialog.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobCompleteDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        Button btnReject = (Button) dialog.findViewById(R.id.btnReject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobRejectDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
            btnCompleted.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_DONE) {
            btnDone.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_VISITED) {
            btnVisted.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_ATTENDING) {
            btnAttending.setTextColor(Color.RED);
        }

        dialog.show();
        return dialog;
    }
    private static void JobRejectDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_reject_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());

        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, Failure.RejectComments));
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

        ImageButton btnReject = (ImageButton) dialog.findViewById(R.id.btnReject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                        "R", GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_REJECT);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof JobListFragment) {
                    JobListFragment jobListFragment = (JobListFragment) fragment;
                    jobListFragment.RestoreItem(breakdown, position);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobVisitedDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_visited_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, Failure.VisitedComments));
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

        ImageButton btnVisited = (ImageButton) dialog.findViewById(R.id.btnVisited);
        btnVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                        "V", GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_VISITED);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof JobListFragment) {
                    JobListFragment jobListFragment = (JobListFragment) fragment;
                    jobListFragment.RestoreItem(breakdown, position);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobAttendingDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_attending_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, Failure.AttendingComments));
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
        ImageButton btnAttending = (ImageButton) dialog.findViewById(R.id.btnAttending);
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                        "A", GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_ATTENDING);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof JobListFragment) {
                    JobListFragment jobListFragment = (JobListFragment) fragment;
                    jobListFragment.RestoreItem(breakdown, position);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobDoneDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_done_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());

        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, Failure.DoneComments));
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

        ImageButton btnDone = (ImageButton) dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                        "D", GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_DONE);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof JobListFragment) {
                    JobListFragment jobListFragment = (JobListFragment) fragment;
                    jobListFragment.RestoreItem(breakdown, position);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Dialog JobCompleteDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_complete_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);

        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());

        //Failure Type Spinner
        final Spinner spinnerType = (Spinner) dialog.findViewById(R.id.spinner_failure_type);
        spinnerType.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, GetColumn(Failure.FailureTypeList)));

        final Spinner spinnerNature = (Spinner) dialog.findViewById(R.id.spinner_failure_nature);
        spinnerNature.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, GetFilteredColumn(Failure.FailureNatureList, "1")));

        final Spinner spinnerCause = (Spinner) dialog.findViewById(R.id.spinner_failure_cause);
        spinnerCause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, GetFilteredColumn(Failure.FailureCauseList, "1")));

        if(breakdown.get_Status()==Breakdown.JOB_COMPLETED){
            DBHandler dbHandler = new DBHandler(fragment.getActivity(), null, null, 1);

            JobCompletion obj= dbHandler.getJobCompletionRec(breakdown.get_Job_No());
            int type = Integer.parseInt(obj.type_failure);//2
            int cause = Integer.parseInt(obj.cause);//9
            int nature = Integer.parseInt(obj.detail_reason_code);//21
            spinnerType.setSelection(type);

            SetNatureSpinners(fragment, spinnerType, spinnerNature, spinnerCause);
            spinnerNature.setSelection(GetSubIndex(Failure.FailureNatureList,type,nature));

            SetCauseSpinners(fragment, spinnerType, spinnerNature, spinnerCause);
            spinnerCause.setSelection(GetSubIndex(Failure.FailureCauseList,nature,cause));

            spinnerType.setEnabled(false);
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);

        }else{
            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                    SetNatureSpinners(fragment, spinnerType, spinnerNature, spinnerCause);
                    if (view != null) {
                        TextView textView = (TextView) view.findViewById(R.id.textView);
                        if (position > 0)
                            textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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
                    SetCauseSpinners(fragment, spinnerType, spinnerNature, spinnerCause);
                    if (view != null) {
                        TextView textView = (TextView) view.findViewById(R.id.textView);
                        if (position > 0)
                            textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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
                        if (position > 0)
                            textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
                        else textView.setTextColor(Color.RED);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
        }


        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, Failure.CompletedComments));
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
        ImageButton btnCompleted = (ImageButton) dialog.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinnerType.getSelectedItemPosition() > 0 &
                        spinnerNature.getSelectedItemPosition() > 0 &
                        spinnerCause.getSelectedItemPosition() > 0) {
                    /*String type = spinnerType.getSelectedItem().toString();
                        String cause = spinnerNature.getSelectedItem().toString();
                    String description = spinnerCause.getSelectedItem().toString();*/
                    /*JobCompletion jobCompletionRec = new JobCompletion(
                            breakdown.get_Job_No(),
                            "T",
                            GetSelectedDateTime(dialog),
                            etComment.getText().toString(),
                            "SUPLOK",
                            "CUSFLT",
                            "SMBRDN",
                            "completedby",
                            "OTHERF");*/
                JobCompletion jobCompletionRec = new JobCompletion();
                jobCompletionRec.job_no = breakdown.get_Job_No();
                jobCompletionRec.job_completed_datetime = GetSelectedDateTime(dialog);
                jobCompletionRec.type_failure = GetId(Failure.FailureTypeList, spinnerType.getSelectedItem().toString());
                jobCompletionRec.detail_reason_code = GetId(Failure.FailureNatureList, spinnerNature.getSelectedItem().toString());
                jobCompletionRec.cause = GetId(Failure.FailureCauseList, spinnerCause.getSelectedItem().toString());

                UpdateCompletedJob(fragment, jobCompletionRec, breakdown);
                dialog.dismiss();
                } else {
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Please provide feedback information..", Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the job_dialog dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof JobListFragment) {
                    JobListFragment jobListFragment = (JobListFragment) fragment;
                    jobListFragment.RestoreItem(breakdown, position);
                }
                dialog.dismiss();
            }
        });

        dialog.show();


        return dialog;
    }


    private static void UpdateJobStatusChange(Fragment fragment, JobChangeStatus jobchangestatus, Breakdown breakdown, int iStatus) {
        DBHandler dbHandler = new DBHandler(fragment.getActivity().getApplicationContext(), null, null, 1);
        dbHandler.addJobStatusChangeRec(jobchangestatus);
        dbHandler.UpdateBreakdownStatus(breakdown, iStatus);
        BackgroundService.SyncBreakdownStatusChange(fragment.getActivity().getApplicationContext());
        if (fragment instanceof JobListFragment) {
            JobListFragment JobFrag = (JobListFragment) fragment;
            //JobFrag.CreateListView(fragment);
            JobFrag.mAdapter.notifyDataSetChanged();
        } else if (fragment instanceof GmapFragment) {
            GmapFragment GmapFrag = (GmapFragment) fragment;
            GmapFrag.RefreshJobsFromDB();
        }
        dbHandler.close();
    }

    private static void UpdateCompletedJob(Fragment fragment, JobCompletion jobcompletion, Breakdown breakdown) {
        DBHandler dbHandler = new DBHandler(fragment.getActivity().getApplicationContext(), null, null, 1);
        dbHandler.addJobCompletionRec(jobcompletion);
        dbHandler.UpdateBreakdownStatus(breakdown, Breakdown.JOB_COMPLETED);
        BackgroundService.SyncBreakdownCompletion(fragment.getActivity().getApplicationContext());
        if (fragment instanceof JobListFragment) {
            JobListFragment JobFrag = (JobListFragment) fragment;
            //JobFrag.CreateListView(fragment);
            JobFrag.mAdapter.notifyDataSetChanged();
        } else if (fragment instanceof GmapFragment) {
            GmapFragment GmapFrag = (GmapFragment) fragment;
            GmapFrag.RefreshJobsFromDB();
        }
        dbHandler.close();
    }

    private static void getDirections(final Fragment fragment, LatLng origin, LatLng destination) {
        //TODO : Exception when current location is not available
        try {
            String sOrigin = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
            String sDestination = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);
            new DirectionFinder((DirectionFinderListener) fragment, sOrigin, sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private static void SetNatureSpinners(Fragment fragment, Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause) {
       // int type = spinnerType.getSelectedItemPosition();
        String type = GetId(Failure.FailureTypeList, spinnerType.getSelectedItem().toString());

        if (type.equals("0")) {
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);
        } else {
            spinnerNature.setEnabled(true);
            spinnerCause.setEnabled(true);
            spinnerNature.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, R.id.textView, GetFilteredColumn(Failure.FailureNatureList, type)));
        }
        spinnerNature.setSelection(0);
        spinnerCause.setSelection(0);
    }

    private static void SetCauseSpinners(Fragment fragment, Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause) {
        // int type = spinnerType.getSelectedItemPosition();
        //int nature = spinnerNature.getSelectedItemPosition();
        String nature = GetId(Failure.FailureNatureList, spinnerNature.getSelectedItem().toString());

        if (nature.equals("0")) {
            spinnerCause.setEnabled(false);
        } else {
            spinnerCause.setEnabled(true);
            spinnerCause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, R.id.textView, GetFilteredColumn(Failure.FailureCauseList,nature)));
        }
        spinnerCause.setSelection(0);
    }


    private static String GetSelectedDateTime(Dialog view) {
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        GregorianCalendar calendar = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute());

        String text = "" + Globals.timeFormat.format(calendar.getTime());
        return text;
    }

    private static String[] GetColumn(String[][] array) {
        String out[] = new String[array.length];
        for (int row = 0; row < array.length; row++) {
            out[row] = array[row][2];
        }
        return out;
    }

    private static String[] GetFilteredColumn(String[][] Array, String ParentId) {
        String out[] = new String[Array.length];
        int i = 0;
        for (String[] array : Array) {
            if (array[3].equals(ParentId)) {
                out[i] = array[2];
                i++;
            }
        }
        return Arrays.copyOfRange(out, 0, i);
    }

    private static String GetId(String[][] Array, String name) {
        for (String[] array : Array) {
            if (array[2].equals(name)) {
                Log.e("FOUND", name + "" + array[1]);
                return array[1];
            }
        }
        Log.e("NOT FOUND", ""+name+" in "+Array.toString());
        return "0";
    }
    private static int GetSubIndex(String[][] Array, int parentId, int id) {
        int i = 0;
        for (String[] array : Array) {
            if (array[3].equals(String.valueOf(parentId))) {
                if (array[1].equals(String.valueOf(id))) {
                    Log.e("GetSubIndex", "GetSubIndex=" + i);
                    return i;
                }
                i++;
            }
        }
       // Log.e("NOT FOUND", " in "+Array.toString());
        return 0;
    }
    private static String GetName(String[][] Array, String id){
        for (String[] array : Array) {
            if (array[1].equals(id)) {
                Log.e("FOUND", id + "" + array[2]);
                return array[2];
            }
        }
        return "0";
    }


}
