package com.a.attendancereportpsu;

public class ReportModel {
    private String name;
    private String surname;
    private String city;
    private Double salary;

    public ReportModel() {
    }

    public ReportModel(String name, String surname, String city, Double salary) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
