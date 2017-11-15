package lk.steps.breakdownassistpluss.RecyclerViewCards;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.R;


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
        holder.JOB_NO.setText(breakdownList.get(position).get_Job_No());
        holder.received_date_time.setText(Globals.parseDate(breakdownList.get(position).get_Received_Time()));
        holder.name.setText(breakdownList.get(position).get_Name());
        holder.address.setText(breakdownList.get(position).get_ADDRESS());
        holder.description.setText(breakdownList.get(position).get_Full_Description());
       // holder.description.setText("-");

        int STATUS = breakdownList.get(position).get_Status();
        Drawable drawable = (Drawable)holder.completed_date_time.getBackground();
        String Completed_Time = "*";
        try{
            if(!breakdownList.get(position).get_Completed_Time().isEmpty()){
                Completed_Time = Globals.parseDate(breakdownList.get(position).get_Completed_Time());
            }
        }catch(Exception e){

        }

        if(STATUS==Breakdown.JOB_DELIVERED){
            holder.JOB_NO.setTextColor(Color.parseColor("#6c0082"));
            holder.completed_date_time.setText("Not acknowledged");
            drawable.setColorFilter(Color.parseColor("#6c0082"), PorterDuff.Mode.SRC_IN);
        }if(STATUS==Breakdown.JOB_ATTENDING){
            holder.JOB_NO.setTextColor(Color.parseColor("#06823e"));
            holder.completed_date_time.setText("Attending");
            drawable.setColorFilter(Color.parseColor("#06823e"), PorterDuff.Mode.SRC_IN);
        }else if(STATUS==Breakdown.JOB_VISITED){
            holder.JOB_NO.setTextColor(Color.parseColor("#9b8404"));
            holder.completed_date_time.setText("Visited");
            drawable.setColorFilter(Color.parseColor("#9b8404"), PorterDuff.Mode.SRC_IN);
        }else if(STATUS==Breakdown.JOB_TEMPORARY_COMPLETED){
            holder.JOB_NO.setTextColor(Color.parseColor("#033e7c"));
            holder.completed_date_time.setText("Temporary completed on " +Completed_Time);
            drawable.setColorFilter(Color.parseColor("#033e7c"), PorterDuff.Mode.SRC_IN);
        }else if(STATUS==Breakdown.JOB_COMPLETED){
            holder.JOB_NO.setTextColor(Color.parseColor("#0d7504"));
            holder.completed_date_time.setText("Completed on "+Completed_Time);
            drawable.setColorFilter(Color.parseColor("#0d7504"), PorterDuff.Mode.SRC_IN);
        }else if(STATUS==Breakdown.JOB_REJECT){
            holder.JOB_NO.setTextColor(Color.parseColor("#000000"));
            holder.completed_date_time.setText("Rejected on "+Completed_Time);
            drawable.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        }else if(STATUS==Breakdown.JOB_WITHDRAWN){
            holder.JOB_NO.setTextColor(Color.parseColor("#000000"));
            holder.completed_date_time.setText("Withdrawn on "+Completed_Time);
            drawable.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        }else {
            holder.completed_date_time.setText("Unattained");
            holder.JOB_NO.setTextColor(Color.parseColor("#c9082b"));
            drawable.setColorFilter(Color.parseColor("#c9082b"), PorterDuff.Mode.SRC_IN);
        }
        String src =breakdownList.get(position).get_OldJob_No();
        if(src==null){
            holder.imgSource.setVisibility(View.INVISIBLE);
        }else if(src.equals("")){
            holder.imgSource.setVisibility(View.INVISIBLE);
        }else{
            holder.imgSource.setVisibility(View.VISIBLE);
        }
        /*else{
            holder.completed_date_time.setText(Globals.parseDate(breakdownList.get(position).get_Completed_Time()));
        }*/
        String lat = breakdownList.get(position).get_LATITUDE();
        if(lat==null){
            holder.imgMap.setVisibility(View.INVISIBLE);
        }else if(lat.trim().length()>3){
            holder.imgMap.setVisibility(View.VISIBLE);
        }else{
            holder.imgMap.setVisibility(View.INVISIBLE);
        }

        int priority = breakdownList.get(position).get_Priority();
        if(priority == 4){
            holder.imgPriority.setVisibility(View.VISIBLE);
        }else{
            holder.imgPriority.setVisibility(View.INVISIBLE);

        }

        if(breakdownList.get(position).get_ParentBreakdownId() == null){
            holder.childrenList.setVisibility(View.GONE);
        }else if(breakdownList.get(position).get_ParentBreakdownId().equals("PARENT")){
            ArrayList<Breakdown> children = new ArrayList<Breakdown>(Globals.dbHandler.GetChildBreakdowns(breakdownList.get(position).get_Job_No()));
            if(children.size()>0){
                //Log.e("children","="+children.size());
                //ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.child_row,R.id.textview, children);
                //holder.childrenList.setAdapter(adapter);

                ChildAdapter adapter = new ChildAdapter(context, children);
                holder.childrenList.setAdapter(adapter);
            }else{
                holder.childrenList.setVisibility(View.GONE);
            }
        }else{
            holder.childrenList.setVisibility(View.GONE);
        }


        /*if(STATUS == Breakdown.JOB_COMPLETED){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#d8d8d8"));
        }else if(STATUS == Breakdown.JOB_TEMPORARY_COMPLETED){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#f9ffce"));
        }else if(STATUS == Breakdown.JOB_ATTENDING){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#c9ffcb"));
        }else if(STATUS == Breakdown.JOB_VISITED){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#dbfffb"));
        }else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#fbddff"));
        }*/
        holder.setBreakdown(breakdownList.get(position));
    }

    @Override
    public int getItemCount() {
        return breakdownList.size();
    }

}

