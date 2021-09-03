package com.example.routineapplication.view.routine;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Routine;
import com.example.routineapplication.service.AlarmHandler;
import com.example.routineapplication.viewmodel.RoutineViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddRoutineFragment extends Fragment {

    TextInputLayout name;
    TextInputLayout description;
    SwitchMaterial enableAlarm;
    TextView setTimeLabel;
    Chip setAlarmTime;
    ChipGroup setAlarmWeekday;
    Button addRoutine;

    private AlarmHandler mAlarmHandler;

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
        name = view.findViewById(R.id.et_add_routine_name);
        description = view.findViewById(R.id.et_add_routine_description);
        enableAlarm = view.findViewById(R.id.sw_enable_alarm);
        setTimeLabel = view.findViewById(R.id.tx_set_time_label);
        setAlarmTime = view.findViewById(R.id.c_set_alarm_time);
        setAlarmWeekday = view.findViewById(R.id.cg_set_alarm_weekday);
        addRoutine = view.findViewById(R.id.btn_add_routine);

        // Set up View Model
        mViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        // Set up AlarmReceiver
        mAlarmHandler = new AlarmHandler();

        // Set up alarm time picker
        setAlarmTime.setOnClickListener(v -> mAlarmHandler.setAlarmTime(this, setAlarmTime));

        // Set up add function
        addRoutine.setOnClickListener(this::addRoutine);

        return view;
    }

    // Verify input then save a new routine to the database
    public void addRoutine(View v) {
        String name = Objects.requireNonNull(AddRoutineFragment.this.name.getEditText()).getText().toString();

        // Show error if name is empty
        if (name.isEmpty())
            AddRoutineFragment.this.name.setError(getString(R.string.empty_error));

            // Toast if time is empty
        else if (enableAlarm.isChecked() && setAlarmTime.getText().toString().equalsIgnoreCase("")) {
            setTimeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
            setTimeLabel.setTypeface(setTimeLabel.getTypeface(), Typeface.BOLD);
            setTimeLabel.setText(getString(R.string.time_asterisk));
        }

        // All input is checked now
        else {
            // Get routine description
            String description = Objects.requireNonNull(AddRoutineFragment.this.description.getEditText()).getText().toString();

            // Check the weekdays enabled
            List<Integer> checkedChipIds = setAlarmWeekday.getCheckedChipIds();
            ArrayList<Integer> enabledWeekdays = getEnabledWeekdays(checkedChipIds);

            // Create new routine from current data
            Routine routine = new Routine(
                    name,
                    description,
                    enableAlarm.isChecked(),
                    setAlarmTime.getText().toString(),
                    enabledWeekdays);

            // Save new routine to database and get new rowId
            long id = mViewModel.insert(routine);

            // Set repeating alarm if enabled
            if (enableAlarm.isChecked())
                mAlarmHandler.setAlarm(requireContext(), setAlarmTime, (int) id, routine.getName(), routine.getDescription(), routine.getEnabledWeekdays());

            // Return to RoutinesFragment
            NavHostFragment.findNavController(this).popBackStack();

            // Tell user
            Toast.makeText(requireContext(), getString(R.string.routine_added), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to get all weekday chip ids
    public ArrayList<Integer> getEnabledWeekdays(List<Integer> checkedChipIds) {
        ArrayList<Integer> enabledWeekdays = new ArrayList<>();

        for (Integer i : checkedChipIds) {
            if (i == R.id.cg_mon)
                enabledWeekdays.add(Calendar.MONDAY);
            else if (i == R.id.cg_tue)
                enabledWeekdays.add(Calendar.TUESDAY);
            else if (i == R.id.cg_wed)
                enabledWeekdays.add(Calendar.WEDNESDAY);
            else if (i == R.id.cg_thu)
                enabledWeekdays.add(Calendar.THURSDAY);
            else if (i == R.id.cg_fri)
                enabledWeekdays.add(Calendar.FRIDAY);
            else if (i == R.id.cg_sat)
                enabledWeekdays.add(Calendar.SATURDAY);
            else if (i == R.id.cg_sun)
                enabledWeekdays.add(Calendar.SUNDAY);
        }

        return enabledWeekdays;
    }
}