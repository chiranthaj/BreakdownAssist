package lk.steps.breakdownassistpluss.RecyclerViewCards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.R;

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
        Breakdown breakdown = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_row, parent, false);
        }
        // Lookup view for data population
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        // Populate the data into the template view using the data object
        if(breakdown!=null){
            tvId.setText(breakdown.get_Job_No());
            tvDate.setText(Globals.parseDate(breakdown.get_Received_Time()));
            tvAddress.setText(breakdown.get_ADDRESS());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}