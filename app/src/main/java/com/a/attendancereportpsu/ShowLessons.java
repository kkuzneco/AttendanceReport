package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import static android.text.format.DateUtils.*;

public class ShowLessons extends AppCompatActivity {
    AlertDialog.Builder builder;    //для диалога выхода
    Button date;                    //отображение и выбор даты
    Calendar dateAndTime = Calendar.getInstance();//получить текущие дату и время
    FirebaseFirestore mFirebaseDatabase;
    String uId;
    String groupId;
    DocumentReference docRef;
    StudentModel headmen;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_show_lessons);
        date = (Button) findViewById(R.id.date);
        getGroupNumber();
        setInitialDate();
    }



    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();

        }
    };
    /*
    функция выхода из учетной записи
     */
    public void exit(){
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Выйти из учетной записи?");

        builder.setCancelable(false);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ShowLessons.this, MainActivity.class);
                setResult(RESULT_OK,intent);
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
    Установить текущую дату
     */
    public void setInitialDate() {
        date.setText(formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR));
         }

    /*
    Выбрать дату
     */
    public void setDate(View v) {
        new DatePickerDialog(ShowLessons.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    public String getUID(){
        return FirebaseAuth.getInstance().getUid();
    }
    public String getGid(){
        return groupId;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
        Обработка нажатия элементов меню
    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_exit ://если выбрано "Выход"
                exit();
                return true;
            case R.id.action_report://если выбрано "Создать отчет"
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    /*
    Добавление занятия, ждем ответа, код запроса 1
     */
    public void addLesson(View v){

        Intent intent = new Intent(ShowLessons.this, LessonAdd.class);
        intent.putExtra("group", getGroupNumber());
        Log.d("GROUP IN SL", groupId);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initFirebase() {
        // (google-services.json - файл, с настройками для firebase, кот. мы получили во время регистрации)
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();

    }

    public String getGroupNumber() {
        //получаем текушего пользователя для определения группы
      ///  final String[] gId = {""};
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null) {
            //получаем идентификатор пользователя - старосты
            uId = currentFirebaseUser.getUid();
            Log.d("User Id", uId);
           // headmen = new StudentModel("123", "student");
            // mFirebaseDatabase.collection("headmen").document(uId).set(headmen);
            DocumentReference docRef = mFirebaseDatabase.collection("headmen").document(uId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            groupId = document.getString("groupId");
                            Log.d("TAG", "DocumentSnapshot data: " + groupId);


                        } else {
                            Log.d("TAG", "No such document");
                           groupId ="";
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                      groupId ="";
                    }
                }
            });
            return groupId;
        }
        else return "";
    }

}
