package com.a.attendancereportpsu;

public class SubjectModel {
    public String groupId;
    public String name;


    SubjectModel(String group_id, String name) {

        this.name = name;
        this.groupId = group_id;
    }

    public String getGroupId(){
        return this.groupId;
    }
    public String getName(){
        return this.name;
    }

}
