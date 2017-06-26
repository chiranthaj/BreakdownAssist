package lk.steps.breakdownassist;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper
{
    private static final int Database_Version =55;
    private static final String Database_Name = "BreakdownAssist.db";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, Database_Name, factory, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query;
        query = "CREATE TABLE `BreakdownRecords` (" +
                "`_id`	                INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`DateTime`	            TEXT," +
                "`_Acct_Num`	        TEXT," +
                "`_Status`	            TEXT," +
                "`_Job_Num`	            TEXT," +  //TODO : Change to Job_no
                "`_Contact_Num`	        TEXT," +
                "`_JOB_Source`	        TEXT," +
                "`_Description`	        TEXT, " +
                "`inbox_ref`	        TEXT UNIQUE, " +
                "`last_timestamp`       TEXT, " +
                "`completed_timestamp`  TEXT " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE `Customers` (" +
                "`_id`          INTEGER PRIMARY KEY," +
                "`ACCT_NUM`     TEXT," +
                "`WALK_ORDER`   TEXT," +
                "`NAME`         TEXT,"+
                "`ADDRESS`      TEXT," +
                "`SUB`          TEXT," +
                "`ECSC`         TEXT," +
                "`TARIFF_COD`   TEXT," +
                "`NO_OF_MET`    TEXT,"+
                "`LATITUDE`     TEXT,"+
                "`LONGITUDE`    TEXT,"+
                "`GPS_ACCURACY` TEXT "+
                ");";
        db.execSQL(query);
        query = "CREATE TABLE `PremisesID` (" +
                "`PremisesID` TEXT PRIMARY KEY," +
                "`ACCT_NUM` TEXT " +
                ");";
        db.execSQL(query);

        query = "CREATE TABLE JobStatusChange (" +
                "Job_Num            TEXT," +
                "st_code            TEXT," +
                "change_datetime    TEXT," +
                "comment            TEXT," +
                "device_timestamp   TEXT," +
                "synchro_mobile_db  TEXT,"  +
                "PRIMARY KEY (Job_Num,st_code,change_datetime)" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS `BreakdownRecords`");
        db.execSQL("DROP TABLE IF EXISTS `Customers`");
        db.execSQL("DROP TABLE IF EXISTS `PremisesID`");
        db.execSQL("DROP TABLE IF EXISTS `JobStatusChange`");
        onCreate(db);
    }

    public void addJobStatusChangeRec(String Job_No,String st_code,String change_datetime, String comment)
    {
        SQLiteDatabase db = getWritableDatabase();

        Date callDayTime = new Date( System.currentTimeMillis());
        String time = Globals.timeFormat.format(callDayTime);

        ContentValues values = new ContentValues();
        values.put("Job_Num",Job_No);  //TODO : Change to job no
        values.put("st_code",st_code);
        values.put("change_datetime",change_datetime);
        values.put("comment",comment);
        values.put("synchro_mobile_db","0");
        values.put("device_timestamp",time);
        db.insertOrThrow( "JobStatusChange", null,values);

        db.close();

    }

    public void addJobStatusChangeObj(JobChangeStatus jobchangestatus_obj)
    {
        addJobStatusChangeRec(
                jobchangestatus_obj.job_no,
                jobchangestatus_obj.st_code,
                jobchangestatus_obj.change_datetime,
                jobchangestatus_obj.comment
                );
    }

    public List<JobChangeStatus> getJobStatusChangeObjNotSync_List()
    {
        JobChangeStatus _jobchangestatus_obj=null;
        SQLiteDatabase db = getWritableDatabase();

        List<JobChangeStatus> newJobChangeStatus = new LinkedList<JobChangeStatus>();

        String query = "SELECT * " +
                " FROM JobStatusChange " +
                " WHERE synchro_mobile_db=0;";//

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                _jobchangestatus_obj= new JobChangeStatus();
                _jobchangestatus_obj.job_no=c.getString(c.getColumnIndex("Job_Num"));
                _jobchangestatus_obj.st_code=c.getString(c.getColumnIndex("st_code"));
                _jobchangestatus_obj.change_datetime=c.getString(c.getColumnIndex("change_datetime"));
                _jobchangestatus_obj.comment=c.getString(c.getColumnIndex("comment"));
                _jobchangestatus_obj.device_timestamp=c.getString(c.getColumnIndex("device_timestamp"));
                _jobchangestatus_obj.synchro_mobile_db=c.getInt(c.getColumnIndex("synchro_mobile_db"));
                newJobChangeStatus.add(_jobchangestatus_obj);
            }
            c.moveToNext();
        }

        return newJobChangeStatus;
    }

     public int UpdateSyncState_JobStatusChangeObj(JobChangeStatus jobchangestatus_obj,int iSynchro_mobile_dbValue)
    {
        int iResult=-1;

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE JobStatusChange SET synchro_mobile_db=" + iSynchro_mobile_dbValue +
                " WHERE Job_Num= '" + jobchangestatus_obj.job_no + "' AND st_code='"+ jobchangestatus_obj.st_code + "' "+
                " AND change_datetime='" + jobchangestatus_obj.change_datetime +"';";
        db.execSQL(query);
        db.close();

        iResult=1; //Return Success
        return iResult;
    }
    public JobChangeStatus getJobStatusChangeObj(String job_no,String st_code, String change_datetime)
    {
        JobChangeStatus _jobchangestatus_obj=null;
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * " +
                " FROM JobStatusChange " +
                " WHERE job_no= '" + job_no + "' AND st_code='"+ st_code + "' "+
                " AND change_datetime='" + change_datetime +"';";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if (!c.isAfterLast() && c.getString(0) != null) //AND and AND only && not &
        {
            _jobchangestatus_obj= new JobChangeStatus();
            _jobchangestatus_obj.job_no=c.getString(c.getColumnIndex("job_no"));
            _jobchangestatus_obj.st_code=c.getString(c.getColumnIndex("st_code"));
            _jobchangestatus_obj.change_datetime=c.getString(c.getColumnIndex("change_datetime"));
            _jobchangestatus_obj.comment=c.getString(c.getColumnIndex("comment"));
            _jobchangestatus_obj.device_timestamp=c.getString(c.getColumnIndex("device_timestamp"));
            _jobchangestatus_obj.synchro_mobile_db=c.getInt(c.getColumnIndex("synchro_mobile_db"));
        }
        c.close();

        return _jobchangestatus_obj;
    }

    public void addBreakdown(String id,String ReceiveDateTime,String Acct_Num,String Description,String Job_No,String Phone_No, String JOB_Source)
    {
        SQLiteDatabase db = getWritableDatabase();
        //Using Try Catch to suppress duplicate entries warnings, when Synching Inbox
        try{

            ContentValues values = new ContentValues();
            values.put("DateTime",ReceiveDateTime);
            values.put("_Acct_Num",Acct_Num);
            values.put("_Description",Description);
            values.put("_Job_Num",Job_No);
            values.put("_Contact_Num",Phone_No);
            values.put("_JOB_Source",JOB_Source);
            values.put("_Status",0);
            values.put("inbox_ref", id + " " +ReceiveDateTime); //TODO:No Exception will occur in db.insert for duplicate entries, they will be just omitted

            db.insert("BreakdownRecords",null,values); //TODO: Use insertOrthrow
        }
        catch (Exception e)
        {

        }
        finally {
            db.close();
        }

    }
    public int AddTestBreakdownObj(Breakdown breakdown,Context context)
    {
        int iResult=-1;
        Date callDayTime = new Date( System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
        String time = timeFormat.format(callDayTime);
        String sNextID =ReadSMS.getNextID(context);
        String sAcct_num=breakdown.get_Acct_Num();

        addBreakdown("T " + sNextID,time,sAcct_num,"No Supply to the house","J99/Z/2999/12/12/99.9","0123456789","CEB_Test");

        iResult=1; //Return Success
        return iResult;
    }
    public int[] getBreakdownCounts()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) AS TOTAL," +
                        "SUM(CASE _Status  WHEN "+Breakdown.Status_JOB_COMPLETED +" THEN 1 ELSE 0 END) AS COMPLETED," +
                        "SUM(CASE _Status  WHEN "+Breakdown.Status_JOB_NOT_ATTENDED + " THEN 1 ELSE 0 END) AS UNATTAINED" +
                        " FROM BreakdownRecords;";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        int[] counts = new int[2];
        if (!c.isAfterLast()){
            counts[0] = c.getInt(c.getColumnIndex("COMPLETED"));
            counts[1] = c.getInt(c.getColumnIndex("UNATTAINED"));
        }
        c.close();
        db.close();
        return counts;
    }
    public long getAttainedTime()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT completed_timestamp AS Date2 ,DateTime AS Date1 FROM BreakdownRecords";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        long avgTime = 0;
        do{
            String date1 = cursor.getString(cursor.getColumnIndex("Date1"));
            String date2 = cursor.getString(cursor.getColumnIndex("Date2"));
            if(date1 != null & date2 != null){
                avgTime = (avgTime + GetTimeDifference(date1,date2)) / 2;
                //Log.d("Duration",GetTimeDifference(date1,date2)+"");
            }
        }while(cursor.moveToNext());

        cursor.close();
        db.close();
        return avgTime;
    }
    public static long GetTimeDifference(String day1, String day2) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);

        long duration = 0;
        try {
            Date d1 = dateFormat.parse(day1);
            Date d2 = dateFormat.parse(day2);

            duration = (d2.getTime() - d1.getTime()) / 1000 / 60 ; // In Minutes
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return duration;
    }
    /*public void getAttainedTime()
    {
        Log.d("TEST","Start");
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT completed_timestamp AS Date2 ,DateTime AS Date1 FROM BreakdownRecords";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        //int[] counts = new int[2];
        if (!c.isAfterLast()){
            //counts[0] = c.getInt(c.getColumnIndex("Duration"));
            Log.d("Date1",c.getString(c.getColumnIndex("Date1"))+"");
            Log.d("Date2",c.getString(c.getColumnIndex("Date2"))+"");
        }
        c.close();
        db.close();
        // return counts;
    }*/
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
            db.close();
        }
        return counts;
    }
    public void importGPSdata(String sDBPath)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete From `Customers`;");

        File dbfile = new File(sDBPath);//"/storage/emulated/0/GPS_HOMAGAMA AREA_P_08_08_2016.db"  ///storage/extSdCard/mydb.db
        SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        String query = "SELECT * FROM `GPS_DATA`;";
        Cursor cursor = db2.rawQuery(query, null);

        long i=0;

        cursor.moveToFirst();

        String sql = "INSERT INTO Customers (_id, ACCT_NUM,NAME,ADDRESS,SUB,ECSC,TARIFF_COD,NO_OF_MET,LATITUDE,LONGITUDE,GPS_ACCURACY,WALK_ORDER) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);

        while (!cursor.isAfterLast())
        {
            if (cursor.getString(0) != null)
            {
                stmt.bindString(1, cursor.getString(cursor.getColumnIndex("_id")));
                stmt.bindString(2, cursor.getString(cursor.getColumnIndex("ACCT_NUM")));
                stmt.bindString(3, cursor.getString(cursor.getColumnIndex("NAME")));
                stmt.bindString(4, cursor.getString(cursor.getColumnIndex("ADDRESS")));
                stmt.bindString(5, cursor.getString(cursor.getColumnIndex("SUB")));
                stmt.bindString(6, cursor.getString(cursor.getColumnIndex("ECSC")));
                stmt.bindString(7, cursor.getString(cursor.getColumnIndex("TARIFF_COD")));
                stmt.bindString(8, cursor.getString(cursor.getColumnIndex("NO_OF_MET")));
                stmt.bindString(9, cursor.getString(cursor.getColumnIndex("LATITUDE")));
                stmt.bindString(10, cursor.getString(cursor.getColumnIndex("LONGITUDE")));
                stmt.bindString(11, cursor.getString(cursor.getColumnIndex("GPS_ACCURACY")));
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
        db.close();
    }

    public void importPremisesID(String sDBPath)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete From `PremisesID`;");

        File dbfile = new File(sDBPath);
        SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        String query = "SELECT * FROM `PREMISES_DATA`;";
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

        db.close();
    }

    public Cursor ReadBreakdownsToCursor(int iStatus)
    {
        SQLiteDatabase db = getWritableDatabase();
        //String query = "SELECT `_id` as ID,`NAME`,`LONGITUDE`,`LATITUDE` FROM `Customers` limit 3;";

        //TODO : Add the breakdowns without location data or account number to the info icon

        String statusQuery ="";
        if (iStatus==-1){/*All*/
            statusQuery="";
        }else if (iStatus==Breakdown.Status_JOB_COMPLETED){/*Completed*/
            statusQuery=" AND `B`.`_Status` =  '1' ";
        }else if (iStatus==Breakdown.Status_JOB_NOT_ATTENDED){/*"pending"*/
            statusQuery=" AND (`B`.`_Status` <>  '1' OR  `B`.`_Status` IS NULL)";
        }else{
            statusQuery=" AND `B`.`_Status` =  '" + iStatus + "' ";
        }

        String query = "SELECT `B`.`_id` AS `_id` ,`C`.`NAME` as `NAME`,C.`LONGITUDE` as `LONGITUDE`,C.`TARIFF_COD` as `TARIFF_COD`," +
                " C.`LATITUDE` as `LATITUDE`, `B`.`_Status` as `Status`, " +
                " `B`.`_Acct_Num` as `_Acct_Num`,`C`.`ADDRESS` as `ADDRESS`,   " +
                " `B`.`_Description` as `Description`, `B`.`_Job_Num` as `_Job_Num`, `B`.`_Contact_Num` as  `_Contact_Num`,  " +
                " `P`.`PremisesID` as `PremisesID` , `B`.`DateTime` as `DateTime1`, `B`.`completed_timestamp` as `DateTime2` " +
                " FROM `BreakdownRecords` `B` " +
                    " LEFT JOIN `Customers` `C` ON `C`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                    " LEFT JOIN `PremisesID` `P` ON `P`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                " WHERE 1 " + statusQuery  +
                " ORDER BY DateTime DESC;";

        Cursor c = db.rawQuery(query, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public List<Breakdown> ReadBreakdowns(int iStatus)
    {
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        Cursor c = ReadBreakdownsToCursor(iStatus);
        while (!c.isAfterLast())
        {
            if (c.getString(0) != null)
            {
                Breakdown newBreakdown=new Breakdown();
                newBreakdown.set_id(c.getString(c.getColumnIndex("_id")));
                newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                newBreakdown.set_Status(c.getShort(c.getColumnIndex("Status")));
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("_Acct_Num")));
                newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
                newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("Description")));
                newBreakdown.set_Job_No(c.getString(c.getColumnIndex("_Job_Num")));
                newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("_Contact_Num")));
                newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
                BreakdownsList.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        //TODO : find a way to close the db ( db.close()) of the c cursor
        return BreakdownsList;
    }

    public String getLastBreakdownID()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT `seq` FROM `sqlite_sequence` where `name`='BreakdownRecords' ;";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        String sNextBreakdownID="";
        if (!c.isAfterLast())
            sNextBreakdownID= c.getString(c.getColumnIndex("seq"));
        c.close();
        db.close();
        return sNextBreakdownID;
    }
    //TODO : Add using customer object
    public List<Breakdown> ReadAllCustomers()
    {
        List<Breakdown> Breakdownslist = new LinkedList<Breakdown>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT `_id` as `ID`,`ACCT_NUM` as `_Acct_Num`,`NAME`,`ADDRESS`,`LONGITUDE`,`LATITUDE` " +
                "FROM `Customers` limit 1000 ;";

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
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("_Acct_Num")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description("No Description");
                Breakdownslist.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return Breakdownslist;
    }

    public String getBreakdown_ID(String sACCT_NUM)
    {
        String sBreakdownID=null;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT `B`.`_id` AS `_id` " +
                " FROM `BreakdownRecords` `B`,`Customers` `C`  WHERE `B`.`_Acct_Num`='" + sACCT_NUM + "' " +
                " AND `C`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (!c.isAfterLast() & c.getString(0) != null)
        {
            sBreakdownID=c.getString(c.getColumnIndex("_id"));
        }
        c.close();
        db.close();
        return sBreakdownID;
    }
    public Breakdown ReadBreakdown_by_ID(String sID){
        Breakdown newBreakdown=null;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT `B`.`_id` AS `_id` ,`C`.`NAME` as `NAME`,C.`LONGITUDE` as `LONGITUDE`," +
                " C.`LATITUDE` as `LATITUDE`, `B`.`_Status` as `Status`, " +
                " `B`.`_Acct_Num` as `_Acct_Num`,`C`.`ADDRESS` as `ADDRESS`,   " +
                " `B`.`_Description` as `Description`, `B`.`_Job_Num` as `_Job_Num`, `B`.`_Contact_Num` as  `_Contact_Num`,  " +
                " `P`.`PremisesID` as `PremisesID` , `B`.`DateTime` as `DateTime1`, `B`.`completed_timestamp` as `DateTime2` " +
                " FROM `BreakdownRecords` `B` " +
                " LEFT JOIN `Customers` `C` ON `C`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                " LEFT JOIN `PremisesID` `P` ON `P`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                " WHERE `B`.`_id` = '" + sID + "';";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if (!c.isAfterLast() && c.getString(0) != null) //AND and AND only && not &
        {
            newBreakdown=new Breakdown();
            newBreakdown.set_id(c.getString(c.getColumnIndex("_id")));
            newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
            newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
            newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
            newBreakdown.set_Status(c.getShort(c.getColumnIndex("Status")));
            newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("_Acct_Num")));
            newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
            newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
            newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
            newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("Description")));
            newBreakdown.set_Job_No(c.getString(c.getColumnIndex("_Job_Num")));
            newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("_Contact_Num")));
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
        List<Breakdown> part1 = SearchInBreakdowns2(word);
        List<Breakdown> part2 = SearchInCustomers2(word);
        part1.addAll(part2);
        return part1;
    }

    private List<Breakdown> SearchInBreakdowns2(String word){
        String WORD = "%" + word.trim().toUpperCase() + "%";

        String query = "SELECT `B`.`_id` AS `_id` ,`C`.`NAME` as `NAME`,C.`LONGITUDE` as `LONGITUDE`,C.`TARIFF_COD` as `TARIFF_COD`," +
                " C.`LATITUDE` as `LATITUDE`, `B`.`_Status` as `Status`, " +
                " `B`.`_Acct_Num` as `_Acct_Num`,`C`.`ADDRESS` as `ADDRESS`,   " +
                " `B`.`_Description` as `Description`, `B`.`_Job_Num` as `_Job_Num`, `B`.`_Contact_Num` as  `_Contact_Num`,  " +
                " `P`.`PremisesID` as `PremisesID` , `B`.`DateTime` as `DateTime1`, `B`.`completed_timestamp` as `DateTime2` " +
                " FROM `BreakdownRecords` `B` " +
                " LEFT JOIN `Customers` `C` ON `C`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                " LEFT JOIN `PremisesID` `P` ON `P`.`ACCT_NUM` = `B`.`_Acct_Num` " +
                " WHERE " +
                "`B`.`_Acct_Num` LIKE'" + WORD +"' OR " +
                "`C`.`NAME` LIKE'" + WORD +"' OR " +
                "`C`.`ADDRESS` LIKE'" + WORD +"'"+
                " ORDER BY DateTime DESC;";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    Breakdown newBreakdown = new Breakdown();
                    newBreakdown.set_id(c.getString(c.getColumnIndex("_id")));
                    newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                    newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                    newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                    newBreakdown.set_Status(c.getShort(c.getColumnIndex("Status")));
                    newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("_Acct_Num")));
                    newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                    newBreakdown.set_Received_Time(c.getString(c.getColumnIndex("DateTime1")));
                    newBreakdown.set_Completed_Time(c.getString(c.getColumnIndex("DateTime2")));
                    newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                    newBreakdown.set_Full_Description(c.getString(c.getColumnIndex("Description")));
                    newBreakdown.set_Job_No(c.getString(c.getColumnIndex("_Job_Num")));
                    newBreakdown.set_Contact_No(c.getString(c.getColumnIndex("_Contact_Num")));
                    newBreakdown.set_PremisesID(c.getString(c.getColumnIndex("PremisesID")));
                    BreakdownsList.add(newBreakdown);
                } while (c.moveToNext());
            }
            c.close();
        }
        return BreakdownsList;
    }

    private List<Breakdown> SearchInCustomers2(String word){
        String WORD = "%" + word.trim().toUpperCase() + "%";
        String query = "SELECT `_id`,`ACCT_NUM`,`NAME`,`TARIFF_COD`, `ADDRESS`,`LONGITUDE`,`LATITUDE` " +
                "FROM `Customers` " +
                "WHERE " +
                "`ACCT_NUM` LIKE'" + WORD +"' OR " +
                "`NAME` LIKE'" + WORD +"' OR " +
                "`ADDRESS` LIKE'" + WORD +"';";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        List<Breakdown> BreakdownsList = new LinkedList<Breakdown>();
        if (c != null) {
            if (c.getCount()>0) {
                c.moveToFirst();
                do {
                    Breakdown newBreakdown = new Breakdown();
                    newBreakdown.set_id(c.getString(c.getColumnIndex("_id")));
                    newBreakdown.set_Name(c.getString(c.getColumnIndex("NAME")));
                    newBreakdown.set_LONGITUDE(c.getString(c.getColumnIndex("LONGITUDE")));
                    newBreakdown.set_LATITUDE(c.getString(c.getColumnIndex("LATITUDE")));
                    newBreakdown.set_Status(0);
                    newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("ACCT_NUM")));
                    newBreakdown.set_TARIFF_COD(c.getString(c.getColumnIndex("TARIFF_COD")));
                    newBreakdown.set_Received_Time("");
                    newBreakdown.set_Completed_Time("");
                    newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                    newBreakdown.set_Full_Description("-");
                    newBreakdown.set_Job_No("No breakdown entries found");
                    newBreakdown.set_Contact_No("");
                    newBreakdown.set_PremisesID("");
                    BreakdownsList.add(newBreakdown);
                } while (c.moveToNext());
            }
            c.close();
            //TODO : find a way to close the db ( db.close()) of the c cursor
        }
        return BreakdownsList;
    }

    //TODO : Add using customer object
    public Breakdown ReadCustomer_by_ACCT_NUM(String sACCT_NUM)
    {
        Breakdown newBreakdown=null;
        //TODO : Fix Exception
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT `_id` as `ID`,`ACCT_NUM` as `_Acct_Num`,`NAME`,`TARIFF_COD`, `ADDRESS`,`LONGITUDE`,`LATITUDE` " +
                "FROM `Customers` WHERE `ACCT_NUM`='" + sACCT_NUM +"';";

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
                newBreakdown.set_Acct_Num(c.getString(c.getColumnIndex("_Acct_Num")));
                newBreakdown.set_ADDRESS(c.getString(c.getColumnIndex("ADDRESS")));
                newBreakdown.set_Full_Description("No Description");
            }
        }catch (Exception ex){
            newBreakdown=null;
        }
        finally {
            c.close();
            db.close();
        }
        return newBreakdown;
    }


    public int UpdateBreakdownStatus(Breakdown breakdown,int Breakdown_Status)
    {// TODO : Maintain to two tables, one for Current status, one for status changed with all the status changes list with timesatamp
        int iResult=-1;
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/d h:m:s a");
        String time = timeFormat.format(System.currentTimeMillis());

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE `BreakdownRecords` SET `_Status`='" +
                String.valueOf(Breakdown_Status) +
                "', `completed_timestamp`= '" +  time + "' " +
                " WHERE `_id`='" +breakdown.get_id() + "';";

        db.execSQL(query);
        db.close();

        iResult=1; //Return Success
        return iResult;
    }

}