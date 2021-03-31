package com.a.attendancereportpsu;

import java.io.Serializable;

public class AttendanceModel implements Serializable {
    public String student_id;
    public Boolean status;



    public AttendanceModel( String id, Boolean status) {
        this.student_id =id ;
        this.status = status;

    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
