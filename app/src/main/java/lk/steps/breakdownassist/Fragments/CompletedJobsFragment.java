package lk.steps.breakdownassist.Fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.MyDBHandler;
import lk.steps.breakdownassist.R;
import lk.steps.breakdownassist.RecyclerViewCards.CompleteJobsRecyclerAdapter;
import lk.steps.breakdownassist.RecyclerViewCards.SwipeableRecyclerViewTouchListener;

public class CompletedJobsFragment extends Fragment {

    private View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.job_listview,container,false);
        dbHandler = new MyDBHandler(getActivity(),null,null,1); //TODO : Close on exit
        displayListView();
        return mView;
    }

    private SimpleCursorAdapter dataAdapter;
    MyDBHandler dbHandler;


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
            displayListView();

        }
    };


    private void displayListView() {

        final ArrayList<Breakdown> dbList = new ArrayList<Breakdown>(dbHandler.ReadBreakdowns(1));

        RecyclerView mRecyclerView = (RecyclerView)mView.findViewById(R.id.recycleview);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);



        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                Toast.makeText(getActivity(), "Tapped " + dbList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton1Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button1 in " + dbList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked Button2 in " + dbList.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCheckBox1Click(View view, int position) {
                Toast.makeText(getActivity(), "Clicked onCheckBox1Click in " + dbList.get(position), Toast.LENGTH_SHORT).show();
            }
        };

        // specify an adapter (see also next example)
        final RecyclerView.Adapter mAdapter = new CompleteJobsRecyclerAdapter(getActivity(),dbList,itemTouchListener);
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
                                    dbList.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped right", Toast.LENGTH_SHORT).show();
                                    dbList.remove(position);
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