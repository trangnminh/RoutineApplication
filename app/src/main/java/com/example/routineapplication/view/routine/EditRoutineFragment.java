package com.example.routineapplication.view.routine;

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

public class EditRoutineFragment extends Fragment {

    TextInputLayout name;
    TextInputLayout description;
    SwitchMaterial enableAlarm;
    TextView setTimeLabel;
    Chip setAlarmTime;
    ChipGroup setAlarmWeekday;
    Button editRoutine;

    private AlarmHandler mAlarmHandler;

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
        name = view.findViewById(R.id.et_edit_routine_name);
        description = view.findViewById(R.id.et_edit_routine_description);
        enableAlarm = view.findViewById(R.id.sw_edit_alarm);
        setTimeLabel = view.findViewById(R.id.tx_edit_time_label);
        setAlarmTime = view.findViewById(R.id.c_edit_alarm_time);
        setAlarmWeekday = view.findViewById(R.id.cg_edit_alarm_weekday);
        editRoutine = view.findViewById(R.id.btn_edit_routine);

        populateRoutineForm(view);

        // Set up View Model
        mViewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        // Set up AlarmReceiver
        mAlarmHandler = new AlarmHandler();

        // Set up alarm time picker
        setAlarmTime.setOnClickListener(v -> mAlarmHandler.setAlarmTime(this, setAlarmTime));

        // Set up add function
        editRoutine.setOnClickListener(this::editRoutine);

        return view;
    }

    public void editRoutine(View v) {
        String name = Objects.requireNonNull(EditRoutineFragment.this.name.getEditText()).getText().toString();

        // Show error if name is empty
        if (name.isEmpty())
            EditRoutineFragment.this.name.setError(getString(R.string.empty_error));

            // Toast if time is empty
        else if (enableAlarm.isChecked() && setAlarmTime.getText().toString().equalsIgnoreCase("")) {
            setTimeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
            setTimeLabel.setText(getString(R.string.time_asterisk));
        }

        // All input is checked now
        else {
            // Get routine description
            String description = Objects.requireNonNull(EditRoutineFragment.this.description.getEditText()).getText().toString();

            // Check the weekdays enabled
            List<Integer> checkedChipIds = setAlarmWeekday.getCheckedChipIds();
            ArrayList<Integer> enabledWeekdays = getEnabledWeekdays(checkedChipIds);

            // Edit current routine from current data
            mRoutine.setName(name);
            mRoutine.setDescription(description);
            mRoutine.setAlarmEnabled(enableAlarm.isChecked());
            mRoutine.setEnabledTime(setAlarmTime.getText().toString());
            mRoutine.setEnabledWeekdays(enabledWeekdays);

            // Save routine to database
            mViewModel.update(mRoutine);

            // Set repeating alarm if enabled, else cancel current alarm
            if (enableAlarm.isChecked())
                mAlarmHandler.setAlarm(requireContext(), setAlarmTime, mRoutine.getId(), mRoutine.getName(), mRoutine.getDescription(), mRoutine.getEnabledWeekdays());
            else
                mAlarmHandler.cancelAlarm(requireContext(), mRoutine.getId());

            // Return to RoutinesFragment
            NavHostFragment.findNavController(this).popBackStack();
            Toast.makeText(requireContext(), getString(R.string.routine_saved), Toast.LENGTH_SHORT).show();
        }
    }

    // Populate form based on current routine
    public void populateRoutineForm(View view) {
        if (mRoutine != null) {
            Objects.requireNonNull(name.getEditText()).setText(mRoutine.getName());
            Objects.requireNonNull(description.getEditText()).setText(mRoutine.getDescription());
            enableAlarm.setChecked(mRoutine.isAlarmEnabled());
            setAlarmTime.setText(mRoutine.getEnabledTime());

            ArrayList<Integer> enabledWeekdays = mRoutine.getEnabledWeekdays();
            for (Integer i : enabledWeekdays) {
                if (i == Calendar.MONDAY) {
                    Chip chip = view.findViewById(R.id.cg_mon_edit);
                    chip.setChecked(true);
                } else if (i == Calendar.TUESDAY) {
                    Chip chip = view.findViewById(R.id.cg_tue_edit);
                    chip.setChecked(true);
                } else if (i == Calendar.WEDNESDAY) {
                    Chip chip = view.findViewById(R.id.cg_wed_edit);
                    chip.setChecked(true);
                } else if (i == Calendar.THURSDAY) {
                    Chip chip = view.findViewById(R.id.cg_thu_edit);
                    chip.setChecked(true);
                } else if (i == Calendar.FRIDAY) {
                    Chip chip = view.findViewById(R.id.cg_fri_edit);
                    chip.setChecked(true);
                } else if (i == Calendar.SATURDAY) {
                    Chip chip = view.findViewById(R.id.cg_sat_edit);
                    chip.setChecked(true);
                } else if (i == Calendar.SUNDAY) {
                    Chip chip = view.findViewById(R.id.cg_sun_edit);
                    chip.setChecked(true);
                }
            }
        }
    }

    // Helper method to get all weekday chip ids
    public ArrayList<Integer> getEnabledWeekdays(List<Integer> checkedChipIds) {
        ArrayList<Integer> enabledWeekdays = new ArrayList<>();

        for (Integer i : checkedChipIds) {
            if (i == R.id.cg_mon_edit)
                enabledWeekdays.add(Calendar.MONDAY);
            else if (i == R.id.cg_tue_edit)
                enabledWeekdays.add(Calendar.TUESDAY);
            else if (i == R.id.cg_wed_edit)
                enabledWeekdays.add(Calendar.WEDNESDAY);
            else if (i == R.id.cg_thu_edit)
                enabledWeekdays.add(Calendar.THURSDAY);
            else if (i == R.id.cg_fri_edit)
                enabledWeekdays.add(Calendar.FRIDAY);
            else if (i == R.id.cg_sat_edit)
                enabledWeekdays.add(Calendar.SATURDAY);
            else if (i == R.id.cg_sun_edit)
                enabledWeekdays.add(Calendar.SUNDAY);
        }

        return enabledWeekdays;
    }
}