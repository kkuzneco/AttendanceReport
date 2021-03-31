package com.a.attendancereportpsu;

public class DeviceModel {
    public String mac_address;
    public String student_id;
    public String name = null;
    public String group_id;

    public DeviceModel(String mac_address, String student_id, String group_id){
        this.mac_address = mac_address;
        this.student_id = student_id;
        this.group_id = group_id;
    }
    public void setName(String name){
        this.name = name;
    }



}
