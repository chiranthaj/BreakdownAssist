package lk.steps.breakdownassistpluss.BreakdownDialogs;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.BreakdownDialogs.AttendingDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.CompletedDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.RejectDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.ReturnDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.TempCompletedDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.VisitedDialog;
import lk.steps.breakdownassistpluss.Fragments.GmapFragment;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinder;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import lk.steps.breakdownassistpluss.MaterialList.MaterialViewsAdapter;
import lk.steps.breakdownassistpluss.MaterialList.Store;
import lk.steps.breakdownassistpluss.Models.FailureObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.Models.JobCompletion;
import lk.steps.breakdownassistpluss.Models.Team;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Strings;
import lk.steps.breakdownassistpluss.Sync.SyncService;


/**
 * Created by JagathPrasanga on 5/22/2017.
 */

public class DetailsDialog {
    private static String[][] FailureTypeList;
    private static String[][] FailureCauseList;
    private static String[][] FailureNatureList;

    private static ArrayList<FailureObject> FailureTypeList2 = new  ArrayList<FailureObject>();
    private static ArrayList<FailureObject> FailureCauseList2 = new  ArrayList<FailureObject>();
    private static ArrayList<FailureObject> FailureNatureList2 = new  ArrayList<FailureObject>();


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
        dialog.setCancelable(false);

        TextView txtJobno = (TextView) dialog.findViewById(R.id.jobno);
        txtJobno.setText(breakdown.get_Job_No().trim());
        TextView txtRecTime = (TextView) dialog.findViewById(R.id.received_date_time);
        txtRecTime.setText("Received time : " + Globals.parseDate(breakdown.get_Received_Time().trim()));
        TextView txtAcctNum = (TextView) dialog.findViewById(R.id.acctnum);

        if (breakdown.get_Acct_Num() != null)
            txtAcctNum.setText("Acc. No. : " + breakdown.get_Acct_Num().trim());
        else txtAcctNum.setText("Acc. No. : Not available");
        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        if (breakdown.NAME != null)
            txtName.setText(breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());

        TableRow rowLandPh = (TableRow)dialog.findViewById(R.id.rowLandPh);

