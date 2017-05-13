package lk.steps.breakdownassist.RecyclerViewCards;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.Fragments.JobListFragment;
import lk.steps.breakdownassist.R;


public class JobsRecyclerAdapter extends RecyclerView.Adapter<JobsRecyclerAdapter.ViewHolder> {

    static   List<Breakdown> dbList;
    static  Context context;
    public static JobListFragment.OnItemTouchListener onItemTouchListener;
    public JobsRecyclerAdapter(Context context, List<Breakdown> dbList, JobListFragment.OnItemTouchListener onItemTouchListener ){
        this.dbList = new ArrayList<Breakdown>();
        this.context = context;
        this.dbList = dbList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public JobsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.job_listview_row, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(JobsRecyclerAdapter.ViewHolder holder, int position) {

        holder.acc_no.setText(dbList.get(position).get_Acct_Num());
        holder.job_no.setText(dbList.get(position).get_Job_No());
        holder.received_date_time.setText(dbList.get(position).get_Received_Time());

        holder.name.setText(dbList.get(position).get_Name());
        holder.address.setText(dbList.get(position).get_ADDRESS());
        //holder.description.setText(dbList.get(position).get_Full_Description());
        if(dbList.get(position).get_Completed_Time() == null){
            holder.completed_date_time.setText("Unattained job");
        }else{
            holder.completed_date_time.setText(dbList.get(position).get_Completed_Time());
        }

    }

    @Override
    public int getItemCount() {
        return dbList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView acc_no,job_no,received_date_time,completed_date_time,name,address,description;
        private Button button1,button2;
        private CheckBox checkBox1;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            acc_no = (TextView) itemLayoutView.findViewById(R.id.acct_num);
            job_no = (TextView)itemLayoutView.findViewById(R.id.job_no);
            received_date_time = (TextView) itemLayoutView.findViewById(R.id.received_date_time);
            completed_date_time = (TextView)itemLayoutView.findViewById(R.id.completed_date_time);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            address = (TextView) itemLayoutView.findViewById(R.id.address);

            // description = (TextView)itemLayoutView.findViewById(R.id.description);

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
            /*Intent intent = new Intent(context,DetailsActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("position",getAdapterPosition());
            intent.putExtras(extras);

            context.startActivity(intent);*/
            Toast.makeText(JobsRecyclerAdapter.context, "you have clicked Row " + getAdapterPosition(), Toast.LENGTH_LONG).show();
        }


    }
}

