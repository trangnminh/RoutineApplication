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

public class AddRoutineFragment extends Fragment {

    TextInputLayout nameInput;
    TextInputLayout descriptionInput;
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
        nameInput = view.findViewById(R.id.add_routine_name);
        descriptionInput = view.findViewById(R.id.add_routine_description);
        addButton = view.findViewById(R.id.add_routine_button);

        // Set up View Model
        mViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        // Configure save function and input check
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getEditText().getText().toString();

                // Show error if name is empty
                if (name.isEmpty())
                    nameInput.setError(getString(R.string.empty_error));
                else {
                    // Save routine to database
                    String description = descriptionInput.getEditText().getText().toString();
                    Routine routine = new Routine(name, description);

                    mViewModel.insert(routine);
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