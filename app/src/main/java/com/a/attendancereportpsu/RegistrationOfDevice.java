package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationOfDevice extends AppCompatActivity {
    String studentName, studentId = null, deviceName = null, macDevice = null, group_id = null;
    Button st, dev;
    DeviceModel device;
    FirebaseFirestore mFirebaseDatabase;
    String groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_of_device);
        st = (Button) findViewById(R.id.student);
        dev = (Button) findViewById(R.id.device_name);
        initFirebase();
        Intent intent = getIntent();
        group_id = intent.getStringExtra("groupId");
        Log.d("myBluetooth", group_id);
    }

    public void chooseStudent(View v) {
        Intent intent = new Intent(RegistrationOfDevice.this, ChooseStudent.class);
        startActivityForResult(intent, 1);
    }
    public void chooseDevice(View v){
        Log.d("myBluetooth", "chooseDevice");
        Intent intent = new Intent(RegistrationOfDevice.this, ChooseDevice.class);
        startActivityForResult(intent, 2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // Bundle bundle = data.getExtras();
            // lecturer = new LecturerModel("","","");
            studentName = data.getStringExtra("student_name");
            studentId = data.getStringExtra("student_id");
            st.setText(studentName);

        }
        if (requestCode == 2) {
            // Bundle bundle = data.getExtras();
            // lecturer = new LecturerModel("","","");
            if(!data.getStringExtra("device_name").equals(null)) {
                if(!data.getStringExtra("device_mac").equals(null)) {
                    deviceName = data.getStringExtra("device_name");
                    macDevice = data.getStringExtra("device_mac");
                    dev.setText(deviceName);
                }
            }
        }

    }
    public void Registrate(View v){
        if(deviceName.equals(null)||macDevice.equals(null)||studentId.equals(null)){
            Toast.makeText(RegistrationOfDevice.this, "Заполнены не все поля!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Зарегистрировать устройство?");

            builder.setCancelable(false);
            builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            device = new DeviceModel(macDevice, studentId, group_id);
                            device.setName(deviceName);

                            mFirebaseDatabase.collection("devices").document(macDevice)
                                    .set(device)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("id", "DocumentSnapshot written with ID: " );
                                            // lesson_id = documentReference.getId();
                                        }


                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("error", "Error adding document", e);
                                        }
                                    });
                            finish();
                        }

                    }
            );
            builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    public void OnCancelClick(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Отменить регистрацию устройства? Все данные будут утеряны.");

        builder.setCancelable(false);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
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

    }
    private void initFirebase() {
        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseFirestore.getInstance();
    }

}

