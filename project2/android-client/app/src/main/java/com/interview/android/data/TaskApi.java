package com.interview.android.data;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TaskApi {
    // Intentional Bug: Incorrect endpoint path. The actual backend is typically serving at /api/tasks
    // This assumes specific base URL configuration which might also be wrong.
    @GET("timelines") 
    Call<List<Task>> getTasks();
}
