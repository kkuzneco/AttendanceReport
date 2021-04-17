package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.Serializable;
import java.util.ArrayList;

public class Students extends AppCompatActivity implements Serializable {
   //адаптер для отображения студентов группы
    StudentAdapter studAdapter;
    ArrayList<AttendanceModel> attendance;
    private BluetoothAdapter bluetoothAdapter;
    DatabaseHelper dbHelper;
    StudentModel student;
    SQLiteDatabase db ;
    ProgressBar pb1;
    RecyclerView listOfStudents;
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
        pb1 = (ProgressBar) findViewById(R.id.pb1);
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
        listOfStudents = findViewById(R.id.studentsRecycler);//привязка из лэйаут
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
                bluetoothChecking();
                return true;
            case R.id.register:
                Intent intent1 = new Intent(Students.this, BluetoothRegister.class);
                intent1.putExtra("groupId", groupNumber);
                startActivity(intent1);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public void bluetoothChecking(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Log.d("myBluetooth", "Bluetooth OK");
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, 1);
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d("myBluetooth", "стоп поиск1");

        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        boolean f = bluetoothAdapter.startDiscovery();

    }
    @Override
    protected void onDestroy() {
      try{
          unregisterReceiver(mReceiver);
      } catch (Exception e) {
          e.printStackTrace();
      }
        Log.d("myBluetooth", "стоп поиск2");
        super.onDestroy();
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            Log.d("myBluetooth", "receive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    String name = device.getName();
                    String mac = device.getAddress();
                    check_device(mac);
                }

            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
               listOfStudents.setVisibility(View.INVISIBLE);
                pb1.setVisibility(View.VISIBLE);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                pb1.setVisibility(View.INVISIBLE);
                listOfStudents.setVisibility(View.VISIBLE);
            }

        }


    };
    public void check_device(String mac){
        try{
            DocumentReference docRef = mFirebaseDatabase.collection("devices").document(mac);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                           if(document.get("group_id").equals(groupNumber)){
                               try {
                                   studAdapter.setAttendance(document.get("student_id").toString(), true);
                                   initRecyclerView();
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           }
                        } else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
