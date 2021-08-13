package com.example.routineapplication.view.routine;

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

public class EditRoutineFragment extends Fragment {

    TextInputLayout nameInput;
    TextInputLayout descriptionInput;
    Button saveButton;

    private Routine mRoutine;

    private RoutineViewModel mViewModel;

    public EditRoutineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_routine, container, false);

        // Get current routine from SafeArgs
        if (getArguments() != null) {
            EditRoutineFragmentArgs args = EditRoutineFragmentArgs.fromBundle(getArguments());
            mRoutine = args.getRoutine();
        }

        // Set up misc views
        nameInput = view.findViewById(R.id.edit_routine_name);
        descriptionInput = view.findViewById(R.id.edit_routine_description);
        saveButton = view.findViewById(R.id.edit_routine_button);

        // Populate form fields
        nameInput.getEditText().setText(mRoutine.getName());
        descriptionInput.getEditText().setText(mRoutine.getDescription());

        // Set up View Model
        mViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        // Configure save function
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gather edited input
                String name = nameInput.getEditText().getText().toString();
                String description = descriptionInput.getEditText().getText().toString();

                // Do input check
                if (name.isEmpty())
                    nameInput.setError(getString(R.string.empty_error));
                else {
                    // Edit current routine
                    mRoutine.setName(name);
                    mRoutine.setDescription(description);

                    // Update in database and tell user
                    mViewModel.update(mRoutine);
                    Toast.makeText(requireContext(), getString(R.string.routine_saved), Toast.LENGTH_SHORT).show();

                    // Return to RoutinesFragment
                    Navigation.findNavController(view)
                            .navigate(R.id.action_editRoutineFragment_to_routinesFragment);
                }
            }
        });
        return view;
    }
}