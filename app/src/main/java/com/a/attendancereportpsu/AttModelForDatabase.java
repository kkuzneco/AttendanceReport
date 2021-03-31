package com.a.attendancereportpsu;

public class AttModelForDatabase {
    public String student_id;
    public Boolean status;
    public String lesson_id;


    public AttModelForDatabase( String student_id, Boolean status, String less_id) {
        this.student_id = student_id;
        this.status = status;
        this.lesson_id =less_id;

    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
