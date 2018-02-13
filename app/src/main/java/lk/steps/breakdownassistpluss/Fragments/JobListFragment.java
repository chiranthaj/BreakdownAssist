package lk.steps.breakdownassistpluss.Fragments;


import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.BreakdownDialogs.CompletedDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.DetailsDialog;
import lk.steps.breakdownassistpluss.BreakdownDialogs.MaterialDialog;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.MainActivity;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.RecyclerViewCards.SwipeableRecyclerViewTouchListener;
import lk.steps.breakdownassistpluss.RecyclerViewCards.JobsRecyclerAdapter;
import lk.steps.breakdownassistpluss.Sync.SyncService;


public class JobListFragment extends Fragment {

    private static View mView;
    public static int iJobs_to_Display = Breakdown.JOB_NOT_ATTENDED;
    public static RecyclerView mRecyclerView;
    public static LatLng currentLocation;
    public static RecyclerView.Adapter mAdapter;
    public static ArrayList<Breakdown> BreakdownList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            iJobs_to_Display = bundle.getInt("JOB_STATUS", -1);
            Log.d("JOB_STATUS", "JOB_STATUS=" + iJobs_to_Display);
            currentLocation = getLastLocation();

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.job_listview, container, false);
        CreateListView(JobListFragment.this);

        return mView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.jobs_to_display_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.menu_jobs_unatended);
        if(iJobs_to_Display == Breakdown.JOB_TEMPORARY_COMPLETED)
            item = menu.findItem(R.id.menu_jobs_temporary);
        else if(iJobs_to_Display == Breakdown.JOB_WITHDRAWN)
            item = menu.findItem(R.id.menu_jobs_rejected);
        else if(iJobs_to_Display == Breakdown.JOB_REJECT)
            item = menu.findItem(R.id.menu_jobs_rejected);
        else if(iJobs_to_Display == Breakdown.JOB_RETURNED)
            item = menu.findItem(R.id.menu_jobs_returned);
        item.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_jobs_all) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_STATUS_ANY;
            CreateListView(JobListFragment.this);
            return true;
        } else if (id == R.id.menu_jobs_completed) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_COMPLETED;
            CreateListView(JobListFragment.this);
            return true;
        } else if (id == R.id.menu_jobs_unatended) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_NOT_ATTENDED;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_delivered) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_DELIVERED;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_acknowledged) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_ACKNOWLEDGED;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_visited) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_VISITED;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_attending) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_ATTENDING;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_temporary) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_TEMPORARY_COMPLETED;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_rejected) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_REJECT;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_recalled) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_RE_CALLED;
            CreateListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_returned) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_RETURNED;
            CreateListView(JobListFragment.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        CreateListView(JobListFragment.this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("lk.steps.breakdownassistpluss.JobListBroadcastReceiver"));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Add to list view
            CreateListView(JobListFragment.this);
            Log.d("TEST","JobListBroadcastReceiver1");
        }
    };


    public static void RestoreItem(Breakdown breakdown, int position){
        BreakdownList.add(position,breakdown);
        mAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
    }

    private static List<String> SelectedBreakdownIds = new ArrayList<String>();;

    public static void CreateListView(final Fragment fragment) {
        BreakdownList = new ArrayList<Breakdown>(Globals.dbHandler.ReadBreakdowns(iJobs_to_Display, false, true));
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycleview);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(fragment.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {

            @Override
            public void onButton1Click(View view, int position) {
                Toast.makeText(fragment.getActivity(), "Clicked Button1 in " + BreakdownList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
                Toast.makeText(fragment.getActivity(), "Clicked Button2 in " + BreakdownList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCheckBox1Click(View view, int position) {
                CheckBox checkBox = (CheckBox)view;
                if(checkBox.isChecked()){
                    SelectedBreakdownIds.add(BreakdownList.get(position).get_Job_No());
                }else{
                    SelectedBreakdownIds.remove(BreakdownList.get(position).get_Job_No());
                }

                //Toast.makeText(fragment.getActivity(), "Clicked onCheckBox1Click in " + BreakdownList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardViewTap(View view, final int position) {
                if (BreakdownList.get(position).getLocation() == null) {
                    Toast.makeText(fragment.getActivity(), "No customer location data found ", Toast.LENGTH_LONG).show();
                    /*Dialog dialog = DetailsDialog.DialogInfo(fragment, BreakdownList.get(position), null, null,position);
                    if (dialog != null)
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mAdapter.notifyDataSetChanged();
                                //mRecyclerView.ref.invalidate();
                                //CreateListView(fragment);
                            }
                        });*/

                    String json = new Gson().toJson(BreakdownList.get(position), new TypeToken<Breakdown>() {
                    }.getType());
                    Intent i = new Intent(fragment.getActivity(), DetailsDialog.class);
                    i.putExtra("breakdown", json);
                    i.putExtra("position", position);
                    fragment.startActivity(i);

                }/*else if (BreakdownList.get(position).getLatitude().equals("0")) {
                    Toast.makeText(fragment.getActivity(), "No customer location data found ", Toast.LENGTH_LONG).show();
                    Dialog dialog = DetailsDialog.DialogInfo(fragment, BreakdownList.get(position), null, null,position);
                    if (dialog != null)
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mAdapter.notifyDataSetChanged();
                                //mRecyclerView.ref.invalidate();
                                //CreateListView(fragment);
                            }
                        });
                } */
                else {
                    final FragmentManager fm;
                    fm = fragment.getFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(), MainActivity.MAP_FRAGMENT_TAG).commit();
                    Toast.makeText(fragment.getActivity(), BreakdownList.get(position).get_Job_No() + " Locating... ", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Fragment fragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                            if (fragment instanceof GmapFragment) {
                                GmapFragment GmapFrag = (GmapFragment) fragment;
                                GmapFrag.FocusBreakdown(BreakdownList.get(position));
                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void onCardViewLongTap(View view, int position) {
                Log.e("SelectedBreakdownIds","="+SelectedBreakdownIds.size());
                if(SelectedBreakdownIds.size()>1){
                    //BreakdownGroup bg = new BreakdownGroup();
                   // bg.SetBreakdownId(BreakdownList.get(position).get_Job_No());
                   // bg.SetParentBreakdownId("PARENT");
                   // bg.SetParentStatusId(String.valueOf(BreakdownList.get(position).get_Status()));
                    GroupDialog(fragment,BreakdownList.get(position).get_Job_No());
                }else if(Globals.dbHandler.IsParent(BreakdownList.get(position).get_Job_No())){
                    UngroupDialog(fragment,BreakdownList.get(position).get_Job_No());
                }
            }
        };

        // specify an adapter (see also next example)

        mAdapter = new JobsRecyclerAdapter(fragment.getActivity(), BreakdownList, itemTouchListener);

        mRecyclerView.setAdapter(mAdapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    Breakdown breakdown = BreakdownList.get(position);
                                    String json = new Gson().toJson(breakdown, new TypeToken<Breakdown>() {}.getType());
                                    if (breakdown.get_Status() == Breakdown.JOB_COMPLETED ) {
                                        Intent i = new Intent(fragment.getActivity(), MaterialDialog.class);
                                        i.putExtra("breakdown", json);
                                        fragment.startActivity(i);
                                    }else{
                                        Intent i = new Intent(fragment.getActivity(), CompletedDialog.class);
                                        i.putExtra("breakdown", json);
                                        i.putExtra("position", position);
                                        fragment.startActivity(i);
                                    }

                                    BreakdownList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    //Toast.makeText(fragment.getActivity(), BreakdownList.get(position).get_Job_No() + " swiped right", Toast.LENGTH_SHORT).show();
                                    /*Dialog dialog = DetailsDialog.DialogInfo(fragment, BreakdownList.get(position), null, null,position);
                                    if (dialog != null)
                                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                mAdapter.notifyDataSetChanged();
                                                //CreateListView(fragment);
                                            }
                                        });*/
                                    String json = new Gson().toJson(BreakdownList.get(position), new TypeToken<Breakdown>() {
                                    }.getType());
                                    Intent i = new Intent(fragment.getActivity(), DetailsDialog.class);
                                    i.putExtra("breakdown", json);
                                    i.putExtra("position", position);
                                    fragment.startActivity(i);


                                    BreakdownList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
        Log.d("CreateListView","done");
    }

    private static void GroupDialog(final Fragment fragment,final String parentId){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.setContentView(R.layout.dialog_ask_grouping);
        dialog.setCancelable(false);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        TextView tv = (TextView) dialog.findViewById(R.id.textDialog);
        tv.setText("Do you want to group selected breakdowns ?");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectedBreakdownIds.remove(parentId);
                Globals.dbHandler.UpdateChildren(SelectedBreakdownIds,parentId);
                SelectedBreakdownIds.clear();
                SyncService.PostGroups(fragment.getActivity().getApplicationContext());
                CreateListView(fragment);
                dialog.cancel();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    private static void UngroupDialog(final Fragment fragment,final String parentId){
        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.setContentView(R.layout.dialog_ask_grouping);
        dialog.setCancelable(false);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        TextView tv = (TextView) dialog.findViewById(R.id.textDialog);
        tv.setText("Do you want to un-group all ?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* SelectedBreakdownIds.remove(parentId);
                Globals.dbHandler.UpdateChildren(SelectedBreakdownIds,parentId);
                SelectedBreakdownIds.clear();
                SyncService.PostGroups(fragment.getActivity().getApplicationContext());
                CreateListView(fragment);*/
                Globals.dbHandler.UpdateUngroups(parentId,"0");
                SelectedBreakdownIds.clear();
                SyncService.PostGroups(fragment.getActivity().getApplicationContext());
                CreateListView(fragment);
                dialog.cancel();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    /**
     * Interface for the touch events in each item
     */
    public interface OnItemTouchListener {

        /**
         * Callback invoked when the Button1 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onButton1Click(View view, int position);

        /**
         * Callback invoked when the Button2 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onButton2Click(View view, int position);

        /**
         * Callback invoked when the Button2 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onCheckBox1Click(View view, int position);

        /**
         * Callback invoked when the user Taps one of the RecyclerView items
         *
         * @param view     the CardView touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onCardViewTap(View view, int position);

        /**
         * Callback invoked when the user Taps one of the RecyclerView items
         *
         * @param view     the CardView touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onCardViewLongTap(View view, int position);
    }

    public LatLng getLastLocation() {
        return new LatLng(7.2944796,80.5906218);
    }



}