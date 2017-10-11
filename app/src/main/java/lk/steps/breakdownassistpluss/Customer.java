package lk.steps.breakdownassistpluss;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Chirantha on 12/11/2016.
 */

public class Customer {
    //TODO : Make these private and use getter and setters
    public String _id;
    public String NAME;
    public String LATITUDE;
    public String LONGITUDE;
    public LatLng LOCATION ;

    public Customer(String id,String Name,String LATITUDE,String LONGITUDE)
    {
        this._id=id;
        this.NAME = Name;
        this.LATITUDE=LATITUDE;
        this.LONGITUDE=LONGITUDE;
    }

    public String getName()
    {
        return NAME;
    }

    public String getCustomerToString()
    {
        return _id + " " + NAME + " " + LATITUDE  + " " + LONGITUDE;
    }
    public LatLng getLocation()
    {
        return new LatLng(Double.parseDouble (LONGITUDE) ,Double.parseDouble (LATITUDE));
    }
}




