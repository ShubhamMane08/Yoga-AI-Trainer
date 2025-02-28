package com.aiyogaguide;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
public class YogaPoseStepsActivity extends AppCompatActivity {
    String yogaPose;
    TextView poseTitle, poseSteps;
    VideoView videoView;
    Button startPose;
    ImageView backarrow;
    ImageButton playButton;
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_pose_steps);
        backarrow=findViewById(R.id.back_arrow);
        playButton = findViewById(R.id.play_button);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YogaPoseStepsActivity.this, YogaPoseInfoActivity.class));
            }
        });
        // INIT
        poseTitle = findViewById(R.id.poseTitle);
        poseSteps = findViewById(R.id.poseSteps);

        videoView=findViewById(R.id.video);
        startPose=findViewById(R.id.startPose);
        Intent intent = getIntent();
        yogaPose = intent.getStringExtra("poseName");
        poseTitle.setText(yogaPose);
        findViewById(R.id.startPose).setOnClickListener(v -> showWarningDialog());

        if(yogaPose.equals("Baddha-Konasana")) {
            {

                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Stretch your legs out in front of you as you sit on the ground.\n\n" +
                        "Step 2: Bend right and left knee and bring your heels close to area between thighs so the soles of both feet touch.\n\n" +
                        "Step 3:Hold feet with hands & try to keep thighs as close to the ground. \n\n" +
                        "Step 4: Straighten up your spine.\n"
                );
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.baddha_konasana_vid_p));
                }
            });
        }

        if(yogaPose.equals("Vrikshasana")) {
            {

                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Stand Straight in front of Camera.\n\n" +
                        "Step 2: Bend right leg placing the right foot on the inner left thigh.\n\n" +
                        "Step 3: Join hands in Namaskar position.\n\n" +
                        "Step 4: Stretch your arms up towards the ceiling.\n"
                );
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tree_p));
                }
            });
        }


        if(yogaPose.equals("Veerabhadrasana-2")) {
            {
                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Stand Straight in front of Camera.\n\n" +
                        "Step 2: Stand in a wide position with your feet parallel and approximately three feet apart. Extend your arms straight out from your sides. \n\n" +
                        "Step 3: Turn your right foot and knee forward, angle your left toes slightly inward, bend your right knee over ankle,distribute weight evenly while reaching through arms.\n"
                );
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.warrior_2_p));
                }
            });
        }
        if(yogaPose.equals("Veerabhadrasana-1")) {
            {
                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Stand Straight with your arm on your sides.\n\n" +
                        "Step 2: Step the left leg back into a lunge where the knee aligns over the ankle, bring the hands to the hips.\n\n" +
                        "Step 3: Reach the arms forward wrap the triceps under. \n\n" +
                        "Step 4: Elevate the arms up overhead where the biceps align somewhere between cheeks and the ears.\n"
                );
            }

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.warrior_1_p));
                }
            });
        }
        if(yogaPose.equals("Veerabhadrasana-3")) {
            poseSteps.setText("Steps:\n\n" +
                    "Step 1: Stand Straight with your arm on your sides.\n\n" +
                    "Step 2: Begin to fold torso forward as you reach right leg behind, bringing torso and right leg in one straight line, point the toes of lifted leg foot and look down. \n\n"+
                    "Step 3: Reach your arm forward and draw your ribs in.\n"
            );
        }
        if (yogaPose.equals("Downward-Dog")) {
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.downward_dog_vid));
                }
            });
            //   videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid_tree_1080p));
            poseSteps.setText("Steps:\n\n" +
                            "Step 1: Bend your knees, resting your hands and your wrists on the floor underneath the shoulders. \n\n" +
                            "Step 2: Push your body back using your hands while curling in your toes.\n\n " +
                            "Step 3: Stretch your legs while lifting your hips.\n"
            );
        }
        if(yogaPose.equals("Natarajasana")) {
            {
                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Stand Straight with your arm on your sides.\n\n" +
                        "Step 2: While inhaling bend your right leg backward and holds right ankle with right hand.\n\n" +
                        "Step 3: Bend your right leg upward and Extend your left hand straight out front.\n\n" +
                        "Step 3: Try to bring your right hand upward as much as you can.\n"
                );
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.natarajasana));
                }
            });
        }
        if (yogaPose.equals("Utkatakonasana")) {
            {

                //   videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid_tree_1080p));
                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Stand Straight in front of Camera.\n\n" +
                        "Step 2: Slowly bend your knees,feet slightly wider than hip-width apart.\n\n" +
                        "Step 3: Bending the elbows to form a L shape.\n"
                );
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.goddest_p));
                }
            });
        }
        if(yogaPose.equals("Trikonasana")) {
            {
                poseSteps.setText("Steps:\n\n" +
                        "Step 1: Take a deep breath,jump and spread out the legs sideways.\n" +
                        "Step 2: Raise both the arm at the shoulder level.\n\n" +
                        "Step 3: Left foot points forward and the right foot points to the right,inhale and extend your torso to the right leading with right hand.\n\n" +
                        "Step 4: As you exhale bring the bench the trunk sideways, bring the right  and near right angle,keeping right knee straight.\n"
                );
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playvideo(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.triangle_final_p));
                }
            });
        }

        if(yogaPose.equals("Ardha-Chandrasana")) {
            poseSteps.setText("Steps:\n\n" +
                    "Step 1: Stand Straight in front of Camera.\n\n" +
                    "Step 2: Come into Parsvakonasana position face towards right side.\n\n" +
                    "Step 3: Rise front leg sole of the foot and lift back leg up in 90 degree,left arm fingertips are underneath shoulder.\n\n"+
                    "Step 4:Gaze down and reach your top arm up towards the sky.\n"
            );
        }
    }
    private void playvideo(Uri videourl){
        playButton.setVisibility(View.GONE);
        videoView.setVideoURI(videourl);

        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setVisibility(View.VISIBLE); // Show the play button again after the video ends
            }
        });
    }

    private void showWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("1) Ensure Background light. \n2) Ensure your whole body to be visible.\n\nAre you sure you want to start?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Continue with next activity

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("poseName", yogaPose);
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    // Close the dialog
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
        private class PlayAudioTask extends AsyncTask<String, Void, Void> {
            private MediaPlayer mediaPlayer;
            Context context;
            public PlayAudioTask(Context context){
                this.context=context;
            }
            @Override
            protected Void doInBackground(String... strings) {
                mediaPlayer = MediaPlayer.create(context,R.raw.audio_music);

                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
            @Override
            protected void onCancelled() {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
            }
        }
}