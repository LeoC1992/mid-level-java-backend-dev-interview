package com.interview.android.data;

import com.google.gson.annotations.SerializedName;

public class Task {
    // Intentional Bug: Field name mismatch. API returns "id", logic expects "taskId"
    // And "is_completed" vs "completed"
    
    @SerializedName("task_id") 
    private int taskId; 

    private String title;

    @SerializedName("completed")
    private boolean completed;

    public Task(int taskId, String title, boolean completed) {
        this.taskId = taskId;
        this.title = title;
        this.completed = completed;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }
}
