package lk.steps.breakdownassist;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper
{
    private static final int Database_Version =32;
    private static final String Database_Name = "BreakdownAssist.db";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, Database_Name, factory, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query;
        query = "CREATE TABLE `BreakdownRecords` (" +
                "`_id`	INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`DateTime`	TEXT," +
                "`_Acct_Num`	TEXT," +
                "`_Status`	TEXT," +
                "`_Job_Num`	TEXT," +
                "`_Contact_Num`	TEXT," +
                "`_JOB_Source`	TEXT," +
                "`_Description`	TEXT, " +
                "`inbox_ref`	TEXT UNIQUE, " +
                "`last_timestamp` TEXT, " +
                "`completed_timestamp` TEXT " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE `Customers` (" +
                "`_id` INTEGER PRIMARY KEY," +
                "`ACCT_NUM` TEXT," +
                "`WALK_ORDER` TEXT," +
                "`NAME` TEXT,"+
                "`ADDRESS` TEXT," +
                "`SUB` TEXT," +
                "`ECSC` TEXT," +
                "`TARIFF_COD` TEXT," +
                "`NO_OF_MET` TEXT,"+
                "`LATITUDE` TEXT,"+
                "`LONGITUDE` TEXT,"+
                "`GPS_ACCURACY` TEXT "+
                ");";
        db.execSQL(query);
        query = "CREATE TABLE `PremisesID` (" +
                "`PremisesID` TEXT PRIMARY KEY," +
                "`ACCT_NUM` TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS `BreakdownRecords`");
        db.execSQL("DROP TABLE IF EXISTS `Customers`");
        db.execSQL("DROP TABLE IF EXISTS `PremisesID`");
        onCreate(db);
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

            db.insert("BreakdownRecords",null,values);
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
                        "SUM(CASE _Status  WHEN 1 THEN 1 ELSE 0 END) AS COMPLETED," +
                        "SUM(CASE _Status  WHEN 0 THEN 1 ELSE 0 END) AS UNATTAINED" +
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
    public int[][] getBreakdownStatistics()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT strftime('%d',DateTime) AS DATE, COUNT(*) AS COUNT" +
                " FROM BreakdownRecords" +
                " GROUP BY strftime('%d',DateTime) " +
                " ORDER BY strftime('%d',DateTime);";
        /*tring query = "SELECT DateTime AS DATE, COUNT(*) AS COUNT" +
                " FROM BreakdownRecords" +
                " GROUP BY DateTime " +
                " ORDER BY DateTime;";*/
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        int[][] counts = new int[2][cursor.getCount()];
        Log.d("cursor COUNT",cursor.getCount() +"");
        int i =0;
        do{
            counts[0][i] = cursor.getInt(cursor.getColumnIndex("DATE"));
            counts[1][i] = cursor.getInt(cursor.getColumnIndex("COUNT"));
            i++;
        }while(cursor.moveToNext());

        cursor.close();
        db.close();
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
        List<Breakdown> Breakdownslist = new LinkedList<Breakdown>();

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
        List<Breakdown> Breakdownslist = new LinkedList<Breakdown>();

        Cursor c =ReadBreakdownsToCursor(iStatus);

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
                Breakdownslist.add(newBreakdown);
            }
            c.moveToNext();
        }
        c.close();
        //TODO : find a way to close the db ( db.close()) of the c cursor
        return Breakdownslist;
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

        String dbString = "";
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
    public Breakdown ReadBreakdown_by_ID(String sID)
    {
        Breakdown newBreakdown=null;

        Cursor c =ReadBreakdownsToCursor(0);
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
    {//TODO : Use Enum class to to have Breakdownstatus.Done like thing
        int iResult=-1;
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy:MM:d h:m:s a");
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