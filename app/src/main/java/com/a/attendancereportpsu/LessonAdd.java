package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.R.id;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Color;

import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonAdd extends AppCompatActivity{
    AlertDialog.Builder builder;    //для диалога отмены
    Calendar dateAndTime = Calendar.getInstance();//получаем календарь
    Button setdateTime, setSubject, setLecturer, setStudents, setTime, buttonForTesting;//buttons
    String groupNumber = "123", lecturer_id = null, subject_id=null, lesson_id;
    SQLiteDatabase db;
    Boolean isAttendanceFilling = false;
    DatabaseHelper dbHelper;
    AttModelForDatabase att;
    LessonModel lesson;
    long date;
    Boolean isEdit = false;
    String time;
    String subject = null, institute = null;
    public ArrayList<AttendanceModel> attendance = new ArrayList<>();
    FirebaseFirestore mFirebaseDatabase;
    LessonModel currentLesson;
    int index = 0, index1 = 0;
    private List<LessonModel> list_lessons = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_add);
        setdateTime=(Button)findViewById(R.id.dateEdit);
        setTime = (Button)findViewById(R.id.setTime);
        setSubject=(Button)findViewById(R.id.setSubject);
        setLecturer=(Button)findViewById(R.id.setLecturer);
        setStudents=(Button)findViewById(R.id.setAttendance);


        currentLesson = new LessonModel(null,null,null,dateAndTime.getTimeInMillis(),null);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();
        initFirebase();
        Intent intent_1 = getIntent();
        groupNumber = intent_1.getStringExtra("group");
        currentLesson.group_id = groupNumber;
        if(intent_1.getSerializableExtra("lesson")!=null) {
            isEdit = true;
            Bundle bundle = intent_1.getExtras();
            currentLesson = (LessonModel) bundle.getSerializable("lesson");
            dateAndTime.setTimeInMillis(currentLesson.date);
            getAttendanceForLesson(new LessonAdd.MyAttendanceCallback() {
                @Override
                public void onCallback() {
                    fillAttendance();
                    Log.d("myLessonsC", "Callback");
                }
            });
        }
        else fillEmptyAttendance();
        Log.d("GROUP IN LAdd", groupNumber);
        setData();

        Log.d("TIIIME",  DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));

    }
    public void fillAttendance(){
        Cursor cursor2 = db.query("attendance", null,  "lesson_id = '"+currentLesson.id+"'", null, null, null, null);
        if(cursor2.moveToFirst()) {
          do {
              int status = cursor2.getColumnIndex("status");
              int student = cursor2.getColumnIndex("student_id");
              attendance.add(new AttendanceModel(cursor2.getString(student), Boolean.parseBoolean(cursor2.getString(status))));
          }
          while(cursor2.moveToNext());
        }
    }
    public void fillEmptyAttendance(){
        Cursor cursor2 = db.query("students", null,  null, null, null, null, null);
        if(cursor2.moveToFirst()) {
            do {

                int student = cursor2.getColumnIndex("id");
                attendance.add(new AttendanceModel(cursor2.getString(student), false));
            }
            while(cursor2.moveToNext());
        }
    }

    public interface MyAttendanceCallback {
        void onCallback();
    }
    public void getAttendanceForLesson(LessonAdd.MyAttendanceCallback myCallback) {
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
                                        if(document.get("lesson_id").equals(currentLesson.id)) {
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



    public void setData(){
        setInitialDate();
        setInitialTime();
        if(currentLesson.equals(null)) {
            setSubject.setText("Выберите предмет");
            setLecturer.setText("Выберите преподавателя");
        }
        else
        {

            Cursor cursor = db.query("subjects", null, "id = '"+currentLesson.subject_id+"'", null, null, null, null);
            if (cursor.moveToFirst()) {
                Log.d("myLessons", "FOUND");
                int indName = cursor.getColumnIndex("name");
                int indType= cursor.getColumnIndex("type");
                setSubject.setText(cursor.getString(indName)+"("+cursor.getString(indType)+")");
            } else
                Log.d("mLog", "0 rows");

            cursor = db.query("lecturers", null, "id = '"+currentLesson.lecturer_id+"'", null, null, null, null);
            if (cursor.moveToFirst()) {
                Log.d("myLessons", "FOUND");
                int indName = cursor.getColumnIndex("name");
                setLecturer.setText(cursor.getString(indName));
            } else
                Log.d("mLog", "0 rows");
            cursor.close();
        }
    }
    /*
    Нажали на "Отмена"
     */
    public void onCancelClick(View v) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Отменить создание занятия? Все данные будут утеряны.");

        builder.setCancelable(false);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
                finish();
            }
        });
        builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    /*
     Нажали на "назад"
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Отменить создание занятия? Все данные будут утеряны.");

        builder.setCancelable(false);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
            }
        });
        builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void clearEditText() {
        setInitialDate();
        setInitialTime();
        setSubject.setText("Выберите предмет");
        setLecturer.setText("Выберите преподавателя");
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(LessonAdd.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(LessonAdd.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
private void setInitialTime(){
        time = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        setTime.setText(DateUtils.formatDateTime(this,
            dateAndTime.getTimeInMillis(),
            DateUtils.FORMAT_SHOW_TIME));
        currentLesson.date = dateAndTime.getTimeInMillis();
        currentLesson.time = DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);
}
    // установка начальных даты и времени
    private void setInitialDate() {
        date = dateAndTime.getTimeInMillis();

        setdateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };


    private void initFirebase() {
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
    }

    /*
    Переходим к окну добавления занятия
     */
    public void ShowSubjectChoice(View v){
        Intent intent = new Intent(LessonAdd.this,Subject.class);

        intent.putExtra("groupId", groupNumber);
        intent.putExtra("subject", currentLesson.subject_id);
        intent.putExtra("institute", institute);
        startActivityForResult(intent,1);

    }
    /*
  Переходим к окну добавления занятия
   */
    public void ShowLecturerChoice(View v){
        Intent intent = new Intent(LessonAdd.this,Lecturer.class);
        intent.putExtra("groupId", groupNumber);
       // lecturer = new LecturerModel("","","");
        intent.putExtra("institute", institute);
        intent.putExtra("lecturer_id", currentLesson.lecturer_id);
        startActivityForResult(intent,2);

    }
    public void ShowStudentChoice(View v){
        isAttendanceFilling = true;
        Intent intent = new Intent(LessonAdd.this,Students.class);
        intent.putExtra("groupId", groupNumber);
        Bundle bundle = new Bundle();
        bundle.putSerializable("attendance",attendance);
        intent.putExtras(bundle);
        //intent.putExtra("attendance", );
        startActivityForResult(intent,3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //выбрали преподавателя
        if (requestCode == 2) {
           // Bundle bundle = data.getExtras();
           // lecturer = new LecturerModel("","","");
            lecturer_id = data.getStringExtra("lecturer_id");
            currentLesson.lecturer_id = lecturer_id;
            if (lecturer_id!= null){
                setData();
            }

        }
        if (requestCode == 1) {
           // String a = data.getStringExtra("name");
            if(data.getStringExtra("subject_id")!=null) {
                subject_id = data.getStringExtra("subject_id");
                currentLesson.subject_id = subject_id;
            }
            if (subject_id!= null) {
                setData();
            }
        }
        if (requestCode == 3) {
          // assert data != null;
            if(resultCode==RESULT_OK) {
                Bundle bundle = data.getExtras();
              // attendance = data.getData();
                attendance = (ArrayList<AttendanceModel>)bundle.getSerializable("selects");
                 // attendance = (ArrayList<AttendanceModel>) bundle.getSerializable("selects");//здесь данные о посещаемости
                 Log.d("selectItemEXIT", String.valueOf(attendance.get(0).student_id));
                 Log.d("selectItemEXIT", String.valueOf(attendance.get(0).status));
            }

        }
    }

    public void saveLesson(View v){

        if(groupNumber!=null&&currentLesson.subject_id!=null&&currentLesson.lecturer_id!=null&&(!isEdit)){

            if(hasConnection(this)){
            lesson = new LessonModel(groupNumber,subject_id,lecturer_id, date, time);

                mFirebaseDatabase.collection("lessons")
                .add(lesson)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("id", "DocumentSnapshot written with ID: " + documentReference.getId());
                        lesson_id = documentReference.getId();
                        int i;
                        for(i = 0; i<attendance.size();i++){
                            att = new AttModelForDatabase(attendance.get(i).student_id,attendance.get(i).status,lesson_id);
                            mFirebaseDatabase.collection("attendance")
                                    .add(att)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("id", "DocumentSnapshot written with ID: " + documentReference.getId());
                                            // lesson_id = documentReference.getId();
                                            Intent intent = new Intent(LessonAdd.this, LessonAdd.class);
                                            setResult(RESULT_OK,intent);
                                            finishActivity(1);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("error", "Error adding document", e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("error", "Error adding document", e);
                    }
                });

            }
            else Toast.makeText(LessonAdd.this, "Ошибка подключения!",
                    Toast.LENGTH_SHORT).show();
        }
        else
            if(isEdit){
                mFirebaseDatabase.collection("lessons")
                        .document(currentLesson.id).set(currentLesson);
                mFirebaseDatabase.collection("attendance").whereEqualTo("lesson_id", currentLesson.id).get()
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
                                       ;
                                        String st = (String) document.get("student_id");
                                       int stat = 0;
                                        for (int j =0;j<attendance.size();j++){
                                            if(st.equals(attendance.get(j).student_id)){
                                                stat = j;
                                                break;
                                            }
                                        }
                                        att = new AttModelForDatabase(attendance.get(stat).student_id, attendance.get(stat).status, currentLesson.id);
                                        mFirebaseDatabase.collection("attendance")
                                                .document(document.getId()).set(att);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }

                        } else {
                            Log.d("myLessons", "Error getting documents: ", task.getException());
                        }
                    }

                });
                Intent intent = new Intent(LessonAdd.this, LessonAdd.class);
                setResult(RESULT_OK,intent);
                finishActivity(3);
                finish();
            }

            else Toast.makeText(LessonAdd.this, "Заполнены не все поля!",
                Toast.LENGTH_SHORT).show();

        }


    public static boolean hasConnection(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
