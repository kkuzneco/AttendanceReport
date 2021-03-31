package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.bluetooth.le.BluetoothLeScanner;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothRegister extends AppCompatActivity {
    ArrayList<String> devices;
    ArrayList<DeviceModel> devicesList;
    DeviceModel device;
    Button register;
    TextView tv;
    String mac;
    RecyclerView device_list;
    ListView lv;
    String group_id;
    DevicesAdapter da;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    FirebaseFirestore mFirebaseDatabase;
    private boolean scanning;
    private BluetoothAdapter bluetoothAdapter;
    static int REQUEST_ENABLE_BT = 1001;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
       // dbHelper.createTableMacs(db);
        Intent intent = getIntent();
        group_id = intent.getStringExtra("groupId");

        devices = new ArrayList<String>();
        devicesList = new ArrayList<>();
        setContentView(R.layout.activity_bluetooth_register);
        tv = (TextView) findViewById(R.id.textView);
        lv = (ListView) findViewById(R.id.list);
        da = new DevicesAdapter();
        initFirebase();
        register = (Button) findViewById(R.id.save_btn);
        device_list = (RecyclerView) findViewById(R.id.device_list);
        tv.setVisibility(View.INVISIBLE);
        formDeviceList();

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, devices);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BluetoothRegister.this);
                builder.setMessage("Удалить запись о регистрации?");

                builder.setCancelable(false);
                builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeData(pos);

                    }
                });
                builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });
        //initRecyclerView();
        loadDevices();
    }
    public void initRecyclerView(){
        Log.d("mlog", "initRecyclerView");
        RecyclerView listOfStudents = findViewById(R.id.device_list);//привязка из лэйаут
        listOfStudents.setLayoutManager(new LinearLayoutManager(this));//менедже
        device_list.setAdapter(da);
    }




    public void formDeviceList(){
        Log.d("mlog", "formList");
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("macs", null, null, null, null, null, null);
        //Cursor cursor = db.execSQL("select students.name from users, macs where students.id=macs.student_id");
        if (cursor.moveToFirst()) {
            int idMac = cursor.getColumnIndex("mac");
            int nameIndex = cursor.getColumnIndex("name");
            int studentId = cursor.getColumnIndex("student_id");
            int groupId = cursor.getColumnIndex("group_id");
            do {
                Cursor cursor1 = db.query("students", null, "id = '"+ cursor.getString(studentId)+"'", null, null, null, null);
                cursor1.moveToFirst();
                int nameStIndex = cursor1.getColumnIndex("name");
                String studentName;
                studentName=cursor1.getString(nameStIndex);
                /* Log.d("mLog", "ID = " + cursor.getString(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", institute = " + cursor.getString(instituteIndex));*/
                device = new DeviceModel(cursor.getString(idMac),cursor.getString(studentId),cursor.getString(groupId));
                device.setName(cursor.getString( nameIndex));
                devicesList.add(device);
                devices.add(studentName+'\n'+device.name);
                // lecturerList.add(cursor.getString(nameIndex) + "\n("+cursor.getString(instituteIndex)+")");
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();

        //здесь должен заполняться список устройств  из Firebase и локальной базы
        //da.listOfDevices.add(new DeviceModel("22:22:22:22:22", "1"));
        //da.listOfDevices.get(0).name = "Test Student";
        //String a = "Test Student"+'\n'+ "name" ;

    }
    private void initFirebase() {
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
    }
    public void loadDevices() {
        Log.d("mlog", "loadDevices");

        db = dbHelper.getWritableDatabase();
        dbHelper.removeMACRows(db);
        final DeviceModel dev = new DeviceModel("","",group_id);
        mFirebaseDatabase.collection("devices").whereEqualTo("group_id", group_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    dev.student_id = document.get("student_id").toString();;
                                   // dev.mac_address = document.get("mac_address").toString();
                                    dev.name = document.get("name").toString();
                                    mac = document.getId();
                                   try{
                                       db.execSQL("insert into macs(mac, name, group_id, student_id) values (" + "'"+mac+"',"+"'"+ dev.name+"',"+"'"+group_id+"',"+"'"+dev.student_id+"');");
                                   } catch (SQLException e) {
                                       e.printStackTrace();
                                   }

                                    //  Log.d("mlog", "insert into students(id, name, group_id) values (" + "'"+student.id+"',"+"'"+student.name+"',"+"'"+student.group_id+"');");
                                }
                            }
//                            Log.d("element", String.valueOf(studentsList.size()));
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }

                });


    }
    public void goToRegistration(View v){
        Intent intent = new Intent(BluetoothRegister.this, RegistrationOfDevice.class);
        intent.putExtra("groupId", group_id);
        startActivityForResult(intent,1);
    }
    public void removeData(int position){
        devices.remove(position);
        mFirebaseDatabase.collection("devices").document(devicesList.get(position).mac_address)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
        devicesList.remove(position);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, devices);
        lv.setAdapter(adapter);

    }
}
