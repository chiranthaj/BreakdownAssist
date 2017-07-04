package lk.steps.breakdownassist.RecyclerViewCards;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.MapView;
import java.util.HashSet;
import java.util.List;
import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.Fragments.JobListFragment;
import lk.steps.breakdownassist.Globals;
import lk.steps.breakdownassist.R;


public class JobsRecyclerAdapter extends RecyclerView.Adapter<MapLocationViewHolder>  {
    protected HashSet<MapView> mMapViews = new HashSet<>();
    static   List<Breakdown> breakdownList;
    static  Context context;
    public static JobListFragment.OnItemTouchListener onItemTouchListener;
    //protected GoogleMap mGoogleMap;
    //public MapView mapView;

    public JobsRecyclerAdapter(Context context, List<Breakdown> data, JobListFragment.OnItemTouchListener onItemTouchListener ){
       // this.breakdownList = new ArrayList<Breakdown>();
        this.context = context;
        this.breakdownList = data;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MapLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
        //        R.layout.job_listview_row,parent, false);
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_listview_row, parent, false);

        // create ViewHolder
        MapLocationViewHolder viewHolder = new MapLocationViewHolder(context, itemLayoutView);
        mMapViews.add(viewHolder.mapView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MapLocationViewHolder holder, int position) {

        holder.acc_no.setText(breakdownList.get(position).get_Acct_Num());
        holder.job_no.setText(breakdownList.get(position).get_Job_No().trim());
        holder.received_date_time.setText(Globals.parseDate(breakdownList.get(position).get_Received_Time()));

        holder.name.setText(breakdownList.get(position).get_Name());
        holder.address.setText(breakdownList.get(position).get_ADDRESS());
        //holder.description.setText(breakdownList.get(position).get_Full_Description());
        holder.description.setText("-");
        if(breakdownList.get(position).get_Completed_Time() == null){
            holder.completed_date_time.setText("Unattained job");
        }else{
            holder.completed_date_time.setText(Globals.parseDate(breakdownList.get(position).get_Completed_Time()));
        }
        String lat = breakdownList.get(position).get_LATITUDE();
        if(lat==null){
            holder.imgMap.setVisibility(View.INVISIBLE);
        }else if(lat.trim().length()>3){
            holder.imgMap.setVisibility(View.VISIBLE);
        }else{
            holder.imgMap.setVisibility(View.INVISIBLE);
        }
        int status = breakdownList.get(position).get_Status();
        if(status == Breakdown.Status_JOB_COMPLETED){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#d8d8d8"));
        }else if(status == Breakdown.Status_JOB_DONE){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#f9ffce"));
        }else if(status == Breakdown.Status_JOB_ATTENDING){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#c9ffcb"));
        }else if(status == Breakdown.Status_JOB_VISITED){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#dbfffb"));
        }else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#fbddff"));
        }
        holder.setBreakdown(breakdownList.get(position));
    }

    @Override
    public int getItemCount() {
        return breakdownList.size();
    }

}

