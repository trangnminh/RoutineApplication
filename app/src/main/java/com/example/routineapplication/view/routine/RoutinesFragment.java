package com.example.routineapplication.view.routine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Routine;
import com.example.routineapplication.model.Task;
import com.example.routineapplication.service.AlarmHandler;
import com.example.routineapplication.viewmodel.RoutineViewModel;
import com.example.routineapplication.viewmodel.TaskViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoutinesFragment extends Fragment implements RoutineRecyclerAdapter.RoutineRecyclerCallback {

    private static final String TAG = "RoutinesFragment";

    TextView totalRoutines;
    FloatingActionButton fab;
    TextView wowSuchEmpty;

    RecyclerView recyclerView;
    RoutineRecyclerAdapter mAdapter;

    private AlarmHandler mAlarmHandler;

    private RoutineViewModel mRoutineViewModel;
    private TaskViewModel mTaskViewModel;

    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        // Moved to onStart because onCreate only inflates the view
        // and haven't set up the NavController
        fab.setOnClickListener(view -> {
            // Navigate to AddRoutineFragment
            Navigation.findNavController(view)
                    .navigate(R.id.action_routinesFragment_to_addRoutineFragment);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_routines, container, false);

        // Set up misc view
        totalRoutines = view.findViewById(R.id.total_routines);
        fab = view.findViewById(R.id.routine_fab);
        wowSuchEmpty = view.findViewById(R.id.wow_such_empty);

        // Set up View Model (must do before Recycler View)
        mRoutineViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);
        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Set up AlarmReceiver
        mAlarmHandler = new AlarmHandler();

        // Observe and react to data change (in ViewModel.getAll())
        mRoutineViewModel.getAll().observe(getViewLifecycleOwner(), routines -> {
            // Update cached data
            mAdapter.setRoutines(routines);

            // Update UI
            int itemCount = mAdapter.getItemCount();

            if (itemCount == 0)
                wowSuchEmpty.setVisibility(View.VISIBLE);
            else
                wowSuchEmpty.setVisibility(View.GONE);

            totalRoutines.setText(String.valueOf(itemCount));
        });

        // Set up Recycler View
        recyclerView = view.findViewById(R.id.routine_recycler);
        mAdapter = new RoutineRecyclerAdapter(mRoutineViewModel.getAll().getValue(), this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void editRoutine(Routine routine, int position) {
        // Pass the required arg (here is Routine)
        RoutinesFragmentDirections.ActionRoutinesFragmentToEditRoutineFragment action =
                RoutinesFragmentDirections.actionRoutinesFragmentToEditRoutineFragment(routine);

        // Navigate to EditRoutineFragment
        NavHostFragment.findNavController(this)
                .navigate(action);
    }

    @Override
    public void cloneRoutine(Routine routine, int position) {
        // Create new routine from current data
        Routine clone = new Routine(
                routine.getName() + " clone",
                routine.getDescription(),
                false,
                routine.getEnabledTime(),
                routine.getEnabledWeekdays());

        // Add a clone of the selected routine
        int cloneId = (int) mRoutineViewModel.insert(clone);

        // Copy tasks of old routine to the clone
        // Cannot access database in the main thread, so a Runnable task is used
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnableTask = () -> {
            try {
                List<Task> tasks = mTaskViewModel.getAllByRoutineIdForClone(routine.getId());
                Log.i(TAG, "cloneRoutine: Cloned from Routine ID=" + routine.getId() + " Number of Tasks=" + tasks.size());

                // Add cloned tasks to database for cloned routine
                for (Task task : tasks)
                    mTaskViewModel.insert(new Task(cloneId, task.getName(), task.getDurationInMinutes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        executorService.execute(runnableTask);

        Toast.makeText(requireContext(), getString(R.string.routine_added), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteRoutine(Routine routine, int position) {
        String routineName = routine.getName();

        // Alert dialog to confirm delete
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        builder.setTitle("Delete " + routineName + "?")
                .setMessage(getString(R.string.cannot_undo))
                .setPositiveButton(getString(R.string.delete), (dialogInterface, i) -> {
                    // Delete the routine and its alarm
                    mRoutineViewModel.delete(routine);
                    mAlarmHandler.cancelAlarm(requireContext(), routine.getId());
                    mAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), getString(R.string.routine_deleted), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.cancel());

        builder.create().show();
    }

    @Override
    public void toRoutineWithTasks(Routine routine) {
        // Pass the required args (here is Routine ID and name)
        RoutinesFragmentDirections.ActionRoutinesFragmentToRoutineWithTasksFragment action =
                RoutinesFragmentDirections.actionRoutinesFragmentToRoutineWithTasksFragment(routine.getId(), routine.getName(), routine.getDescription());

        // Navigate to EditRoutineFragment
        NavHostFragment.findNavController(this)
                .navigate(action);
    }
}