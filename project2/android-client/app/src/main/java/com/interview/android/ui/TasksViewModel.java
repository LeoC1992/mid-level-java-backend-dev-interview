package com.interview.android.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.interview.android.data.Task;
// import com.interview.android.data.TaskRepository; // Not implemented for brevity, direct in VM

import java.util.List;

public class TasksViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    // Intentional Bug: We might be missing the repository or context here to actually fetch
    // But the fetching logic is inside MainActivity in the buggy version anyway.

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> newTasks) {
        tasks.setValue(newTasks);
    }
}
