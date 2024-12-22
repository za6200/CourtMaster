package com.example.courtmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class Show_Program extends AppCompatActivity {

    TextView ProgramTV, ExerciseDetailsTV;
    int position = 0, error = 0;
    Training_Program trainingProgram;
    Intent programInfo;
    Button nextEx;
    YouTubePlayer activeYouTubePlayer = null;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_program);
        ProgramTV = findViewById(R.id.ProgramTV);
        ExerciseDetailsTV = findViewById(R.id.ExerciseDetails);
        nextEx = findViewById(R.id.nextEx);
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        getLifecycle().addObserver(youTubePlayerView);

        // Get the training program from the intent
        programInfo = getIntent();
        trainingProgram = (Training_Program) programInfo.getSerializableExtra("Training Program");
        position = 0;

        if (trainingProgram != null && position != -1) {
            updateExerciseDetails(); // Update the UI with the first exercise details

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    if(trainingProgram.getProgram().get(position).getUrl().isEmpty() && error < 1)
                    {
                        normalShowAlertDialog("Invalid video ID for this exercise.");
                        youTubePlayerView.setVisibility(View.INVISIBLE);
                        error++;
                    }
                    activeYouTubePlayer = youTubePlayer; // Save the YouTubePlayer instance
                    loadCurrentVideo(); // Load the first video
                }
                @Override
                public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {
                    if(error < 1) {
                        normalShowAlertDialog("Invalid video ID for this exercise.");
                        youTubePlayerView.setVisibility(View.INVISIBLE);
                        error++;
                    }
                }

            });
        }
    }

    public void nextExercise(View view) {
        if (position + 1 < trainingProgram.getProgram().size()) {
            position++; // Move to the next exercise
            updateExerciseDetails(); // Update the UI
            loadCurrentVideo(); // Load the video for the new exercise
        } else {
            showAlertDialog("Training Program Ended");
            position = 0; // Reset to the beginning
        }
    }

    private void updateExerciseDetails() {
        ExerciseDetailsTV.setText("Exercise name: " + trainingProgram.getProgram().get(position).getName() + "\n\nLevel: " + trainingProgram.getProgram().get(position).getLevel() + "\n\nRepeat: " + trainingProgram.getProgram().get(position).getRepeat() + "\n\nDescription: " + trainingProgram.getProgram().get(position).getDescription());
    }

    private void loadCurrentVideo() {
        if (activeYouTubePlayer != null) {
            String videoId = trainingProgram.getProgram().get(position).getUrl();
            if (videoId != null && !videoId.isEmpty()) {
                activeYouTubePlayer.cueVideo(videoId, 0); // Cue the video without auto-play
            }
        }
    }

    private void normalShowAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // You can add additional actions if needed
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message)
                .setCancelable(false)
                .setNeutralButton("Restart Program", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        position = 0;
                        updateExerciseDetails();
                        loadCurrentVideo(); // Load the first video
                    }
                })
                .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent PersonalTraining = new Intent(Show_Program.this, Built_In_Programs.class);
                        startActivity(PersonalTraining);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

