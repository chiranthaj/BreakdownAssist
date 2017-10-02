package lk.steps.breakdownassistpluss.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import lk.steps.breakdownassistpluss.R;

/**
 * Created by Chirantha on 20/11/2016.
 */

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View myContentsView;
    public InfoWindowAdapter(LayoutInflater layoutInflater){

        myContentsView =  layoutInflater.inflate(R.layout.infowindow_contents, null);
    }
//Interchange to apply the design/background to the whole window
/*    @Override
    public View getInfoWindow(Marker marker) {
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());
//TODO : Pass a bundle with more data
        return myContentsView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }*/
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet().trim());
       // tvSnippet.setText("seret5rg 5656frw3ef exbgwhgu we8rt xhhqegxu wdtir ywehrxg afwrc wxuirir");
//TODO : Pass a bundle with more data
        return myContentsView;
    }
}




