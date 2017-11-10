package lk.steps.breakdownassistpluss.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.List;
import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.MainActivity;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.RecyclerViewCards.JobsRecyclerAdapter;
import lk.steps.breakdownassistpluss.RecyclerViewCards.SwipeableRecyclerViewTouchListener;

public class SearchViewFragment extends Fragment {
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.job_listview,container,false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String keyWord = bundle.getString("KEY_WORD","0");
            Log.d("keyWord =",keyWord+"");
            RefreshListView(keyWord);
        }

        return mView;
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        dbHandler = new DBHandler(getActivity(),null,null,1);
        if (bundle != null) {
           String keyWord = bundle.getString("KEY_WORD","0");
            final List<Breakdown> BreakdownsList = dbHandler.SearchInBreakdowns(keyWord);
            //Log.d("RESULTS =",results.getCount()+"");

        }
    }*/
    private void RefreshListView(String keyWord) {

        //final ArrayList<Breakdown> BreakdonwList = new ArrayList<Breakdown>(dbHandler.ReadBreakdowns(iJobs_to_Display));
        final List<Breakdown> BreakdownsList = Globals.dbHandler.SearchInDatabase(keyWord);
        /*if (_BreakdownsList.size() == 0) {
            _BreakdownsList = dbHandler.SearchInCustomers(keyWord);
        }*/

        //final List<Breakdown> BreakdownsList = _BreakdownsList;

        RecyclerView mRecyclerView = (RecyclerView)mView.findViewById(R.id.recycleview);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        JobListFragment.OnItemTouchListener itemTouchListener = new JobListFragment.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {

            //Toast.makeText(getActivity(), "Tapped " + BreakdonwList.get(position).get_id(), Toast.LENGTH_SHORT).show();
                final int listPossition=position;
                final FragmentManager fm;
                fm = getFragmentManager();
                //Toast.makeText(getActivity(), "Tapped " + BreakdonwList.get(position).get_id(), Toast.LENGTH_SHORT).show();
                fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(), MainActivity.MAP_FRAGMENT_TAG).commit();
                Toast.makeText(getActivity(), BreakdownsList.get(position).get_Job_No() + " Locating... "  , Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Fragment currentFragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                        if (currentFragment instanceof GmapFragment) {
                            GmapFragment GmapFrag= (GmapFragment) currentFragment;
                            GmapFrag.FocusBreakdown(BreakdownsList.get(listPossition));
                        }
                    }
                }, 2000);
            }

            @Override
            public void onCardViewLongTap(View view, int position) {
                //
            }

            @Override
            public void onButton1Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button1 in " + BreakdownsList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button2 in " + BreakdownsList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCheckBox1Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked onCheckBox1Click in " + BreakdownsList.get(position), Toast.LENGTH_SHORT).show();
            }
        };

        // specify an adapter (see also next example)

        final RecyclerView.Adapter mAdapter = new JobsRecyclerAdapter(getActivity(),BreakdownsList, itemTouchListener);

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

                                    BreakdownsList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped right", Toast.LENGTH_SHORT).show();

                                    BreakdownsList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }

}