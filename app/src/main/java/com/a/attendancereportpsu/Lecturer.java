package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Lecturer extends AppCompatActivity {
    ListView lecturerListView;
    ArrayList<String> lecturerList;
    ArrayList<String> lecturers;
    int position = 0;
    int index = 0;
    FirebaseFirestore mFirebaseDatabase;
    String groupNumber;
    String lecturer;
    Button saveLecturer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

        initFirebase();
        lecturerList = new ArrayList<String>();
        saveLecturer = (Button)findViewById(R.id.save_btn);
        lecturerListView = (ListView)findViewById(R.id.lecturerListView);
        lecturerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Intent intent_1 = getIntent();
        groupNumber = intent_1.getStringExtra("groupId");
        lecturer = intent_1.getStringExtra("lecturer");
        Log.d("GROUP IN SUB",groupNumber);
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        lecturerList.add("lecturer1");
        // newF();
        prepare();
    }

    private void prepare(){
        lecturers= getLecturersList(groupNumber);
        //    Log.d("ff", subjects.get(3));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, lecturers);
       lecturerListView.setAdapter(adapter);
    }
    public ArrayList<String> getLecturersList(String groupNumber){
        //   subjectList = new ArrayList<String>();
        lecturerList.add("testLecturer");

        mFirebaseDatabase.collection("lecturers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    prepareLecturerList(document.get("fullname").toString());
//                                 SubjectModel sm=document.toObject(SubjectModel.class);
                                    // subjectList.add(document.get("name").toString());
                                    // for (int i = 0; i <= subjectList.size() - 1; i++)
                                    //    Log.d("element", subjectList.get(i));
                                    //Log.d("jop", document.getId() + " => " + document.getData());
                                    //index += 1;
                                }
                            }
                            Log.d("element", String.valueOf(lecturerList.size()));
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }


                });
        return lecturerList;

    }

    public void prepareLecturerList(String subj)
    {
        String s = new String();
        s = subj;
        lecturerList.add(s);

        Log.d("size", String.valueOf(lecturerList.size()));


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

    public void save(View v){
        Intent intent = new Intent(Lecturer.this, LessonAdd.class);

        intent.putExtra("name", "Иванов Иван Иванович");
        setResult(RESULT_OK,intent);
        finish();
    }
}
