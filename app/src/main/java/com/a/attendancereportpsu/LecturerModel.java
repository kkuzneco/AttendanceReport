package com.a.attendancereportpsu;

public class LecturerModel  {
    public String id;
    public String name;
    public String institute;


    public LecturerModel( String id, String name, String institute) {
        this.id = id;
        this.name = name;
        this.institute = institute;

    }

    public String getInstitute(){
        return this.institute;
    }
    public String getName(){
        return this.name;
    }
    public String getId(){
        return this.id;
    }

}

