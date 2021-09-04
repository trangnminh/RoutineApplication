package com.example.routineapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Set up Toolbar
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph()).build();
            toolbar = findViewById(R.id.toolbar);
            NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

            showDateTime();

            // Handle navigation changes
            navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
                if (navDestination.getId() == R.id.routinesFragment)
                    showDateTime();
            });
        }
    }

    private void showDateTime() {
        // Show current date time on home Toolbar
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        LocalDateTime now = LocalDateTime.now();
        toolbar.setTitle(dtf.format(now));
    }
}