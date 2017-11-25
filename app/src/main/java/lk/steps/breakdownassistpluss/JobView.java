package lk.steps.breakdownassistpluss;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import lk.steps.breakdownassistpluss.Fragments.GmapFragment;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinder;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import lk.steps.breakdownassistpluss.MaterialList.MaterialViewsAdapter;
import lk.steps.breakdownassistpluss.MaterialList.Store;
import lk.steps.breakdownassistpluss.Sync.SyncService;


/**
 * Created by JagathPrasanga on 5/22/2017.
 */

public class JobView {
    private static String[][] FailureTypeList;
    private static String[][] FailureCauseList;
    private static String[][] FailureNatureList;

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

        if (breakdown.get_Acct_Num() != null)
            txtAcctNum.setText("Acc. No. : " + breakdown.get_Acct_Num().trim());
        else txtAcctNum.setText("Acc. No. : Not available");
        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        if (breakdown.get_Name() != null)
            txtName.setText(breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());

        TableRow contacts = (TableRow)dialog.findViewById(R.id.contacts);
        ImageButton btnCall = (ImageButton) dialog.findViewById(R.id.btnMakeCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + breakdown.get_Contact_No().trim()));
                fragment.getActivity().startActivity(intent);
            }
        });
        TextView txtPhoneNo = (TextView) dialog.findViewById(R.id.phoneno);
        if (breakdown.get_Contact_No() != null) {
            txtPhoneNo.setText(breakdown.get_Contact_No().trim());
            contacts.setVisibility(View.VISIBLE);
        } else {
            contacts.setVisibility(View.GONE);
            //btnCall.setVisibility(View.GONE);
        }
        TextView txtNote = (TextView) dialog.findViewById(R.id.txtNote);
        if(TextUtils.isEmpty(breakdown.get_Note())){
            txtNote.setVisibility(View.GONE);
        }else{
            txtNote.setVisibility(View.VISIBLE);
            txtNote.setText(breakdown.get_Note());
        }


        TextView txtFullDescription = (TextView) dialog.findViewById(R.id.fulldescription);
        txtFullDescription.setText(breakdown.get_Full_Description());

        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreCard(fragment, breakdown, position);
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
                if (marker != null) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," +
                                    marker.getPosition().longitude));
                    fragment.getActivity().startActivity(intent);
                }
                dialog.dismiss();
                return true;
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
        //Toast.makeText(fragment.getActivity().getApplicationContext(),
        //        "Breakdown STATUS ="+breakdown.get_Status(), Toast.LENGTH_LONG).show();
        if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
            btnAttending.setEnabled(false);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(false);
            btnReject.setEnabled(false);
            btnCompleted.setEnabled(false);
            btnCompleted.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED) {
            btnAttending.setEnabled(false);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(false);
            btnReject.setEnabled(false);
            btnCompleted.setEnabled(true);
            btnDone.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_VISITED) {
            btnAttending.setEnabled(true);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(true);
            btnReject.setEnabled(true);
            btnCompleted.setEnabled(true);
            btnVisted.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_ATTENDING) {
            btnAttending.setEnabled(false);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(true);
            btnReject.setEnabled(true);
            btnCompleted.setEnabled(true);
            btnAttending.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_REJECT) {
            btnAttending.setEnabled(false);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(false);
            btnReject.setEnabled(false);
            btnCompleted.setEnabled(false);
            btnReject.setTextColor(Color.RED);
        }

        dialog.show();
        return dialog;
    }


    private static void JobVisitedDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
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
                        Breakdown.JOB_VISITED, GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_VISITED);
                JobListFragment.CreateListView(fragment);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreCard(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobAttendingDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
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
                        Breakdown.JOB_ATTENDING, GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_ATTENDING);
                JobListFragment.CreateListView(fragment);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreCard(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void JobDoneDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
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
                        Breakdown.JOB_TEMPORARY_COMPLETED, GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_TEMPORARY_COMPLETED);
                JobListFragment.CreateListView(fragment);
                JobMaterialDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RestoreCard(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Dialog JobCompleteDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return null;
        FailureTypeList = Failure.GetFailureTypeList(fragment.getActivity().getApplicationContext());
        FailureCauseList = Failure.GetFailureCauseList(fragment.getActivity().getApplicationContext());
        FailureNatureList = Failure.GetFailureNatureList(fragment.getActivity().getApplicationContext());

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

        final EditText sin = (EditText) dialog.findViewById(R.id.etSin);
        sin.setText(breakdown.get_SUB());

        //Failure Type Spinner
        final Spinner spinnerType = (Spinner) dialog.findViewById(R.id.spinner_failure_type);
        spinnerType.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, GetColumn(FailureTypeList)));

        final Spinner spinnerNature = (Spinner) dialog.findViewById(R.id.spinner_failure_nature);
        spinnerNature.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, GetFilteredColumn(FailureNatureList, "1")));

        final Spinner spinnerCause = (Spinner) dialog.findViewById(R.id.spinner_failure_cause);
        spinnerCause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, GetFilteredColumn(FailureCauseList, "1")));

        if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
            JobCompletion obj = Globals.dbHandler.getJobCompletionRec(breakdown.get_Job_No());
            int type = Integer.parseInt(obj.type_failure);//2
            int cause = Integer.parseInt(obj.cause);//9
            int nature = Integer.parseInt(obj.detail_reason_code);//21
            spinnerType.setSelection(type);

            SetNatureSpinners(fragment, spinnerType, spinnerNature, spinnerCause);
            spinnerNature.setSelection(GetSubIndex(FailureNatureList, type, nature));

            SetCauseSpinners(fragment, spinnerType, spinnerNature, spinnerCause);
            spinnerCause.setSelection(GetSubIndex(FailureCauseList, nature, cause));

            spinnerType.setEnabled(false);
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);

        } else {
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

                if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
                    JobMaterialDialog(fragment, breakdown, position);
                    dialog.dismiss();
                }else if(sin.getText().toString().length()<4 & !sin.getText().toString().equals("")){
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Erroneous SIN", Toast.LENGTH_LONG).show();
                } else if (spinnerType.getSelectedItemPosition() > 0 &
                        spinnerNature.getSelectedItemPosition() > 0 &
                        spinnerCause.getSelectedItemPosition() > 0) {
                    JobCompletion jobCompletionRec = new JobCompletion();
                    jobCompletionRec.JOB_NO = breakdown.get_Job_No();
                    jobCompletionRec.job_completed_datetime = GetSelectedDateTime(dialog);
                    jobCompletionRec.type_failure = GetId(FailureTypeList, spinnerType.getSelectedItem().toString());
                    jobCompletionRec.detail_reason_code = GetId(FailureNatureList, spinnerNature.getSelectedItem().toString());
                    jobCompletionRec.cause = GetId(FailureCauseList, spinnerCause.getSelectedItem().toString());
                    breakdown.set_SUB(sin.getText().toString());
                    UpdateCompletedJob(fragment, jobCompletionRec, breakdown);
                    JobListFragment.CreateListView(fragment);
                    Globals.AverageTime = Globals.dbHandler.getAttendedTime();
                    JobMaterialDialog(fragment, breakdown, position);
                    dialog.dismiss();
                } else {
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Please provide feedback information..", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*Button btnMaterial = (Button) dialog.findViewById(R.id.btnMaterial);
        btnMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerType.getSelectedItemPosition() > 0 &
                        spinnerNature.getSelectedItemPosition() > 0 &
                        spinnerCause.getSelectedItemPosition() > 0) {
                    JobCompletion jobCompletionRec = new JobCompletion();
                    jobCompletionRec.JOB_NO = breakdown.get_Job_No();
                    jobCompletionRec.job_completed_datetime = GetSelectedDateTime(dialog);
                    jobCompletionRec.type_failure = GetId(FailureTypeList, spinnerType.getSelectedItem().toString());
                    jobCompletionRec.detail_reason_code = GetId(FailureNatureList, spinnerNature.getSelectedItem().toString());
                    jobCompletionRec.cause = GetId(FailureCauseList, spinnerCause.getSelectedItem().toString());

                    UpdateCompletedJob(fragment, jobCompletionRec, breakdown);
                    JobListFragment.CreateListView(fragment);
                    Globals.AverageTime = Globals.dbHandler.getAttendedTime();
                    JobMaterialDialog(fragment,breakdown,position);
                    dialog.dismiss();
                } else {
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Please provide feedback information..", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the job_dialog dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RestoreCard(fragment, breakdown, position);
                dialog.dismiss();
            }
        });

        dialog.show();


        return dialog;
    }

    private static void JobRejectDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
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
                        Breakdown.JOB_REJECT, GetSelectedDateTime(dialog), etComment.getText().toString());
                UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_REJECT);
                JobListFragment.CreateListView(fragment);
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreCard(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private static void JobMaterialDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
        MaterialViewsAdapter.selectedMaterials = new ArrayList<>();
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_material);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);

        List<MaterialObject> MaterialList = new ArrayList<>();
        List<MaterialObject> _MaterialList = Globals.dbHandler.getMaterials(breakdown.get_Job_No());

        for (int i = 0; i < Store.Materials.length; i++) {
            MaterialObject _obj = Search(_MaterialList, Store.Materials[i][1]);
            int n = 0;
            if (_obj != null) {
                n = _obj.getQuantity();
            }
            MaterialObject obj = new MaterialObject(false, Store.Materials[i][1], Store.Materials[i][2], n);
            MaterialList.add(obj);
        }

        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        final MaterialViewsAdapter adapter = new MaterialViewsAdapter(fragment.getActivity(), R.layout.material_row, MaterialList);
        listView.setAdapter(adapter);


        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.get_Name() != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.get_Name().trim() + "\n" + breakdown.get_ADDRESS().trim());
        else
            txtView.setText(breakdown.get_Job_No());

        ImageButton btnAddMaterials = (ImageButton) dialog.findViewById(R.id.btnAddMaterials);
        // if button is clicked, close the job_dialog dialog
        btnAddMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for (int i=0;i<MaterialViewsAdapter.selectedMaterials.size();i++){
                //    MaterialObject item = MaterialViewsAdapter.selectedMaterials.get(i);
                //}
                Globals.dbHandler.addMaterials(breakdown.get_Job_No(), MaterialViewsAdapter.selectedMaterials);
                try{
                    SyncService.PostMaterials(fragment.getActivity().getApplicationContext());
                }catch(Exception e){}
                dialog.dismiss();
            }
        });
        ImageButton btnCancel = (ImageButton) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the job_dialog dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RestoreCard(fragment,breakdown, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static MaterialObject Search(List<MaterialObject> list, String materialCode) {
        if (list == null) return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCode().contains(materialCode)) {
                return list.get(i);
            }
        }
        return null;
    }

    private static void RestoreCard(Fragment fragment, Breakdown breakdown, int position) {
        if (fragment instanceof JobListFragment) {
            JobListFragment jobListFragment = (JobListFragment) fragment;
            jobListFragment.RestoreItem(breakdown, position);
        }
    }


    private static void UpdateJobStatusChange(Fragment fragment, JobChangeStatus jobchangestatus, Breakdown breakdown, int iStatus) {
        Globals.dbHandler.addJobStatusChangeRec(jobchangestatus);
        Globals.dbHandler.UpdateBreakdownStatus(breakdown, iStatus);
        SyncService.PostBreakdownStatusChange(fragment.getActivity().getApplicationContext());
        if (fragment instanceof JobListFragment) {
            JobListFragment JobFrag = (JobListFragment) fragment;
            //JobFrag.CreateListView(fragment);
            JobFrag.mAdapter.notifyDataSetChanged();
        } else if (fragment instanceof GmapFragment) {
            GmapFragment GmapFrag = (GmapFragment) fragment;
            GmapFrag.RefreshJobsFromDB();
        }
    }

    private static void UpdateCompletedJob(Fragment fragment, JobCompletion jobcompletion, Breakdown breakdown) {
        Globals.dbHandler.addJobCompletionRec(jobcompletion);
        Globals.dbHandler.UpdateBreakdownStatus(breakdown, Breakdown.JOB_COMPLETED);
        SyncService.PostBreakdownCompletion(fragment.getActivity().getApplicationContext());

        if (fragment instanceof JobListFragment) {
            JobListFragment JobFrag = (JobListFragment) fragment;
            //JobFrag.CreateListView(fragment);
            JobFrag.mAdapter.notifyDataSetChanged();
        } else if (fragment instanceof GmapFragment) {
            GmapFragment GmapFrag = (GmapFragment) fragment;
            GmapFrag.RefreshJobsFromDB();
        }
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
        String type = GetId(FailureTypeList, spinnerType.getSelectedItem().toString());

        if (type.equals("0")) {
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);
        } else {
            spinnerNature.setEnabled(true);
            spinnerCause.setEnabled(true);
            spinnerNature.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, R.id.textView, GetFilteredColumn(FailureNatureList, type)));
        }
        spinnerNature.setSelection(0);
        spinnerCause.setSelection(0);
    }

    private static void SetCauseSpinners(Fragment fragment, Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause) {
        // int type = spinnerType.getSelectedItemPosition();
        //int nature = spinnerNature.getSelectedItemPosition();
        String nature = GetId(FailureNatureList, spinnerNature.getSelectedItem().toString());

        if (nature.equals("0")) {
            spinnerCause.setEnabled(false);
        } else {
            spinnerCause.setEnabled(true);
            spinnerCause.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                    R.layout.spinner_row, R.id.textView, GetFilteredColumn(FailureCauseList, nature)));
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
            out[row] = array[row][4];//Sinhala items
        }
        return out;
    }

    private static String[] GetFilteredColumn(String[][] Array, String ParentId) {
        String out[] = new String[Array.length];
        int i = 0;
        for (String[] array : Array) {
            if (array[2].equals(ParentId)) {
                out[i] = array[4];
                i++;
            }
        }
        return Arrays.copyOfRange(out, 0, i);
    }

    private static String GetId(String[][] Array, String name) {
        for (String[] array : Array) {
            if (array[4].equals(name)) {
                Log.e("FOUND", name + "" + array[1]);
                return array[1];
            }
        }
        Log.e("NOT FOUND", "" + name + " in " + Array.toString());
        return "0";
    }

    private static int GetSubIndex(String[][] Array, int parentId, int id) {
        int i = 0;
        for (String[] array : Array) {
            if (array[2].equals(String.valueOf(parentId))) {
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

    private static String GetName(String[][] Array, String id) {
        for (String[] array : Array) {
            if (array[1].equals(id)) {
                Log.e("FOUND", id + "" + array[2]);
                return array[4];
            }
        }
        return "0";
    }


}
