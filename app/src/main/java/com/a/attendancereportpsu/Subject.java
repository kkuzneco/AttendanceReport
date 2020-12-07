package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.errorprone.annotations.Var;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Subject extends AppCompatActivity {

    ListView subjectListView;
    ArrayList<String> subjectList;
    ArrayList<String> subjects;
    int position = 0;
    int index = 0;
    FirebaseFirestore mFirebaseDatabase;
    String groupNumber;
    SubjectModel[] subjectList1 = new SubjectModel[9];
    //Collection<SubjectModel> collection =new ArrayList<>();

    // RecyclerView listOfSubject;
    //SubjectAdapter subAdapter;
    Button saveAttendance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        initFirebase();
        subjectList = new ArrayList<String>();
        saveAttendance = (Button)findViewById(R.id.button);
        subjectListView = (ListView)findViewById(R.id.subjectListView);
        subjectListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Intent intent_1 = getIntent();
        groupNumber = intent_1.getStringExtra("groupId");

        Log.d("GROUP IN SUB",groupNumber);
        subjectList.add("subject");
      // newF();
        prepare();



      //  Log.d("ARRAY", subjectList.get(2).toString());
       /* ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this,
                android.R.layout.simple_list_item_checked, subjectList);
        subjectListView.setAdapter(adapter);
        subjectListView.setVisibility(View.VISIBLE);*/
        //    loadSubjects();
    }
    private void prepare(){
       subjects= getSubjectList(groupNumber);
   //    Log.d("ff", subjects.get(3));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, subjects);
        subjectListView.setAdapter(adapter);
    }
    public ArrayList<String> getSubjectList(String groupNumber){
     //   subjectList = new ArrayList<String>();
        subjectList.add("testSubject");
        Task<QuerySnapshot> querySnapshotTask = mFirebaseDatabase.collection("subjects").whereEqualTo("group_id", groupNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                 prepareSubjectList(document.get("name").toString());
//                                 SubjectModel sm=document.toObject(SubjectModel.class);
                                   // subjectList.add(document.get("name").toString());
                                   // for (int i = 0; i <= subjectList.size() - 1; i++)
                                    //    Log.d("element", subjectList.get(i));
                                    //Log.d("jop", document.getId() + " => " + document.getData());
                                    //index += 1;
                                }
                            }
                            Log.d("element", String.valueOf(subjectList.size()));
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }


                });
        return subjectList;

    }

    public void prepareSubjectList(String subj)
    {
        String s = new String();
        s = subj;
        subjectList.add(s);
        subjectList1[index] = new SubjectModel("22407", subj);
        Log.d("size", String.valueOf(subjectList.size()));


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

    public void Save(View v){
        Intent intent = new Intent(Subject.this, LessonAdd.class);
        String subjectName = "Тестирование ПО";
        intent.putExtra("name", subjectName);
        setResult(RESULT_OK,intent);
        finish();
    }
}

