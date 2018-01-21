package lk.steps.breakdownassistpluss;


import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.Models.JobCompletion;
import lk.steps.breakdownassistpluss.Models.Statistics;
import lk.steps.breakdownassistpluss.Sync.BreakdownGroup;
import lk.steps.breakdownassistpluss.Sync.SyncMaterialObject;
import lk.steps.breakdownassistpluss.Sync.SyncObject;

public class DBHandler extends SQLiteOpenHelper
{
    private static final int Database_Version = 122;
    private static final String DatabaseNAME = "BreakdownAssist.db";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DatabaseNAME, factory, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query;

        /*query = "CREATE TABLE Breakdowns ("+
                "id	                INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "BreakdownId	    TEXT UNIQUE,"+
                "ParentBreakdownId  TEXT,"+
                "PremisesId         TEXT,"+
                "JobNo              TEXT,"+
                "ReceivedTime	    TEXT,"+
                "AssignedTime	    TEXT,"+
                "Name 	            TEXT,"+
                "Address	        TEXT,"+
                "AcctNo 	        TEXT,"+
                "MobilePhNo 	    TEXT,"+
                "LandPhNo 	        TEXT,"+
                "Priority    	    INTEGER,"+
                "Source	            TEXT,"+
                "StatusId	        TEXT,"+
                "AreaId             TEXT,"+
                "EcscId	            TEXT,"+
                "AreaId	            TEXT,"+
                "TeamId	            TEXT,"+
                "Note               TEXT,"+
                "Lat    	        TEXT,"+
                "Lon  	            TEXT,"+
                "Sin  	            TEXT,"+
                "UserId  	        TEXT,"+
                "TARIFF_COD  	        TEXT,"+
                "DESCRIPTION	        TEXT,"+
                "SMS_INBOX_REFERENCE	TEXT UNIQUE,"+
                "BA_SERVER_SYNCED       TEXT,"+
                "GroupSynced           TEXT,"+
                "CompletedTime         TEXT"+
                ");";
        db.execSQL(query);*/


        query = "CREATE TABLE BreakdownRecords ("+
                "id	                INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "JOB_NO	                TEXT UNIQUE,"+
                "PARENT_BREAKDOWN_ID    TEXT,"+
                "OLD_JOB_NO             TEXT,"+
                "DateTime	            TEXT,"+
                "ACCT_NUM 	            TEXT,"+
                "STATUS	                TEXT,"+
                "CONTACT_NO 	        TEXT,"+
                "MobilePhNo 	        TEXT,"+
                "LandPhNo 	            TEXT,"+
                "PRIORITY    	        INTEGER,"+
                "JOB_SOURCE	            TEXT,"+
                "NAME	                TEXT,"+
                "SUB                    TEXT,"+
                "ADDRESS	            TEXT,"+
                "LATITUDE    	        TEXT,"+
                "LONGITUDE  	        TEXT,"+
                "GPS_ACCURACY  	        TEXT,"+
                "TARIFF_COD  	        TEXT,"+
                "DESCRIPTION	        TEXT,"+
                "ECSC	                TEXT,"+
                "AREA	                TEXT,"+
                "TEAM	                TEXT,"+
                "NOTE                   TEXT,"+
                "SMS_INBOX_REFERENCE	TEXT UNIQUE,"+
                "BA_SERVER_SYNCED       TEXT,"+
                "GROUP_SYNCED           TEXT,"+
                "COMPLETED_TIME         TEXT"+
                ");";
        db.execSQL(query);


        query = "CREATE TABLE PremisesID (" +
                "PremisesID TEXT PRIMARY KEY,"+
                "ACCT_NUM               TEXT "+
                ");";
        db.execSQL(query);

        query = "CREATE TABLE JobStatusChange (" +
                "id	                INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "JOB_NO                 TEXT,"+
                "STATUS                 TEXT,"+
                "change_datetime        TEXT,"+
                "comment                TEXT,"+
                "device_timestamp       TEXT,"+
                "synchro_mobile_db      TEXT"+
                //"PRIMARY KEY (JOB_NO,STATUS)" +
                ");";
        db.execSQL(query);


        query = "CREATE TABLE JobCompletion ("+
                "JOB_NO                 TEXT,"+
                "type_failure           TEXT,"+
                "cause                  TEXT,"+
                "detail_reason_code     TEXT,"+
                "job_completed_datetime TEXT,"+
                "job_completed_by       TEXT,"+
                "STATUS                TEXT,"+
                "comment                TEXT,"+
                "action_code            TEXT,"+
                "device_timestamp       TEXT,"+
                "synchro_mobile_db      TEXT,"+
                "PRIMARY KEY (JOB_NO)" +
                ");";
        db.execSQL(query);

        query = "CREATE TABLE GpsTracking ("+
                "id          INTEGER PRIMARY KEY," +
                "timestamp              TEXT,"+
                "lat                    TEXT,"+
                "lon                    TEXT,"+
                "speed                  TEXT,"+
                "accuracy               TEXT,"+
                "altitude               TEXT,"+
                "direction              TEXT,"+
                "distance              TEXT,"+
                "sync_done              TEXT"+
                ");";
        db.execSQL(query);

