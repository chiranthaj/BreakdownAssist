package lk.steps.breakdownassistpluss.Sync;

/**
 * Created by JagathPrasanga on 10/30/2017.
 */

public class BreakdownGroup {
    private String BreakdownId ;
    private String ParentBreakdownId ;
    private String ParentStatusId ;

    public String GetBreakdownId(){
        return this.BreakdownId;
    }

    public String GetParentBreakdownId(){
        return this.ParentBreakdownId;
    }

    public String GetParentStatusId(){
        return this.ParentStatusId;
    }

    public void SetBreakdownId(String breakdownId){
        this.BreakdownId = breakdownId;
    }
    public void SetParentBreakdownId(String parentBreakdownId){
        this.ParentBreakdownId = parentBreakdownId;
    }
    public void SetParentStatusId(String parentStatusId){
        this.ParentStatusId = parentStatusId;
    }

}
