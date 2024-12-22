package com.example.courtmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class Show_Program extends AppCompatActivity {

    TextView ProgramTV, ExerciseDetailsTV;
    int position = 0;
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
        position = 0;

        if (trainingProgram != null && position != -1) {
            ProgramTV.setText(trainingProgram.getName());
            ExerciseDetailsTV.setText("Exercise name: " + trainingProgram.getProgram().get(position).getName() + "\n\nLevel: " + trainingProgram.getProgram().get(position).getLevel() + "\n\nRepeat: " + trainingProgram.getProgram().get(position).getRepeat() + "\n\nDescription: " + trainingProgram.getProgram().get(position).getDescription() + "\n");
            position++;
        } else {
            showAlertDialog("Training program not found!");
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
            showAlertDialog("Training Program Ended");
            position = 0;
        }

    }
    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Restart Program", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        position = 0;
                        ExerciseDetailsTV.setText("Exercise name: " + trainingProgram.getProgram().get(position).getName() + "\n\nLevel: " + trainingProgram.getProgram().get(position).getLevel() + "\n\nRepeat: " + trainingProgram.getProgram().get(position).getRepeat() + "\n\nDescription: " + trainingProgram.getProgram().get(position).getDescription() + "\n");
                        position++;
                    }
                })
                .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent PersonalTraining = new Intent(Show_Program.this, MainScreen.class);
                        startActivity(PersonalTraining);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
