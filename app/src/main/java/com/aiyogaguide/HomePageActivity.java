package com.aiyogaguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        findViewById(R.id.button2).setOnClickListener(v -> {
            String url = "https://www.yogabreezebali.com/blog/yoga-poses-index/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        findViewById(R.id.button).setOnClickListener(v -> startActivity(new Intent(this, YogaPoseDetectionActivity.class)));
    }
}