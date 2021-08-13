package com.example.routineapplication.fragments.routine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Routine;
import com.example.routineapplication.viewmodel.RoutineViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RoutinesFragment extends Fragment implements RoutineRecyclerAdapter.RoutineRecyclerCallback {

    TextView totalRoutines;
    FloatingActionButton fab;
    ImageView bottomIndicator;

    RecyclerView recyclerView;
    RoutineRecyclerAdapter mAdapter;

    private RoutineViewModel mViewModel;

    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        // Moved to onStart because onCreate only inflates the view
        // and haven't set up the NavController
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to AddRoutineFragment
                Navigation.findNavController(view)
                        .navigate(R.id.action_routinesFragment_to_addRoutineFragment);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_routines, container, false);

        // Set up misc view
        totalRoutines = view.findViewById(R.id.total_routines);
        fab = view.findViewById(R.id.routine_fab);
        bottomIndicator = view.findViewById(R.id.routine_recycler_indicator);

        // Set up View Model (must do before Recycler View)
        mViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);
        mViewModel.getAll().observe(getViewLifecycleOwner(), new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routines) {
                // Update data
                mAdapter.setRoutines(routines);

                // Update UI
                totalRoutines.setText(String.valueOf(mAdapter.getItemCount()));
            }
        });

        // Set up Recycler View
        recyclerView = (RecyclerView) view.findViewById(R.id.routine_recycler);
        mAdapter = new RoutineRecyclerAdapter(mViewModel.getAll().getValue(), this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Show indicator if not yet scrolled to bottom
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    bottomIndicator.setVisibility(View.INVISIBLE);
                else
                    bottomIndicator.setVisibility(View.VISIBLE);
            }
        });

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
    public void deleteRoutine(Routine routine, int position) {
        String routineName = routine.getName();

        // Alert dialog to confirm delete
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        builder.setTitle("Delete " + routineName + "?")
                .setMessage(getString(R.string.cannot_undo))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Delete the routine
                        mViewModel.delete(routine);
                        mAdapter.notifyItemRemoved(position);
                        Toast.makeText(requireContext(), getString(R.string.routine_deleted), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        builder.create().show();
    }

    @Override
    public void toRoutineTasks(Routine routine) {

    }
}