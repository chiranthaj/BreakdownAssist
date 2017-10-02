package lk.steps.breakdownassistpluss;

import java.util.Date;

/**
 * Created by Chirantha on 25/06/2017.
 */

public class JobCompletion {
    public String job_no;
    public String st_code;
    public String job_completed_datetime;  //TODO :Change this to a suitable variable format
    public String comment;
    public String device_timestamp; //TODO :Change this to a suitable variable format
    public int synchro_mobile_db;
    public String detail_reason_code;
    public String cause;
    public String type_failure;
    public String job_completed_by;
    public String action_code;

    public String change_datetime;  //TODO :Change this to a suitable variable format

    public JobCompletion() {
    }

    public JobCompletion(String job_no, String st_code, String job_completed_datetime,
                         String comment, String detail_reason_code, String cause, String type_failure,
                         String job_completed_by, String action_code) {
        this.job_no = job_no;
        this.st_code = st_code;
        this.job_completed_datetime = job_completed_datetime;
        this.comment = comment;
        this.detail_reason_code = detail_reason_code;
        this.cause = cause;
        this.type_failure = type_failure;
        this.job_completed_by = job_completed_by;
        this.action_code = action_code;
        this.device_timestamp=Globals.timeFormat.format(new Date( System.currentTimeMillis()));
        this.synchro_mobile_db=0;
    }
}
