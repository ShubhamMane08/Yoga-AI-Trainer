package com.aiyogaguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OnbordingActivity2 extends AppCompatActivity {

    Button btn1,btn2,btn3,btn4;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onbording2);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        floatingActionButton = findViewById(R.id.floatingBtn);

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(btn1,"Develops","self-awareness")
                                .outerCircleColor(R.color.pink_700)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(15)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(25)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(50),
                        TapTarget.forView(btn2,"Increases","Energy Level")
                                .outerCircleColor(R.color.pink_200)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(30)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(50),
                        TapTarget.forView(btn3,"Strengthens","Intuition")
                                .outerCircleColor(R.color.pink_700)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(30)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(50),
                        TapTarget.forView(btn4,"Improves","Self-love")
                                .outerCircleColor(R.color.pink_200)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(30)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(50),
                        TapTarget.forView(floatingActionButton,"Button 3","This is Button 3")
                                .outerCircleColor(R.color.pink_700)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.black)
                                .textColor(R.color.black)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(50)).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

//                        Toast.makeText(OnbordingActivity2.this,"Sequence Finished",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OnbordingActivity2.this,YogaPoseInfoActivity.class));

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                        Toast.makeText(OnbordingActivity2.this,"GREAT!",Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                }).start();

    }
}