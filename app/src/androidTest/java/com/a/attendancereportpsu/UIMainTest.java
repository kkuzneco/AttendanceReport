package com.a.attendancereportpsu;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class UIMainTest {
    FirebaseFirestore mFirebaseDatabase;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    ShowLessons sl;
    private final Activity[] currentActivity = new Activity[1];

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    public ActivityTestRule<ShowLessons> activityTestShowLessons = new ActivityTestRule<>(ShowLessons.class);
    public IntentsTestRule<ShowLessons>  intentsTestRule = new IntentsTestRule<>(ShowLessons.class);
    public IntentsTestRule<MainActivity>  intentsTestRule1 = new IntentsTestRule<>(MainActivity.class);
    @Before

    public void setUp() throws Exception {
        sl = new ShowLessons();
        monitorCurrentActivity();
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
   public void inValidFormTest()  {

        onView(withId(R.id.fieldEmail)).perform(replaceText("tester@test"));
        onView(withId(R.id.fieldEmail)).perform(closeSoftKeyboard());
         onView(withId(R.id.fieldPassword)).perform(replaceText("123456"));
        onView(withId(R.id.fieldPassword)).perform(closeSoftKeyboard());
        //Log.d("TAG", activityActivityTestRule.getActivity().getUID());
        assertEquals(false,activityActivityTestRule.getActivity().validateForm());

    }
    @Test
    public void validFormTest() throws InterruptedException {
        onView(withId(R.id.fieldEmail)).perform(replaceText("tester@test.ru"));
        onView(withId(R.id.fieldEmail)).perform(closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(replaceText("123456"));
        onView(withId(R.id.fieldPassword)).perform(closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        Thread.sleep(300);
        Activity cur = getCurrentActivity();
      //onView(getCurrentActivity().withId(R.id.button2)).perform((click()));

    }
    private Activity getCurrentActivity() {
        return currentActivity[0];
    }
    private void monitorCurrentActivity() {
        activityActivityTestRule.getActivity().getApplication()
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) { }

                    @Override
                    public void onActivityStarted(final Activity activity) { }

                    @Override
                    public void onActivityResumed(final Activity activity) {
                        currentActivity[0] = activity;
                    }

                    @Override
                    public void onActivityPaused(final Activity activity) { }

                    @Override
                    public void onActivityStopped(final Activity activity) { }

                    @Override
                    public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) { }

                    @Override
                    public void onActivityDestroyed(final Activity activity) { }
                });
    }

    @Test
    public void inValidFormTest_2()  {
        //activityActivityTestRule.getActivity().signIn("tester@test.ru", "123", true);
        onView(withId(R.id.fieldEmail)).perform(replaceText("tester@test.ru"));
        onView(withId(R.id.fieldEmail)).perform(closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(replaceText(""));
        onView(withId(R.id.fieldPassword)).perform(closeSoftKeyboard());
        //Log.d("TAG", activityActivityTestRule.getActivity().getUID());
        assertEquals(false,activityActivityTestRule.getActivity().validateForm());
    }
    @Test
    public void inValidFormTest_3()  {
        //activityActivityTestRule.getActivity().signIn("tester@test.ru", "123", true);
        onView(withId(R.id.fieldEmail)).perform(replaceText("tester@test.ru"));
        onView(withId(R.id.fieldEmail)).perform(closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(replaceText("123"));
        onView(withId(R.id.fieldPassword)).perform(closeSoftKeyboard());
        //Log.d("TAG", activityActivityTestRule.getActivity().getUID());
        assertEquals(true,activityActivityTestRule.getActivity().validateForm());
    }
    @Test
    public void intentCheck()  {
        Intent resultData = new Intent();
        String groupNumber = "22000";
        resultData.putExtra("group", groupNumber);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("com.a.attendancereportpsu")).respondWith(result);
        //activityActivityTestRule.getActivity().signIn("tester@test.ru", "123", true);
        onView(withId(R.id.fieldEmail)).perform(replaceText("tester@test.ru"));
        onView(withId(R.id.fieldEmail)).perform(closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(replaceText("123"));
        onView(withId(R.id.fieldPassword)).perform(closeSoftKeyboard());
        //Log.d("TAG", activityActivityTestRule.getActivity().getUID());
        assertEquals(true,activityActivityTestRule.getActivity().validateForm());
    }
    @After
    public void tearDown() throws Exception {
        Intents.release();
        activityActivityTestRule.getActivity().signOut();
    }
}
