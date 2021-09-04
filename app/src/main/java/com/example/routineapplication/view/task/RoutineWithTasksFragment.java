package com.example.routineapplication.view.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Task;
import com.example.routineapplication.viewmodel.TaskViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RoutineWithTasksFragment extends Fragment implements TaskRecyclerAdapter.TaskRecyclerCallback {

    private static final String TAG = "RoutineWithTasksFragment";
    TextView routineWithTasksName;
    TextView routineWithTasksDescription;
    TextView totalTasks;
    FloatingActionButton runRoutineFab;
    FloatingActionButton addTaskFab;
    TextView wowSuchEmpty;
    RecyclerView recyclerView;
    TaskRecyclerAdapter mAdapter;
    private int mRoutineId;
    private String mRoutineName;
    private String mRoutineDescription;
    private TaskViewModel mTaskViewModel;

    public RoutineWithTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        runRoutineFab.setOnClickListener(view -> {
        });

        // Moved to onStart because onCreate only inflates the view
        // and haven't set up the NavController
        addTaskFab.setOnClickListener(view -> {
            // Navigate to AddTaskFragment
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_with_tasks, container, false);

        // Get current routine from SafeArgs
        if (getArguments() != null) {
            RoutineWithTasksFragmentArgs args = RoutineWithTasksFragmentArgs.fromBundle(getArguments());
            mRoutineId = args.getRoutineId();
            mRoutineName = args.getRoutineName();
            mRoutineDescription = args.getRoutineDescription();

            Log.i(TAG, "onCreateView: Retrieved Routine ID=" + mRoutineId);
        }

        // Set up misc view
        routineWithTasksName = view.findViewById(R.id.routine_with_tasks_name);
        routineWithTasksName.setText(mRoutineName);

        routineWithTasksDescription = view.findViewById(R.id.routine_with_tasks_description);

        if (mRoutineDescription.isEmpty())
            routineWithTasksDescription.setVisibility(View.GONE);
        else {
            routineWithTasksDescription.setVisibility(View.VISIBLE);
            routineWithTasksDescription.setText(mRoutineDescription);
        }

        totalTasks = view.findViewById(R.id.total_tasks);
        runRoutineFab = view.findViewById(R.id.run_routine_fab);
        addTaskFab = view.findViewById(R.id.add_task_fab);
        wowSuchEmpty = view.findViewById(R.id.wow_such_empty);

        // Set up View Model (must do before Recycler View)
        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observe and react to data change
        mTaskViewModel.getAllByRoutineId(mRoutineId).observe(getViewLifecycleOwner(), tasks -> {
            // Update cached data
            mAdapter.setTasks(tasks);

            // Update UI
            int itemCount = mAdapter.getItemCount();

            if (itemCount == 0)
                wowSuchEmpty.setVisibility(View.VISIBLE);
            else
                wowSuchEmpty.setVisibility(View.GONE);

            totalTasks.setText(String.valueOf(itemCount));
        });

        // Set up Recycler View
        recyclerView = view.findViewById(R.id.task_recycler);
        mAdapter = new TaskRecyclerAdapter(mTaskViewModel.getAllByRoutineId(mRoutineId).getValue(), this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void editTask(Task task, int position) {

    }

    @Override
    public void cloneTask(Task task, int position) {
        Task clone = new Task(task.getRoutineId(), task.getName() + " clone", task.getDurationInMinutes());
        mTaskViewModel.insert(clone);
        Toast.makeText(requireContext(), getString(R.string.task_added), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteTask(Task task, int position) {
        String taskName = task.getName();

        // Alert dialog to confirm delete
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        builder.setTitle("Delete " + taskName + "?")
                .setMessage(getString(R.string.cannot_undo))
                .setPositiveButton(getString(R.string.delete), (dialogInterface, i) -> {
                    // Delete the task
                    mTaskViewModel.delete(task);
                    mAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), getString(R.string.task_deleted), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());

        builder.create().show();
    }
}