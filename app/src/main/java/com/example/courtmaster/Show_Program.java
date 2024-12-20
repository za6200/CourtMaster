package com.example.courtmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class Show_Program extends AppCompatActivity {

    TextView ProgramTV, ExerciseDetailsTV;
    int position;
    Training_Program trainingProgram;
    Intent programInfo;
    Button nextEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_program);
        ProgramTV = findViewById(R.id.ProgramTV);
        ExerciseDetailsTV = findViewById(R.id.ExerciseDetails);
        nextEx = findViewById(R.id.nextEx);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtubePlayer);
        getLifecycle().addObserver(youTubePlayerView);

        programInfo = getIntent();
        trainingProgram = (Training_Program) programInfo.getSerializableExtra("Training Program");
        position = programInfo.getIntExtra("position", -1);

        if (trainingProgram != null && position != -1) {
            ProgramTV.setText(trainingProgram.getName());
            ExerciseDetailsTV.setText("Exercise name: " + trainingProgram.getProgram().get(position).getName() + "\n\nLevel: " + trainingProgram.getProgram().get(position).getLevel() + "\n\nRepeat: " + trainingProgram.getProgram().get(position).getRepeat() + "\n\nDescription: " + trainingProgram.getProgram().get(position).getDescription() + "\n");
            position++;
        } else {
            Toast.makeText(this, "Training program not found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextExercise(View view) {
        position++;
        if(position <= trainingProgram.getProgram().size())
        {
            position--;
            nextEx.setText("Next Exercise");
            ExerciseDetailsTV.setText("Exercise name: " + trainingProgram.getProgram().get(position).getName() + "\n\nLevel: " + trainingProgram.getProgram().get(position).getLevel() + "\n\nRepeat: " + trainingProgram.getProgram().get(position).getRepeat() + "\n\nDescription: " + trainingProgram.getProgram().get(position).getDescription() + "\n");
            position++;
        }
        else {
            Toast.makeText(this, "Traning Program Ended", Toast.LENGTH_SHORT).show();
            nextEx.setText("Go Back");
            position = 0;
        }

    }
}
