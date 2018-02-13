package lk.steps.breakdownassistpluss.Models;

import java.util.List;

/**
 * Created by JagathPrasanga on 2/13/2018.
 */

public class Interruption {
    public String InterruptionId ;
    //public String AreaId ;
    public String AreaName ;
    public String EcscName ;
    public String Sin ;
    public String StartTime ;
    public String EndTime ;
    public String Timestamp ;
    public String InterruptionTypeId ;
    public String InterruptionTypeName ;
    public String Description ;
    public String UserId ;
    public String FullName ;
    public List<InterruptedSub> Subs ;

    public String SubsText ;
}
