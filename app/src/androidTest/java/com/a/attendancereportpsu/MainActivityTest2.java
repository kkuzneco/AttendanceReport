package com.a.attendancereportpsu;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest2 extends Context {
/*
* Проверка обмена между окнами и переход между ними. По сути - сценарий, проверяющий правильность переходов и доступность элементов
* */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @After
    public void tearDown() throws Exception {
        mActivityTestRule.getActivity().signOut();
    }
    @Test
    public void mainActivityTest2() throws InterruptedException {
        Calendar dateAndTime = Calendar.getInstance();
        Date date = new Date();
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.fieldEmail),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                                2)),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("tester@test.ru"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.fieldPassword),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.signInButton), withText("войти"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click(),closeSoftKeyboard());
        Thread.sleep(2200);
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button2), withText("добавить занятие"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());
        Thread.sleep(500);
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.cancel), withText("отмена"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                5),
                        isDisplayed()));
        appCompatButton3.perform(click());
        Thread.sleep(500);
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("ДА"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton4.perform(scrollTo(), click());
        Thread.sleep(500);
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.date), withText("30 ноября 2020 г."),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton5.perform(click());
        Thread.sleep(600);
        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton6.perform(scrollTo(), click());
        Thread.sleep(600);
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("Еще"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());
        Thread.sleep(500);
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Создать отчет"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());
        Thread.sleep(500);
        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.button2), withText("добавить занятие"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton7.perform(click());

        Thread.sleep(500);
        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.dateEdit), withText( containsString("30 ноября 2020 г., ")),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton8.perform(click());
        Thread.sleep(500);
        ViewInteraction appCompatButton9 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton9.perform(scrollTo(), click());
        Thread.sleep(500);
        ViewInteraction appCompatButton10 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton10.perform(scrollTo(), click());
        Thread.sleep(500);
        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.setSubject), withText("Выбрать предмет"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton11.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public AssetManager getAssets() {
        return null;
    }

    public Resources getResources() {
        return null;
    }

    public PackageManager getPackageManager() {
        return null;
    }

    public ContentResolver getContentResolver() {
        return null;
    }

    public Looper getMainLooper() {
        return null;
    }

    public Context getApplicationContext() {
        return null;
    }

    public void setTheme(int i) {

    }

    public Resources.Theme getTheme() {
        return null;
    }

    public ClassLoader getClassLoader() {
        return null;
    }

    public String getPackageName() {
        return null;
    }

    public ApplicationInfo getApplicationInfo() {
        return null;
    }

    public String getPackageResourcePath() {
        return null;
    }

    public String getPackageCodePath() {
        return null;
    }

    public SharedPreferences getSharedPreferences(String s, int i) {
        return null;
    }

    public boolean moveSharedPreferencesFrom(Context context, String s) {
        return false;
    }

    public boolean deleteSharedPreferences(String s) {
        return false;
    }

    public FileInputStream openFileInput(String s) throws FileNotFoundException {
        return null;
    }

    public FileOutputStream openFileOutput(String s, int i) throws FileNotFoundException {
        return null;
    }

    public boolean deleteFile(String s) {
        return false;
    }

    public File getFileStreamPath(String s) {
        return null;
    }

    public File getDataDir() {
        return null;
    }

    public File getFilesDir() {
        return null;
    }

    public File getNoBackupFilesDir() {
        return null;
    }

    @Nullable
    public File getExternalFilesDir(@Nullable String s) {
        return null;
    }

    public File[] getExternalFilesDirs(String s) {
        return new File[0];
    }

    public File getObbDir() {
        return null;
    }

    public File[] getObbDirs() {
        return new File[0];
    }

    public File getCacheDir() {
        return null;
    }

    public File getCodeCacheDir() {
        return null;
    }

    @Nullable
    public File getExternalCacheDir() {
        return null;
    }

    public File[] getExternalCacheDirs() {
        return new File[0];
    }

    public File[] getExternalMediaDirs() {
        return new File[0];
    }

    public String[] fileList() {
        return new String[0];
    }

    public File getDir(String s, int i) {
        return null;
    }

    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory) {
        return null;
    }

    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory, @Nullable DatabaseErrorHandler databaseErrorHandler) {
        return null;
    }

    public boolean moveDatabaseFrom(Context context, String s) {
        return false;
    }

    public boolean deleteDatabase(String s) {
        return false;
    }

    public File getDatabasePath(String s) {
        return null;
    }

    public String[] databaseList() {
        return new String[0];
    }

    public Drawable getWallpaper() {
        return null;
    }

    public Drawable peekWallpaper() {
        return null;
    }

    public int getWallpaperDesiredMinimumWidth() {
        return 0;
    }

    public int getWallpaperDesiredMinimumHeight() {
        return 0;
    }

    public void setWallpaper(Bitmap bitmap) throws IOException {

    }

    public void setWallpaper(InputStream inputStream) throws IOException {

    }

    public void clearWallpaper() throws IOException {

    }

    public void startActivity(Intent intent) {

    }

    public void startActivity(Intent intent, @Nullable Bundle bundle) {

    }

    public void startActivities(Intent[] intents) {

    }

    public void startActivities(Intent[] intents, Bundle bundle) {

    }

    public void startIntentSender(IntentSender intentSender, @Nullable Intent intent, int i, int i1, int i2) throws IntentSender.SendIntentException {

    }

    public void startIntentSender(IntentSender intentSender, @Nullable Intent intent, int i, int i1, int i2, @Nullable Bundle bundle) throws IntentSender.SendIntentException {

    }

    public void sendBroadcast(Intent intent) {

    }

    public void sendBroadcast(Intent intent, @Nullable String s) {

    }

    public void sendOrderedBroadcast(Intent intent, @Nullable String s) {

    }

    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String s, @Nullable BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s1, @Nullable Bundle bundle) {

    }

    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle) {

    }

    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle, @Nullable String s) {

    }

    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, @Nullable String s, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s1, @Nullable Bundle bundle) {

    }

    public void sendStickyBroadcast(Intent intent) {

    }

    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s, @Nullable Bundle bundle) {

    }

    public void removeStickyBroadcast(Intent intent) {

    }

    public void sendStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {

    }

    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s, @Nullable Bundle bundle) {

    }

    public void removeStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {

    }

    @Nullable
    public Intent registerReceiver(@Nullable BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        return null;
    }

    @Nullable
    public Intent registerReceiver(@Nullable BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, int i) {
        return null;
    }

    @Nullable
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, @Nullable String s, @Nullable Handler handler) {
        return null;
    }

    @Nullable
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, @Nullable String s, @Nullable Handler handler, int i) {
        return null;
    }

    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {

    }

    @Nullable
    public ComponentName startService(Intent intent) {
        return null;
    }

    @Nullable
    public ComponentName startForegroundService(Intent intent) {
        return null;
    }

    public boolean stopService(Intent intent) {
        return false;
    }

    public boolean bindService(Intent intent, @NonNull ServiceConnection serviceConnection, int i) {
        return false;
    }

    public void unbindService(@NonNull ServiceConnection serviceConnection) {

    }

    public boolean startInstrumentation(@NonNull ComponentName componentName, @Nullable String s, @Nullable Bundle bundle) {
        return false;
    }

    public Object getSystemService(@NonNull String s) {
        return null;
    }

    @Nullable
    public String getSystemServiceName(@NonNull Class<?> aClass) {
        return null;
    }

    public int checkPermission(@NonNull String s, int i, int i1) {
        return 0;
    }

    public int checkCallingPermission(@NonNull String s) {
        return 0;
    }

    public int checkCallingOrSelfPermission(@NonNull String s) {
        return 0;
    }

    public int checkSelfPermission(@NonNull String s) {
        return 0;
    }

    public void enforcePermission(@NonNull String s, int i, int i1, @Nullable String s1) {

    }

    public void enforceCallingPermission(@NonNull String s, @Nullable String s1) {

    }

    public void enforceCallingOrSelfPermission(@NonNull String s, @Nullable String s1) {

    }

    public void grantUriPermission(String s, Uri uri, int i) {

    }

    public void revokeUriPermission(Uri uri, int i) {

    }

    public void revokeUriPermission(String s, Uri uri, int i) {

    }

    public int checkUriPermission(Uri uri, int i, int i1, int i2) {
        return 0;
    }

    public int checkCallingUriPermission(Uri uri, int i) {
        return 0;
    }

    public int checkCallingOrSelfUriPermission(Uri uri, int i) {
        return 0;
    }

    public int checkUriPermission(@Nullable Uri uri, @Nullable String s, @Nullable String s1, int i, int i1, int i2) {
        return 0;
    }

    public void enforceUriPermission(Uri uri, int i, int i1, int i2, String s) {

    }

    public void enforceCallingUriPermission(Uri uri, int i, String s) {

    }

    public void enforceCallingOrSelfUriPermission(Uri uri, int i, String s) {

    }

    public void enforceUriPermission(@Nullable Uri uri, @Nullable String s, @Nullable String s1, int i, int i1, int i2, @Nullable String s2) {

    }

    public Context createPackageContext(String s, int i) throws PackageManager.NameNotFoundException {
        return null;
    }

    public Context createContextForSplit(String s) throws PackageManager.NameNotFoundException {
        return null;
    }

    public Context createConfigurationContext(@NonNull Configuration configuration) {
        return null;
    }

    public Context createDisplayContext(@NonNull Display display) {
        return null;
    }

    public Context createDeviceProtectedStorageContext() {
        return null;
    }

    public boolean isDeviceProtectedStorage() {
        return false;
    }
}
