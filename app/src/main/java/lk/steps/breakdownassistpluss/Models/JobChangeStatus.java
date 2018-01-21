package lk.steps.breakdownassistpluss.Models;


import java.util.Date;

import lk.steps.breakdownassistpluss.Globals;

/**
 * Created by Chirantha on 13/06/2017.
 */

public class JobChangeStatus {
    public String job_no;
    public String status;
    public String AreaId;
    public String EcscId;
    public String TeamId;
    public String ReceivedTime;
    public String ParentBreakdownId;
    public String change_datetime;  //TODO :Change this to a suitable variable format
    public String comment;
    public String device_timestamp; //TODO :Change this to a suitable variable format
    public int synchro_mobile_db;
    public String cause;
    public String detail_reason_code;
    public String type_failure;


    public JobChangeStatus(String _job_no, int STATUS, String change_datetime, String comment) {
        this.job_no = _job_no;
        this.status = String.valueOf(STATUS);
        this.change_datetime = change_datetime;
        this.comment = comment;
        this.device_timestamp= Globals.timeFormat.format(new Date( System.currentTimeMillis()));
        this.synchro_mobile_db=0;
    }

    public JobChangeStatus() {
    }
}
