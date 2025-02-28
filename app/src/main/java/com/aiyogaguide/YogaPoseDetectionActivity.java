package com.aiyogaguide;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class YogaPoseDetectionActivity extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4,btn5,btn6,btn7,btn8,btn9,btn10;
    ImageView backarrow;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_pose_detection);
        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YogaPoseDetectionActivity.this, NavActivity.class));
            }
        });

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn10 = findViewById(R.id.btn10);


        btn1.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Vrikshasana");
            startActivity(intent);
        });

        btn2.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Baddha-Konasana");
            startActivity(intent);
        });
        btn3.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Veerabhadrasana-2");
            startActivity(intent);
        });
        btn4.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Downward-Dog");
            startActivity(intent);
        });
        btn5.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Natarajasana");
            startActivity(intent);
        });
        btn6.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Utkatakonasana");
            startActivity(intent);
        });
        btn7.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Ardha-Chandrasana");
            startActivity(intent);
        });
        btn8.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Trikonasana");
            startActivity(intent);
        });
        btn9.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Veerabhadrasana-1");
            startActivity(intent);
        });
        btn10.setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseInfoActivity.class);
            intent.putExtra("poseName", "Veerabhadrasana-3");
            startActivity(intent);
        });

    }




}