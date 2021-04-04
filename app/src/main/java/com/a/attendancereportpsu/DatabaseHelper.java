package com.a.attendancereportpsu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//класс для работы с бд
public class DatabaseHelper extends SQLiteOpenHelper {
    StudentModel student;
    FirebaseFirestore mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "PSUAtt", null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {

        db.execSQL("create table students ("
                + "id text,"
                + "name text,"
                + "group_id text" + ");");

        db.execSQL("create table lecturers ("
                + "id text,"
                + "name text,"
                + "institute text" + ");");

        db.execSQL("create table subjects ("
                + "id text,"
                + "name text,"
                + "group_id text,"
                + "institute text,"
                + "type text"
                + ");");
        db.execSQL("create table macs ("
                + "mac text PRIMARY KEY,"
                + "name text,"
                + "group_id text,"
                + "student_id text" + ");");
        db.execSQL("create table lessons ("
                + "id text PRIMARY KEY,"
                + "subject_id text,"
                + "lecturer_id text,"
                + "date long,"
                + "time text"
                + ");");

        Log.d("mLog", "База успешно создана! ");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + "students");
        db.execSQL("drop table if exists " + "subjects");
        db.execSQL("drop table if exists " + "lecturers");
        Log.d("mLog", "Пересоздаем БД ");
        onCreate(db);
    }

    public void removeRows(SQLiteDatabase db) {
        db.execSQL("delete from students");
        Log.d("mLog", "Все строчки удалены. ");
    }

    public void removeLecturerRows(SQLiteDatabase db) {
        db.execSQL("delete from lecturers");
        Log.d("mLog", "Все строчки удалены. ");
    }

    public void removeSubjectRows(SQLiteDatabase db) {
        db.execSQL("delete from subjects");

    }
    public void removeLessonsRows(SQLiteDatabase db) {
        db.execSQL("delete from lessons");
    }

    public void createTableMacs(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + "macs");
        db.execSQL("create table macs ("
                + "mac text PRIMARY KEY,"
                + "name text,"
                + "group_id text,"
                + "student_id text" + ");");
    }
    public void removeMACRows(SQLiteDatabase db) {
        db.execSQL("delete from macs");
    }
     public void createTableLessons(SQLiteDatabase db){
         db.execSQL("drop table if exists lessons");
         db.execSQL("create table lessons ("
                 + "id text PRIMARY KEY,"
                 + "subject_id text,"
                 + "lecturer_id text,"
                 + "date long,"
                 + "time text"
                 + ");");
     }
}