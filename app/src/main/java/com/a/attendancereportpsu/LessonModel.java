package com.a.attendancereportpsu;

public class LessonModel {
    public String subject_name;
    public String lecturer_name;
    public String date;
    public String time;
    // public String department;
    public String id;

    public LessonModel(String id, String subject, String lecturer, String date, String time) {
        this.id = id;
        this.subject_name = subject;
        this.lecturer_name = lecturer;
        this.date= date;
        this.time = time;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }
}