        query = "CREATE TABLE Materials ("+
                "BREAKDOWN_ID	        TEXT ,"+
                "MATERIAL_CODE          TEXT,"+
                "QUANTITY	            NUMBER,"+
                "SYNC_DONE	            TEXT,"+
                "PRIMARY KEY (BREAKDOWN_ID, MATERIAL_CODE));";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS BreakdownRecords");
        db.execSQL("DROP TABLE IF EXISTS Customers");
        db.execSQL("DROP TABLE IF EXISTS PremisesID");
        db.execSQL("DROP TABLE IF EXISTS JobStatusChange");
        db.execSQL("DROP TABLE IF EXISTS JobCompletion");
        db.execSQL("DROP TABLE IF EXISTS GpsTracking");
        db.execSQL("DROP TABLE IF EXISTS Materials");
        onCreate(db);
    }

    /*public Breakdown GetCustomerData(String AcctNum){
        Breakdown breakdown= new Breakdown();
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT *  FROM Customers  WHERE ACCT_NUM='"+AcctNum+"';";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                breakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                breakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                breakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                breakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                breakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                breakdown.set_GPS_ACCURACY(c.getString(c.getColumnIndex("GPS_ACCURACY")));
                breakdown.set_SUB(c.getString(c.getColumnIndex("ECSC"))+ c.getString(c.getColumnIndex("SUB")));
                //Log.e("Customers",AcctNum+"*"+c.getString(c.getColumnIndex("NAME")));
            }
            c.moveToNext();
        }
        c.close();
        return breakdown;
    }*/

    public boolean IsDatabaseEmpty(){
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, "BreakdownRecords");
        db.close();

        Log.e("IsDatabaseEmpty","Number of rows = "+cnt);

        return cnt < 1;
    }

    public void addTrackPoint(String timestamp, String lat, String lon, String speed,
                              String accuracy, String altitude, String direction, String distance)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("timestamp",timestamp);  //TODO : Change to job no
        values.put("lat",lat);
        values.put("lon",lon);
        values.put("speed",speed);
        values.put("accuracy",accuracy);
        values.put("altitude",altitude);
        values.put("direction",direction);
        values.put("distance",distance);
        values.put("sync_done","0");

        db.insert( "GpsTracking", null,values); //TODO : Use insertOrThrow
        //db.close();
    }

    public List<MaterialObject> getMaterials(String breakdownId){
        SQLiteDatabase db = getWritableDatabase();
        List<MaterialObject> list = new LinkedList<MaterialObject>();
        String query = "SELECT * FROM Materials WHERE BREAKDOWN_ID='"+breakdownId+"';";//
        Cursor c = db.rawQuery(query, null);
        if(c==null)return list;
        if(c.getCount() < 1 )return list;
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                MaterialObject obj = new MaterialObject(
                        true,
                        c.getString(c.getColumnIndex("MATERIAL_CODE")),
                        "",
                        c.getInt(c.getColumnIndex("QUANTITY"))
                );
                list.add(obj);
            }
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public List<SyncMaterialObject> getNotSyncMaterials(){

        SyncMaterialObject obj=null;
        SQLiteDatabase db = getWritableDatabase();
        List<SyncMaterialObject> list = new LinkedList<SyncMaterialObject>();
        String query = "SELECT * FROM Materials WHERE SYNC_DONE='0' and length(BREAKDOWN_ID)=10 ;";//
        Cursor c = db.rawQuery(query, null);
        if(c==null)return list;
        if(c.getCount() < 1 )return list;
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                obj= new SyncMaterialObject();
                obj.BreakdownId=c.getString(c.getColumnIndex("BREAKDOWN_ID"));
                obj.MaterialId=c.getString(c.getColumnIndex("MATERIAL_CODE"));
                obj.Quantity=c.getString(c.getColumnIndex("QUANTITY"));
                obj.UserId=Globals.mToken.user_id;

                list.add(obj);
            }
            c.moveToNext();
        }
        c.close();
        return list;
    }


    public void addMaterials(String breakdownId, List<MaterialObject> list){
        SQLiteDatabase db = getWritableDatabase();

        for(MaterialObject obj : list){
            Log.e("addMaterials",obj.getCode()+"="+obj.getQuantity());
            String query = "INSERT OR REPLACE INTO Materials(BREAKDOWN_ID, MATERIAL_CODE, QUANTITY, SYNC_DONE) " +
                    "VALUES('"+breakdownId+"', '"+obj.getCode()+"',"+obj.getQuantity()+",'0');";//
            db.execSQL(query);
        }
    }


    public void UpdateMaterials(SyncMaterialObject obj)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE Materials SET SYNC_DONE='1' WHERE BREAKDOWN_ID='" +obj.BreakdownId + "' and MATERIAL_CODE ='"+obj.MaterialId+"';";
        db.execSQL(query);
        //db.close();
    }

    public List<TrackerObject> getNotSyncTrackingData(){
        TrackerObject obj=null;
        SQLiteDatabase db = getWritableDatabase();
        List<TrackerObject> list = new LinkedList<TrackerObject>();
        String query = "SELECT * FROM GpsTracking WHERE sync_done=0 ORDER BY id DESC limit 20 ;";//
        Cursor c = db.rawQuery(query, null);
        if(c==null)return list;
        if(c.getCount() < 1 )return list;
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                obj= new TrackerObject();
                obj.id=c.getString(c.getColumnIndex("id"));
                obj.UserId=Globals.mToken.user_id;
                obj.timestamp=c.getString(c.getColumnIndex("timestamp"));
                obj.lat=c.getString(c.getColumnIndex("lat"));
                obj.lon=c.getString(c.getColumnIndex("lon"));
                obj.speed=c.getString(c.getColumnIndex("speed"));
                obj.accuracy = c.getString(c.getColumnIndex("accuracy"));
                obj.altitude = c.getString(c.getColumnIndex("altitude"));
                obj.direction = c.getString(c.getColumnIndex("direction"));
                obj.distance = c.getString(c.getColumnIndex("distance"));
                list.add(obj);
            }
            c.moveToNext();
        }
        c.close();
        return list;
    }


    public List<BreakdownGroup> getNotSyncGroups(){
        BreakdownGroup obj=null;
        SQLiteDatabase db = getWritableDatabase();
        List<BreakdownGroup> list = new LinkedList<BreakdownGroup>();
        String query = "SELECT * FROM BreakdownRecords WHERE GROUP_SYNCED = 0 limit 10;";//
        Cursor c = db.rawQuery(query, null);
        if(c==null)return list;
        if(c.getCount() < 1 )return list;
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                obj= new BreakdownGroup();
                obj.SetBreakdownId(c.getString(c.getColumnIndex("JOB_NO")));
                obj.SetParentBreakdownId(c.getString(c.getColumnIndex("PARENT_BREAKDOWN_ID")));
                obj.SetParentStatusId(c.getString(c.getColumnIndex("STATUS")));
                list.add(obj);
            }
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public void UpdateUngroups(String parentId)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE BreakdownRecords SET PARENT_BREAKDOWN_ID='0000000000' WHERE JOB_NO='" +parentId + "';";
        db.execSQL(query);
        query = "UPDATE BreakdownRecords SET PARENT_BREAKDOWN_ID='0000000000' WHERE PARENT_BREAKDOWN_ID='" +parentId + "';";
        db.execSQL(query);
        //db.close();
    }

    public void UpdateTrackingDataByTimeStamp(String TimeStamp)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE GpsTracking SET sync_done='1' WHERE timestamp='" +TimeStamp + "';";
        db.execSQL(query);
        //db.close();
    }

    public void UpdateTrackingData(TrackerObject obj)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE GpsTracking SET sync_done='1' WHERE id='" +obj.id + "';";
        db.execSQL(query);
        //db.close();
    }

    public void UpdateGroupSynced(BreakdownGroup obj)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE BreakdownRecords SET GROUP_SYNCED='1' WHERE JOB_NO='" +obj.GetBreakdownId() + "';";
        db.execSQL(query);
    }
    public long addJobCompletionRec(JobCompletion jobcompletion_obj){

        List<String> family = getFamily(jobcompletion_obj.JOB_NO);
        SQLiteDatabase db = getWritableDatabase();
        long result = 0;
        for (String member:family) {
            ContentValues values = new ContentValues();
            values.put("JOB_NO",member);  //TODO : Change to job no
            values.put("STATUS",jobcompletion_obj.STATUS);
            values.put("job_completed_datetime",jobcompletion_obj.job_completed_datetime);
            values.put("comment",jobcompletion_obj.comment);
            values.put("detail_reason_code",jobcompletion_obj.detail_reason_code);
            values.put("cause",jobcompletion_obj.cause);
            values.put("type_failure",jobcompletion_obj.type_failure);
            values.put("job_completed_by",jobcompletion_obj.job_completed_by);
            values.put("action_code",jobcompletion_obj.action_code);
            values.put("synchro_mobile_db",jobcompletion_obj.synchro_mobile_db);
            values.put("device_timestamp",jobcompletion_obj.device_timestamp);
            result=db.insert( "JobCompletion", null,values); //TODO : Use insertOrThrow
        }


        return result;
        //db.close();
    }

    public JobCompletion getJobCompletionRec(String JOB_NO){
        SQLiteDatabase db = getWritableDatabase();
        JobCompletion obj= new JobCompletion();
        String query = "SELECT * FROM JobCompletion WHERE JOB_NO='"+JOB_NO+"';";//
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {

                obj.job_completed_datetime=c.getString(c.getColumnIndex("job_completed_datetime"));
                obj.type_failure=c.getString(c.getColumnIndex("type_failure"));
                obj.cause=c.getString(c.getColumnIndex("cause"));
                obj.detail_reason_code=c.getString(c.getColumnIndex("detail_reason_code"));
            }
            c.moveToNext();
        }
        c.close();
        return obj;
    }
    public int UpdateSyncState_NewBreakdown(Breakdown breakdown,int STATUS)
    {
        int iResult=-1;

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE BreakdownRecords SET BA_SERVER_SYNCED=" + STATUS +
                " WHERE JOB_NO= '" + breakdown.get_Job_No() + "';";
        db.execSQL(query);
        //db.close();

        iResult=1; //Return Success
        return iResult;
    }

    public int UpdateNewJobNumber(String oldJobNo, Breakdown breakdown)
    {
        try {
            Log.e("UpdateNewJobNumber", "0");
            SQLiteDatabase db = getWritableDatabase();
            String query = "UPDATE BreakdownRecords SET " +
                    "BA_SERVER_SYNCED='1', " +
                    "JOB_NO='" + breakdown.get_Job_No() + "', " +
                    "OLD_JOB_NO='" + oldJobNo + "', " +
                    "NAME='" + breakdown.get_Name() + "', " +
                    "ADDRESS='" + breakdown.get_ADDRESS() + "', " +
                    "LATITUDE='" + breakdown.getLatitude() + "', " +
                    "LONGITUDE='" + breakdown.getLongitude() + "', " +
                    "TARIFF_COD='" + breakdown.get_TARIFF_COD() + "', " +
                    "AREA='" + breakdown.get_AREA() + "', " +
                    "ECSC='" + breakdown.get_ECSC() + "', " +
                    "SUB='" + breakdown.get_SUB() + "', " +
                    "GPS_ACCURACY='" + breakdown.get_GPS_ACCURACY() + "'" +
                    " WHERE JOB_NO= '" + oldJobNo + "';";
            db.execSQL(query);

            Log.e("UpdateNewJobNumber", "1");
            query = "UPDATE JobStatusChange SET " +
                    "JOB_NO='" + breakdown.get_Job_No() + "' WHERE JOB_NO= '" + oldJobNo + "';";
            db.execSQL(query);


            Log.e("UpdateNewJobNumber", "2");
            query = "UPDATE JobCompletion SET " +
                    "JOB_NO='" + breakdown.get_Job_No() + "' WHERE JOB_NO= '" + oldJobNo + "';";
            db.execSQL(query);
            Log.e("UpdateNewJobNumber", "3");

            Log.e("UpdateNewJobNumber", "2");
            query = "UPDATE Materials SET " +
                    "BREAKDOWN_ID='" + breakdown.get_Job_No() + "' WHERE BREAKDOWN_ID= '" + oldJobNo + "';";
            db.execSQL(query);
            Log.e("UpdateNewJobNumber", "3");
            return 1;
        }catch(Exception e){
            return 0;
        }

    }


    public String GetNewJobNumber(String oldJobNo)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "select JOB_NO from BreakdownRecords WHERE OLD_JOB_NO= '" + oldJobNo + "';";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            String newJobNo = c.getString(0);
            Log.e("GetNewJobNumber","oldJobNo="+oldJobNo);
            Log.e("GetNewJobNumber","newJobNo="+newJobNo);
            //db.close();
            return newJobNo;
        }catch(Exception e){
            return oldJobNo;
        }

    }


    public List<Breakdown> getNewBreakdowns(){
        Breakdown breakdown=null;
        SQLiteDatabase db = getWritableDatabase();

        List<Breakdown> breakdowns = new LinkedList<Breakdown>();

        String query = "SELECT * FROM BreakdownRecords WHERE BA_SERVER_SYNCED<>1;";//

        Cursor c = db.rawQuery(query, null);
        if(c==null )return breakdowns;
        if(c.getCount() < 1 )return breakdowns;
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                breakdown= new Breakdown();
                //breakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                //breakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                //breakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                breakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
                breakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                //breakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                breakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime")));
                //breakdown.set_Completed_Time(c.getString(c.getColumnIndex("COMPLETED_TIME")));
                //breakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                breakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
                breakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
                breakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
                breakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
                //breakdown.set_PremisesID(c.getString(c.getColumnIndex("PREMISES_ID")));
                breakdowns.add(breakdown);
            }
            c.moveToNext();
        }
        c.close();
        return breakdowns;
    }


    public List<JobCompletion> getBreakdownCompletion()
{
    JobCompletion _jobcompletion_obj=null;
    SQLiteDatabase db = getWritableDatabase();

    List<JobCompletion> newJobCompletion = new LinkedList<JobCompletion>();

    String query = "SELECT JobCompletion.*, BreakdownRecords.AREA , BreakdownRecords.ECSC FROM JobCompletion " +
            " join BreakdownRecords on JobCompletion.JOB_NO = BreakdownRecords.JOB_NO " +
            " WHERE JobCompletion.synchro_mobile_db=0 and length(JobCompletion.JOB_NO)=10;";

    Cursor c = db.rawQuery(query, null);
    if(c==null)return newJobCompletion;
    if(c.getCount() < 1 )return newJobCompletion;
    c.moveToFirst();
    while (!c.isAfterLast())
    {
        if (c.getString(0) != null)
        {
            _jobcompletion_obj= new JobCompletion();
            _jobcompletion_obj.JOB_NO=c.getString(c.getColumnIndex("JOB_NO"));
            _jobcompletion_obj.STATUS=c.getString(c.getColumnIndex("STATUS"));
            _jobcompletion_obj.AreaId=c.getString(c.getColumnIndex("AREA"));
            _jobcompletion_obj.EcscId=c.getString(c.getColumnIndex("ECSC"));
            _jobcompletion_obj.job_completed_datetime=c.getString(c.getColumnIndex("job_completed_datetime"));
            _jobcompletion_obj.comment=c.getString(c.getColumnIndex("comment"));
            _jobcompletion_obj.detail_reason_code = c.getString(c.getColumnIndex("detail_reason_code"));
            _jobcompletion_obj.cause = c.getString(c.getColumnIndex("cause"));
            _jobcompletion_obj.type_failure = c.getString(c.getColumnIndex("type_failure"));
            _jobcompletion_obj.job_completed_by = c.getString(c.getColumnIndex("job_completed_by"));
            _jobcompletion_obj.action_code = c.getString(c.getColumnIndex("action_code"));
            _jobcompletion_obj.device_timestamp=c.getString(c.getColumnIndex("device_timestamp"));
            _jobcompletion_obj.synchro_mobile_db=c.getInt(c.getColumnIndex("synchro_mobile_db"));
            newJobCompletion.add(_jobcompletion_obj);
        }
        c.moveToNext();
    }
    c.close();
    return newJobCompletion;
}

    public List<String> getFamily(String parentId)
    {
        SQLiteDatabase db = getWritableDatabase();

        List<String> family = new LinkedList<String>();

        String query = "SELECT * FROM BreakdownRecords WHERE " +
                "PARENT_BREAKDOWN_ID ='"+parentId+"' or " +
                "JOB_NO ='"+parentId+"';";//

        Cursor c = db.rawQuery(query, null);
        if(c==null)return family;
        if(c.getCount() < 1 )return family;
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                family.add(c.getString(c.getColumnIndex("JOB_NO")));
            }
            c.moveToNext();
        }
        c.close();
        return family;
    }
    public long addJobStatusChangeRec(JobChangeStatus jobchangestatus_obj)
    {
        List<String> family = getFamily(jobchangestatus_obj.job_no);
        SQLiteDatabase db = getWritableDatabase();
        long result = 0;
        for (String member:family) {
            try{
                ContentValues values = new ContentValues();
                values.put("JOB_NO",member);  //TODO : Change to job no
                values.put("STATUS",jobchangestatus_obj.status);
                values.put("change_datetime",jobchangestatus_obj.change_datetime);
                values.put("comment",jobchangestatus_obj.comment);
                values.put("synchro_mobile_db",jobchangestatus_obj.synchro_mobile_db);
                values.put("device_timestamp",jobchangestatus_obj.device_timestamp);
                result = db.insert( "JobStatusChange", null,values); //TODO : Use insertOrThrow

            }catch(Exception e){
            }
        }
        return result;
        //db.close();
    }

    public int UpdateSyncStates(SyncObject obj,int iSynchro_mobile_dbValue)
    {
        int iResult=-1;
        Log.e("UpdateSyncStates","1");
       // Log.e("UpdateSyncStates","BreakdownId="+obj.BreakdownId);
       // Log.e("UpdateSyncStates","StatusTime="+obj.StatusTime);
       // Log.e("UpdateSyncStates","StatusId="+obj.StatusId);
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE JobStatusChange SET synchro_mobile_db=" + iSynchro_mobile_dbValue +
                " WHERE JOB_NO= '" + obj.BreakdownId + "' AND STATUS='"+ obj.StatusId + "' "+
                " AND change_datetime='" + obj.StatusTime +"';";
        db.execSQL(query);
        //db.close();
        Log.e("UpdateSyncStates","2");
        iResult=1; //Return Success
        return iResult;
    }
    public List<SyncObject> getBreakdownStatusChangeNew() {

        SQLiteDatabase db = getWritableDatabase();

        List<SyncObject> list = new LinkedList<SyncObject>();

        String query = "SELECT JobStatusChange.JOB_NO, JobStatusChange.STATUS,JobStatusChange.comment,JobStatusChange.change_datetime" +
                ", BreakdownRecords.AREA, BreakdownRecords.SUB , BreakdownRecords.ECSC, BreakdownRecords.TEAM, BreakdownRecords.DateTime, BreakdownRecords.PARENT_BREAKDOWN_ID " +
                " FROM JobStatusChange " +
                " join BreakdownRecords on JobStatusChange.JOB_NO = BreakdownRecords.JOB_NO " +
                "WHERE ( JobStatusChange.synchro_mobile_db = 0 or JobStatusChange.synchro_mobile_db = -1) and " +
                "length(JobStatusChange.JOB_NO)=10 and (PARENT_BREAKDOWN_ID == '0000000000' or PARENT_BREAKDOWN_ID == 'PARENT');";//

        Cursor c = db.rawQuery(query, null);
        if(c==null || c.getCount() < 1)return list;

        c.moveToFirst();
        while (!c.isAfterLast())
        {

            if (c.getString(0) != null)
            {
                SyncObject syncObject= new SyncObject();
                syncObject.BreakdownId=c.getString(c.getColumnIndex("JOB_NO"));
                syncObject.StatusId=c.getString(c.getColumnIndex("STATUS"));
                syncObject.AreaId=c.getString(c.getColumnIndex("AREA"));
                syncObject.EcscId=c.getString(c.getColumnIndex("ECSC"));
                syncObject.ReceivedTime=c.getString(c.getColumnIndex("DateTime"));
                syncObject.StatusTime = c.getString(c.getColumnIndex("change_datetime"));
                syncObject.Note=c.getString(c.getColumnIndex("comment"));
                syncObject.ParentBreakdownId=c.getString(c.getColumnIndex("PARENT_BREAKDOWN_ID"));
                syncObject.Sin=c.getString(c.getColumnIndex("SUB"));
                syncObject.TeamId= c.getString(c.getColumnIndex("TEAM"));
                syncObject.UserId = Globals.mToken.user_id;
                list.add(syncObject);
            }
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public int UpdateSyncState_JobCompletionObj(JobCompletion jobCompletion_obj,int iSynchro_mobile_dbValue)
    {
        int iResult=-1;

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE JobCompletion SET synchro_mobile_db=" + iSynchro_mobile_dbValue +
                " WHERE JOB_NO= '" + jobCompletion_obj.JOB_NO + "';";
        db.execSQL(query);
        //db.close();

        iResult=1; //Return Success
        return iResult;
    }


    public long InsertOrUpdateBreakdown(Breakdown breakdown){
        SQLiteDatabase db = getWritableDatabase();
        //Using Try Catch to suppress duplicate entries warnings, when Synching Inbox
        try{
            ContentValues values = new ContentValues();
            values.put("DateTime",breakdown.get_Received_Time());
            values.put("ACCT_NUM",breakdown.get_Acct_Num());
            values.put("PARENT_BREAKDOWN_ID",breakdown.get_ParentBreakdownId());
            values.put("DESCRIPTION",breakdown.get_Full_Description());
            values.put("SMS_INBOX_REFERENCE",breakdown.get_inbox_ref());

            values.put("NOTE",breakdown.get_Note());
            values.put("SUB",breakdown.get_SUB());
            values.put("TEAM",Globals.mToken.team_id);
            values.put("ECSC",breakdown.get_ECSC());
            values.put("AREA",breakdown.get_AREA());
            values.put("CONTACT_NO",breakdown.get_Contact_No());
            values.put("PRIORITY",breakdown.get_Priority());
            values.put("JOB_SOURCE",breakdown.get_JOB_SOURCE());
            values.put("NAME",breakdown.get_Name());
            values.put("TARIFF_COD",breakdown.get_TARIFF_COD());
            values.put("ADDRESS",breakdown.get_ADDRESS());
            values.put("LATITUDE",breakdown.getLatitude());
            values.put("LONGITUDE",breakdown.getLongitude());
            values.put("GPS_ACCURACY",breakdown.get_GPS_ACCURACY());
            values.put("BA_SERVER_SYNCED",breakdown.get_BA_SERVER_SYNCED());
            values.put("MobilePhNo",breakdown.MobilePhNo);
            values.put("LandPhNo",breakdown.LandPhNo);

            if(breakdown.get_Status() > Breakdown.JOB_DELIVERED){
                values.put("STATUS", breakdown.get_Status());
            }else{
                values.put("STATUS", Breakdown.JOB_DELIVERED);
            }

           Log.e("result","ECSC="+breakdown.get_ECSC());

            long result = db.update("BreakdownRecords", values, "JOB_NO='"+breakdown.get_Job_No()+"'", null);

            if(result < 1){
                values.put("JOB_NO",breakdown.get_Job_No());
                result =  db.insert("BreakdownRecords",null,values); //TODO: Use insertOrthrow
            }

            return result;
        }
        catch (Exception e)
        {
            Log.e("CalAttainedTime",e.getMessage());
            return 0;
        }
    }


    public void AddBreakdownGroups(List<BreakdownGroup> groups) {
        //Log.e("UPDATEQ","AddBreakdownGroups");
        SQLiteDatabase db = getWritableDatabase();
        for (BreakdownGroup group:groups) {
            //Log.e("TTTT0",group.GetParentBreakdownId()+"/"+group.GetParentStatusId()+"/"+group.GetBreakdownId());
            String query = "UPDATE BreakdownRecords SET " +
                    " PARENT_BREAKDOWN_ID='" + group.GetParentBreakdownId() + "',"+
                    " STATUS='" + group.GetParentStatusId() + "' "+
                    " WHERE JOB_NO= '" + group.GetBreakdownId() + "';";
            db.execSQL(query);
            //Log.e("TTTT0",group.GetParentBreakdownId()+"/"+group.GetParentStatusId()+"/"+group.GetBreakdownId());
        }
        String query = "UPDATE BreakdownRecords SET " +
                " PARENT_BREAKDOWN_ID='PARENT',"+
                " STATUS='" + groups.get(0).GetParentStatusId() + "' "+
                " WHERE JOB_NO= '" + groups.get(0).GetParentBreakdownId() + "';";
        db.execSQL(query);
    }


    public Statistics getBreakdownCounts()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) AS TOTAL," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_DELIVERED +"  THEN 1 ELSE 0 END) AS DELIVERED," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_ACKNOWLEDGED +" THEN 1 ELSE 0 END) AS ACKNOWLEDGED," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_VISITED +" THEN 1 ELSE 0 END) AS VISITED, " +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_ATTENDING +" THEN 1 ELSE 0 END) AS ATTENDING," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_TEMPORARY_COMPLETED +" THEN 1 ELSE 0 END) AS TEMPORARY_COMPLETED," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_COMPLETED +" THEN 1 ELSE 0 END) AS COMPLETED," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_WITHDRAWN +" THEN 1 ELSE 0 END) AS WITHDRAWN," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_REJECT +" THEN 1 ELSE 0 END) AS REJECT," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_RE_CALLED +" THEN 1 ELSE 0 END) AS RE_CALLED," +
                "SUM(CASE STATUS  WHEN "+Breakdown.JOB_RETURNED +" THEN 1 ELSE 0 END) AS RETURNED" +
                " FROM BreakdownRecords;";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Statistics stat = new Statistics();

        if (!c.isAfterLast()){
            stat.DELIVERED = c.getInt(c.getColumnIndex("DELIVERED"));
            stat.ACKNOWLEDGED = c.getInt(c.getColumnIndex("ACKNOWLEDGED"));
            stat.VISITED = c.getInt(c.getColumnIndex("VISITED"));
            stat.ATTENDING = c.getInt(c.getColumnIndex("ATTENDING"));
            stat.TEMPORARY_COMPLETED = c.getInt(c.getColumnIndex("TEMPORARY_COMPLETED"));
            stat.COMPLETED = c.getInt(c.getColumnIndex("COMPLETED"));
            stat.WITHDRAWN = c.getInt(c.getColumnIndex("WITHDRAWN"));
            stat.REJECT = c.getInt(c.getColumnIndex("REJECT"));
            stat.RE_CALLED = c.getInt(c.getColumnIndex("RE_CALLED"));
            stat.RETURNED = c.getInt(c.getColumnIndex("RETURNED"));
        }

        c.close();
        //db.close();
        return stat;
    }
    public long getAttendedTime()
    {
        String toDay = Globals.timeFormat.format(new Date()).split(" ")[0];
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COMPLETED_TIME AS Date2, DateTime AS Date1 FROM BreakdownRecords " +
                "where (STATUS = '5' OR STATUS = '6') and DateTime like '"+toDay+"%'";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor==null) return 0;
        if(cursor.getCount()<1) return 0;
        cursor.moveToFirst();

        long avgTime = 0;
        int i = 0;
        do{
            try{
                String date1 = cursor.getString(cursor.getColumnIndex("Date1"));
                String date2 = cursor.getString(cursor.getColumnIndex("Date2"));
                if(date1 != null & date2 != null){
                    i++;
                    avgTime = avgTime + GetTimeDifference(date1,date2);
                    Log.d("Duration",GetTimeDifference(date1,date2)+"");
                }
            }catch(Exception e){

            }

        }while(cursor.moveToNext());
        if(i==0)i=1;
        avgTime=avgTime/i;
        cursor.close();
        //db.close();
        return avgTime;
    }
    public static long GetTimeDifference(String day1, String day2) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
        long duration = 0;
        try {
            if(day1 == null | day2 == null) return duration;
            else if (day1.equals("") | day2.equals(""))return duration;
           // Log.e("day1","="+day1);
           // Log.e("day2","="+day2);
            Date d1 = Globals.timeFormat.parse(day1);
            Date d2 = Globals.timeFormat.parse(day2);
            //Log.d("TEST",day1+"-"+day2);
            duration = (d2.getTime() - d1.getTime()) / 1000 / 60 ; // In Minutes
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return duration;
    }

    public String[][] getBreakdownStatistics()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT substr(DateTime,0,11) AS DATE, COUNT(*) AS COUNT" +
                " FROM BreakdownRecords " +
                "GROUP BY substr(DateTime,0,11) " +
                "ORDER BY substr(DateTime,0,11)";

        Cursor cursor = db.rawQuery(query, null);
        String[][] counts = new String[2][cursor.getCount()];
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            int i =0;
            do{
                counts[0][i] = cursor.getString(cursor.getColumnIndex("DATE"));
                counts[1][i] = cursor.getString(cursor.getColumnIndex("COUNT"));
                i++;
            }while(cursor.moveToNext());

            cursor.close();
            //db.close();
        }
        return counts;
    }
    public void importGPSdata(String sDBPath)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete From Customers;");

        File dbfile = new File(sDBPath);//"/storage/emulated/0/GPS_HOMAGAMA AREA_P_08_08_2016.db"  ///storage/extSdCard/mydb.db
        SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        String query = "SELECT * FROM GPS_DATA;";
        Cursor cursor = db2.rawQuery(query, null);

        long i=0;

        cursor.moveToFirst();

        String sql = "INSERT INTO Customers (id, ACCT_NUM,NAME,ADDRESS,SUB,ECSC,TARIFF_COD,NO_OF_MET,LATITUDE,LONGITUDE,GPS_ACCURACY,WALK_ORDER) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);

        while (!cursor.isAfterLast())
        {
            if (cursor.getString(0) != null)
            {
                String LATITUDE = "0";
                String LONGITUDE = "0";
                String GPS_ACCURACY = "-1";
                if(!cursor.isNull(cursor.getColumnIndex("LATITUDE")))
                    LATITUDE=cursor.getString(cursor.getColumnIndex("LATITUDE"));
                if(!cursor.isNull(cursor.getColumnIndex("LONGITUDE")))
                    LONGITUDE=cursor.getString(cursor.getColumnIndex("LONGITUDE"));
                if(!cursor.isNull(cursor.getColumnIndex("LATITUDE")))
                    GPS_ACCURACY=cursor.getString(cursor.getColumnIndex("GPS_ACCURACY"));

                stmt.bindString(1, cursor.getString(cursor.getColumnIndex("_id")));
                stmt.bindString(2, cursor.getString(cursor.getColumnIndex("ACCT_NUM")));
                stmt.bindString(3, cursor.getString(cursor.getColumnIndex("NAME")));
                stmt.bindString(4, cursor.getString(cursor.getColumnIndex("ADDRESS")));
                stmt.bindString(5, cursor.getString(cursor.getColumnIndex("SUB")));
                stmt.bindString(6, cursor.getString(cursor.getColumnIndex("ECSC")));
                stmt.bindString(7, cursor.getString(cursor.getColumnIndex("TARIFF_COD")));
                stmt.bindString(8, cursor.getString(cursor.getColumnIndex("NO_OF_MET")));
                stmt.bindString(9, LATITUDE);
                stmt.bindString(10, LONGITUDE);
                stmt.bindString(11, GPS_ACCURACY);
                stmt.bindString(12, cursor.getString(cursor.getColumnIndex("WALK_ORDER")));

                stmt.execute();
                stmt.clearBindings();
                i++;
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }

    public void importPremisesID(String sDBPath)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete From PremisesID;");

        File dbfile = new File(sDBPath);
        SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        String query = "SELECT * FROM PREMISES_DATA;";
        Cursor cursor = db2.rawQuery(query, null);

        long i=0;

        cursor.moveToFirst();

        String sql = "INSERT INTO PremisesID (PremisesID, ACCT_NUM) " +
                " VALUES (?, ?);";
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);

        while (!cursor.isAfterLast())
        {
            if (cursor.getString(0) != null)
            {
                stmt.bindString(1, cursor.getString(cursor.getColumnIndex("PREMISES_ID")));
                stmt.bindString(2, cursor.getString(cursor.getColumnIndex("ACCT_NUM")));

                stmt.execute();
                stmt.clearBindings();
                i++;
            }
            cursor.moveToNext();
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        //db.close();
    }

    public Cursor ReadBreakdownsToCursor(int iStatus, Boolean GPS_Only, Boolean NoChildren )
    {
        SQLiteDatabase db = getWritableDatabase();
        //String query = "SELECT id as ID,NAME,LONGITUDE,LATITUDE FROM Customers limit 3;";
        Log.e("job STATUS","="+iStatus);
        //TODO : Add the breakdowns without location data or account number to the info icon

        String statusQuery ="";
        if (iStatus==-1){/*All*/
            statusQuery="";
        }else if (iStatus==Breakdown.JOB_COMPLETED){/*Completed*/
            statusQuery=" AND B.STATUS =  '6' ";
        }else if (iStatus==Breakdown.JOB_NOT_ATTENDED){/*"pending"*/
            statusQuery=" AND (B.STATUS = '1' OR B.STATUS = '2' OR B.STATUS = '4' OR B.STATUS IS NULL)";
        }else{
            statusQuery=" AND B.STATUS =  '" + iStatus + "' ";
        }

        if(NoChildren){
            statusQuery = statusQuery + " and ( PARENT_BREAKDOWN_ID = '0000000000' or PARENT_BREAKDOWN_ID = 'PARENT' or PARENT_BREAKDOWN_ID IS NULL )";
        }


        String gpsQuery ="";
        if (GPS_Only){
            gpsQuery=" AND B.GPS_ACCURACY <>  '-1' ";
        }

        String query = "SELECT B.id AS id ,B.NAME as NAME,B.LONGITUDE as LONGITUDE,B.TARIFF_COD as TARIFF_COD, " +
                " B.OLD_JOB_NO, B.LATITUDE as LATITUDE, B.STATUS as STATUS, " +
                " B.ACCT_NUM as ACCT_NUM,B.ADDRESS as ADDRESS, B.SUB,  " +
                " B.DESCRIPTION as DESCRIPTION, B.JOB_NO as JOB_NO, B.CONTACT_NO as  CONTACT_NO, B.PARENT_BREAKDOWN_ID,  " +
                " P.PremisesID as PremisesID , B.DateTime as DateTime1, B.COMPLETED_TIME as DateTime2, " +
                " B.PRIORITY as PRIORITY, B.NOTE " +
                " FROM BreakdownRecords B " +
        //            " LEFT JOIN Customers C ON C.ACCT_NUM = B.ACCT_NUM " +
                    " LEFT JOIN PremisesID P ON P.ACCT_NUM = B.ACCT_NUM " +
                " WHERE 1 " + statusQuery  + gpsQuery +
                " ORDER BY DateTime DESC, ACCT_NUM ASC;";


       // Log.e("query",query);
        Cursor c = db.rawQuery(query, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public List<Breakdown> ReadBreakdowns(int iStatus, Boolean GPS_Only, Boolean NoChildren)
    {
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        Cursor c = ReadBreakdownsToCursor(iStatus,GPS_Only,NoChildren);
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                Breakdown newBreakdown=new Breakdown();
                newBreakdown.set_id(c.getString(c.getColumnIndex("id")));
                newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                newBreakdown.set_ParentBreakdownId(c.getString(c.getColumnIndex("PARENT_BREAKDOWN_ID")));
                newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                newBreakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
                newBreakdown.set_SUB(c.getString(c.getColumnIndex("SUB")));
                newBreakdown.set_Note(c.getString(c.getColumnIndex("NOTE")));
                newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
                newBreakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
                newBreakdown.set_OldJob_No(c.getString(c.getColumnIndex("OLD_JOB_NO")));
                newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
                newBreakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
                newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
                BreakdownsList.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        //TODO : find a way to close the db ( db.close()) of the c cursor
        return BreakdownsList;
    }



    public List<Breakdown> GetChildBreakdowns(String parentBreakdownId)
    {
        SQLiteDatabase db = getWritableDatabase();
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        String query = "SELECT B.id AS id ,B.NAME as NAME,B.LONGITUDE as LONGITUDE,B.TARIFF_COD as TARIFF_COD, " +
                " B.OLD_JOB_NO, B.LATITUDE as LATITUDE, B.STATUS as STATUS, " +
                " B.ACCT_NUM as ACCT_NUM,B.ADDRESS as ADDRESS, B.SUB,  " +
                " B.DESCRIPTION as DESCRIPTION, B.JOB_NO as JOB_NO, B.CONTACT_NO as  CONTACT_NO, B.PARENT_BREAKDOWN_ID,  " +
                " P.PremisesID as PremisesID , B.DateTime as DateTime1, B.COMPLETED_TIME as DateTime2, " +
                " B.PRIORITY as PRIORITY " +
                " FROM BreakdownRecords B " +
                " LEFT JOIN PremisesID P ON P.ACCT_NUM = B.ACCT_NUM " +
                " WHERE PARENT_BREAKDOWN_ID = '"+parentBreakdownId+"' " +
                " ORDER BY DateTime DESC, ACCT_NUM ASC;";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                Breakdown newBreakdown=new Breakdown();
                newBreakdown.set_id(c.getString(c.getColumnIndex("id")));
                newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                newBreakdown.set_ParentBreakdownId(c.getString(c.getColumnIndex("PARENT_BREAKDOWN_ID")));
                newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                newBreakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
                newBreakdown.set_SUB(c.getString(c.getColumnIndex("SUB")));
                newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
                newBreakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
                newBreakdown.set_OldJob_No(c.getString(c.getColumnIndex("OLD_JOB_NO")));
                newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
                newBreakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
                newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
                BreakdownsList.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        //TODO : find a way to close the db ( db.close()) of the c cursor
        return BreakdownsList;
    }

    public List<Breakdown> ReadNotAckedBreakdowns()
    {
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT B.id AS id ,B.NAME as NAME,B.LONGITUDE as LONGITUDE,B.TARIFF_COD as TARIFF_COD," +
                " B.LATITUDE as LATITUDE, B.STATUS as STATUS, " +
                " B.ACCT_NUM as ACCT_NUM,B.ADDRESS as ADDRESS,   " +
                " B.DESCRIPTION as DESCRIPTION, B.JOB_NO as JOB_NO, B.CONTACT_NO as  CONTACT_NO,  " +
                " P.PremisesID as PremisesID , B.DateTime as DateTime1, B.COMPLETED_TIME as DateTime2, " +
                " B.PRIORITY as PRIORITY " +
                " FROM BreakdownRecords B " +
              //  " LEFT JOIN Customers C ON C.ACCT_NUM = B.ACCT_NUM " +
                " LEFT JOIN PremisesID P ON P.ACCT_NUM = B.ACCT_NUM " +
                " WHERE STATUS = '0' OR STATUS = '1'"  +
                " ORDER BY DateTime DESC, ACCT_NUM ASC;";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                Breakdown newBreakdown=new Breakdown();
                newBreakdown.set_id(c.getString(c.getColumnIndex("id")));
                newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                //newBreakdown.set_BREAKDOWN_ID(c.getString(c.getColumnIndex("BREAKDOWN_ID")));
                newBreakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
                newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
                newBreakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
                newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
                newBreakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
                newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
                BreakdownsList.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        //TODO : find a way to close the db ( db.close()) of the c cursor
        return BreakdownsList;
    }


    //TODO : Add using customer object
    public List<Breakdown> ReadAllCustomers()
    {
        List<Breakdown> Breakdownslist = new LinkedList<Breakdown>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT id as ID,ACCT_NUM as ACCT_NUM,NAME,ADDRESS,LONGITUDE,LATITUDE " +
                "FROM Customers limit 1000 ;";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                Breakdown newBreakdown=new Breakdown();
                newBreakdown.set_id(c.getString(c.getColumnIndex("ID")));
                newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                newBreakdown.set_Status(0);
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description("No DESCRIPTION");
                Breakdownslist.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        //db.close();
        return Breakdownslist;
    }

    public String getBreakdown_ID(String sACCT_NUM)
    {
        String sBreakdownID=null;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT B.id AS id " +
                " FROM BreakdownRecords B,Customers C  WHERE B.ACCT_NUM='" + sACCT_NUM + "' " +
                " AND C.ACCT_NUM = B.ACCT_NUM " +
                ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (!c.isAfterLast() & c.getString(0) != null)
        {
            sBreakdownID=c.getString(c.getColumnIndex("id"));
        }
        c.close();
        //db.close();
        return sBreakdownID;
    }
    public Breakdown ReadBreakdown_by_JonNo(String JOB_NO){
        Breakdown newBreakdown=null;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT B.id AS id ,B.NAME as NAME,B.LONGITUDE as LONGITUDE," +
                " B.LATITUDE as LATITUDE, B.STATUS as STATUS, " +
                " B.ACCT_NUM as ACCT_NUM,B.ADDRESS as ADDRESS,   " +
                " B.DESCRIPTION as DESCRIPTION, B.JOB_NO as JOB_NO,B.SUB as SUB, B.CONTACT_NO as  CONTACT_NO,  " +
                " P.PremisesID as PremisesID , B.DateTime as DateTime1, B.COMPLETED_TIME as DateTime2, " +
                " B.PRIORITY as PRIORITY " +
                " FROM BreakdownRecords B " +
                //    " LEFT JOIN Customers C ON C.ACCT_NUM = B.ACCT_NUM " +
                " LEFT JOIN PremisesID P ON P.ACCT_NUM = B.ACCT_NUM " +
                " WHERE B.JOB_NO = '" + JOB_NO + "';";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if (!c.isAfterLast() && c.getString(0) != null) //AND and AND only && not &
        {
            newBreakdown=new Breakdown();
            newBreakdown.set_id(c.getString(c.getColumnIndex("id")));
            newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
            newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
            newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
            newBreakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
            newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
            newBreakdown.set_SUB(c.getString(c.getColumnIndex("SUB")));
            newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
            newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
            newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
            newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
            newBreakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
            newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
            newBreakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
            newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
        }
        c.close();
        return newBreakdown;
    }

    public Breakdown ReadBreakdown_by_ID(String sID){
        Breakdown newBreakdown=null;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT B.id AS id ,B.NAME as NAME,B.LONGITUDE as LONGITUDE," +
                " B.LATITUDE as LATITUDE, B.STATUS as STATUS, " +
                " B.ACCT_NUM as ACCT_NUM,B.ADDRESS as ADDRESS,   " +
                " B.DESCRIPTION as DESCRIPTION, B.JOB_NO as JOB_NO,B.SUB as SUB, B.CONTACT_NO as  CONTACT_NO,  " +
                " P.PremisesID as PremisesID , B.DateTime as DateTime1, B.COMPLETED_TIME as DateTime2, " +
                " B.PRIORITY as PRIORITY " +
                " FROM BreakdownRecords B " +
            //    " LEFT JOIN Customers C ON C.ACCT_NUM = B.ACCT_NUM " +
                " LEFT JOIN PremisesID P ON P.ACCT_NUM = B.ACCT_NUM " +
                " WHERE B.id = '" + sID + "';";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if (!c.isAfterLast() && c.getString(0) != null) //AND and AND only && not &
        {
            Log.e("SUB",c.getString(c.getColumnIndex("SUB")));
            newBreakdown=new Breakdown();
            newBreakdown.set_id(c.getString(c.getColumnIndex("id")));
            newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
            newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
            newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
            newBreakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
            newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
            newBreakdown.set_SUB(c.getString(c.getColumnIndex("SUB")));
            newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
            newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
            newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
            newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
            newBreakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
            newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
            newBreakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
            newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
        }
        c.close();
        return newBreakdown;
    }
    public Breakdown ReadBreakdown_by_ACCT_NUM(String sACCT_NUM)
    {
        return ReadBreakdown_by_ID(getBreakdown_ID(sACCT_NUM));
    }

    public List<Breakdown> SearchInDatabase(String word) {
        List<Breakdown> part1 = SearchInBreakdowns(word);
    //    List<Breakdown> part2 = SearchInCustomers2(word);
       // part1.addAll(part2);
        return part1;
    }

    private List<Breakdown> SearchInBreakdowns(String word){
        String WORD = "%" + word.trim().toUpperCase() + "%";

        String query = "SELECT B.id AS id ,B.NAME as NAME,B.LONGITUDE as LONGITUDE,B.TARIFF_COD as TARIFF_COD," +
                " B.LATITUDE as LATITUDE, B.STATUS as STATUS, " +
                " B.ACCT_NUM as ACCT_NUM,B.ADDRESS as ADDRESS,   " +
                " B.DESCRIPTION as DESCRIPTION, B.JOB_NO as JOB_NO, B.CONTACT_NO as  CONTACT_NO,  " +
                " P.PremisesID as PremisesID , B.DateTime as DateTime1, B.COMPLETED_TIME as DateTime2, " +
                " B.PRIORITY as PRIORITY " +
                " FROM BreakdownRecords B " +
           //     " LEFT JOIN Customers C ON C.ACCT_NUM = B.ACCT_NUM " +
                " LEFT JOIN PremisesID P ON P.ACCT_NUM = B.ACCT_NUM " +
                " WHERE " +
                "B.ACCT_NUM LIKE'" + WORD +"' OR " +
                "B.NAME LIKE'" + WORD +"' OR " +
                "B.ADDRESS LIKE'" + WORD +"'"+
                " ORDER BY DateTime DESC;";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    Breakdown newBreakdown = new Breakdown();
                    newBreakdown.set_id(c.getString(c.getColumnIndex("id")));
                    newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                    newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                    newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                    newBreakdown.set_Status(c.getShort(c.getColumnIndex("STATUS")));
                    newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                    newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                    newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
                    newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
                    newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                    newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("DESCRIPTION")));
                    newBreakdown.set_Job_No(c.getString(c.getColumnIndex("JOB_NO")));
                    newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("CONTACT_NO")));
                    newBreakdown.set_Priority(c.getInt(c.getColumnIndex("PRIORITY")));
                    newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
                    BreakdownsList.add(newBreakdown);
                } while (c.moveToNext());
            }
            c.close();
        }
        return BreakdownsList;
    }

    //TODO : Add using customer object
    public Breakdown ReadCustomer_by_ACCT_NUM(String sACCT_NUM)
    {
        Breakdown newBreakdown=null;
        //TODO : Fix Exception
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT id as ID,ACCT_NUM as ACCT_NUM,NAME,TARIFF_COD, ADDRESS,LONGITUDE,LATITUDE " +
                "FROM Customers WHERE ACCT_NUM='" + sACCT_NUM +"';";

        Cursor c = db.rawQuery(query, null);

        try{
            c.moveToFirst();
            if (!c.isAfterLast() && c.getString(0) != null)
            {
                newBreakdown=new Breakdown();
                newBreakdown.set_id(c.getString(c.getColumnIndex("ID")));
                newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                newBreakdown.set_Status(0);
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description("No DESCRIPTION");
            }
        }catch (Exception ex){
            newBreakdown=null;
        }
        finally {
            c.close();
            //db.close();
        }
        return newBreakdown;
    }


    public int UpdateBreakdownStatus(Breakdown breakdown,int Breakdown_Status)
    {
        Log.e("UPDATEQ","UpdateBreakdownStatus");
        int iResult=-1;
        List<String> family = getFamily(breakdown.get_Job_No());
        SQLiteDatabase db = getWritableDatabase();
        String time = Globals.timeFormat.format(System.currentTimeMillis());
        for (String member:family) {
            String query = "UPDATE BreakdownRecords SET STATUS='" + String.valueOf(Breakdown_Status) +"', " +
                    "COMPLETED_TIME= '" +  time + "', " +
                    "TEAM= '" +  breakdown.get_TeamId() + "', " +
                    "SUB= '" +  breakdown.get_SUB() + "' " +
                    " WHERE JOB_NO='" +member + "';";

            db.execSQL(query);
        }
        //db.close();

        iResult=1; //Return Success
        return iResult;
    }

    private List<String> GetMegaFamily(List<String> family, String extraId){
        family.add(extraId);
        List<String> megaFamily =  new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT JOB_NO FROM BreakdownRecords WHERE " +
                " JOB_NO = '" + TextUtils.join("' or  JOB_NO = '", family ) +"' " +
                " or PARENT_BREAKDOWN_ID = '" + TextUtils.join("' or  PARENT_BREAKDOWN_ID = '", family )+ "';";

        Log.e("query",query);
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            if (c.getCount()>0) {
                c.moveToFirst();
                do {
                    megaFamily.add(c.getString(c.getColumnIndex("JOB_NO")));
                    Log.e("megaFamily",c.getString(c.getColumnIndex("JOB_NO")));
                } while (c.moveToNext());
            }
            c.close();
        }
        return megaFamily;
    }

    private BreakdownGroup FindForParent(List<String> children, BreakdownGroup parent){
        SQLiteDatabase db = getWritableDatabase();
        BreakdownGroup defaultParent = new BreakdownGroup();
        defaultParent.SetBreakdownId(parent.GetBreakdownId());
        defaultParent.SetParentBreakdownId(parent.GetParentBreakdownId());
        defaultParent.SetParentStatusId(parent.GetParentStatusId());

        String query = "SELECT JOB_NO , PARENT_BREAKDOWN_ID, STATUS FROM BreakdownRecords WHERE PARENT_BREAKDOWN_ID ='PARENT' " +
                "and ( JOB_NO = '" + TextUtils.join("' or  JOB_NO = '", children ) +"');";

        Log.e("query",query);
        Cursor c = db.rawQuery(query, null);

        try{
            c.moveToFirst();
            if (!c.isAfterLast() && c.getString(0) != null)
            {
                Log.e("TEST125",c.getString(c.getColumnIndex("JOB_NO")));
                defaultParent.SetBreakdownId(c.getString(c.getColumnIndex("JOB_NO")));
                defaultParent.SetParentBreakdownId(c.getString(c.getColumnIndex("PARENT_BREAKDOWN_ID")));
                defaultParent.SetParentStatusId(c.getString(c.getColumnIndex("STATUS")));
            }
        }catch (Exception ex){
            Log.e("FindForParent",ex.getMessage());
        }
        return defaultParent;
    }
    private BreakdownGroup GetBreakdownGroup(String parentId){
        SQLiteDatabase db = getWritableDatabase();
        BreakdownGroup parent = new BreakdownGroup();

        String query = "SELECT JOB_NO , PARENT_BREAKDOWN_ID, STATUS FROM BreakdownRecords WHERE JOB_NO ='"+parentId+"';";

        Log.e("query",query);
        Cursor c = db.rawQuery(query, null);

        try{
            c.moveToFirst();
            if (!c.isAfterLast() && c.getString(0) != null)
            {
                Log.e("TEST125",c.getString(c.getColumnIndex("JOB_NO")));
                parent.SetBreakdownId(c.getString(c.getColumnIndex("JOB_NO")));
                parent.SetParentBreakdownId(c.getString(c.getColumnIndex("PARENT_BREAKDOWN_ID")));
                parent.SetParentStatusId(c.getString(c.getColumnIndex("STATUS")));
            }
        }catch (Exception ex){
            Log.e("FindForParent",ex.getMessage());
        }
        return parent;
    }

    public int UpdateChildren(List<String> children,String parentId)
    {Log.e("UPDATEQ","UpdateChildren");
        children = GetMegaFamily(children,parentId);

        BreakdownGroup parent = GetBreakdownGroup(parentId);
        //Log.e("TEST1","="+parent.GetBreakdownId());
        //Log.e("TEST2","="+parent.GetParentStatusId());
        BreakdownGroup _parent = FindForParent(children, parent);

        if(!_parent.GetBreakdownId().equals(parent.GetBreakdownId())){
            //Log.e("TEST11","=");
            children.add(parent.GetBreakdownId());
            children.remove(_parent.GetBreakdownId());

            parent.SetBreakdownId(_parent.GetBreakdownId());
            parent.SetParentStatusId(_parent.GetParentStatusId());
        }

        int iResult=-1;
        //SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/d h:m:s a");
        String time = Globals.timeFormat.format(System.currentTimeMillis());
        //Log.e("RRRRRRRR",breakdown.get_id() +","+time+","+Breakdown_Status);
        SQLiteDatabase db = getWritableDatabase();
        for (String child:children) {
            //Log.e("TEST7","="+child);
            String query = "UPDATE BreakdownRecords SET " +
                    "PARENT_BREAKDOWN_ID ='" + parent.GetBreakdownId() +"', " +
                    "STATUS ='" + parent.GetParentStatusId() +"', " +
                    "GROUP_SYNCED= '0' " +
                    " WHERE JOB_NO ='" +child + "';";
            db.execSQL(query);
        }
        String query = "UPDATE BreakdownRecords SET " +
                "PARENT_BREAKDOWN_ID ='PARENT', " +
                "STATUS ='" + parent.GetParentStatusId() +"', " +
                "GROUP_SYNCED= '0' " +
                " WHERE JOB_NO='" +parent.GetBreakdownId() + "';";
        db.execSQL(query);

        //db.close();

        iResult=1; //Return Success
        return iResult;
    }

    public int UpdateBreakdownStatusByJobNo(String jobNo,String time, String Breakdown_Status)
    {
        Log.e("UPDATEQ","UpdateBreakdownStatusByJobNo");
        int iResult=-1;
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE BreakdownRecords SET STATUS='" + Breakdown_Status + "', COMPLETED_TIME= '" +  time + "' " +
                " WHERE (JOB_NO='" +jobNo + "' or OLD_JOB_NO='" +jobNo + "') and CAST(STATUS as INTEGER) < " + Breakdown_Status + " ;";

        db.execSQL(query);
        Log.e("GetBreakdownsStatus","jobStatus.STATUS:"+Breakdown_Status);
        iResult=1; //Return Success*/
        return iResult;
    }

    public int UpdateEditBreakdownByJobNo(Breakdown breakdown)
    {
        /*if(breakdown.get_Contact_No()== null)breakdown.set_Contact_No("");
        if(breakdown.get_Note()== null)breakdown.set_Note("");

        Log.e("UPDATEQ","UpdateEditBreakdown");
        int iResult=-1;
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE BreakdownRecords SET " +
                "NAME='" + breakdown.get_Name() + "'," +
                "ADDRESS='" + breakdown.get_ADDRESS() + "'," +
                "DESCRIPTION='" + breakdown.get_Full_Description() + "'," +
                "CONTACT_NO='" + breakdown.get_Contact_No() + "'," +
                "NOTE='" + breakdown.get_Note() + "'," +
              // "ECSC='" + breakdown.get_ECSC() + "'," +
              // " AREA= '" +  breakdown.get_AREA() + "' " +
                "PRIORITY='" + breakdown.get_Priority() + "' " +
                " WHERE JOB_NO='" +breakdown.get_Job_No() + "';";
        Log.e("UpdateEditBreakdown","NOTE="+breakdown.get_Note());
        db.execSQL(query);
        //Log.e("GetBreakdownsStatus","jobStatus.STATUS:"+Breakdown_Status);
        iResult=1; //Return Success*/

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("NAME",breakdown.get_Name());
        cv.put("ADDRESS",breakdown.get_ADDRESS());
        cv.put("DESCRIPTION",breakdown.get_Full_Description());
        if(breakdown.get_Contact_No()!= null)cv.put("CONTACT_NO",breakdown.get_Contact_No());
        if(breakdown.get_Note()!= null)cv.put("NOTE",breakdown.get_Note());
        cv.put("PRIORITY",breakdown.get_Priority());

        db.update("BreakdownRecords", cv, "JOB_NO="+breakdown.get_Job_No(), null);

        return 1;
    }


}