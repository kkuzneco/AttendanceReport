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

import java.util.ArrayList;

public class Lecturer extends AppCompatActivity {
    ArrayList<String> lecturerList;
    ArrayList<String> lecturers;
    int position = 0;
    int index = 0;

    FirebaseFirestore mFirebaseDatabase;
    String groupNumber;
    DatabaseHelper dbHelper;
    LecturerModel lecturer;
    SQLiteDatabase db;
    LecturerAdapter lecturerAdapter;

    Button saveLecturer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);
        initFirebase();
        String lecturer_id;
        //адаптер списка
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
      //  dbHelper.onUpgrade(db, db.getVersion(), db.getVersion()+1);
        lecturerAdapter = new LecturerAdapter();

        //lecturerList = new ArrayList<String>();
        //кнопка сохранения
        saveLecturer = (Button)findViewById(R.id.save_btn);
       // dbHelper.onUpgrade(db, db.getVersion(), db.getVersion()+1);
        //lecturerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //получаем номер группы и предыдущий выбор
        Intent intent_1 = getIntent();

        formList();
        initRecyclerView();

        //dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);

        Log.d("mlog", "Окно открылось!");
        saveLecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Lecturer.this, LessonAdd.class);
                Bundle bundle = new Bundle();
                if(lecturerAdapter.chosen >= 0) {
                    lecturer = lecturerAdapter.listOfLecturers.get(lecturerAdapter.chosen);
                    bundle.putString("lecturer_id", lecturer.id);
                    bundle.putString("institute", lecturer.institute);
                }
                else{
                    bundle.putString("institute", lecturer.institute);
                    bundle.putString("lecturer_id", null);
                }
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finishActivity(2);
                finish();
            }
        });
        lecturer_id = intent_1.getStringExtra("lecturer_id");
       // if(lecturer_id!=null)
         //   Log.d("getId",lecturer_id);
        //ищем индекс с созданном масиве текущего выбранного преподавателя
        if(lecturer_id != null) {
            int i;
            for (i = 0; i < lecturerAdapter.listOfLecturers.size(); i++){
              if (lecturerAdapter.listOfLecturers.get(i).id.equals(lecturer_id)){
                    lecturerAdapter.chosen = i;
                    Log.d("getIdh",String.valueOf(lecturerAdapter.chosen));
             }

        }
        }
        else
            lecturerAdapter.chosen = 0;
        groupNumber = intent_1.getStringExtra("groupId");
        //lecturer = intent_1.getStringExtra("lecturer");
        Log.d("GROUP IN SUB",groupNumber);
        loadLecturer();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_update://если выбрано "Обновить список"
                lecturerAdapter.clearItems();
                lecturerAdapter.listOfLecturers.clear();
                formList();
                lecturerAdapter.setItems();
                return true;
            case R.id.action_uFb://если выбрано "Обновиться из облака"
                loadLecturer();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public void initRecyclerView(){
        Log.d("mlog", "initRecyclerView");
        RecyclerView listOfStudents = findViewById(R.id.lecturerRecycleView);//привязка из лэйаут
        listOfStudents.setLayoutManager(new LinearLayoutManager(this));//менедже
        listOfStudents.setAdapter(lecturerAdapter);
    }

    public void loadLecturer() {
        Log.d("mlog", "loadStudents");

        db = dbHelper.getWritableDatabase();
        dbHelper.removeLecturerRows(db);
        lecturer = new LecturerModel("","","");
        mFirebaseDatabase.collection("lecturers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    lecturer.institute = document.get("institute").toString();;
                                    lecturer.name = document.get("fullname").toString();
                                    lecturer.id = document.getId().toString();
                                    db.execSQL("insert into lecturers(id, name, institute) values (" + "'"+lecturer.id+"', "+"'"+lecturer.name+"', "+"'"+lecturer.institute+"');");
                                    //Log.d("mlog", "insert into students(id, name, group_id) values (" + "'"+student.id+"',"+"'"+student.name+"',"+"'"+student.group_id+"');");
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
        Cursor cursor = db.query("lecturers", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int instituteIndex = cursor.getColumnIndex("institute");
            do {

                Log.d("mLog", "ID = " + cursor.getString(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", institute = " + cursor.getString(instituteIndex));
                lecturer = new LecturerModel(cursor.getString(idIndex),cursor.getString(nameIndex),cursor.getString(instituteIndex));
                lecturerAdapter.listOfLecturers.add(lecturer);
               // lecturerList.add(cursor.getString(nameIndex) + "\n("+cursor.getString(instituteIndex)+")");
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();

    }



    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        // (google-services.json - файл, с настройками для firebase, кот. мы получили во время регистрации)
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        //получаем ссылку для работы с базой данных
        //  mDatabaseReference = mFirebaseDatabase.getReference();

    }


}
