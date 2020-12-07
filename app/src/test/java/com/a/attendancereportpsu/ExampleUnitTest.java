package com.a.attendancereportpsu;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Calendar;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.formatDateTime;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    Calendar dateAndTime = Calendar.getInstance();
    LessonModel lm, lm1;
    StudentModel sm;
    Subject subject;
    String wrong_email;
    String email;
    MainActivity ma;
    ShowLessons sl;

    @Mock
    private FirebaseAuth firebaseAuth;
    @Before
    public void CreateModels(){
        lm = new LessonModel("fff", "fko39Sj8SSk0nr", "49fmksldlsm", "22/06/20", "23:54");
        sm = new StudentModel("hf9wslfwjikfj9", "Kseniya");
        sl = new ShowLessons();
        subject = new Subject();
        lm1 = new LessonModel("","","","","");
        ma=new MainActivity();
        lm1.setid("54");
        wrong_email = "kkuzneco@cs";
        email = "kuznecova.ks@yandex.ru";
    }

    @Test
    public void function_1_ModelLesson(){
            assertEquals("fff", lm.getid());
    }
    @Test
    public void function_2_ModelLesson(){
        assertEquals("54", lm1.getid());
    }
    @Test
    public void function_1_StudentModel(){
        assertEquals("hf9wslfwjikfj9", sm.getGroupId());
    }
    @Test
    public void check_Incorrect_email(){
        assertEquals(false, ma.emailValid(wrong_email));
    }
    @Test
    public void check_correct_email(){
        assertEquals(true, ma.emailValid(email));
    }
    @Test
    public void check_empty_email(){
        assertEquals(false, ma.emailValid(""));
    }
    @Test
    public void subjectListGetTest(){
        ArrayList<String> subject_list = new ArrayList<>();
        subject_list = subject.getSubjectList("22000");
        assertNotNull(subject_list);
    }

}