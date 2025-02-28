package com.aiyogaguide;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aiyogaguide.databinding.ActivityNavAboutBinding;

public class NavAbout extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityNavAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_about);
        ImageView imageView=findViewById(R.id.back_arrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavAbout.this,NavActivity.class));
            }
        });

    }
}