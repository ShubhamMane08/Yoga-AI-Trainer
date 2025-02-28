package com.aiyogaguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;



public class OnbordingMain extends AppCompatActivity {

    Button secondActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onbording_main);

        secondActivity = findViewById(R.id.secondActivity);

        TapTargetView TabTargetView = null;
        TabTargetView.showFor(this,
                TapTarget.forView(secondActivity,"The Magic of BreathWork","You will be redirected to second activity")
                        .outerCircleColor(R.color.Accent)
                        .outerCircleAlpha(0.76f)
                        .targetCircleColor(R.color.white)
                        .titleTextSize(30)
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
                        .targetRadius(60),
                new TapTargetView.Listener(){

                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        Intent i = new Intent(OnbordingMain.this, com.aiyogaguide.OnbordingActivity2.class);
                        startActivity(i);
                        finish();
                    }
                });

    }
}