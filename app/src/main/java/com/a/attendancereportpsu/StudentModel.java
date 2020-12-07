package com.a.attendancereportpsu;
public class StudentModel {
    public String group_id;
    public String name;
    public String id;


    public StudentModel( String id, String groupId, String name) {
        this.id = id;
        this.group_id = groupId;
        this.name = name;

    }

    public String getGroupId(){
        return this.group_id;
    }
    public String getName(){
        return this.name;
    }
    public String getId(){
        return this.id;
    }
}
