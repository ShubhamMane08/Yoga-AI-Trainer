//package com.aiyogaguide;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.graphics.drawable.AnimationDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.content.SharedPreferences;
//import androidx.preference.PreferenceManager;
//import com.google.android.material.navigation.NavigationView;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//public class NavActivity extends AppCompatActivity {
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//    private static final String SHARED_PREFS_NAME = "user_session";
//    Button button,button2;
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nav);
//        drawerLayout = findViewById(R.id.drawer_layout);
//        button=findViewById(R.id.start);
//        DrawerLayout drawerLayout1=findViewById(R.id.drawer_layout);
//        AnimationDrawable animationDrawable= (AnimationDrawable) drawerLayout1.getBackground();
//        animationDrawable.setEnterFadeDuration(2500);
//        animationDrawable.setExitFadeDuration(5000);
//        animationDrawable.start();
////        button2=findViewById(R.id.explore);
//        navigationView = findViewById(R.id.nav_view);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//
//
//
//        button2=findViewById(R.id.explore);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "https://www.yogabreezebali.com/blog/yoga-poses-index/";
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(url));
//            startActivity(intent);
//            }
//        });
////        findViewById(R.id.button2).setOnClickListener(v -> {
////            String url = "https://www.yogabreezebali.com/blog/yoga-poses-index/";
////            Intent intent = new Intent(Intent.ACTION_VIEW);
////            intent.setData(Uri.parse(url));
////            startActivity(intent);
////        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(NavActivity.this, YogaPoseDetectionActivity.class));
//            }
//        });
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                int id = item.getItemId();
////                if (id == R.id.nav_home) {
////                    Intent intent = new Intent(NavActivity.this, NavHome.class);
////                    startActivity(intent);
//               // }
//                 if (id == R.id.nav_about) {
//                    Intent intent = new Intent(NavActivity.this, NavAbout.class);
//                    startActivity(intent);
//                } else if (id == R.id.nav_settings) {
//                    Intent intent = new Intent(NavActivity.this, NavSetting.class);
//                    startActivity(intent);
//                }
//                else if (id == R.id.nav_share) {
////                    Intent intent = new Intent(NavActivity.this, NavShare.class);
////                    startActivity(intent);
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_SUBJECT,"Check out the Appliction");
//                    intent.putExtra(Intent.EXTRA_TEXT,"Your Application Link");
//                    startActivity(Intent.createChooser(intent,"Share Via"));
//                }
//                else if(id == R.id.nav_logout){
//
//                     Intent intent = new Intent(NavActivity.this, NavLogout.class);
//                     startActivity(intent);
//
//
//                 }
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            }
//        });
//    }
//}

package com.aiyogaguide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class NavActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final String SHARED_PREFS_NAME = "user_session";
    private Button button, button2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        button = findViewById(R.id.start);
        button2 = findViewById(R.id.explore);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the toolbar and navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Background animation for the drawer layout
        AnimationDrawable animationDrawable = (AnimationDrawable) drawerLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        // Set up button click listeners
        button2.setOnClickListener(v -> {
            String url = "https://www.yogabreezebali.com/blog/yoga-poses-index/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        button.setOnClickListener(v -> startActivity(new Intent(NavActivity.this, YogaPoseDetectionActivity.class)));

        // Set up navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_about) {
                Intent intent = new Intent(NavActivity.this, NavAbout.class);
                startActivity(intent);
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(NavActivity.this, NavSetting.class);
                startActivity(intent);
            } else if (id == R.id.nav_share) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out the Application");
                intent.putExtra(Intent.EXTRA_TEXT, "Your Application Link");
                startActivity(Intent.createChooser(intent, "Share Via"));
            } else if (id == R.id.nav_logout) {
                showLogoutConfirmationDialog();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog to confirm logout
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logout() {
        // Clear session data and redirect to LoginActivity
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clear all saved data
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(NavActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

