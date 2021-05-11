package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static android.text.format.DateUtils.*;

public class ShowLessons extends AppCompatActivity {
    AlertDialog.Builder builder;    //для диалога выхода
    Button date;                    //отображение и выбор даты
    Calendar dateAndTime = Calendar.getInstance();//получить текущие дату и время
    FirebaseFirestore mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase firebaseData;
    LessonModel lesson;
    String uId;
    public FirebaseAuth mAuth;
    long start; //время начала дня для фильтрации по занятиям
    long finish;//время окончания дня
    RecyclerView listOfLessons; // список предметов
    ArrayList<LessonCard> cards; // список карточкек предметов
    String groupId;
    private ArrayList<LessonModel> list_lessons = new ArrayList<>();
    StudentModel student;
    LessonAdapter lessonAdapter;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_lessons = new ArrayList<>();
        setContentView(R.layout.activity_show_lessons);
        date = (Button) findViewById(R.id.date);
        listOfLessons = findViewById(R.id.lessonRecycler);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        if(hasConnection(this)){
            initFirebase();
            groupId=getGroupNumber();
            // обновляемся из облачной базы при создании
            formLessonList(new ShowLessons.MyCallback() {
                @Override
                public void onCallback() {
                    createListFromDatabase();
                }
            });
        }
        mAuth = FirebaseAuth.getInstance();
        user_check();
        cards = new ArrayList<>();
        setInitialDate();
        //создаем модель студента. Далее используется для заполения БД
        student = new StudentModel("","","");
       // list_lessons.add(new LessonModel("22407", "Subject","lecturer",1617540, "12:00:00" ));
        initRecyclerView();
    }
    public interface MyCallback {
        void onCallback();
    }
    private void user_check(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.equals(null)) {
            Intent intent = new Intent(ShowLessons.this, MainActivity.class);
            startActivity(intent);
        }
    }
    public void formLessonList(ShowLessons.MyCallback myCallBack){
        dbHelper.removeLessonsRows(db);
        mFirebaseDatabase.collection("lessons").whereEqualTo("group_id", groupId).get()
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
    private void createListFromDatabase(){
        list_lessons.clear();
        cards.clear();
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("lessons", null, "date>="+start+" and date<="+finish+";", null, null, null, "date");
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
                lesson = new LessonModel(cursor.getString(idIndex),groupId, cursor.getString(subjectIndex), cursor.getString(lecturerIndex), cursor.getLong(dateIndex), cursor.getString(timeIndex));
                list_lessons.add(lesson);
                Cursor cursor1 = db.query("subjects", null, "id = '"+lesson.subject_id+"'", null, null, null, null);
                if (cursor1.moveToFirst()) {
                    int idName = cursor1.getColumnIndex("name");
                    int idType = cursor1.getColumnIndex("type");
                    cards.add(new LessonCard(cursor1.getString(idName), cursor1.getString(idType), lesson.time));
                }
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");

        cursor.close();
        initRecyclerView();
    }

    public void initRecyclerView(){
        lessonAdapter = new LessonAdapter(cards);
        listOfLessons.setLayoutManager(new LinearLayoutManager(this));//менедже
        listOfLessons.setAdapter(lessonAdapter);
    }
    void onDeleteClick(int position){
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы уверены, что хотите удалить занятие?");

        builder.setCancelable(false);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cards.remove(position);
                String lessonId = list_lessons.get(position).id;
                mFirebaseDatabase.collection("lessons").document(lessonId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("myLessons", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("myLessons", "Error deleting document", e);
                            }
                        });
                      mFirebaseDatabase.collection("attendance").whereEqualTo("lesson_id", lessonId).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            mFirebaseDatabase.collection("attendance").document(document.getId()).delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("myLessons", "DocumentSnapshot successfully deleted!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("myLessons", "Error deleting document", e);
                                                        }
                                                    });
                                        }
                                    }
//                            Log.d("element", String.valueOf(studentsList.size()));
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }

                            }

                        });
                list_lessons.remove(position);
                db.execSQL("DELETE FROM lessons WHERE ID = '"+ lessonId+"';");
                initRecyclerView();

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
    void onEditClick(int position){
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Редактировать занятие?");

        builder.setCancelable(false);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ShowLessons.this, LessonAdd.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("lesson", (Serializable) list_lessons.get(position));
                intent.putExtra("group", getGroupNumber());
                intent.putExtras(bundle);
                Log.d("GROUP IN SL", groupId);
                startActivityForResult(intent,3);
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
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(year,monthOfYear,dayOfMonth,23,59,59);
            finish = dateAndTime.getTimeInMillis();
            dateAndTime.set(year,monthOfYear,dayOfMonth,0,0,0);
            start = dateAndTime.getTimeInMillis();
            setInitialDate();
            cards.clear();
            createListFromDatabase();
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
  /*
            Создание меню
   */
    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        getMenuInflater().inflate(R.menu.menu_main, menu2);
        return true;
    }

    /*
        Обработка нажатия элементов меню
    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.update:
                formLessonList(new ShowLessons.MyCallback() {
                    @Override
                    public void onCallback() {
                        createListFromDatabase();
                        initRecyclerView();
                    }
                });
                return true;
            case R.id.action_exit://если выбрано "Выход"
                exit();
                return true;
            case R.id.action_report://если выбрано "Создать отчет"
                Intent intent = new Intent(ShowLessons.this, Report.class);
                intent.putExtra("group",groupId);
                startActivityForResult(intent,2);
                Log.d("TAG", groupId);
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("lesson", null);
        Log.d("GROUP IN SL", groupId);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1)
            Toast.makeText(ShowLessons.this, "Занятие создано!",
                Toast.LENGTH_SHORT).show();
        if(requestCode ==3)
            Toast.makeText(ShowLessons.this, "Изменения применены!",
                    Toast.LENGTH_SHORT).show();

    }

    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        firebaseData = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseData.getReference();
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
                      groupId =null;
                    }
                }
            });

            return groupId;
        }
        else return null;
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
