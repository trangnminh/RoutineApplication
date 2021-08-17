package com.example.routineapplication.view.routine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routineapplication.R;
import com.example.routineapplication.model.Routine;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RoutineRecyclerAdapter extends RecyclerView.Adapter<RoutineRecyclerAdapter.RoutineViewHolder> {

    List<Routine> mRoutines;

    RoutineRecyclerCallback mCallback;

    public RoutineRecyclerAdapter(List<Routine> routines, RoutineRecyclerCallback callback) {
        this.mRoutines = routines;
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.routine_item, parent, false);

        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        // Populate card view based on item data
        Routine routine = mRoutines.get(position);

        holder.routineName.setText(routine.getName());

        String description = routine.getDescription();

        if (description.isEmpty())
            holder.routineDescription.setVisibility(View.GONE);
        else {
            holder.routineDescription.setVisibility(View.VISIBLE);
            holder.routineDescription.setText(description);
        }

        if (!routine.isAlarmEnabled())
            holder.routineSchedule.setVisibility(View.GONE);
        else {
            holder.routineSchedule.setVisibility(View.VISIBLE);

            String enabledTime = routine.getEnabledTime();
            ArrayList<Integer> enabledWeekdays = routine.getEnabledWeekdays();
            StringBuilder builder = new StringBuilder();

            if (enabledWeekdays.size() == 7)
                builder.append("Everyday");
            else {
                for (Integer i : enabledWeekdays) {
                    if (i == Calendar.MONDAY)
                        builder.append("Mon");
                    else if (i == Calendar.TUESDAY)
                        builder.append("Tue");
                    else if (i == Calendar.WEDNESDAY)
                        builder.append("Wed");
                    else if (i == Calendar.THURSDAY)
                        builder.append("Thu");
                    else if (i == Calendar.FRIDAY)
                        builder.append("Fri");
                    else if (i == Calendar.SATURDAY)
                        builder.append("Sat");
                    else if (i == Calendar.SUNDAY)
                        builder.append("Sun");

                    if (enabledWeekdays.indexOf(i) != enabledWeekdays.size() - 1)
                        builder.append(", ");
                }
            }

            builder.append(" at ");
            builder.append(enabledTime);

            holder.routineSchedule.setText(builder.toString());
        }
    }

    @Override
    public int getItemCount() {
        if (mRoutines != null)
            return mRoutines.size();
        return 0;
    }

    public void setRoutines(List<Routine> mRoutines) {
        this.mRoutines = mRoutines;
        notifyDataSetChanged();
    }

    public interface RoutineRecyclerCallback {
        void editRoutine(Routine routine, int position);

        void deleteRoutine(Routine routine, int position);

        void toRoutineTasks(Routine routine);
    }

    class RoutineViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView card;
        TextView routineName;
        TextView routineDescription;
        TextView routineSchedule;
        Button editButton;
        Button deleteButton;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.routine_card);
            routineName = itemView.findViewById(R.id.routine_card_name);
            routineDescription = itemView.findViewById(R.id.routine_card_description);
            routineSchedule = itemView.findViewById(R.id.routine_card_schedule);
            editButton = itemView.findViewById(R.id.edit_routine_button);
            deleteButton = itemView.findViewById(R.id.delete_routine_button);

            editButton.setOnClickListener(view -> mCallback.editRoutine(mRoutines.get(getAdapterPosition()), getAdapterPosition()));

            deleteButton.setOnClickListener(view -> mCallback.deleteRoutine(mRoutines.get(getAdapterPosition()), getAdapterPosition()));

            card.setOnClickListener(view -> mCallback.toRoutineTasks(mRoutines.get(getAdapterPosition())));
        }
    }
}
