package com.aiyogaguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class YogaPoseInfoActivity extends AppCompatActivity {

    String yogaPose;
    TextView poseTitle, benefits;
    ImageView poseImage;
    ImageView backarrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_pose_info);

        // INIT
        poseTitle = findViewById(R.id.poseTitle);
        benefits = findViewById(R.id.benefits);
        poseImage = findViewById(R.id.poseImage);
        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YogaPoseInfoActivity.this, YogaPoseDetectionActivity.class));
            }
        });

        findViewById(R.id.next).setOnClickListener(v -> {
            Intent intent = new Intent(this, YogaPoseStepsActivity.class);
            intent.putExtra("poseName", yogaPose);
            startActivity(intent);
        });
        Intent intent = getIntent();
        yogaPose = intent.getStringExtra("poseName");
        poseTitle.setText(yogaPose);
        if(yogaPose.equals("Baddha-Konasana")) {
            poseImage.setImageResource(R.drawable.baddha_konasana);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Keeps your hips and knees flexible and healthy. \n\n" +
                    "2. Encourages you to sit up straighter. \n\n" +
                    "3. Can relieve discomfort during period.\n\n" +
                    "4. Improves circulation to your lower body.\n\n" +
                    "5. Improves flexibility in groin.");
        }
        if(yogaPose.equals("Vrikshasana")) {
            poseImage.setImageResource(R.drawable.tree_pose1);
            benefits.setText("BENEFITS: \n\n" +
                    "1. Improving lung capacity and respiratory function.\n\n" +
                    "2. Reduces stress and anxiety by promoting mental clarity.\n\n" +
                    "3. Stretch the spine, relax the lower back & stretch the hips.\n\n" +
                    "4. Calms the mind.\n\n" +
                    "5. Improves overall posture by aligning the spine.");
        }
        if(yogaPose.equals("Veerabhadrasana-1")) {
            poseImage.setImageResource(R.drawable.warrior_final_1);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Enhances balance and stability.\n\n" +
                    "2. Improves focus and concentration.\n\n" +
                    "3. Increases Stamina and Endurance.\n\n" +
                    "4. Help to reduce stress and anxiety.\n\n" +
                    "5. Opens up the chest and shoulders.");
        }
        if(yogaPose.equals("Veerabhadrasana-2")) {
            poseImage.setImageResource(R.drawable.warrior_final_2);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Strengthens the Legs and Core.\n\n" +
                    "2. Improves Balance and Stability.\n\n" +
                    "3. Increases Stamina and Endurance.\n\n" +
                    "4. Opens the Chest and Shoulders.\n\n" +
                    "5. Boosts Confidence and Focus.");
        }
        if(yogaPose.equals("Veerabhadrasana-3")) {
            poseImage.setImageResource(R.drawable.warrior_final_3);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Requires balance and awareness of body position.\n\n" +
                    "2. help to build confidence and mental toughness.\n\n" +
                    "3. helping to improve overall balance.\n\n" +
                    "4. improving flexibility and reducing tension in the upper body.\n\n" +
                    "5. Boosts Confidence and Focus.");
        }


        if(yogaPose.equals("Downward-Dog")) {
            poseImage.setImageResource(R.drawable.downward_dog);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Stimulates your digestive organs, supporting better digestion. \n\n" +
                    "2. Encourages blood flow to your brain and upper body.\n\n" +
                    "3. Lengthens and releases tension in your spine.\n\n" +
                    "4. Helps reduce stress and anxiety.\n\n" +
                    "5. Promote better sleep.");
        }
        if(yogaPose.equals("Natarajasana")) {
            poseImage.setImageResource(R.drawable.nat1);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Strengthens the muscles of legs,ankles.\n\n" +
                    "2. Promoting emotional well-being.\n\n" +
                    "3. Increase circulation and oxygen flow throughout the body.\n\n" +
                    "4. Calm the nerveous system and reduce stress.\n\n" +
                    "5. Release tension and negativity.");
        }
        if(yogaPose.equals("Utkatakonasana")) {
            poseImage.setImageResource(R.drawable.utkatkonasana_final1);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Symbolizes feminine energy, resulting in inner joy.\n\n" +
                    "2. Strengthens the pelvic floor, thighs, knees, and ankles.\n\n" +
                    "3. Strengthens reproductive organs and improves fertility.\n\n" +
                    "4. Stimulates the cardiovascular system.\n\n" +
                    "5. Best pose during labour.");
}
        if(yogaPose.equals("Ardha-Chandrasana")) {
            poseImage.setImageResource(R.drawable.ardhachandra);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Helps improve body coordination and focus.\n\n" +
                    "2. Encourages proper alignment and better posture.\n\n" +
                    "3. Calms the mind and reduces stress levels.\n\n" +
                    "4. Builds strength in your thighs, calves,and ankles.\n\n" +
                    "5. Engages and strengthens your abdominal muscles.");
        }
        if(yogaPose.equals("Trikonasana")) {
            poseImage.setImageResource(R.drawable.triangle_final_pose);
            benefits.setText("BENEFITS:\n\n" +
                    "1. Expands your chest and lungs, improving breathing.\n\n" +
                    "2. Increases flexibility and openness in the hips.\n\n" +
                    "3. Enhances your sense of balance and stability.\n\n" +
                    "4. Calms the mind and helps reduce stress.\n\n" +
                    "5. Stimulates digestive organs, aiding in better digestion.");
        }
    }
}