package lk.steps.breakdownassistpluss;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by JagathPrasanga on 2017-06-28.
 */

public class MapMarker {
    public static BitmapDescriptor GetBitmap(Breakdown breakdown){
        //TODO : Depending on the priority,and current STATUS mark the colour  and the shape
        BitmapDescriptor MarkerICON = null;
        if (breakdown.get_Status()==Breakdown.JOB_COMPLETED){
            if(breakdown.get_TARIFF_COD() == null)
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_complete);
            else if(breakdown.get_TARIFF_COD().equals("11") | breakdown.get_TARIFF_COD().equals("13")) // Domestic
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_complete);
            else if(breakdown.get_TARIFF_COD().equals("21") | breakdown.get_TARIFF_COD().equals("22")) // Industrial
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_complete);
            else if(breakdown.get_TARIFF_COD().equals("31") | breakdown.get_TARIFF_COD().equals("32")) // General
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_complete);
            else
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_complete);
        }else{
            if(breakdown.get_Priority() == Breakdown.Priority_Urgent){ // Urgent jobs
                if(breakdown.get_TARIFF_COD() == null)
                    if(breakdown.get_Status() == Breakdown.JOB_STATUS_ANY){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_critical_done);
                    }else{
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_critical);
                    }
                else if(breakdown.get_TARIFF_COD().equals("11") | breakdown.get_TARIFF_COD().equals("13")) // Domestic
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_critical_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_critical);
                    }
                else if(breakdown.get_TARIFF_COD().equals("21") | breakdown.get_TARIFF_COD().equals("22")) // Industrial
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_lv_critical_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_lv_critical);
                    }
                else if(breakdown.get_TARIFF_COD().equals("31") | breakdown.get_TARIFF_COD().equals("32")) // General
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_lv_critical_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_lv_critical);
                    }
                else
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_critical_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_critical);
                    }
            }else if(breakdown.get_Priority() == Breakdown.Priority_High){ // Urgent jobs
                if(breakdown.get_TARIFF_COD() == null)
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_high_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_high);
                    }
                else if(breakdown.get_TARIFF_COD().equals("11") | breakdown.get_TARIFF_COD().equals("13")) // Domestic
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_high_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_high);
                    }
                else if(breakdown.get_TARIFF_COD().equals("21") | breakdown.get_TARIFF_COD().equals("22")) // Industrial
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_lv_high_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_lv_high);
                    }
                else if(breakdown.get_TARIFF_COD().equals("31") | breakdown.get_TARIFF_COD().equals("32")) // General
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_lv_high_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_lv_high);
                    }
                else
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_high_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_high);
                    }
            }
            else //if(breakdown.get_Priority() == Breakdown.Priority_NORMAL){ // NORMAL jobs
            {
                if(breakdown.get_TARIFF_COD() == null)
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_normal_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_normal);
                    }
                else if(breakdown.get_TARIFF_COD().equals("11") | breakdown.get_TARIFF_COD().equals("13")) // Domestic
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_normal_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_normal);
                    }
                else if(breakdown.get_TARIFF_COD().equals("21") | breakdown.get_TARIFF_COD().equals("22")) // Industrial
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_lv_normal_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory_lv_normal);
                    }
                else if(breakdown.get_TARIFF_COD().equals("31") | breakdown.get_TARIFF_COD().equals("32")) // General
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_lv_normal_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop_lv_normal);
                    }
                else
                    if(breakdown.get_Status() == Breakdown.JOB_TEMPORARY_COMPLETED){
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_normal_done);
                    }else {
                        MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.domestic_lv_normal);
                    }
            }
        }
        //return AddBreakDownToMap(breakdown,MarkerICON);
        return MarkerICON;
    }
}
