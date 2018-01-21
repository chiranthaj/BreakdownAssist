package lk.steps.breakdownassistpluss.Models;

/**
 * Created by JagathPrasanga on 1/21/2018.
 */

public class BreakdownNew {

    public String BreakdownId ;
    public String ParentBreakdownId ;
    public String PremisesId ;
    public String AssignedTime ;
    public String Name ;
    public String Address ;
    public String ReceivedTime ;
    public String ComplainTypeId ;
    public String ComplainTypeName ;
    public String AcctNo ;
    public String MobilePhNo ;
    public String LandPhNo ;
    public String StatusId ;
    public String StatusName ;
    public String AreaId ;
    public String Sin ;
    public String EcscId ;
    public String AreaName ;
    public String EcscName ;
    public String TeamId ;
    public String TeamName ;
    public String FailureTypeId ;
    public String FailureNatureId ;
    public String FailureCauseId ;
    public String Lon ;
    public String Lat ;
    public String StatusTime ;
    public String Priority ;
    public String DeliveredToTeam ;
    public String Note ;
    public String UserId ;
    public String JobNo ;


    /* Breakdown status */
    public  static int NOT_DELIVERED = 0;
    public  static int DELIVERED = 1;
    public  static int ACKNOWLEDGED = 2;
    public  static int VISITED = 3;
    public  static int ATTENDING = 4;
    public  static int TEMPORARY_COMPLETED = 5;
    public  static int COMPLETED = 6;
    public  static int WITHDRAWN = 7;
    public  static int REJECTED = 8;
    public  static int RE_CALLED = 9;
    public  static int RETURNED = 10;
    public  static int FORWARDED = 11;
}
