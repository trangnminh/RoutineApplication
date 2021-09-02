package com.example.routineapplication.view.routine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Task;
import com.example.routineapplication.viewmodel.TaskViewModel;
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
    private TaskViewModel mViewModel;

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
        routineWithTasksDescription.setText(mRoutineDescription);

        totalTasks = view.findViewById(R.id.total_tasks);
        runRoutineFab = view.findViewById(R.id.run_routine_fab);
        addTaskFab = view.findViewById(R.id.add_task_fab);
        wowSuchEmpty = view.findViewById(R.id.wow_such_empty);

        // Set up View Model (must do before Recycler View)
        mViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observe and react to data change
        mViewModel.getAllByRoutineId(mRoutineId).observe(getViewLifecycleOwner(), tasks -> {
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
        mAdapter = new TaskRecyclerAdapter(mViewModel.getAllByRoutineId(mRoutineId).getValue(), this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void editTask(Task task, int position) {

    }

    @Override
    public void cloneTask(Task task, int position) {

    }

    @Override
    public void deleteTask(Task task, int position) {

    }
}