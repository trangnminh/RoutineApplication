package com.example.routineapplication.view.task;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
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
        Button optionsButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.task_card);
            taskName = itemView.findViewById(R.id.task_card_name);
            taskDurationInMinutes = itemView.findViewById(R.id.task_card_duration);
            taskDurationInMinutesUnit = itemView.findViewById(R.id.task_card_duration_unit);
            optionsButton = itemView.findViewById(R.id.task_options_button);

            optionsButton.setOnClickListener(view -> {
                // Use wrapper to apply theme to menu
                Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, optionsButton);
                popupMenu.getMenuInflater().inflate(R.menu.task_item_menu, popupMenu.getMenu());

                // PopUp menu does not support icons
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    popupMenu.setForceShowIcon(true);

                // Handle each menu option
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.task_option_edit)
                        mCallback.editTask(mTasks.get(getAdapterPosition()), getAdapterPosition());

                    if (menuItem.getItemId() == R.id.task_option_clone)
                        mCallback.cloneTask(mTasks.get(getAdapterPosition()), getAdapterPosition());

                    if (menuItem.getItemId() == R.id.task_option_delete)
                        mCallback.deleteTask(mTasks.get(getAdapterPosition()), getAdapterPosition());

                    return false;
                });

                popupMenu.show();
            });
        }
    }
}
