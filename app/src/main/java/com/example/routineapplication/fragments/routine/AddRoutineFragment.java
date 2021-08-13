package com.example.routineapplication.fragments.routine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Routine;
import com.example.routineapplication.viewmodel.RoutineViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class AddRoutineFragment extends Fragment {

    TextInputLayout name;
    TextInputLayout description;
    Button addButton;

    private RoutineViewModel mViewModel;

    public AddRoutineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_routine, container, false);

        // Set up misc views
        name = view.findViewById(R.id.add_routine_name);
        description = view.findViewById(R.id.add_routine_description);
        addButton = view.findViewById(R.id.add_routine_button);

        // Set up View Model
        mViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        // Configure save function and input check
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = name.getEditText().getText().toString();

                // Show error if name is empty
                if (newName.isEmpty())
                    name.setError(getString(R.string.empty_error));
                else {
                    // Save routine to database
                    String newDescription = description.getEditText().getText().toString();
                    Routine newRoutine = new Routine(newName, newDescription);

                    mViewModel.insert(newRoutine);
                    Toast.makeText(requireContext(), getString(R.string.routine_added), Toast.LENGTH_SHORT).show();

                    // Return to RoutinesFragment
                    Navigation.findNavController(view)
                            .navigate(R.id.action_addRoutineFragment_to_routinesFragment);
                }
            }
        });
        return view;
    }
}