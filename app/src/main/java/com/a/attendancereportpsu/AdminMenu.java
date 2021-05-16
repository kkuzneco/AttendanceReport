package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.a.attendancereportpsu.ui.dashboard.DashboardFragment;
import com.a.attendancereportpsu.ui.home.HomeFragment;
import com.a.attendancereportpsu.ui.notifications.NotificationsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminMenu extends AppCompatActivity {
    ImageButton doReport;
    AdminDBHelper dbHelper;
    SQLiteDatabase db;
    FirebaseFirestore mFirebaseDatabase;
    final String INSTITUTE = "institute";
    SharedPreferences sPref;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        Log.d("chosen","chosen report");
                        loadFragment(HomeFragment.newInstance());
                        return true;
                    case R.id.action_dial:
                        Log.d("chosen","chosen 2");
                        loadFragment(DashboardFragment.newInstance());
                        return true;
                    case R.id.action_mail:
                        Log.d("chosen","chosen 3");
                        loadFragment(NotificationsFragment.newInstance());
                        return true;
                }
                Log.d("chosen","chosen 3");
                return false;
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        dbHelper = new AdminDBHelper(this);
        db = dbHelper.getWritableDatabase();
        sPref = getSharedPreferences("institute",MODE_PRIVATE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(ShowLessons.hasConnection(this)){
            initFirebase();
            getInstitute();
            fillGroups();

        }

    }
    public void chooseCourse(){
        // setup the alert builder
        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose some animals");

// add a checkbox list
        String[] animals = {"1", "2", "3", "4", "5"};
        boolean[] checkedItems = {true, false, false, true, false};
        builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public interface MyCallback {
        void onCallback();
    }
    public void initFirebase() {
       //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
       FirebaseApp.initializeApp(this);
       //получаем точку входа для базы данных
       mFirebaseDatabase = FirebaseFirestore.getInstance();


    }

    private void fillGroups(){
        Log.d("fill groups", "поиск...");
        String i =  sPref.getString("institute", "1");
        Log.d("fill groups",i);
        dbHelper.removeGroupsRows(db);
        mFirebaseDatabase.collection("groups").whereEqualTo("institute", sPref.getString("institute", "1")).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {

                                    try {
                                        Log.d("Admin groups", "поиск...");
                                        Log.d("Admin groups","insert into groups(id, number, course) values (" + "'" + document.getId() + "'," + "'" + document.get("number") + "'," + "'" + document.get("lecturer_id") + "'," + (Long)document.get("date") +  ","+ "'" + document.get("time") + "');");
                                        //  lessons.add(new LessonModel(document.getId(), String.valueOf(document.get("group_id")),  String.valueOf(document.get("subject_id")), String.valueOf(document.get("lecturer_id")), Long.valueOf((Long)document.get("date")),String.valueOf(document.get("time"))));
                                        long groupNumber = (long)document.get("number");
                                        long course;
                                        if(groupNumber<1000)
                                            course = (groupNumber%100)/10;
                                        else
                                            course = (groupNumber%1000)/100;
                                        db.execSQL("insert into groups(id, number, course) values (" + "'" + document.getId() + "'," + "'" + document.get("number") + "'," + "'" + course + "');");
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }


                        } else {
                            Log.d("myLessons", "Error getting documents: ", task.getException());
                        }

                    }

                });
    }
    public void getInstitute() {
        //получаем текушего пользователя для определения группы
        ///  final String[] gId = {""};
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null) {
            //получаем идентификатор пользователя - старосты
            String uId = currentFirebaseUser.getUid();
            Log.d("User Id", uId);
            // headmen = new StudentModel("123", "student");
            // mFirebaseDatabase.collection("headmen").document(uId).set(headmen);

            DocumentReference docRef = mFirebaseDatabase.collection("users").document(uId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String institute = document.getString("institute");

                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString(INSTITUTE, institute);
                            ed.commit();
                            Log.d("TAG", "DocumentSnapshot data: " + institute);


                        } else {
                            Log.d("TAG", "No such document");

                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());

                    }

                }
            });

            return ;
        }
        else return;
    }
}