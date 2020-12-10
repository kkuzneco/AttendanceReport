package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
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
    StudentAdapter studAdapter;
    ArrayList<StudentModel> studentsList = new ArrayList<>();
    FirebaseFirestore mFirebaseDatabase;
    String groupNumber = "22407";
   public  StudentModel st;
    Button saveAtt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        saveAtt = (Button)findViewById(R.id.save_btn);
        initFirebase();
        saveAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Students.this, LessonAdd.class);
                //   ArrayList<Boolean> array = new ArrayList<>();
                Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(0).status));
                Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(1).status));
                Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(2).status));
                Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(3).status));
                Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(4).status));
                Bundle bundle = new Bundle();
                bundle.putSerializable("selects",studAdapter.attendance);
                //    Log.d("selectItemEXIT", String.valueOf(studAdapter.selects[1]));
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);

                finishActivity(3);
                finish();
            }
        });
        studentsList.add(new StudentModel("1111", groupNumber, "Студент1"));
    studentsList.add(new StudentModel("2222", groupNumber, "Студент2"));
    studentsList.add(new StudentModel("3333", groupNumber, "Студент3"));
    studentsList.add(new StudentModel("4444", groupNumber, "Студент4"));
    studentsList.add(new StudentModel("5555", groupNumber, "Студент5"));
    studAdapter = new StudentAdapter();
        loadStudents();
        initRecyclerView();
    }
    public void initRecyclerView(){
        Log.d("element", String.valueOf(studentsList.get(studentsList.size()-1).getName()));
        RecyclerView listOfStudents = findViewById(R.id.studentsRecycler);//привязка из лэйаут
        listOfStudents.setLayoutManager(new LinearLayoutManager(this));//менедже
        listOfStudents.setAdapter(studAdapter);
    }

    public void loadStudents() {
     //   studentsList = getStudents();
       // studentsList.add("STUDNT");

        mFirebaseDatabase.collection("students").whereEqualTo("group_id", groupNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    studentsList.add(new StudentModel(document.getId().toString(), document.get("group_id").toString(), document.get("name").toString()));
                                    Log.d("element", String.valueOf(studentsList.get(studentsList.size()-1).getName()));
//                                 SubjectModel sm=document.toObject(SubjectModel.class);
                                    // subjectList.add(document.get("name").toString());
                                    // for (int i = 0; i <= subjectList.size() - 1; i++)
                                    //    Log.d("element", subjectList.get(i));
                                    //Log.d("jop", document.getId() + " => " + document.getData());
                                    //index += 1;
                                }
                            }
//                            Log.d("element", String.valueOf(studentsList.size()));
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }


                });
       studAdapter.setItems(studentsList);


    }

  //  public ArrayList<StudentModel> getStudents() {
  //     return Arrays.asList(studentList);
 ///   }

    public void save(View v){
        Intent intent = new Intent(Students.this, LessonAdd.class);
        //   ArrayList<Boolean> array = new ArrayList<>();
       Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(0).status));
        Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(1).status));
        Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(2).status));
        Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(3).status));
        Log.d("selectItemEXIT", String.valueOf(studAdapter.attendance.get(4).status));
        Bundle bundle = new Bundle();
        bundle.putSerializable("selects",studAdapter.attendance);
        //    Log.d("selectItemEXIT", String.valueOf(studAdapter.selects[1]));
         intent.putExtras(bundle);
        setResult(RESULT_OK,intent);

      finishActivity(3);
      finish();
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
