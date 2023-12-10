package com.fitness.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseHelperTest {

    @Mock
    private SQLiteDatabase mockDb;
    @Mock
    private Cursor mockCursor;
    private DatabaseHelper dbHelper;

    @Before
    public void setUp() {
        dbHelper = new DatabaseHelper(null) {
            @Override
            public SQLiteDatabase getWritableDatabase() {
                return mockDb;
            }

            @Override
            public SQLiteDatabase getReadableDatabase() {
                return mockDb;
            }
        };
    }

    @Test
    public void getCredentials_ValidCredentials_ReturnsTrue() {
        when(mockDb.rawQuery(anyString(), any(String[].class))).thenReturn(mockCursor);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.getInt(0)).thenReturn(1);

        boolean result = dbHelper.getCredentials("user@example.com", "password");

        assertTrue(result);
    }

    @Test
    public void isEmailExists_EmailExists_ReturnsTrue() {
        when(mockDb.rawQuery(anyString(), any(String[].class))).thenReturn(mockCursor);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.getInt(0)).thenReturn(1);

        boolean result = dbHelper.isEmailExists("user@example.com");

        assertTrue(result);
    }



    // Additional test cases for other methods can be added here...
}
