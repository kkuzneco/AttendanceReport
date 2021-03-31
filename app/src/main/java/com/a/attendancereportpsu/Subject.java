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

public class Subject extends AppCompatActivity {

    RecyclerView subjectListView;
    ArrayList<SubjectModel> subjects;
    FirebaseFirestore mFirebaseDatabase;
    String groupNumber, institute;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    String subject = null;
    int index;
    Button saveSubj;
    SubjectModel subModel;
    SubjectAdapter subjectAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        initFirebase();
        subModel = new SubjectModel("","","", "","");
        dbHelper=new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        subjects = new ArrayList<SubjectModel>();
        subjectAdapter = new SubjectAdapter();
        //subjectAdapter.clearItems();
        subject="";
        saveSubj = (Button)findViewById(R.id.save_btn);
        subjectListView = (RecyclerView) findViewById(R.id.subjectRecycleView);
        Intent intent_1 = getIntent();
        institute = intent_1.getStringExtra("institute");
        groupNumber = intent_1.getStringExtra("groupId");
        if(intent_1.getStringExtra("subject")!=null)
            subject = intent_1.getStringExtra("subject");
        Log.d("GROUP IN SUB",groupNumber);
        formList();
        initRecyclerView();
        if((!subject.equals(""))&&(subjectAdapter.listOfSubjects.size()>0)){
            Log.d("subjectID",subject);
            Cursor cursor = db.query("subjects", null, "id = ?", new String[]{subject}, null, null,null);
            int nameIndex = cursor.getColumnIndex("name");
            int typeIndex = cursor.getColumnIndex("type");
            int i=0;
            index = -1;
            cursor.moveToFirst();
            for(i=0; i<subjectAdapter.listOfSubjects.size(); i++){
                if(subjectAdapter.listOfSubjects.get(i).type.equals(cursor.getString(typeIndex)) && subjectAdapter.listOfSubjects.get(i).name.equals(cursor.getString(nameIndex)))
                    index = i;
            }
            if(index!=-1)
                 subjectAdapter.chosen = index;

        }


        loadSubject();

    }
    public void initRecyclerView(){
        Log.d("mlog", "initRecyclerView");
        //RecyclerView listOfStudents = findViewById(R.id.subjectRecycleView);//привязка из лэйаут
        subjectListView.setLayoutManager(new LinearLayoutManager(this));//менедже
        subjectListView.setAdapter(subjectAdapter);
    }
    public void formList(){

      //  Log.d("mlog", institute);
        db = dbHelper.getWritableDatabase();
        Cursor cursor;
        if(institute!=null){
            Log.d("institute1", institute);

            cursor = db.query("subjects", null, "institute = ?", new String[]{institute}, null, null, null);
        }
        else
           cursor = db.query("subjects", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int groupIndex = cursor.getColumnIndex("group_id");
            int instituteIndex = cursor.getColumnIndex("institute");
            int typeIndex = cursor.getColumnIndex("type");
            do {

                Log.d("mLog", "ID = " + cursor.getString(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", institute = " + cursor.getString(instituteIndex));
                subModel = new SubjectModel(cursor.getString(idIndex),cursor.getString(groupIndex),cursor.getString(nameIndex),cursor.getString(instituteIndex), cursor.getString(typeIndex));
                subjectAdapter.listOfSubjects.add(subModel);
                // lecturerList.add(cursor.getString(nameIndex) + "\n("+cursor.getString(instituteIndex)+")");
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_update://если выбрано "Обновить список"
                subjectAdapter.clearItems();
                //subjectAdapter.listOfLecturers.clear();
                formList();
                subjectAdapter.setItems();
                return true;
            case R.id.action_uFb://если выбрано "Обновиться из облака"
                loadSubject();
                return true;
        }
        return false;
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
    public void loadSubject() {
        Log.d("mlog", "loadSubjects");

        db = dbHelper.getWritableDatabase();
        dbHelper.removeSubjectRows(db);
       // lecturer = new LecturerModel("","","");
        mFirebaseDatabase.collection("subjects").whereEqualTo("group_id", groupNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    subModel.groupId = groupNumber;
                                    subModel.institute = document.get("institute").toString();
                                    subModel.name = document.get("name").toString();
                                    subModel.id = document.getId().toString();
                                    subModel.type = document.get("type").toString();
                                    db.execSQL("insert into subjects(id, name, group_id, institute, type) values (" + "'"+subModel.id+"', "+"'"+subModel.name+"', "+"'"+subModel.groupId +"',"+"'"+subModel.institute+"',"+"'"+subModel.type+"');");
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

    public void Save(View v){
        Intent intent = new Intent(Subject.this, LessonAdd.class);
        Bundle bundle = new Bundle();
        if(subjectAdapter.chosen >= 0) {
            subModel = subjectAdapter.listOfSubjects.get(subjectAdapter.chosen);
            bundle.putString("subject_id", subModel.id);

           // bundle.putString("institute", subModel.institute);
        }
        else{
          //  bundle.putString("institute", subModel.institute);
            bundle.putString("subject_id", null);
        }
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finishActivity(1);
        finish();
    }
}

