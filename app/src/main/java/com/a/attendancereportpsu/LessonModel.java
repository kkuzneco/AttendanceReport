package com.a.attendancereportpsu;

public class LessonModel {
    public String subject_id;
    public String lecturer_id;
    public String date;
    public String time;
    // public String department;
    public String group_id;

    public LessonModel(String group_id, String subject, String lecturer, String date, String time) {
        this.group_id = group_id;
        this.subject_id = subject;
        this.lecturer_id = lecturer;
        this.date= date;
        this.time = time;
    }


}
