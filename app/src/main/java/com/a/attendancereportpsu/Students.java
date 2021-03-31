package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class Students extends AppCompatActivity implements Serializable {
   //адаптер для отображения студентов группы
    StudentAdapter studAdapter;
    ArrayList<AttendanceModel> attendance;
    //сюда положим список студентов
    // создаем объект для создания и управления версиями БД
    DatabaseHelper dbHelper;
    StudentModel student;
    SQLiteDatabase db ;
    ArrayList<StudentModel> studentsList;
    FirebaseFirestore mFirebaseDatabase;
    String groupNumber;
    Button update;
    Button saveAtt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mlog", "ONCREATE");
        setContentView(R.layout.activity_students);
        studAdapter = new StudentAdapter();
        saveAtt = (Button)findViewById(R.id.save_btn);
        dbHelper = new DatabaseHelper(this);
        Intent intent = getIntent();

        groupNumber = intent.getStringExtra("groupId");
        initFirebase();
        Log.d("myBluetooth",groupNumber);
        formList();
        initRecyclerView();
        studAdapter.setItems();
        Bundle bundle = intent.getExtras();
        attendance = (ArrayList<AttendanceModel>)bundle.getSerializable("attendance");
        if(attendance!=null)
            studAdapter.check_items(attendance);
        Log.d("mlog", "Окно открылось!");
        saveAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Students.this, LessonAdd.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selects",studAdapter.attendance);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finishActivity(3);
                finish();
                db.close();
            }
        });
        loadStudents();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Students.this, LessonAdd.class);
        Bundle bundle = new Bundle();
        setResult(0,intent);
        finish();
    }

    public void initRecyclerView(){
        Log.d("mlog", "initRecyclerView");
        RecyclerView listOfStudents = findViewById(R.id.studentsRecycler);//привязка из лэйаут
        listOfStudents.setLayoutManager(new LinearLayoutManager(this));//менедже
        listOfStudents.setAdapter(studAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.menu_students, menu1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_update://если выбрано "Обновить список"
                studAdapter.clearItems();
                formList();
                studAdapter.setItems();
                return true;
            case R.id.action_uFb://если выбрано "Обновиться из облака"
                loadStudents();
                return true;
            case R.id.bluetooth_check://если выбрано "Отметить по Bluetooth"
                //Intent intent = new Intent(Students.this, BluetoothCheck.class);
               // startActivity(intent);
                return true;
            case R.id.register:
                Intent intent1 = new Intent(Students.this, BluetoothRegister.class);
                intent1.putExtra("groupId", groupNumber);
                startActivity(intent1);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public void loadStudents() {
        Log.d("mlog", "loadStudents");

        db = dbHelper.getWritableDatabase();
        dbHelper.removeRows(db);
        student = new StudentModel("","","");
        mFirebaseDatabase.collection("students").whereEqualTo("group_id", groupNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    student.group_id = document.get("group_id").toString();;
                                    student.name = document.get("name").toString();
                                    student.id = document.getId().toString();
                                    db.execSQL("insert into students(id, name, group_id) values (" + "'"+student.id+"',"+"'"+student.name+"',"+"'"+student.group_id+"');");
                                    Log.d("mlog", "insert into students(id, name, group_id) values (" + "'"+student.id+"',"+"'"+student.name+"',"+"'"+student.group_id+"');");
                                }
                            }
//                            Log.d("element", String.valueOf(studentsList.size()));
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }

                });


    }
    public void formList(){
        Log.d("mlog", "formList");
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("students", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int groupIndex = cursor.getColumnIndex("group_id");
            do {

                Log.d("mLog", "ID = " + cursor.getString(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", group = " + cursor.getString(groupIndex));
                student = new StudentModel(cursor.getString(idIndex),cursor.getString(groupIndex),cursor.getString(nameIndex));
                studAdapter.listOfStudents.add(student);
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();

    }

    public void save(View v){

        Intent intent = new Intent(Students.this, LessonAdd.class);
       Bundle bundle = new Bundle();
       bundle.putSerializable("selects",studAdapter.attendance);
       intent.putExtras(bundle);
       //  intent.putSer("selects", studAdapter.attendance);
        setResult(RESULT_OK,intent);
      //finishActivity(3);
      finish();
    }

    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        //получаем ссылку для работы с базой данных
    }
}
