package com.example.loadermanagertest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Uri uri = Uri.parse("content://com.example.loadermanagertest.PersonContentProvider/person");
        ContentResolver contentResolver = appContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", "生命一号");
        contentResolver.insert(uri,values);
    }
}
