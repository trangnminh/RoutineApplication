package com.example.routineapplication.view.routine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.routineapplication.R;
import com.example.routineapplication.view.statistics.RoutineStatisticsFragment;
import com.example.routineapplication.view.task.RoutineWithTasksFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class RoutineDetailsFragment extends Fragment {

    private static final String TAG = "RoutineDetailsFragment";

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    private int mRoutineId;
    private String mRoutineName;
    private String mRoutineDescription;

    public RoutineDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_details, container, false);

        // Get current routine from SafeArgs
        if (getArguments() != null) {
            RoutineDetailsFragmentArgs args = RoutineDetailsFragmentArgs.fromBundle(getArguments());
            mRoutineId = args.getRoutineId();
            mRoutineName = args.getRoutineName();
            mRoutineDescription = args.getRoutineDescription();

            Log.i(TAG, "onCreateView: Retrieved Routine ID=" + mRoutineId);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new RoutineWithTasksFragment());
        viewPagerAdapter.addFragment(new RoutineStatisticsFragment());

        viewPager = view.findViewById(R.id.routine_view_pager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = view.findViewById(R.id.routine_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            if (position == 0) {
                tab.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list));
                tab.setText(getString(R.string.tasks));
            }

            if (position == 1) {
                tab.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_streak));
                tab.setText(getString(R.string.statistics));
            }

        }).attach();
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {

        List<Fragment> mFragments = new ArrayList<>();

        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the coordinated fragment inside list
            Fragment fragment = mFragments.get(position);

            if (position == 0) {
                // Send routine info to TasksFragment
                Bundle args = new Bundle();
                args.putInt(RoutineWithTasksFragment.ARG_ROUTINE_ID, mRoutineId);
                fragment.setArguments(args);
            }

            return fragment;
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }
}