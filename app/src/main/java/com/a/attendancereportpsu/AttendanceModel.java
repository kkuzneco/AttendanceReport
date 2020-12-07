package com.a.attendancereportpsu;

public class AttendanceModel {
    public String student_id;
    public Boolean status;



    public AttendanceModel( String id, Boolean status) {
        this.student_id =id ;
        this.status = status;

    }
}
