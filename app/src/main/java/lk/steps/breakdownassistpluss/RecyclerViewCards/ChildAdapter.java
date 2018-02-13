package lk.steps.breakdownassistpluss.RecyclerViewCards;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Sync.SyncService;

/**
 * Created by JagathPrasanga on 11/1/2017.
 */


public class ChildAdapter extends ArrayAdapter<Breakdown> {
    public ChildAdapter(Context context, ArrayList<Breakdown> breakdowns) {
        super(context, 0, breakdowns);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Breakdown breakdown = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_row, parent, false);
        }
        // Lookup view for data population
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        ImageButton btnRemove = (ImageButton) convertView.findViewById(R.id.btnRemove);
        // Populate the data into the template view using the data object
        if(breakdown!=null){
            tvId.setText(breakdown.get_Job_No());
            tvDate.setText(Globals.parseDate(breakdown.get_Received_Time()));
            tvAddress.setText(breakdown.ADDRESS);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(getContext(), breakdown.get_Job_No(), Toast.LENGTH_SHORT).show();
                    RemoveChildDialog(breakdown.get_Job_No());
                }
            });
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private void RemoveChildDialog(final String childId){
        final Context context= this.getContext();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_ask_grouping);
        dialog.setCancelable(false);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        TextView tv = (TextView) dialog.findViewById(R.id.textDialog);
        tv.setText("Do you want to remove breakdown "+childId+" from the group ?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.dbHandler.RemoveChild(childId,"0");
                SyncService.PostGroups(context);

                Intent intent = new Intent();
                intent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
                intent.putExtra("breakdown_list_refresh_req", "breakdown_list_refresh_req");
                context.sendBroadcast(intent);

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

}