package com.interview.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.interview.android.data.DatabaseHelper;
import com.interview.android.data.Task;
import com.interview.android.data.TaskApi;
import com.interview.android.ui.TasksAdapter;
import com.interview.android.ui.TasksViewModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Intentional Bug: Static reference to Activity causes memory leak
    public static Activity sActivity;

    private TasksViewModel viewModel;
    private TasksAdapter adapter;
    private DatabaseHelper dbHelper;
    private TaskApi api;

    // Intentional Bug: Network call in constructor
    public MainActivity() {
        super();
        
        // Setup Retrofit here (bad practice, context not ready if needed)
        Retrofit retrofit = new Retrofit.Builder()
                // Intentional Bug: Wrong Base URL for Android Emulator (should be 10.0.2.2 usually)
                .baseUrl("http://localhost:3000/") 
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        api = retrofit.create(TaskApi.class);

        // Intentional Bug: Network On Main Thread Exception will happen here if called
        // catch block effectively hides it or crashes.
        // Also constructor is too early for this.
        try {
           Response<List<Task>> response = api.getTasks().execute();
           if (response.isSuccessful() && response.body() != null) {
               // Doing nothing with it really, just crashing
           }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (android.os.NetworkOnMainThreadException e) {
            // Intentional: User needs to find this
            e.printStackTrace();
        } catch (Exception e) {
             // Catch all
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sActivity = this; // Leak

        RecyclerView recyclerView = findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TasksAdapter();
        recyclerView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);

        // Intentional Bug: Incorrect ViewModel instantiation prevents it from surviving rotation
        // Should be: new ViewModelProvider(this).get(TasksViewModel.class);
        viewModel = new TasksViewModel();

        // Intentional Bug: Database operation on Main Thread
        // This simulates loading data from offline cache
        List<Task> offlineTasks = dbHelper.getAllTasks(); 
        if (offlineTasks != null && !offlineTasks.isEmpty()) {
            adapter.setTasks(offlineTasks);
        }

        // Fetch fresh data
        fetchTasks();
    }

    private void fetchTasks() {
        // Intentional Bug: This is already running on Main Thread
        // api.getTasks() returns a Call. 
        // Calling .execute() is synchronous and blocks Main Thread -> ANR or Crash
        
        try {
            // This will throw NetworkOnMainThreadException
            Response<List<Task>> response = api.getTasks().execute();
            
            if (response.isSuccessful() && response.body() != null) {
                List<Task> tasks = response.body();
                
                // Save to local DB (Intentional Bug: DB Op on Main Thread)
                for (Task t : tasks) {
                    dbHelper.addTask(t);
                }

                // Update UI
                viewModel.setTasks(tasks);
                adapter.setTasks(tasks); 
            } else {
                Toast.makeText(this, "Error fetching tasks", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Likely crashes here due to NetworkOnMainThread
        }
    }
}