        ImageButton btnLandPhNoCall = (ImageButton) dialog.findViewById(R.id.btnLandPhNoCall);
        btnLandPhNoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + breakdown.LandPhNo));
                fragment.getActivity().startActivity(intent);
            }
        });
        TextView txtLandPhNo = (TextView) dialog.findViewById(R.id.txtLandPhNo);
        if (breakdown.LandPhNo != null) {
            txtLandPhNo.setText(breakdown.LandPhNo);
            rowLandPh.setVisibility(View.VISIBLE);
        } else {
            rowLandPh.setVisibility(View.GONE);
        }

        TableRow rowMobilePh = (TableRow)dialog.findViewById(R.id.rowMobilePh);

        ImageButton btnMobilePhNoCall = (ImageButton) dialog.findViewById(R.id.btnMobilePhNoCall);
        btnMobilePhNoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + breakdown.MobilePhNo));
                fragment.getActivity().startActivity(intent);
            }
        });
        TextView txtMobilePhNo = (TextView) dialog.findViewById(R.id.txtMobilePhNo);
        if (breakdown.MobilePhNo != null) {
            txtMobilePhNo.setText(breakdown.MobilePhNo);
            rowMobilePh.setVisibility(View.VISIBLE);
        } else {
            rowMobilePh.setVisibility(View.GONE);
        }

        TextView txtNote = (TextView) dialog.findViewById(R.id.txtNote);
        if(TextUtils.isEmpty(breakdown.get_Note())){
            txtNote.setVisibility(View.GONE);
        }else{
            txtNote.setVisibility(View.VISIBLE);
            txtNote.setText(breakdown.get_Note());
        }


        TextView txtFullDescription = (TextView) dialog.findViewById(R.id.fulldescription);
        txtFullDescription.setText(Strings.GetDescription(breakdown.get_Full_Description()));

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


                String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                Intent i = new Intent(fragment.getActivity(), VisitedDialog.class);
                i.putExtra("breakdown", json);
                i.putExtra("position", position);
                fragment.startActivity(i);


               // JobVisitedDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        Button btnAttending = (Button) dialog.findViewById(R.id.btnAttending);
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                if (breakdown.get_Status() == Breakdown.JOB_ATTENDING) {
                    Intent i = new Intent(fragment.getActivity(), NotAttendingDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    fragment.startActivity(i);
                }else{
                    Intent i = new Intent(fragment.getActivity(), AttendingDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    fragment.startActivity(i);
                }


               // JobAttendingDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });


        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                Intent i = new Intent(fragment.getActivity(), TempCompletedDialog.class);
                i.putExtra("breakdown", json);
                i.putExtra("position", position);
                fragment.startActivity(i);*/

                String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                if (breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED) {
                    Intent i = new Intent(fragment.getActivity(), MaterialDialog.class);
                    i.putExtra("breakdown", json);
                    fragment.startActivity(i);
                }else{
                    Intent i = new Intent(fragment.getActivity(), TempCompletedDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    fragment.startActivity(i);
                }


               // JobDoneDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });
        Button btnCompleted = (Button) dialog.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
                    Intent i = new Intent(fragment.getActivity(), MaterialDialog.class);
                    i.putExtra("breakdown", json);
                    fragment.startActivity(i);
                }else{
                    Intent i = new Intent(fragment.getActivity(), CompletedDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    fragment.startActivity(i);
                }


                //JobCompleteDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });

        Button btnReject = (Button) dialog.findViewById(R.id.btnReject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                Intent i = new Intent(fragment.getActivity(), RejectDialog.class);
                i.putExtra("breakdown", json);
                i.putExtra("position", position);
                fragment.startActivity(i);
                //JobRejectDialog(fragment, breakdown, position);
                dialog.dismiss();
            }
        });

        Button btnReturn = (Button) dialog.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                Intent i = new Intent(fragment.getActivity(), ReturnDialog.class);
                i.putExtra("breakdown", json);
                i.putExtra("position", position);
                fragment.startActivity(i);

               // JobReturnDialog(fragment, breakdown, position);
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
            btnCompleted.setEnabled(true);
            btnReturn.setEnabled(false);
            //btnCompleted.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED) {
            btnAttending.setEnabled(false);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(true);
            btnReject.setEnabled(false);
            btnCompleted.setEnabled(true);
            btnReturn.setEnabled(true);
            //btnDone.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_VISITED) {
            btnAttending.setEnabled(true);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(true);
            btnReject.setEnabled(true);
            btnCompleted.setEnabled(true);
            btnReturn.setEnabled(true);
            btnVisted.setTextColor(Color.RED);
        } else if (breakdown.get_Status() == Breakdown.JOB_ATTENDING) {
            btnAttending.setEnabled(true);
            btnVisted.setEnabled(true);
            btnDone.setEnabled(true);
            btnReject.setEnabled(true);
            btnCompleted.setEnabled(true);
            btnReturn.setEnabled(true);
           // btnAttending.setTextColor(Color.RED);
            btnAttending.setText("Not Attending");
        } else if (breakdown.get_Status() == Breakdown.JOB_REJECT) {
            btnAttending.setEnabled(false);
            btnVisted.setEnabled(false);
            btnDone.setEnabled(false);
            btnReject.setEnabled(false);
            btnCompleted.setEnabled(false);
            btnReturn.setEnabled(false);
            btnReject.setTextColor(Color.RED);
        }

        dialog.show();
        return dialog;
    }


    /*private static void JobVisitedDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null ) return;
        if(!fragment.isAdded())return;
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_visited_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.NAME != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
        else
            txtView.setText(breakdown.get_Job_No());
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);

        ArrayList<FailureObject> list = new ArrayList<FailureObject>();
        for (String[] array : Strings.DoneComments) {
            FailureObject obj = new FailureObject();
            obj.AreaCode=array[0];
            obj.Id=array[1];
            obj.ParentId=array[2];
            obj.English=array[3];
            obj.Sinhala=array[4];
            list.add(obj);
        }
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
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
                    textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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
                if(spinner.getSelectedItemPosition()==0){
                    Toast.makeText(fragment.getActivity(), "Please select a reason", Toast.LENGTH_SHORT).show();
                }else {
                    FailureObject obj = (FailureObject) spinner.getSelectedItem();
                    JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                            Breakdown.JOB_VISITED, GetSelectedDateTime(dialog), obj.English);
                    UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_VISITED);
                    JobListFragment.CreateListView(fragment);
                    dialog.dismiss();
                }
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
    }*/

    /*private static void JobAttendingDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
        if(!fragment.isAdded())return;
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_attending_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.NAME != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
        else
            txtView.setText(breakdown.get_Job_No());
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, Strings.AttendingComments));
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

    }*/

   /* private static void JobDoneDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
        if(!fragment.isAdded())return;
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_temp_complete_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.NAME != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
        else
            txtView.setText(breakdown.get_Job_No());

        ArrayList<FailureObject> list = new ArrayList<FailureObject>();
        for (String[] array : Strings.DoneComments) {
            FailureObject obj = new FailureObject();
            obj.AreaCode=array[0];
            obj.Id=array[1];
            obj.ParentId=array[2];
            obj.English=array[3];
            obj.Sinhala=array[4];
            list.add(obj);
        }
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
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
                    textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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
                if(spinner.getSelectedItemPosition()==0){
                    Toast.makeText(fragment.getActivity(), "Please select a reason", Toast.LENGTH_SHORT).show();
                }else {
                    FailureObject obj = (FailureObject) spinner.getSelectedItem();
                    JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                            Breakdown.JOB_TEMPORARY_COMPLETED, GetSelectedDateTime(dialog), obj.English);
                    UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_TEMPORARY_COMPLETED);
                    JobListFragment.CreateListView(fragment);
                    JobMaterialDialog(fragment, breakdown, position);
                    dialog.dismiss();
                }
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
    }*/

    /*public static Dialog JobCompleteDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return null;
        if(!fragment.isAdded())return null;

        LoadFailureTypeList(fragment);
        LoadFailureCauseList(fragment,"1");
        LoadFailureNatureList(fragment,"1");


        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_complete_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.NAME != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
        else
            txtView.setText(breakdown.get_Job_No());

        final EditText sin = (EditText) dialog.findViewById(R.id.etSin);
        sin.setText(breakdown.get_SUB());

        //Strings Type Spinner
        final Spinner spinnerType = (Spinner) dialog.findViewById(R.id.spinner_failure_type);
        spinnerType.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, FailureTypeList2));

        final Spinner spinnerNature = (Spinner) dialog.findViewById(R.id.spinner_failure_nature);
        spinnerNature.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, FailureNatureList2));


        final Spinner spinnerCause = (Spinner) dialog.findViewById(R.id.spinner_failure_cause);
        spinnerCause.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
                R.layout.spinner_row, R.id.textView, FailureCauseList2));

        if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
            JobCompletion obj = Globals.dbHandler.getJobCompletionRec(breakdown.get_Job_No());
            int type = Integer.parseInt(obj.type_failure);//2

            spinnerType.setSelection(type);

            LoadFailureNatureList(fragment,obj.type_failure);
            spinnerNature.setSelection(GetSubIndex(FailureNatureList2,obj.detail_reason_code));


            LoadFailureCauseList(fragment,obj.detail_reason_code);
            spinnerCause.setSelection(GetSubIndex(FailureCauseList2, obj.cause));

            spinnerType.setEnabled(false);
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);

        } else {
            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                        FailureObject obj = (FailureObject) parent.getItemAtPosition(position);
                        SetNatureSpinners(fragment, spinnerType, spinnerNature, spinnerCause, obj);

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
                    FailureObject obj = (FailureObject) parent.getItemAtPosition(position);
                        SetCauseSpinners(fragment, spinnerType, spinnerNature, spinnerCause,obj);

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
                        if(textView != null){
                            if (position > 0)
                                textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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


        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner.setAdapter(new ArrayAdapter<String>(fragment.getActivity(),
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
        ImageButton btnCompleted = (ImageButton) dialog.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
                    JobMaterialDialog(fragment, breakdown, position);
                    dialog.dismiss();
                }else if(sin.getText().toString().length()<4 | sin.getText().toString().equals("")){
               // }else if(sin.getText().toString().length()<4 & !sin.getText().toString().equals("")){
                    Toast.makeText(fragment.getActivity().getApplicationContext(),
                            "Erroneous SIN", Toast.LENGTH_LONG).show();
                } else if (spinnerType.getSelectedItemPosition() > 0 &
                        spinnerNature.getSelectedItemPosition() > 0 &
                        spinnerCause.getSelectedItemPosition() > 0) {
                    JobCompletion jobCompletionRec = new JobCompletion();
                    jobCompletionRec.JOB_NO = breakdown.get_Job_No();
                    jobCompletionRec.job_completed_datetime = GetSelectedDateTime(dialog);
                    FailureObject typeObj = (FailureObject) spinnerType.getSelectedItem();
                    FailureObject natureObj = (FailureObject) spinnerNature.getSelectedItem();
                    FailureObject causeObj = (FailureObject) spinnerCause.getSelectedItem();
                    jobCompletionRec.type_failure = typeObj.Id;
                    jobCompletionRec.detail_reason_code = natureObj.Id;
                    jobCompletionRec.cause = causeObj.Id;
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
    }*/

    /*private static void JobRejectDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
        if(!fragment.isAdded())return;
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_reject_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        if (breakdown.NAME != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
        else
            txtView.setText(breakdown.get_Job_No());

        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);

        ArrayList<FailureObject> list = new ArrayList<FailureObject>();
        for (String[] array : Strings.RejectComments) {
            FailureObject obj = new FailureObject();
            obj.AreaCode=array[0];
            obj.Id=array[1];
            obj.ParentId=array[2];
            obj.English=array[3];
            obj.Sinhala=array[4];
            list.add(obj);
        }

        //Spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);

        spinner.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
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
                    textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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
                if(spinner.getSelectedItemPosition()==0){
                    Toast.makeText(fragment.getActivity(), "Please select a reason", Toast.LENGTH_SHORT).show();
                }else{
                    FailureObject obj = (FailureObject) spinner.getSelectedItem();
                    JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                            Breakdown.JOB_REJECT, GetSelectedDateTime(dialog), obj.English);
                    UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_REJECT);
                    JobListFragment.CreateListView(fragment);
                    dialog.dismiss();
                }
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
    }*/

   /* private static List<Team> GetTeamsList(Fragment fragment){

        final List<Team> teams = new ArrayList<Team>();

        String json = Common.ReadStringPreferences(fragment.getActivity(),"teams_in_area",null);
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
    }*/


    /*private static void JobReturnDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
        if(!fragment.isAdded())return;
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_return_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
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

        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        final Spinner teamSpinner = (Spinner) dialog.findViewById(R.id.spinner2);

        final List<Team> teams = GetTeamsList(fragment);

        teamSpinner.setAdapter(new ArrayAdapter<Team>(fragment.getActivity(),
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
                    textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //Spinner
        final Spinner reasonSpinner = (Spinner) dialog.findViewById(R.id.spinner1);
        reasonSpinner.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
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
                    textView.setTextColor(fragment.getResources().getColor(R.color.darkGreen));
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
                if(teamSpinner.getSelectedItemPosition()==0){
                    Toast.makeText(fragment.getActivity(), "Please select a team to forward", Toast.LENGTH_SHORT).show();
                }else if(reasonSpinner.getSelectedItemPosition()==0){
                    Toast.makeText(fragment.getActivity(), "Please select a reason", Toast.LENGTH_SHORT).show();
                }else {
                    String teamId = Globals.mToken.team_id;
                    if(teamSpinner.getSelectedItemPosition()>1){
                        teamId = teams.get(teamSpinner.getSelectedItemPosition()).teamId;
                        FailureObject obj = (FailureObject) reasonSpinner.getSelectedItem();
                        JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                                Breakdown.JOB_FORWARDED, GetSelectedDateTime(dialog), obj.English);
                        breakdown.set_TeamId(teamId);

                        UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_FORWARDED);
                    }else{
                        FailureObject obj = (FailureObject) reasonSpinner.getSelectedItem();
                        JobChangeStatus jobStatusChangeRec = new JobChangeStatus(breakdown.get_Job_No(),
                                Breakdown.JOB_RETURNED, GetSelectedDateTime(dialog), obj.English);
                        breakdown.set_TeamId(teamId);

                        UpdateJobStatusChange(fragment, jobStatusChangeRec, breakdown, Breakdown.JOB_RETURNED);
                    }

                    JobListFragment.CreateListView(fragment);
                    dialog.dismiss();
                }
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
    }*/


   /* private static void JobMaterialDialog(final Fragment fragment, final Breakdown breakdown, final int position) {
        if (fragment == null) return;
        if(!fragment.isAdded())return;
        MaterialViewsAdapter.selectedMaterials = new ArrayList<>();
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_material);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

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
        if (breakdown.NAME != null)
            txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
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
*/
    private static void RestoreCard(Fragment fragment, Breakdown breakdown, int position) {
        if (fragment instanceof JobListFragment) {
            JobListFragment jobListFragment = (JobListFragment) fragment;
            jobListFragment.RestoreItem(breakdown, position);
        }
    }


   /* private static void UpdateJobStatusChange(Fragment fragment, JobChangeStatus jobchangestatus, Breakdown breakdown, int iStatus) {
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
    }*/

   /* private static void UpdateCompletedJob(Fragment fragment, JobCompletion jobcompletion, Breakdown breakdown) {
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
    }*/

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


    private static void SetNatureSpinners(Fragment fragment, Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause, FailureObject obj) {

        String type =obj.Id;
        Log.e("TEST","Nature="+obj.Id+","+obj.Sinhala);
        if (type.equals("0")) {
            spinnerNature.setEnabled(false);
            spinnerCause.setEnabled(false);
        } else {
            LoadFailureNatureList(fragment, type);
            spinnerNature.setEnabled(true);
            spinnerCause.setEnabled(true);
            spinnerNature.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
                    R.layout.spinner_row, R.id.textView, FailureNatureList2));
            Log.e("TEST","FailureNatureList2 size="+FailureNatureList2.size());
        }
        spinnerNature.setSelection(0);
        spinnerCause.setSelection(0);
    }

    private static void SetCauseSpinners(Fragment fragment, Spinner spinnerType, Spinner spinnerNature, Spinner spinnerCause, FailureObject obj) {
        String nature =obj.Id;
        Log.e("TEST","Cause="+obj.Sinhala);
        if (nature.equals("0")) {
            spinnerCause.setEnabled(false);
        } else {
            spinnerCause.setEnabled(true);
            LoadFailureCauseList(fragment, nature);
            spinnerCause.setAdapter(new ArrayAdapter<FailureObject>(fragment.getActivity(),
                    R.layout.spinner_row, R.id.textView, FailureCauseList2));

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

    private static void LoadFailureTypeList(Fragment fragment){
        if(fragment==null) return;
        FailureTypeList = Strings.GetFailureTypeList(fragment.getActivity().getApplicationContext());

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
    private static void LoadFailureCauseList(Fragment fragment,String parentId){
        if(fragment==null) return;
        FailureCauseList = Strings.GetFailureCauseList(fragment.getActivity().getApplicationContext());

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
    private static void LoadFailureNatureList(Fragment fragment,String parentId){
        if(fragment==null) return;
        FailureNatureList = Strings.GetFailureNatureList(fragment.getActivity().getApplicationContext());
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

}
