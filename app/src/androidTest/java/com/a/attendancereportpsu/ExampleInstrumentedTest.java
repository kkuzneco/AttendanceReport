package com.a.attendancereportpsu;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.rule.ActivityTestRule;
import	androidx.test.espresso.action.ViewActions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest  {
    FirebaseFirestore mFirebaseDatabase;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    ShowLessons sl;
    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    public ActivityTestRule<ShowLessons> activityTestShowLessons = new ActivityTestRule<>(ShowLessons.class);
    public IntentsTestRule<MainActivity>  intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before

    public void setUp() throws Exception {
        sl = new ShowLessons();
        mFirebaseDatabase = FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        Intents.init();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.a.attendancereportpsu", appContext.getPackageName());
    }


  @Test
    public void validAuthTest() throws InterruptedException {
      String s = "123";
      activityActivityTestRule.getActivity().signIn("tester@test.ru", "123456",true);
      Thread.sleep(3000);
      Log.d("TAG", activityActivityTestRule.getActivity().getUID());
     assertEquals(activityActivityTestRule.getActivity().getUID(),"Nd40A9yA9jdHZ6Zsd9uqWAEmEMr2");
  }

  @Test
    public void inValidAuthTest() throws InterruptedException{
      activityActivityTestRule.getActivity().signIn("tester@test.ru", "123", true);
      Thread.sleep(3000);
     // Log.d("GROUNUMBER",activityActivityTestRule.getActivity().getUID());
      assertEquals(null,activityActivityTestRule.getActivity().getUID());
  }


    @After
    public void tearDown() throws Exception {
        Intents.release();
        activityActivityTestRule.getActivity().signOut();
    }
}
