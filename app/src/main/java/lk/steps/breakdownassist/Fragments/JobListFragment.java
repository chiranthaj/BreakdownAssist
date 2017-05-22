package lk.steps.breakdownassist.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.BreakdownView;
import lk.steps.breakdownassist.MainActivity;
import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.R;
import lk.steps.breakdownassist.RecyclerViewCards.SwipeableRecyclerViewTouchListener;
import lk.steps.breakdownassist.RecyclerViewCards.JobsRecyclerAdapter;


public class JobListFragment extends Fragment {

    private static View mView;
    private static DBHandler dbHandler;
    private static int iJobs_to_Display=Breakdown.Status_JOB_NOT_ATTENDED;
    //private int JOB_STATUS;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            iJobs_to_Display = bundle.getInt("JOB_STATUS", -1);
            Log.d("JOB_STATUS","JOB_STATUS="+iJobs_to_Display);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.job_listview,container,false);
        dbHandler = new DBHandler(getActivity(),null,null,1); //TODO : Close on exit
        RefreshListView(JobListFragment.this);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.jobs_to_display_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_jobs_all) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display=Breakdown.Status_JOB_ANY;
            RefreshListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_completed) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display=Breakdown.Status_JOB_COMPLETED;
            RefreshListView(JobListFragment.this);
            return true;
        }else if (id == R.id.menu_jobs_unatended) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display=Breakdown.Status_JOB_NOT_ATTENDED;
            RefreshListView(JobListFragment.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassist.NewBreakdownBroadcast"));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Add to list view
            RefreshListView(JobListFragment.this);

        }
    };


    public static void RefreshListView(final Fragment fragment) {


        final ArrayList<Breakdown> BreakdonwList = new ArrayList<Breakdown>(dbHandler.ReadBreakdowns(iJobs_to_Display));


        RecyclerView mRecyclerView = (RecyclerView)mView.findViewById(R.id.recycleview);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(fragment.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, final int position) {
                if(TextUtils.isEmpty(BreakdonwList.get(position).get_LATITUDE())) {
                    Toast.makeText(fragment.getActivity(), "No customer location data found ", Toast.LENGTH_LONG).show();
                    BreakdownView.Dialog(fragment,BreakdonwList.get(position),null,null);
                }else{
                    final FragmentManager fm;
                    fm = fragment.getFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(),MainActivity.MAP_FRAGMENT_TAG).commit();
                    Toast.makeText(fragment.getActivity(), BreakdonwList.get(position).get_Job_No() + " Locating... "  , Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            Fragment currentFragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                            if (currentFragment instanceof GmapFragment) {
                                GmapFragment GmapFrag= (GmapFragment) currentFragment;
                                GmapFrag.FocusBreakdown(BreakdonwList.get(position));
                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void onButton1Click(View view, int position) {
                Toast.makeText(fragment.getActivity(), "Clicked Button1 in " + BreakdonwList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
                Toast.makeText(fragment.getActivity(), "Clicked Button2 in " + BreakdonwList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCheckBox1Click(View view, int position) {
                Toast.makeText(fragment.getActivity(), "Clicked onCheckBox1Click in " + BreakdonwList.get(position), Toast.LENGTH_SHORT).show();
            }
        };

        // specify an adapter (see also next example)

        final RecyclerView.Adapter mAdapter = new JobsRecyclerAdapter(fragment.getActivity(),BreakdonwList, itemTouchListener);

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
//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped left", Toast.LENGTH_SHORT).show();

                                    BreakdonwList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped right", Toast.LENGTH_SHORT).show();

                                    BreakdonwList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }


    /**
     * Interface for the touch events in each item
     */
    public interface OnItemTouchListener {
        /**
         * Callback invoked when the user Taps one of the RecyclerView items
         *
         * @param view     the CardView touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onCardViewTap(View view, int position);

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
    }


}