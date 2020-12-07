package com.a.attendancereportpsu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonAdd extends AppCompatActivity {
    AlertDialog.Builder builder;    //для диалога отмены
    Calendar dateAndTime = Calendar.getInstance();//получаем календарь
    Button setdateTime, setSubject, setLecturer, setStudents;//buttons
    String groupNumber = "123";
    String lecturer = "";
    String date;
    String time;
    public ArrayList<AttendanceModel> attendance = new ArrayList<>();
    FirebaseFirestore mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    DatabaseReference resultRef;
    int index = 0, index1 = 0;
    private List<LessonModel> list_lessons = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_add);
        setdateTime=(Button)findViewById(R.id.dateEdit);
        setSubject=(Button)findViewById(R.id.setSubject);
        setLecturer=(Button)findViewById(R.id.setLecturer);
        setStudents=(Button)findViewById(R.id.setAttendance);
        Intent intent_1 = getIntent();
        groupNumber = intent_1.getStringExtra("group");
        Log.d("GROUP IN LAdd", groupNumber);
        setInitialDateTime();
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
        Intent intent = new Intent(LessonAdd.this, ShowLessons.class);
        setResult(RESULT_OK);
        finish();
    }

    void clearEditText() {
        setInitialDateTime();
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

    // установка начальных даты и времени
    private void setInitialDateTime() {
        date = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        time = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        setdateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
            setTime(setdateTime);
        }
    };


    private void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        // (google-services.json - файл, с настройками для firebase, кот. мы получили во время регистрации)
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        //получаем ссылку для работы с базой данных
        //  mDatabaseReference = mFirebaseDatabase.getReference();

    }

    /*
    Переходим к окну добавления занятия
     */
    public void ShowSubjectChoice(View v){
        Intent intent = new Intent(LessonAdd.this,Subject.class);
        intent.putExtra("groupId", groupNumber);
        startActivityForResult(intent,1);

    }
    /*
  Переходим к окну добавления занятия
   */
    public void ShowLecturerChoice(View v){
        Intent intent = new Intent(LessonAdd.this,Lecturer.class);
        intent.putExtra("groupId", groupNumber);
        intent.putExtra("lecturer", lecturer);
        startActivityForResult(intent,2);

    }
    public void ShowStudentChoice(View v){
        Intent intent = new Intent(LessonAdd.this,Students.class);
        intent.putExtra("groupId", groupNumber);
        startActivityForResult(intent,3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            String a = data.getStringExtra("name");
            Log.d("mLog", a);
            setLecturer.setText(a);
        }
        if (requestCode == 1) {
            String a = data.getStringExtra("name");
            Log.d("mLog", a);
            setSubject.setText(a);
        }
        if (requestCode == 3) {
            assert data != null;
            Bundle bundle = data.getExtras();
            attendance = (ArrayList<AttendanceModel>) bundle.getSerializable("selects");//здесь данные о посещаемости
            Log.d("selectItemEXIT", String.valueOf(attendance.get(0).student_id));
            Log.d("selectItemEXIT", String.valueOf(attendance.get(0).status));
            Log.d("selectItemEXIT", String.valueOf(attendance.get(1).student_id));
            Log.d("selectItemEXIT", String.valueOf(attendance.get(1).status));
            Log.d("selectItemEXIT", String.valueOf(attendance.get(2).student_id));
            Log.d("selectItemEXIT", String.valueOf(attendance.get(2).status));
        }
    }
}
