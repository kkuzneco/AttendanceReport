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
    String wrong_email1;
    String wrong_email2;
    String wrong_email3;
    String mail2;
    String email;
    MainActivity ma;
    ShowLessons sl;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Before
    public void CreateData() {
        sl = new ShowLessons();
        subject = new Subject();
        lm1 = new LessonModel("", "", "", "", "");
        ma = new MainActivity();
        lm1.setid("54");
        wrong_email1 = "kkuzneco@cs";
        wrong_email2 = "tester@test";
        wrong_email3 = "test";
        email = "tester@yandex.ru";
        mail2 = "kkuzneco@cs.karelia.ru";
    }


    @Test
    public void check_Incorrect_email1() {
        assertEquals(false, ma.emailValid(wrong_email1));
    }

    @Test
    public void check_Incorrect_email3() {
        assertEquals(false, ma.emailValid(wrong_email3));
    }


    @Test
    public void check_correct_email() {
        assertEquals(true, ma.emailValid(email));
    }

    @Test
    public void check_correct_email2() {
        assertEquals(true, ma.emailValid(mail2));
    }

    @Test
    public void check_empty_email() {
        assertEquals(false, ma.emailValid(""));
    }

    @Test
    public void subjectListGetTest() {
        ArrayList<String> subject_list = new ArrayList<>();
        subject_list = subject.getSubjectList("22000");
        assertNotNull(subject_list);
        assertTrue(subject_list.contains("testSubject"));
    }

    @Test
    public void subjectFind() {
        ArrayList<String> subject_list = new ArrayList<>();
        subject_list = subject.findByFilter("А", "22407");
        assertEquals(2, subject_list.size());
        assertTrue(subject_list.get(0).toString() == "Алгебра");
        assertTrue(subject_list.get(1).toString() == "Анализ требований");
    }

    @Test
    public void subjectFind_2() {
        ArrayList<String> subject_list = new ArrayList<>();
        subject_list = subject.findByFilter("Анализ требований", "22407");
        assertEquals(1, subject_list.size());
        assertTrue(subject_list.get(0).toString() == "Анализ требований");
    }
    @Test
    public void subjectFind_3() {
        ArrayList<String> subject_list = new ArrayList<>();
        subject_list = subject.findByFilter("", "22407");
        assertEquals(5, subject_list.size());
        ArrayList<String> arrayExample = new ArrayList<>();
        arrayExample.add("Алгебра");
        arrayExample.add("Анализ требований");
        arrayExample.add("Обеспечение информационной безопасности");
        arrayExample.add("Тестирование ПО");
        arrayExample.add("Философия");
        assertTrue(subject_list.get(0).toString() == "Алгебра");
        assertTrue(subject_list.get(1).toString() == "Анализ требований");
        assertTrue(subject_list.get(2).toString() == "Обеспечение информационной безопасности");
        assertTrue(subject_list.get(3).toString() == "Тестирование ПО");
        assertTrue(subject_list.get(4).toString() == "Философия");
    }
    @Test
    public void subjectFind_4() {
        ArrayList<String> subject_list = new ArrayList<>();
        subject_list = subject.findByFilter("История", "22407");
        assertEquals(0, subject_list.size());
        assertTrue(subject_list.isEmpty());
    }
}

