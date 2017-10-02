package lk.steps.breakdownassistpluss;


import java.util.Date;

/**
 * Created by Chirantha on 13/06/2017.
 */

public class JobChangeStatus {
    public String job_no;
    public String status;
    public String st_code;
    public String change_datetime;  //TODO :Change this to a suitable variable format
    public String comment;
    public String device_timestamp; //TODO :Change this to a suitable variable format
    public int synchro_mobile_db;
    public String cause;
    public String detail_reason_code;
    public String type_failure;

    public JobChangeStatus(String job_no, String st_code, String change_datetime, String comment) {
        this.job_no = job_no;
        this.st_code = st_code;
        this.change_datetime = change_datetime;
        this.comment = comment;
        this.device_timestamp=Globals.timeFormat.format(new Date( System.currentTimeMillis()));
        this.synchro_mobile_db=0;
    }

    public JobChangeStatus() {
    }
}
