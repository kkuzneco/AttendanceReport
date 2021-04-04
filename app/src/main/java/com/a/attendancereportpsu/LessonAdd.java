package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonAdd extends AppCompatActivity{
    AlertDialog.Builder builder;    //для диалога отмены
    Calendar dateAndTime = Calendar.getInstance();//получаем календарь
    Button setdateTime, setSubject, setLecturer, setStudents, setTime, buttonForTesting;//buttons
    String groupNumber = "123", lecturer_id = null, subject_id=null, lesson_id;
    SQLiteDatabase db;
    AttModelForDatabase att;
    LessonModel lesson;
    long date;
    String time;
    String subject = null, institute = null;
    public ArrayList<AttendanceModel> attendance = new ArrayList<>();
    FirebaseFirestore mFirebaseDatabase;

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

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        Intent intent_1 = getIntent();
        groupNumber = intent_1.getStringExtra("group");

        Log.d("GROUP IN LAdd", groupNumber);
        setInitialDate();
        setInitialTime();
        initFirebase();
        Log.d("TIIIME",  DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME));

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
        intent.putExtra("subject", subject_id);
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
        intent.putExtra("lecturer_id", lecturer_id);
        startActivityForResult(intent,2);

    }
    public void ShowStudentChoice(View v){
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
            if (lecturer_id!= null){
                institute = data.getStringExtra("institute");
                //String helper = "id = '"+String.valueOf(lecturer_id)+"'";
                Log.d("mLog", String.valueOf(lecturer_id));
                Cursor helper = db.query("lecturers",null, "id = ?", new String[]{lecturer_id}, null, null,null);
                helper.moveToFirst();
                int IndexOfName = helper.getColumnIndex("name");
                Log.d("mLog", String.valueOf(helper));
                String result = helper.getString(IndexOfName);
                Log.d("mLog", result);
                setLecturer.setText(result);
            }

        }
        if (requestCode == 1) {
           // String a = data.getStringExtra("name");
            if(data.getStringExtra("subject_id")!=null)
               subject_id = data.getStringExtra("subject_id");
            if (subject_id!= null) {
                Cursor helper = db.query("subjects", null, "id = ?", new String[]{subject_id}, null, null, null);
                helper.moveToFirst();
                int IndexOfName = helper.getColumnIndex("name");
                subject = helper.getString(IndexOfName);
                // Log.d("mLog", a);
                setSubject.setText(subject);
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
        if(groupNumber!=null&&subject_id!=null&&lecturer_id!=null){
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
