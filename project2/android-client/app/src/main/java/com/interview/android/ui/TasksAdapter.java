package com.interview.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.interview.android.R;
import com.interview.android.data.Task;
import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> newTasks) {
        // Intentional Bug: Incorrect dataset change notification
        // This will crash if the size differences are significant or references aren't handled well
        // notifyItemRangeRemoved(0, tasks.size()); 
        
        // Actually, just notifyItemRangeInserted without clearing old?
        // Or clear then notifyItemRangeInserted?
        this.tasks.clear();
        this.tasks.addAll(newTasks);
        
        // Intentional Bug: Using notifyItemRangeInserted with possibly incorrect index logic when clearing
        notifyItemRangeInserted(0, newTasks.size()); 
        // This fails to remove the old items visually if not using notifyDataSetChanged() or proper DiffUtil
        // And will crash if scrolling up to old items that no longer exist in data but Recycler thinks they do.
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Intentional Bug: Potential IndexOutOfBounds if notify logic was wrong
        Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
        }
    }
}
