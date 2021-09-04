package com.example.routineapplication.view.task;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Task;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder> {

    List<Task> mTasks;

    TaskRecyclerCallback mCallback;

    public TaskRecyclerAdapter(List<Task> tasks, TaskRecyclerCallback callback) {
        this.mTasks = tasks;
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.task_item, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Populate card view based on item data
        Task task = mTasks.get(position);

        holder.taskName.setText(task.getName());

        int minutes = task.getDurationInMinutes();
        holder.taskDurationInMinutes.setText(String.valueOf(minutes));

        Resources res = holder.itemView.getContext().getResources();
        String durationInMinutes = res.getQuantityString(R.plurals.minute, minutes);
        holder.taskDurationInMinutesUnit.setText(durationInMinutes);
    }

    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        return 0;
    }

    public void setTasks(List<Task> mTasks) {
        this.mTasks = mTasks;
        notifyDataSetChanged();
    }

    public interface TaskRecyclerCallback {
        void editTask(Task task, int position);

        void cloneTask(Task task, int position);

        void deleteTask(Task task, int position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView card;
        TextView taskName;
        TextView taskDurationInMinutes;
        TextView taskDurationInMinutesUnit;
        Button editButton;
        Button cloneButton;
        Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.task_card);
            taskName = itemView.findViewById(R.id.task_card_name);
            taskDurationInMinutes = itemView.findViewById(R.id.task_card_duration);
            taskDurationInMinutesUnit = itemView.findViewById(R.id.task_card_duration_unit);
            editButton = itemView.findViewById(R.id.edit_task_button);
            cloneButton = itemView.findViewById(R.id.clone_task_button);
            deleteButton = itemView.findViewById(R.id.delete_task_button);

            editButton.setOnClickListener(view -> mCallback.editTask(mTasks.get(getAdapterPosition()), getAdapterPosition()));
            cloneButton.setOnClickListener(view -> mCallback.cloneTask(mTasks.get(getAdapterPosition()), getAdapterPosition()));
            deleteButton.setOnClickListener(view -> mCallback.deleteTask(mTasks.get(getAdapterPosition()), getAdapterPosition()));
        }
    }
}
