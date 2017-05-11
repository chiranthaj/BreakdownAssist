package lk.steps.breakdownassist;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Chirantha on 01/11/2016.
 */

public class Breakdown {
    public static final int Status_JOB_ANY = -1;
    public static final int Status_JOB_COMPLETED = 1;
    public static final int Status_JOB_VISITED = 2;
    public static final int Status_JOB_NOT_ATTENDED = 0;
    public static final int Status_JOB_NOT_FOUND = 3;


    private String _id;
    private String _Received_Time;
    private String _Completed_Time;
    private String _Acct_Num;
    private String _Name;
    private String _ADDRESS;
    private String _LATITUDE;
    private String _LONGITUDE;
    private LatLng _location ;
    private int _Status;
    private String _SUB;
    private String _ECSC;
    private String _TARIFF_COD;
    private String _GPS_ACCURACY;
    private String _Full_Description;
    private int _Priority;
    private String _Job_No;
    private String _Contact_No;
    private String _PremisesID;

    public String get_PremisesID() {
        return _PremisesID;
    }

    public void set_PremisesID(String _PremisesID) {
        this._PremisesID = _PremisesID;
    }

    public Breakdown(){

    }

    public Breakdown(String id,String Name,String LATITUDE,String LONGITUDE)
    {
        this._id=id;
        this._Name = Name;
        this._LATITUDE=LATITUDE;
        this._LONGITUDE=LONGITUDE;
    }
    public Breakdown(String id,String Name,String LATITUDE,String LONGITUDE,int Status)
    {
        this._id=id;
        this._Name = Name;
        this._LATITUDE=LATITUDE;
        this._LONGITUDE=LONGITUDE;
        this._Status=Status;
    }

    public LatLng getLocation()
    {
        if (this._LATITUDE!=null &&  this._LONGITUDE !=null)
        {
            return new LatLng(Double.parseDouble (_LATITUDE),Double.parseDouble (_LONGITUDE) );
        }else
            return null;

    }

    public String get_ADDRESS() {
        return _ADDRESS;
    }

    public void set_ADDRESS(String _ADDRESS) {
        this._ADDRESS = _ADDRESS;
    }

    public String get_Acct_Num() {
        return _Acct_Num;
    }

    public void set_Acct_Num(String _Acct_Num) {
        this._Acct_Num = _Acct_Num;
    }

    public String get_Received_Time() {
        return _Received_Time;
    }
    public void set_Received_Time(String _Received_Time) {
        this._Received_Time = _Received_Time;
    }
    public String get_Completed_Time() {
        return _Completed_Time;
    }
    public void set_Completed_Time(String _Received_Time) {
        this._Completed_Time = _Completed_Time;
    }
    public String get_ECSC() {
        return _ECSC;
    }

    public void set_ECSC(String _ECSC) {
        this._ECSC = _ECSC;
    }

    public String get_Full_Description() {
        return _Full_Description;
    }

    public void set_Full_Description(String _Full_Description) {
        this._Full_Description = _Full_Description;
    }

    public String get_GPS_ACCURACY() {
        return _GPS_ACCURACY;
    }

    public void set_GPS_ACCURACY(String _GPS_ACCURACY) {
        this._GPS_ACCURACY = _GPS_ACCURACY;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_LATITUDE() {
        return _LATITUDE;
    }

    public void set_LATITUDE(String _LATITUDE) {
        this._LATITUDE = _LATITUDE;
    }

    public LatLng get_location() {
        return _location;
    }

    public void set_location(LatLng _location) {
        this._location = _location;
    }

    public String get_LONGITUDE() {
        return _LONGITUDE;
    }

    public void set_LONGITUDE(String _LONGITUDE) {
        this._LONGITUDE = _LONGITUDE;
    }

    public String get_Name() {
        return _Name;
    }

    public void set_Name(String _Name) {
        this._Name = _Name;
    }

    public int get_Priority() {
        return _Priority;
    }

    public void set_Priority(int _Priority) {
        this._Priority = _Priority;
    }

    public int get_Status() {
        return _Status;
    }

    public void set_Status(int _Status) {
        this._Status = _Status;
    }

    public String get_SUB() {
        return _SUB;
    }

    public void set_SUB(String _SUB) {
        this._SUB = _SUB;
    }

    public String get_TARIFF_COD() {
        return _TARIFF_COD;
    }

    public void set_TARIFF_COD(String _TARIFF_COD) {
        this._TARIFF_COD = _TARIFF_COD;
    }

    public String get_Contact_No() {
        return _Contact_No;
    }

    public void set_Contact_No(String _Contact_No) {
        this._Contact_No = _Contact_No;
    }

    public String get_Job_No() {
        return _Job_No;
    }

    public void set_Job_No(String _Job_No) {
        this._Job_No = _Job_No;
    }
}




