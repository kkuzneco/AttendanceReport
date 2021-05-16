package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.core.view.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Internal;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.protobuf.DescriptorProtos.*;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.*;

public class Report extends AppCompatActivity {
    Calendar dateAndTime;
    Button dateStart;
    LessonModel lesson;
    StudentModel student;
    int counter;
    ArrayList<StudentModel> students;
    ArrayList<LessonModel> lessons;
    ArrayList<String> lessonsIds;
    Button dateFinish;
    String groupNumber;
    SQLiteDatabase db;
    int cont = 0;
    int blockCount = 0;
    int m = 0;
    FirebaseFirestore mFirebaseDatabase;
    DatabaseHelper dbHelper;
    ArrayList<String> subjects;
    ArrayList<HSSFSheet> sheet;
    CheckedTextView textView;
    long start;
    long finish;
    String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        dateAndTime = Calendar.getInstance();
        subjects = new ArrayList<>();
        textView = findViewById(R.id.checked_tv);
        textView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (textView.isChecked()) {
                    textView.setChecked(false);
                    textView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                } else {
                    textView.setChecked(true);
                    textView.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                }
            }
        });
        dbHelper = new DatabaseHelper(this);
        //dbHelper.createTableAttendance(dbHelper.getWritableDatabase());
        students = new ArrayList<>();
        lessons = new ArrayList<>();
        lessonsIds = new ArrayList<>();
        initFirebase();
        dateStart = (Button) findViewById(R.id.startDate);
        dateFinish = (Button) findViewById(R.id.finishDate);
        setDefaultDateStart();
        groupNumber = "22407";
       counter = 0;
       cont = 0;
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
        fillLessonsTable(new MyAttendanceCallback() {
            @Override
            public void onCallback() {

            }
        });
        //формируем из бд список занятий подходящих под фильтр

        //вносим в БД данные о посещаемости этих занятий
        lessonsIds.clear();
        //создаем локальный список студентов также из базы
        createStudentsList();
        //createSortingLessonsList();
        createLessonsWithDateFilter();
        blockCount = 2;

    }
    private void clearData(){
        lessons.clear();
        lessonsIds.clear();
        subjects.clear();
    }
    private boolean createLessonsWithDateFilter() {
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("lessons", null, "date>="+start+" and date<="+finish+";",
                null, null, null, "date");
        Log.d("myLessons", String.valueOf(start));
        Log.d("myLessons", String.valueOf(finish));
        if (cursor.moveToFirst()) {
            Log.d("myLessons", "FOUND");
            int idIndex = cursor.getColumnIndex("id");
            int lecturerIndex = cursor.getColumnIndex("lecturer_id");
            int subjectIndex = cursor.getColumnIndex("subject_id");
            int dateIndex = cursor.getColumnIndex("date");
            int timeIndex = cursor.getColumnIndex("time");
            do {

                Log.d("mLog", "ID = " + cursor.getString(idIndex) +
                        ", subject = " + cursor.getString(subjectIndex) +
                        ", time = " + cursor.getString(timeIndex));
                lesson = new LessonModel(cursor.getString(idIndex),groupNumber, cursor.getString(subjectIndex), cursor.getString(lecturerIndex), cursor.getLong(dateIndex), cursor.getString(timeIndex));
               // lesson = new LessonModel(cursor.getString(idIndex),groupNumber, cursor.getString(subjectIndex), cursor.getString(lecturerIndex), cursor.getLong(dateIndex), cursor.getString(timeIndex));
                lessons.add(lesson);
                int subIndex = cursor.getColumnIndex("subject_id");
                if(!subjects.contains(cursor.getString(subIndex)))
                    subjects.add(cursor.getString(subIndex));
                lessonsIds.add(cursor.getString(idIndex));
                Log.d("myLessons", String.valueOf(lessons.size()) + " " + lessons.get(lessons.size() - 1).time);
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");
        cursor.close();
        return true;

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
            dateAndTime.set(year,monthOfYear,dayOfMonth,0,0,0);
            setInitialDateStart();
        }
    };
    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(year,monthOfYear,dayOfMonth,23,59,59);
            setInitialDateFinish();
        }
    };

    private void setInitialDateStart() {
        // time = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        String date1 = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);
        String date2= DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        Log.d("myLessonsD", date1 +" "+date2);

        String date = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        start = dateAndTime.getTimeInMillis();
        dateStart.setText(date);
        //dbHelper.removeAttRows(dbHelper.getWritableDatabase());
        Log.d("myReport", String.valueOf(students.size()));
        lessonsIds.clear();
        //createLessonsWithDateFilter();
    }

    private void setInitialDateFinish() {
        String date1 = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);
        String date2= DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        Log.d("myLessonsD", date1 +" "+date2);
        String date = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        finish = dateAndTime.getTimeInMillis();
        dateFinish.setText(date);

        lessonsIds.clear();
       // createLessonsWithDateFilter();
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

    public interface MyAttendanceCallback {
        void onCallback();
    }
    public void fillLessonsTable(MyAttendanceCallback myCallBack){

        mFirebaseDatabase.collection("lessons").whereEqualTo("group_id", groupNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    try {
                                        Log.d("myLessons", "поиск...");
                                        Log.d("myLessons","insert into lessons(id, subject_id, lecurer_id, date, time) values (" + "'" + document.getId() + "'," + "'" + document.get("subject_id") + "'," + "'" + document.get("lecturer_id") + "'," + (Long)document.get("date") +  ","+ "'" + document.get("time") + "');");
                                        //  lessons.add(new LessonModel(document.getId(), String.valueOf(document.get("group_id")),  String.valueOf(document.get("subject_id")), String.valueOf(document.get("lecturer_id")), Long.valueOf((Long)document.get("date")),String.valueOf(document.get("time"))));
                                        db.execSQL("insert into lessons(id, subject_id, lecturer_id, date, time) values (" + "'" + document.getId() + "'," + "'" + document.get("subject_id") + "'," + "'" + document.get("lecturer_id") + "'," + (Long)document.get("date") +  ","+ "'" + document.get("time") + "');");
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                            myCallBack.onCallback();
                        } else {
                            Log.d("myLessons", "Error getting documents: ", task.getException());
                        }

                    }

                });


    }
    public void getAttendanceForLesson(MyAttendanceCallback myCallback) {
        //для каждого студента\
        Log.d("myReport", "заполняем  ");
        mFirebaseDatabase.collection("attendance").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {

                                                try {
                                                    Log.d("myLessons", "поиск...");
                                                    Log.d("myLessons", "insert into attendance(id, lesson_id, student_id, status) values (" + "'" + document.getId() + "'," + "'" + document.get("lesson_id") + "'," + "'" + document.get("student_id") + "'," + (Boolean) document.get("status") + ");");
                                                  if(lessonsIds.contains(document.get("lesson_id"))) {
                                                      Log.d("myLessons", "found");//  lessons.add(new LessonModel(document.getId(), String.valueOf(document.get("group_id")),  String.valueOf(document.get("subject_id")), String.valueOf(document.get("lecturer_id")), Long.valueOf((Long)document.get("date")),String.valueOf(document.get("time"))));
                                                      db.execSQL("insert into attendance(id, lesson_id, student_id,status) values (" + "'" + document.getId() + "'," + "'" + document.get("lesson_id") + "'," + "'" + document.get("student_id") + "'," + "'" + (Boolean) document.get("status") + "'" + ");");

                                                  }} catch (SQLException e) {
                                                    e.printStackTrace();
                                                }

                                        }
                                    }
                                    myCallback.onCallback();
                                } else {
                                    Log.d("myLessons", "Error getting documents: ", task.getException());
                                }

                            }

                        });

    }

        public void startCreateReport(View v){
        clearData();
           boolean creating =  createLessonsWithDateFilter();
           if(creating){
               if(start<finish)
                   createReport();
               else
                   Toast.makeText(this, "Дата начала отчета больше, чем дата окончания", Toast.LENGTH_SHORT).show();
           }
        }

        public void createReport(){
            getAttendanceForLesson(new MyAttendanceCallback() {
                @Override
                public void onCallback() {
                    cont++;
                    Log.d("myLessonsC", "Callback");
                    createExcelReport();
                }
            });
 //      dbHelper.removeLessonsRows(dbHelper.getWritableDatabase());
   //         lessons.clear();
//            getLessonsList();
        }

    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        //получаем ссылку для работы с базой данных
    }
    public void createStudentsList(){
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("students", null, null, null, null, null, "name");

        if (cursor.moveToFirst()) {
            Log.d("myLessons", "FOUND");
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");

            do {
                student = new StudentModel(cursor.getString(idIndex),groupNumber,cursor.getString(nameIndex));
                students.add(student);

            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");
        cursor.close();
    }

    public void createExcelReport()  {

        getAttendanceForLesson(new MyAttendanceCallback() {
            @Override
            public void onCallback() {
                    goToCreating();
            }
        });
    }
    public void goToCreating(){
        HSSFSheet sheet1 = null;
        ArrayList<LessonModel> lessonFilter = new ArrayList<>();
        int j = 0;
        // создание самого excel файла в памяти
        HSSFWorkbook workbook = new HSSFWorkbook();

        // создание листа с названием "Просто лист"
        int i;
        //все id предметов в базе (для каждого предмета)
        for (i = 0; i < subjects.size(); i++) {
            lessonFilter.clear();
            Log.d("myLessons", String.valueOf(subjects.size()));
            j = 0;
            //получаем название текущего предмета
            Cursor cursor = db.query("subjects", null, "id='" + subjects.get(i)+"'", null, null, null, null);
            cursor.moveToFirst();
            int typeName = cursor.getColumnIndex("type");
            int indexName = cursor.getColumnIndex("name");
            //название здесь
            String u = cursor.getString(indexName)+'('+cursor.getString(typeName)+')';
          //  String id_= subjects.get(i);
            cursor.close();

            //получаем название текущего предмета
            Cursor cursor_filter = db.query("lessons", null, "subject_id='" + subjects.get(i)+"'", null, null, null, "date");
            cursor_filter.moveToFirst();
            int indexId = cursor_filter.getColumnIndex("id");
            int indexSubject = cursor_filter.getColumnIndex("subject_id");
            int indexLecturer = cursor_filter.getColumnIndex("lecturer_id");
            int indexDate = cursor_filter.getColumnIndex("date");
            int indexTime = cursor_filter.getColumnIndex("time");
            //название здесь
            if(cursor_filter.moveToFirst()){
                do{
                  lessonFilter.add(new LessonModel(cursor_filter.getString(indexId),groupNumber,cursor_filter.getString(indexSubject),cursor_filter.getString(indexLecturer),cursor_filter.getLong(indexDate),cursor_filter.getString(indexTime)));
                }
            while (cursor_filter.moveToNext());}
            //  String id_= subjects.get(i);
            cursor_filter.close();
            Log.d("myLessons", u);
            try {
                sheet1 = workbook.createSheet(u);

                //создаем лист с названием предмета

                Row row = sheet1.createRow(0);
                //запишем данные в 0 строку
                int k;
                for (k = 0; k < lessonFilter.size(); k++) {

                        //создаем 0 строку
                        Log.d("myLessons", "нашли предмет с текущим аиди");
                        //создаем на листе строку 0
                        Log.d("myLessons", "lessons found");
                        //столбец
                        j++;

                        //получаем время в формате для текущего занятия
                        dateAndTime.setTimeInMillis(lessonFilter.get(k).date);
                        Log.d("myLessonsj", String.valueOf(j));
                        Log.d("myLessonsj", String.valueOf(dateAndTime.getTimeInMillis()));
                        row.createCell(j).setCellValue(DateUtils.formatDateTime(this,
                                dateAndTime.getTimeInMillis(),
                                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));

                }

                //по каждому фильтрованному  занятию проходим

                 int rowNum = 0;
                 for (m = 0; m < students.size(); m++) {
                            Row row1 = sheet1.createRow(m + 1);
                            row1.createCell(0).setCellValue(students.get(m).name);
                            for (int k1 = 0; k1 <lessonFilter.size(); k1++) {
                            counter = 1;

                            int l;
                                    Log.d("myLessonsBC", String.valueOf(m));
                                    //  row1.createCell(counter).setCellValue("+");
                                    try {
                                        Cursor cursor2 = db.query("attendance", null, "lesson_id='" + lessonFilter.get(k1).id + "'" + " and student_id = '" + students.get(m).id + "'", null, null, null, null);
                                        cursor2.moveToFirst();
                                        int status = cursor2.getColumnIndex("status");
                                        if (Boolean.parseBoolean(cursor2.getString(status))== false) {
                                            Log.d("myLessons", "false");
                                            Cell r = row1.createCell(k1 + 1);
                                            r.setCellValue("-");
                                           // r.setCellStyle(style1);

                                        } else {
                                            Log.d("myLessons", "true");
                                            Cell r =row1.createCell(k1+1);
                                            r.setCellValue("+");
                                           // r.setCellStyle(style2);

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                        }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            try  {
                Context mContext =  this;
                filepath=mContext.getExternalFilesDir(null).getAbsolutePath()+"/"+String.format("Study reports/Report" +groupNumber+"_"+ DateUtils.formatDateTime(this,
                        start,
                        DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR)+"-"+DateUtils.formatDateTime(this,
                        finish,
                        DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR)+".xls");
                Log.d("myLessons", filepath);
               FileOutputStream out = new FileOutputStream(new File(mContext.getExternalFilesDir(null).getAbsolutePath(), String.format("Study reports/Report" +groupNumber+"_"+ DateUtils.formatDateTime(this,
                        start,
                        DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR)+"-"+DateUtils.formatDateTime(this,
                        finish,
                        DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR)+".xls")));
            workbook.write(out);
            out.close();

                   System.out.println("Excel файл успешно создан!");
                   if (textView.isChecked()){
                       try {
                           StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                           StrictMode.setVmPolicy(builder.build());
                           Intent intent = new Intent(Intent.ACTION_SEND);
                           intent.setType("application/excel");
                           File fl = new File(filepath);
                           Uri path = Uri.fromFile(fl);
                           intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Посещаемость "+ groupNumber);
                           intent.putExtra(Intent.EXTRA_TEXT, "Отчет по посещаемости группы "+ groupNumber+" c "
                                   + DateUtils.formatDateTime(this, start, DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR)+" по "+DateUtils.formatDateTime(this,
                                   finish,
                                   DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
                         //  intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath)); // сюда прилетает картинка

                           String to[] = { "kuznetcova.ks@yandex.ru"};
                           intent.putExtra(Intent.EXTRA_EMAIL, to);

                           intent.putExtra(Intent.EXTRA_STREAM, path);

                           Intent chosenIntent = Intent.createChooser(intent, "Заголовок в диалоговом окне");
                           startActivity(chosenIntent);

                       } catch (Exception e) {
                           e.printStackTrace();
                       }

                   }
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
        private void makeFolder(){
            Context mContext = this;
            File file = new File(mContext.getExternalFilesDir(null).getAbsolutePath(),"Study reports");

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
          //  Toast.makeText(Report.this, "Folder already exist", Toast.LENGTH_SHORT).show();
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