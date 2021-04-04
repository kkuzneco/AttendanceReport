package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Report extends AppCompatActivity {
    Calendar dateAndTime;
    Button dateStart;
    LessonModel lesson;
    ArrayList<LessonModel> lessons;
    Button dateFinish;
    String groupNumber;
    SQLiteDatabase db;
    FirebaseFirestore mFirebaseDatabase;
    DatabaseHelper dbHelper;
    ArrayList<String> subjects;
    ArrayList<HSSFSheet> sheet;
    long start;
    long finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        dateAndTime = Calendar.getInstance();
        subjects = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);
        // dbHelper.createTableLessons(dbHelper.getWritableDatabase());

        lessons = new ArrayList<>();
        initFirebase();
        dateStart = (Button) findViewById(R.id.startDate);
        dateFinish = (Button) findViewById(R.id.finishDate);
        setDefaultDateStart();
        groupNumber = "22407";
        getLessonsList();
        setDefaultDateFinish();
        dateFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFinish();
            }
        });
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateStart();
            }
        });
        if (hasPermissions()){
            // our app has permissions.
            makeFolder();

        }
        else {
            requestPermissionWithRationale();
            Log.d("myReport", "нет доступа к памяти");
        }
    }
    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,1);
        }
    }
    public void setDefaultDateStart() {
        Log.d("myReport", String.valueOf(dateAndTime.getTimeInMillis()));
        dateAndTime.add(Calendar.DAY_OF_YEAR, -7);
        Log.d("myReport", String.valueOf(dateAndTime.getTimeInMillis()));
        setInitialDateStart();
    }

    public void setDefaultDateFinish() {
        dateAndTime = Calendar.getInstance();
        setInitialDateFinish();
    }

    public void setDateStart() {
        new DatePickerDialog(Report.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    public void setDateFinish() {
        new DatePickerDialog(Report.this, d1,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateStart();
        }
    };
    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateFinish();
        }
    };

    private void setInitialDateStart() {
        // time = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        String date = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        start = dateAndTime.getTimeInMillis();
        dateStart.setText(date);

    }

    private void setInitialDateFinish() {
        // time = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);

        String date = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        finish = dateAndTime.getTimeInMillis();
        dateFinish.setText(date);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case 1:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            //user granted all permissions we can perform our task.
            makeFolder();
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("myLessons", "Нет доступа к папке");
                }
            }
        }

    }

    public void getLessonsList() {
        db = dbHelper.getWritableDatabase();
        dbHelper.removeLessonsRows(db);
        lesson = new LessonModel("", "", "", 0, "");
        mFirebaseDatabase.collection("lessons").whereEqualTo("group_id", groupNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    lesson.group_id = document.get("group_id").toString();
                                    lesson.subject_id = document.get("subject_id").toString();
                                    lesson.lecturer_id = document.get("lecturer_id").toString();
                                    lesson.time = document.get("time").toString();
                                    lesson.date = (long) document.get("date");
                                    lessons.add(new LessonModel(document.get("group_id").toString(), document.get("subject_id").toString(), document.get("lecturer_id").toString(), (long) document.get("date"), document.get("time").toString()));
                                    Log.d("sortedMy", lesson.date + lesson.subject_id);
                                    db.execSQL("insert into lessons(id, lecturer_id, subject_id,date,time) values (" + "'" + document.getId() + "'," + "'" + lesson.lecturer_id + "'," + "'" + lesson.subject_id + "'," + "'" + lesson.date + "'," + "'" + lesson.time + "');");
                                    // Log.d("mlog", "insert into students(id, name, group_id) values (" + "'"+student.id+"',"+"'"+student.name+"',"+"'"+student.group_id+"');");
                                }
                            }
