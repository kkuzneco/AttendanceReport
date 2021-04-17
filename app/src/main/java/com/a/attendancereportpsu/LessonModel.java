package com.a.attendancereportpsu;

public class LessonModel  {
    public String subject_id;
    public String lecturer_id;
    public long date;
    public String time;
    // public String department;
    public String group_id;
    public String id;

    public LessonModel(String id, String group_id, String subject, String lecturer, long date, String time) {
        this.id = id;
        this.group_id = group_id;
        this.subject_id = subject;
        this.lecturer_id = lecturer;
        this.date= date;
        this.time = time;
    }
    public LessonModel( String group_id, String subject, String lecturer, long date, String time) {
        this.group_id = group_id;
        this.subject_id = subject;
        this.lecturer_id = lecturer;
        this.date= date;
        this.time = time;
    }

}
