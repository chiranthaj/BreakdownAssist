package lk.steps.breakdownassistpluss;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Chirantha on 01/11/2016.
 */

public class Breakdown {
    public static final int JOB_STATUS_ANY = -1;
    public static final int JOB_NOT_ATTENDED = 0;
    public static final int JOB_DELIVERED = 1;
    public static final int JOB_ACKNOWLEDGED = 2;
    public static final int JOB_VISITED = 3;
    public static final int JOB_ATTENDING = 4;
    public static final int JOB_TEMPORARY_COMPLETED = 5;
    public static final int JOB_COMPLETED = 6;
    public static final int JOB_WITHDRAWN = 7;
    public static final int JOB_REJECT = 8;

    public static final int Priority_Normal = 3;
    public static final int Priority_High = 2;
    public static final int Priority_Urgent = 1;
    public static final int Priority_Not_Assigned = 0;

    private String _id;
    private String RECEIVED_TIME;
    private String COMPLETED_TIME;
    private String BA_SERVER_SYNCED;
    private String ACCT_NUM;
    private String NAME;
    private String ADDRESS;
    private String LATITUDE;
    private String LONGITUDE;
    private LatLng LOCATION ;
    private int STATUS;
    private String SUB;
    private String ECSC;
    private String AREA;
    private String TEAM_ID;
    private String TARIFF_COD;
    private String GPS_ACCURACY;
    private String DESCRIPTION;
    private String JOB_SOURCE;
    private int PRIORITY;
    private String JOB_NO;
    private String USER_ID;
    private String OLD_JOB_NO;
    private String CONTACT_NO;
    private String PREMISES_ID;
    private String inbox_ref;
    private String PARENT_BREAKDOWN_ID;

    public String get_PremisesID() {
        return PREMISES_ID;
    }

    public void set_PremisesID(String PREMISES_ID) {
        this.PREMISES_ID = PREMISES_ID;
    }

    public Breakdown(){

    }

    public Breakdown(String id,String Name,String LATITUDE,String LONGITUDE)
    {
        this._id=id;
        this.NAME = Name;
        this.LATITUDE=LATITUDE;
        this.LONGITUDE=LONGITUDE;
    }
    public Breakdown(String id,String Name,String LATITUDE,String LONGITUDE,int STATUS)
    {
        this._id=id;
        this.NAME = Name;
        this.LATITUDE=LATITUDE;
        this.LONGITUDE=LONGITUDE;
        this.STATUS=STATUS;
    }

    public LatLng getLocation()
    {
        if (this.LATITUDE!=null &&  this.LONGITUDE !=null)
        {
            return new LatLng(Double.parseDouble (LATITUDE),Double.parseDouble (LONGITUDE) );
        }else
            return null;

    }

    public String get_ADDRESS() {
        return ADDRESS;
    }

    public void set_ADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String get_Acct_Num() {
        return ACCT_NUM;
    }
    public String get_ParentBreakdownId() {
        return PARENT_BREAKDOWN_ID;
    }
    public void set_ParentBreakdownId(String ParentBreakdownId) {
        this.PARENT_BREAKDOWN_ID=ParentBreakdownId;
    }
    public void set_Acct_Num(String ACCT_NUM) {
        this.ACCT_NUM = ACCT_NUM;
    }

    public String get_Received_Time() {
        return RECEIVED_TIME;
    }
    public void set_Received_Time(String RECEIVED_TIME) {
        this.RECEIVED_TIME = RECEIVED_TIME;
    }
    public String get_Completed_Time() {
        return COMPLETED_TIME;
    }
    public void set_Completed_Time(String COMPLETED_TIME) {
        this.COMPLETED_TIME = COMPLETED_TIME;
    }
    public String get_ECSC() {
        return ECSC;
    }

    public void set_ECSC(String ECSC) {
        this.ECSC = ECSC;
    }

    public String get_Full_Description() {
        return DESCRIPTION;
    }

    public void set_Full_Description(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String get_GPS_ACCURACY() {
        return GPS_ACCURACY;
    }

    public void set_GPS_ACCURACY(String GPS_ACCURACY) {
        this.GPS_ACCURACY = GPS_ACCURACY;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_inbox_ref() {
        return inbox_ref;
    }

    public void set_inbox_ref(String _inbox_ref) {
        this.inbox_ref = _inbox_ref;
    }

    public String get_JOB_SOURCE() {
        return JOB_SOURCE;
    }

    public void set_JOB_SOURCE(String _JOB_SOURCE) {
        this.JOB_SOURCE = _JOB_SOURCE;
    }


    public String get_LATITUDE() {
        return LATITUDE;
    }

    public void set_LATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public LatLng get_location() {
        if(LATITUDE != null & LONGITUDE != null)
        return new LatLng(Double.parseDouble(LATITUDE),Double.parseDouble(LONGITUDE));
        else return new LatLng(0,0);
    }

    public void set_location(LatLng LOCATION) {
        this.LOCATION = LOCATION;
    }

    public String get_LONGITUDE() {
        return LONGITUDE;
    }

    public void set_LONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public String get_Name() {
        return NAME;
    }

    public void set_Name(String NAME) {
        this.NAME = NAME;
    }

    public int get_Priority() {
        return PRIORITY;
    }

    public void set_Priority(int PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

    public int get_Status() {
        return STATUS;
    }

    public void set_Status(int STATUS) {
        this.STATUS = STATUS;
    }

    public String get_SUB() {
        return SUB;
    }

    public void set_SUB(String SUB) {
        this.SUB = SUB;
    }

    public String get_TARIFF_COD() {
        return TARIFF_COD;
    }

    public void set_TARIFF_COD(String TARIFF_COD) {
        this.TARIFF_COD = TARIFF_COD;
    }

    public String get_Contact_No() {
        return CONTACT_NO;
    }

    public void set_Contact_No(String CONTACT_NO) {
        this.CONTACT_NO = CONTACT_NO;
    }

    public String get_Job_No() {
        return JOB_NO;
    }

    public void set_Job_No(String JOB_NO) {
        this.JOB_NO = JOB_NO;
    }
    public String get_OldJob_No() {
        return OLD_JOB_NO;
    }

    public void set_OldJob_No(String _OLD_JOB_NO) {
        this.OLD_JOB_NO = _OLD_JOB_NO;
    }

    public String get_BA_SERVER_SYNCED() {
        return BA_SERVER_SYNCED;
    }

    public void set_BA_SERVER_SYNCED(String _BA_SERVER_SYNCED) {
        this.BA_SERVER_SYNCED = _BA_SERVER_SYNCED;
    }

    public void set_Area(String area) {
        this.AREA = area;
    }
    public void set_TeamId(String teamId) {
        this.TEAM_ID = teamId;
    }
    public void set_UserId(String userId) {
        this.USER_ID = userId;
    }

}




