package com.a.attendancereportpsu.ui.home;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.a.attendancereportpsu.AdminDBHelper;
import com.a.attendancereportpsu.AdminMenu;
import com.a.attendancereportpsu.LessonModel;
import com.a.attendancereportpsu.R;
import com.a.attendancereportpsu.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private ArrayList<Integer> chosenCourses;
    private HomeViewModel homeViewModel;
    Button button1;
    Button button2;
    Button create;
    Button dateStart;
    FirebaseFirestore mFirebaseDatabase;
    Calendar dateAndTime;
    Button dateFinish;
    AdminDBHelper dbHelper;
    SQLiteDatabase db;
    long start;
    long finish;

    Integer[] courses = null;
   Integer[] groups = null;
    public HomeFragment(){

    }

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
     public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        button1 = (Button) root.findViewById(R.id.course);
        button1.setOnClickListener(this);
        create =  (Button) root.findViewById(R.id.create);
        button2 = (Button) root.findViewById(R.id.grp);
        dateStart = (Button) root.findViewById(R.id.startDate);
        dateFinish= (Button) root.findViewById(R.id.finishDate);
        button2.setOnClickListener(this);
        dateAndTime = Calendar.getInstance();
        courses = courseFilter();
        dateFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFinish();
            }
        });
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateStart();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              createReps();
            }
        });
        if (hasPermissions()){
            // our app has permissions.
            makeFolder();

        }
        dbHelper = new AdminDBHelper(getContext());
        db = dbHelper.getReadableDatabase();
        chosenCourses = new ArrayList<Integer>();
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = PermissionChecker.checkCallingOrSelfPermission(getContext(), perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    public void setDateStart() {
        new DatePickerDialog(getContext(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    public void setDateFinish() {
        new DatePickerDialog(getContext(), d1,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(year,monthOfYear,dayOfMonth,0,0,0);
            setInitialDateStart();
        }
    };
    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(year,monthOfYear,dayOfMonth,23,59,59);
            setInitialDateFinish();
        }
    };

    private void setInitialDateStart() {
        // time = DateUtils.formatDateTime(this,  dateAndTime.getTimeInMillis(),DateUtils.FORMAT_SHOW_TIME);
        String date1 = DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);
        String date2= DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        Log.d("myLessonsD", date1 +" "+date2);

        String date = DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        start = dateAndTime.getTimeInMillis();
        dateStart.setText(date);
        //dbHelper.removeAttRows(dbHelper.getWritableDatabase());
       // Log.d("myReport", String.valueOf(students.size()));
        //lessonsIds.clear();
        //createLessonsWithDateFilter();
    }

    private void setInitialDateFinish() {
        String date1 = DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);
        String date2= DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        Log.d("myLessonsD", date1 +" "+date2);
        String date = DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        finish = dateAndTime.getTimeInMillis();
        dateFinish.setText(date);

       // lessonsIds.clear();
        // createLessonsWithDateFilter();
    }
    private void makeFolder(){
     Context c = getContext();
        File file = new File(c.getExternalFilesDir(null).getAbsolutePath(),"Study reports");

        if (!file.exists()){
            Boolean ff = file.mkdir();
            if (ff){
                Log.d("myLessons", "создали папку");
                Toast.makeText(c, "Folder created successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("myLessons", "не создали папку");
                Toast.makeText(getContext(), "Failed to create folder", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Log.d("myLessons", "папка есь");
            //  Toast.makeText(Report.this, "Folder already exist", Toast.LENGTH_SHORT).show();
        }
    }
    int translateIdToIndex(int id) {
        int index = -1;
        switch (id) {
            case R.id.course:
                Log.d("Home", "func1");
                index = 1;
                break;
            case R.id.grp:
                Log.d("Home", "func");
                index = 2;
                break;
        }
        return index;
    }
    @Override
    public void onClick(View v) {
        int buttonIndex = translateIdToIndex(v.getId());
        Log.d("Home", String.valueOf(buttonIndex));
        // Временный код для получения индекса нажатой кнопки
        Toast.makeText(getActivity(), String.valueOf(buttonIndex),
                Toast.LENGTH_SHORT).show();
        // setup the alert builder
        if(buttonIndex ==1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Выберите курс(-ы)");
            String[] courses1 = new String[courses.length];
            boolean[] checkedItems = new boolean[courses.length];
            // add a checkbox list
            for (int g = 0; g<courses.length;g++)
                 courses1[g] = courses[g].toString();
            for (int g = 0; g<courses1.length;g++)
                checkedItems[g]=false;
        builder.setMultiChoiceItems(courses1, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                chosenCourses.clear();
                for(int i = 0; i<courses.length;i++) {
                    if (checkedItems[i]==true)
                        chosenCourses.add(courses[i]);
                }
                String g = "";
                for(int j =0;j<chosenCourses.size();j++)
                    g+=String.valueOf(chosenCourses.get(j))+" ";
                button1.setText(g);
                groups = groupFilter(checkedItems);

            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
        if(buttonIndex==2) {
            Log.d("Home", "2 burron pressed");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Выберите группу(-ы)");
            // add a checkbox list

            if(chosenCourses.isEmpty()) {
                groups = fillGroups();
            }
            String[]  groups1 = new String[groups.length];

                boolean[] checkedItems1 = new boolean[groups.length];
                for (int g = 0; g < groups.length; g++)
                    checkedItems1[g] = false;
                for (int g = 0; g < groups.length; g++)
                    groups1[g] = groups[g].toString();

            builder.setMultiChoiceItems(groups1, checkedItems1, new DialogInterface.OnMultiChoiceClickListener() {
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
                    chosenCourses.clear();
                    for(int i = 0; i<groups1.length;i++) {
                        if (checkedItems1[i]==true)
                            chosenCourses.add(i+1);
                    }
                    String g = "";
                    for(int j =0;j<groups1.length;j++)
                        g+=String.valueOf(groups1[j])+" ";
                    button2.setText(g);
                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
}
public interface MyCallback {
        void onCallback();
    }
public Integer[] courseFilter(){
        ArrayList<Integer> crs = new ArrayList<Integer>();
        dbHelper = new AdminDBHelper(getContext());
        db = dbHelper.getReadableDatabase();
    Cursor cursor = db.query("groups", null, null, null, "course", null, "course");
    if(cursor.moveToFirst()){
        do{
            int cIndex = cursor.getColumnIndex("course");
            crs.add(cursor.getInt(cIndex));
        }
        while (cursor.moveToNext());
    }
    Integer[] a=new Integer[crs.size()];
    a = crs.toArray(a);
    return a;
}
public Integer[] groupFilter(boolean[] checked){
    ArrayList<Integer> grps = new ArrayList<Integer>();
    dbHelper = new AdminDBHelper(getContext());
    db = dbHelper.getReadableDatabase();
    boolean flag = false;
        String filter = "course in (";
        for (int i = 0; i< checked.length;i++){
            if (checked[i]){
                if(flag)
                    filter+=", ";
                filter+= String.valueOf(courses[i]);
                flag = true;
            }

        }
        filter+=")";

        Cursor cursor = db.query("groups",null, filter, null, null,null, "number");
    if(cursor.moveToFirst()){
        do{
            int cIndex = cursor.getColumnIndex("number");
            grps.add(cursor.getInt(cIndex));
        }
        while (cursor.moveToNext());
    }
    Integer[] a=new Integer[grps.size()];
    a = grps.toArray(a);
    return a;

}
public Integer[] fillGroups(){
    ArrayList<Integer> grps = new ArrayList<Integer>();
    Cursor cursor = db.query("groups",null, null, null, null,null, "number");
    if(cursor.moveToFirst()){
        do{
            int cIndex = cursor.getColumnIndex("number");
            grps.add(cursor.getInt(cIndex));
        }
        while (cursor.moveToNext());
    }
    Integer[] a=new Integer[grps.size()];
    a = grps.toArray(a);
    return a;
}
  public void createReps(){

            if(courses!=null||groups!=null){
                if (groups!=null) {
                    int length = groups.length;
                    int counter = 0;
                    while (counter != length) {
                        createRepForGroup(groups[counter]);
                        counter++;
                    }

                  }

            }
            else{
                Toast.makeText(getContext(), "Пожалуйста, заполните по крайней мере одно поле", Toast.LENGTH_SHORT).show();
            }

  }
    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        FirebaseApp.initializeApp(getContext());
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        //получаем ссылку для работы с базой данных
    }
  public void createRepForGroup(int group){
      fillLessonsTable(new MyCallback() {
          @Override
          public void onCallback() {

          }
      }, group);
  }

    public void fillLessonsTable(MyCallback myCallBack, int group){

        mFirebaseDatabase.collection("lessons").whereEqualTo("group_id", group).get()
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
}