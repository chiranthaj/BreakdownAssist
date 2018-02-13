package lk.steps.breakdownassistpluss.BreakdownDialogs;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinder;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassistpluss.MainActivity;
import lk.steps.breakdownassistpluss.Models.FailureObject;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Strings;
import mehdi.sakout.fancybuttons.FancyButton;

public class DetailsDialog extends AppCompatActivity {
    private static String[][] FailureTypeList;
    private static String[][] FailureCauseList;
    private static String[][] FailureNatureList;

    private static ArrayList<FailureObject> FailureTypeList2 = new  ArrayList<FailureObject>();
    private static ArrayList<FailureObject> FailureCauseList2 = new  ArrayList<FailureObject>();
    private static ArrayList<FailureObject> FailureNatureList2 = new  ArrayList<FailureObject>();

    Context mContext;
    Breakdown breakdown;
    int position;
    //Marker mMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_dialog);

        Bundle bundle = getIntent().getExtras();
        mContext=this;

        Log.d("TEST","onCreate1");
        if (bundle != null) {
            String json = bundle.getString("breakdown");
            breakdown = new Gson().fromJson(json, new TypeToken<Breakdown>() {}.getType());
            position = bundle.getInt("position");

            LoadFailureTypeList();
            LoadFailureCauseList("1");
            LoadFailureNatureList("1");

            TextView txtJobno = (TextView) findViewById(R.id.jobno);
            txtJobno.setText(breakdown.get_Job_No().trim());
            TextView txtRecTime = (TextView) findViewById(R.id.received_date_time);
            txtRecTime.setText("Received time : " + Globals.parseDate(breakdown.get_Received_Time().trim()));
            TextView txtAcctNum = (TextView) findViewById(R.id.acctnum);

            if (breakdown.get_Acct_Num() != null)
                txtAcctNum.setText("Acc. No. : " + breakdown.get_Acct_Num().trim());
            else txtAcctNum.setText("Acc. No. : Not available");
            TextView txtName = (TextView) findViewById(R.id.name);
            if (breakdown.NAME != null)
                txtName.setText(breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());

            TableRow rowLandPh = (TableRow) findViewById(R.id.rowLandPh);

            ImageButton btnLandPhNoCall = (ImageButton) findViewById(R.id.btnLandPhNoCall);
            btnLandPhNoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + breakdown.LandPhNo));
                    startActivity(intent);
                }
            });
            TextView txtLandPhNo = (TextView) findViewById(R.id.txtLandPhNo);
            if (breakdown.LandPhNo != null) {
                txtLandPhNo.setText(breakdown.LandPhNo);
                rowLandPh.setVisibility(View.VISIBLE);
            } else {
                rowLandPh.setVisibility(View.GONE);
            }

            TableRow rowMobilePh = (TableRow) findViewById(R.id.rowMobilePh);

            ImageButton btnMobilePhNoCall = (ImageButton) findViewById(R.id.btnMobilePhNoCall);
            btnMobilePhNoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + breakdown.MobilePhNo));
                    startActivity(intent);
                }
            });
            TextView txtMobilePhNo = (TextView) findViewById(R.id.txtMobilePhNo);
            if (breakdown.MobilePhNo != null) {
                txtMobilePhNo.setText(breakdown.MobilePhNo);
                rowMobilePh.setVisibility(View.VISIBLE);
            } else {
                rowMobilePh.setVisibility(View.GONE);
            }

            TextView txtNote = (TextView) findViewById(R.id.txtNote);
            if (TextUtils.isEmpty(breakdown.get_Note())) {
                txtNote.setVisibility(View.GONE);
            } else {
                txtNote.setVisibility(View.VISIBLE);
                txtNote.setText(breakdown.get_Note());
            }


            TextView txtFullDescription = (TextView) findViewById(R.id.fulldescription);
            txtFullDescription.setText(Strings.GetDescription(breakdown.get_Full_Description()));

            FancyButton btnCancel = (FancyButton) findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // RestoreCard(fragment, breakdown, position);
                    finish();
                }
            });

            FancyButton btnNavigate = (FancyButton) findViewById(R.id.btnNavigate);
            if (breakdown.getLocation() == null) btnNavigate.setEnabled(false);
            btnNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Press and Hold for Google Navigation !!",
                            Toast.LENGTH_SHORT).show();
                    if (Globals.LastLocation != null) {
                        Log.d("TEST","btnNavigate1");

                       /* Intent intent = new Intent("lk.steps.breakdownassistpluss.GmapActivityBroadcastReceiver");
                        intent.putExtra("map_direction_req", "map_direction_req");
                        intent.putExtra("lat",breakdown.getLatitude());
                       intent.putExtra("lon",breakdown.getLongitude());
                        mContext.sendBroadcast(intent);*/
                        Uri uri = Uri.parse("https://www.google.com/maps/dir/Current+Location/"+ breakdown.getLatitude() + "," + breakdown.getLongitude() );
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        i.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        startActivity(i);

                        Log.d("TEST","btnNavigate2");
                       //getDirections(getApplicationContext(), Globals.LastLocation, breakdown.getLocation());
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Current location is not available, Please try again", Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            });
            btnNavigate.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getApplicationContext(), "Opening Google Map Navigation...",
                            Toast.LENGTH_SHORT).show();
                    if (breakdown.getLocation() != null) {
                       Uri uri = Uri.parse("https://www.google.com/maps/dir/Current+Location/"+ breakdown.getLatitude() + "," + breakdown.getLongitude() );
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    i.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity");
                    startActivity(i);
                    }
                    finish();
                    return true;
                }

            });


            FancyButton btnVisted = (FancyButton) findViewById(R.id.btnVisted);
            btnVisted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    Intent i = new Intent(getApplication(), VisitedDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    startActivity(i);


                    // JobVisitedDialog(fragment, breakdown, position);
                    finish();
                }
            });
            FancyButton btnAttending = (FancyButton) findViewById(R.id.btnAttending);
            btnAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    if (breakdown.get_Status() == Breakdown.JOB_ATTENDING) {
                        Intent i = new Intent(getApplication(), NotAttendingDialog.class);
                        i.putExtra("breakdown", json);
                        i.putExtra("position", position);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getApplication(), AttendingDialog.class);
                        i.putExtra("breakdown", json);
                        i.putExtra("position", position);
                        startActivity(i);
                    }


                    // JobAttendingDialog(fragment, breakdown, position);
                    finish();
                }
            });


            FancyButton btnDone = (FancyButton) findViewById(R.id.btnDone);
            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                Intent i = new Intent(fragment.getActivity(), TempCompletedclass);
                i.putExtra("breakdown", json);
                i.putExtra("position", position);
                fragment.startActivity(i);*/

                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    if (breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED) {
                        Intent i = new Intent(getApplication(), MaterialDialog.class);
                        i.putExtra("breakdown", json);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getApplication(), TempCompletedDialog.class);
                        i.putExtra("breakdown", json);
                        i.putExtra("position", position);
                        startActivity(i);
                    }


                    // JobDoneDialog(fragment, breakdown, position);
                    finish();
                }
            });
            FancyButton btnCompleted = (FancyButton) findViewById(R.id.btnCompleted);
            btnCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    Intent i = new Intent(getApplication(), CompletedDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    startActivity(i);
                    finish();
                }
            });
            FancyButton btnMaterials = (FancyButton) findViewById(R.id.btnMaterials);
            btnMaterials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    Intent i = new Intent(getApplication(), MaterialDialog.class);
                    i.putExtra("breakdown", json);
                    startActivity(i);
                    finish();
                }
            });
            btnMaterials.setEnabled(breakdown.get_Status() == Breakdown.JOB_COMPLETED |
                    breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED);

            FancyButton btnReject = (FancyButton) findViewById(R.id.btnReject);
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    Intent i = new Intent(getApplication(), RejectDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    startActivity(i);
                    //JobRejectDialog(fragment, breakdown, position);
                    finish();
                }
            });

            FancyButton btnReturn = (FancyButton) findViewById(R.id.btnReturn);
            btnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {
                    }.getType());
                    Intent i = new Intent(getApplication(), ReturnDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    startActivity(i);

                    // JobReturnDialog(fragment, breakdown, position);
                    finish();
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
        }
    
        this.setFinishOnTouchOutside(false);
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

    private static void getDirections(final Fragment fragment, LatLng origin, LatLng destination) {
        try {
            String sOrigin = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
            String sDestination = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);
            new DirectionFinder((DirectionFinderListener) fragment, sOrigin, sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
