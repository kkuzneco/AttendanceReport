package com.a.attendancereportpsu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDBHelper  extends SQLiteOpenHelper {
    StudentModel student;
    FirebaseFirestore mFirebaseDatabase;
    DatabaseReference mDatabaseReference;


    public AdminDBHelper(@Nullable Context context) {
        super(context,"PSUAdmin", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table groups ("
                + "id text,"
                + "number long,"
                + "course int" + ");");
        db.execSQL("create table students ("
                + "id text,"
                + "name text,"
                + "group_id text" + ");");
        db.execSQL("create table lessons ("
                + "id text PRIMARY KEY,"
                + "subject_id text,"
                + "lecturer_id text,"
                + "date long,"
                + "time text"
                + ");");
        db.execSQL("create table attendance ("
                + "id text PRIMARY KEY,"
                + "lesson_id text,"
                + "student_id text,"
                + "status boolean"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void removeGroupsRows(SQLiteDatabase db) {
        db.execSQL("delete from groups");
    }
}
