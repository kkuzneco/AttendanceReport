package com.a.attendancereportpsu;

import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.a.attendancereportpsu.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    Класс аутентификации
 */
public class MainActivity extends BaseActivity implements
        View.OnClickListener{
    public String str = "";
    private static final String TAG = "EmailPassword";
    public ActivityMainBinding mBinding;
    public FirebaseAuth mAuth;
    public boolean validForm = true;
    DatabaseReference mDatabaseReference;
    DatabaseReference children;
    FirebaseFirestore mFirebaseDatabase;
    FirebaseDatabase firebaseData;
    CollectionReference headmen;
    ProgressBar pb;

    /*
    При создании окна
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        mBinding.signInButton.setOnClickListener(this);
        //получить данные из Fb
        mAuth = FirebaseAuth.getInstance();
        initFirebase();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Берет текущего пользователя Fb
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //обновляем состояние в соответствие с полученным пользователем
        updateUI(currentUser);

    }

    /*
    Обновляет экран в соответствие с полученным текущим пользователем базы
     */
    public String updateUI(FirebaseUser user) {

        if (user != null) {
            String usrId = user.getUid();
            Log.d("mLog", usrId);
            mFirebaseDatabase.collection("users").document(usrId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String type = (String) document.get("type");
                                if (document.exists()) {
                                    if (type.equals("student")) {
                                        Intent intent = new Intent(MainActivity.this, ShowLessons.class);
                                        startActivityForResult(intent, 1);
                                        return;
                                    }
                                    else {
                                        Intent intent = new Intent(MainActivity.this, AdminMenu.class);
                                        startActivityForResult(intent, 1);
                                        return;
                                    }
                                }
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d(TAG, "No such document");
                                }

                    }});
            return usrId;
    }
        else return "";
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        String k;
        if (i == R.id.signInButton) {
            Log.d(TAG, "onClickFUNCTION");
            validForm = validateForm();
            if(validForm) k = "true";
            else  k = "false";
            Log.d(TAG, k);
            signIn(mBinding.fieldEmail.getText().toString(), mBinding.fieldPassword.getText().toString(), validForm);
        }
    }

public String getUID(){
        return mAuth.getUid();
}
    public void initFirebase() {
        //инициализируем наше приложение для Firebase согласно параметрам в google-services.json
        FirebaseApp.initializeApp(this);
        //получаем точку входа для базы данных
        firebaseData = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseData.getReference();
        mFirebaseDatabase = FirebaseFirestore.getInstance();
       }
    /*
    * входим в систему
    * */
    public String signIn(String email, String password, boolean valid) {
        pb.setVisibility(View.VISIBLE);
        if (!valid) {
            pb.setVisibility(View.INVISIBLE);
            return "false";

        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("EmailPassword", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            str = updateUI(user);
                            pb.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "ID" + str);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("ERROR", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                            updateUI(null);
                            str = "null";
                        }

                    }
                });
        return str;
    }

    /*
    * проверка правильно ли заполнена форма (УБРАТО КОММЕНТАРИИ ИЗ СТРОК ПРОВЕРКИ!!)
    */
    public boolean validateForm() {
        boolean valid = true;
        String email = mBinding.fieldEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
           mBinding.fieldEmail.setError("Required.");
            valid = false;
        } else {
            if(!emailValid(email)) {
            mBinding.fieldEmail.setError("Incorrect email");
                valid = false;
            }
         else
               mBinding.fieldEmail.setError(null);
        }
        String password = mBinding.fieldPassword.getText().toString();

        if (TextUtils.isEmpty(password)) {
          mBinding.fieldPassword.setError("Required.");
            valid = false;
        } else {
           mBinding.fieldPassword.setError(null);
        }
        return valid;
    }
/*
* Выход из учетной записи
* */
    public void signOut() {
       mAuth.signOut();
       updateUI(null);
    }
/*
    При получении сигнала о выходе
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         signOut();
         Toast.makeText(MainActivity.this, "Вы вышли из учетной записи",
            Toast.LENGTH_SHORT).show();
    }
    /*
    проверка эл почны на валидность
     */
    public boolean emailValid(String email){
        if (email != null)
        {
            Pattern p = Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher m = p.matcher(email);
            return m.find();
        }
        return false;
    }
}
