package com.aiyogaguide;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class NavSetting extends AppCompatActivity {

    private Switch switchMusic, switchVoiceGuide, switchSoundEffect;
    private SeekBar seekBarVoiceGuide, seekBarSoundEffect;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_setting);

        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Initialize UI elements
        switchMusic = findViewById(R.id.switch_music);
        switchVoiceGuide = findViewById(R.id.switch_voice_guide);
        switchSoundEffect = findViewById(R.id.switch_sound_effect);
        seekBarVoiceGuide = findViewById(R.id.seekbar_voice_guide);
        seekBarSoundEffect = findViewById(R.id.seekbar_sound_effect);
        Button buttonOk = findViewById(R.id.button_ok);
        ImageView imageView=findViewById(R.id.back_arrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavSetting.this,NavActivity.class));
            }
        });

        // Set initial states
        switchMusic.setChecked(true);
        switchVoiceGuide.setChecked(true);
        switchSoundEffect.setChecked(true);

        // Set listeners for SeekBars
        seekBarVoiceGuide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolume(AudioManager.STREAM_MUSIC, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarSoundEffect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolume(AudioManager.STREAM_NOTIFICATION, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Handle the OK button
        buttonOk.setOnClickListener(v -> finish());
    }

    private void setVolume(int streamType, int level) {
        int maxVolume = audioManager.getStreamMaxVolume(streamType);
        int volume = (maxVolume * level) / 100;
        audioManager.setStreamVolume(streamType, volume, 0);
    }
}