//                            Log.d("element", String.valueOf(studentsList.size()));
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }

                });

    }


    public void createSortingLessonsList(View v) {

        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("lessons", null, null, null, null, null, "date");

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                int lecturerIndex = cursor.getColumnIndex("lecturer_id");
                int subjectIndex = cursor.getColumnIndex("subject_id");
                int dateIndex = cursor.getColumnIndex("date");
                int timeIndex = cursor.getColumnIndex("time");
                do {

                    Log.d("mLog", "ID = " + cursor.getString(idIndex) +
                            ", subject = " + cursor.getString(subjectIndex) +
                            ", time = " + cursor.getString(timeIndex));
                    lesson = new LessonModel(groupNumber, cursor.getString(subjectIndex), cursor.getString(lecturerIndex), cursor.getLong(dateIndex), cursor.getString(timeIndex));
                    lessons.add(lesson);
                    Log.d("myLessons", String.valueOf(lessons.size()) + " " + lessons.get(lessons.size() - 1).time);
                } while (cursor.moveToNext());
            } else
                Log.d("mLog", "0 rows");
            cursor.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("lessons", null, null, null, "subject_id", null, null);

            if (cursor.moveToFirst()) {
                int subjectIndex = cursor.getColumnIndex("subject_id");
                do {
                    subjects.add(cursor.getString(subjectIndex));
                } while (cursor.moveToNext());
            } else
                Log.d("mLog", "0 rows");
            cursor.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        createExcelReport();

    }

    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        //получаем ссылку для работы с базой данных
    }

    public void createExcelReport() {
        HSSFSheet sheet1 = null;
        int j = 0;
        // создание самого excel файла в памяти
        HSSFWorkbook workbook = new HSSFWorkbook();
        // создание листа с названием "Просто лист"
        int i;
        //все id предметов в базе (для каждого предмета)
        for (i = 0; i < subjects.size(); i++) {
            j = 0;
            //получаем название текущего предмета
            Cursor cursor = db.query("subjects", null, "id='" + subjects.get(i)+"'", null, null, null, null);
            cursor.moveToFirst();

            int indexName = cursor.getColumnIndex("name");
            //название здесь
            String u = cursor.getString(indexName);
          //  String id_= subjects.get(i);

            Log.d("myLessons", u);
            try {
                //создаем лист с названием предмета
                sheet1 = workbook.createSheet(u);
                //запишем данные в 0 строку
                int rowNum = 0;

                //по каждому фильтрованному  занятию проходим
                for (i = 0; i < lessons.size(); i++) {

                    // Row row = sheet.get(i).createRow(rowNum);
                    //если по текущему предмету заятие
                    if(lessons.get(i).subject_id.equals(subjects.get(i))){
                        //создаем 0 строку

                    //если у текущего занятия входит в рамки времени
                    if (lessons.get(i).date >= start && lessons.get(i).date <= finish) {
                        //создаем на листе строку 0
                        j++;
                        Row row = sheet1.createRow(rowNum);
                        //получаем время в формате текущего занятия
                        dateAndTime.setTimeInMillis(lessons.get(i).date);

                        row.createCell(j).setCellValue(DateUtils.formatDateTime(this,
                                    dateAndTime.getTimeInMillis(),
                                    DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
                        j++;
                    }
                }
                    else
                        continue;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
//            sheet.add(sheet1);
        }
        // счетчик для строк
      /*  int rowNum = 0;
        for (i = 0; i < lessons.size(); i++) {
           // Row row = sheet.get(i).createRow(rowNum);
            Row row = sheet1.createRow(rowNum);
            dateAndTime.setTimeInMillis(lessons.get(i).date);
            row.createCell(0).setCellValue(DateUtils.formatDateTime(this,
                    dateAndTime.getTimeInMillis(),
                    DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));


        }*/
        try  {
            FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Study reports/Report.xls"));
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Excel файл успешно создан!");

    }
    private void makeFolder(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Study reports");

        if (!file.exists()){
            Boolean ff = file.mkdir();
            if (ff){
                Log.d("myLessons", "создали папку");
                Toast.makeText(Report.this, "Folder created successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("myLessons", "не создали папку");
                Toast.makeText(Report.this, "Failed to create folder", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Log.d("myLessons", "папка есь");
            Toast.makeText(Report.this, "Folder already exist", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(Report.this.findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

}