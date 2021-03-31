package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ChooseStudent extends AppCompatActivity {
    ListView lv;
    ArrayList<String> students;
    ArrayList<String> ids;
    DatabaseHelper dbHelper;
    StudentModel student;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_student);
        lv = (ListView) findViewById(R.id.students);
        students = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);

        ids = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, students);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseStudent.this, RegistrationOfDevice.class);
                Bundle bundle = new Bundle();
                bundle.putString("student_id", ids.get(position));
                bundle.putString("student_name", students.get(position));
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finishActivity(1);
                finish();
            }
        });
        formList();
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
                student = new StudentModel(cursor.getString(idIndex),cursor.getString(groupIndex),cursor.getString(nameIndex));
                students.add(student.name);
                ids.add(students.lastIndexOf(student.name),student.id);
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu4) {
        getMenuInflater().inflate(R.menu.menu, menu4);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_update://если выбрано "Обновить список"
                formList();

                return true;
            case R.id.action_uFb://если выбрано "Обновиться из облака"

                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}