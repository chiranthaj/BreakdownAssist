package lk.steps.breakdownassist;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Chirantha on 12/11/2016.
 */

public class Customer {
    //TODO : Make these private and use getter and setters
    public String _id;
    public String _Name;
    public String _LATITUDE;
    public String _LONGITUDE;
    public LatLng _location ;

    public Customer(String id,String Name,String LATITUDE,String LONGITUDE)
    {
        this._id=id;
        this._Name = Name;
        this._LATITUDE=LATITUDE;
        this._LONGITUDE=LONGITUDE;
    }

    public String getName()
    {
        return _Name;
    }

    public String getCustomerToString()
    {
        return _id + " " + _Name + " " + _LATITUDE  + " " + _LONGITUDE;
    }
    public LatLng getLocation()
    {
        return new LatLng(Double.parseDouble (_LONGITUDE) ,Double.parseDouble (_LATITUDE));
    }
}




