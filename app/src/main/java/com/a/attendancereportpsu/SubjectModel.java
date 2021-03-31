package com.a.attendancereportpsu;

public class SubjectModel {
    public String groupId;
    public String name;
    public String institute;
    public String id;
    public String type;


    SubjectModel(String id,String group_id, String name, String institute, String type) {
        this.id =id;
        this.name = name;
        this.groupId = group_id;
        this.institute = institute;
        this.type = type;
    }

    public String getGroupId(){
        return this.groupId;
    }
    public String getName(){
        return this.name;
    }

}
