package com.interview.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    // Intentional Bug: Schema mismatch. We define 'status' here but might try to insert 'completed' bool or vice versa.
    public static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Intentional Bug: Missing column in creation compared to what we try to query or insert later.
        // Or simply wrong type.
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT" 
                // Missing COLUMN_STATUS in create statement!
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Intentional Bug: This method will be called on the main thread from MainActivity
    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase(); // Potentially heavy I/O
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, task.getTaskId());
        values.put(COLUMN_TITLE, task.getTitle());
        // Intentional Bug: "completed" column doesn't exist in schema (we didn't add it in onCreate either)
        // And even if we added COLUMN_STATUS, we are putting "completed" here.
        values.put("completed", task.isCompleted() ? 1 : 0); 
        
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }
    
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Intentional Bug: accessing incorrect column index or name
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int statusIndex = cursor.getColumnIndex("status"); // might be -1 if column missing

                Task task = new Task(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        statusIndex != -1 && cursor.getInt(statusIndex) == 1
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
}
