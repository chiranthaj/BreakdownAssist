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
            holder.cardView.setCardBackgroundColor(Color.parseColor("#f4f4f4"));
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

       // holder.getDirections(JobListFragment.currentLocation,breakdownList.get(position).get_location());
    }

    @Override
    public int getItemCount() {
        return breakdownList.size();
    }


    /*public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView acc_no,job_no,received_date_time,completed_date_time,name,address,description;
        private Button button1,button2;
        private CheckBox checkBox1;
        private ImageView imgMap;
        private CardView cardView;
        private MapView mapView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            cardView = (CardView) itemLayoutView.findViewById(R.id.card_view);
            acc_no = (TextView) itemLayoutView.findViewById(R.id.acct_num);
            job_no = (TextView)itemLayoutView.findViewById(R.id.job_no);
            received_date_time = (TextView) itemLayoutView.findViewById(R.id.received_date_time);
            completed_date_time = (TextView)itemLayoutView.findViewById(R.id.completed_date_time);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            address = (TextView) itemLayoutView.findViewById(R.id.address);
            imgMap = (ImageView) itemLayoutView.findViewById(R.id.imgMap);
            description = (TextView)itemLayoutView.findViewById(R.id.description);
            mapView = (MapView) itemView.findViewById(R.id.map_view);

            button1 = (Button) itemView.findViewById(R.id.card_view_button1);
            button2 = (Button) itemView.findViewById(R.id.card_view_button2);
            checkBox1 = (CheckBox) itemView.findViewById(R.id.card_view_checkBox1);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                }
            });
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onButton1Click(v, getLayoutPosition());
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onButton2Click(v, getLayoutPosition());
                }
            });
            checkBox1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCheckBox1Click(v, getLayoutPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
           // Intent intent = new Intent(context,DetailsActivity.class);
           // Bundle extras = new Bundle();
           // extras.putInt("position",getAdapterPosition());
           // intent.putExtras(extras);

           // context.startActivity(intent);
            //Toast.makeText(JobsRecyclerAdapter.context, "you have clicked Row " + getAdapterPosition(), Toast.LENGTH_LONG).show();
        }


    }*/

}

