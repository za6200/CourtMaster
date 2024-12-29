package com.example.courtmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class Show_Program extends AppCompatActivity {

    TextView ProgramTV, ExerciseDetailsTV;
    RatingBar programRatingBar;
    int position = 0, error = 0;
    Training_Program trainingProgram;
    Intent programInfo;
    Button nextEx;
    YouTubePlayer activeYouTubePlayer = null;
    YouTubePlayerView youTubePlayerView;
    boolean MyProgram, DataChange, rated;
    int exNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_program);

        ProgramTV = findViewById(R.id.ProgramTV);
        ExerciseDetailsTV = findViewById(R.id.ExerciseDetails);
        programRatingBar = findViewById(R.id.programRatingBar);
        nextEx = findViewById(R.id.nextEx);
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        getLifecycle().addObserver(youTubePlayerView);

        // Initially hide the rating bar
        programRatingBar.setVisibility(View.GONE);

        programRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {

            }
        });

        // Get the training program from the intent
        programInfo = getIntent();
        trainingProgram = (Training_Program) programInfo.getSerializableExtra("Training Program");
        MyProgram = programInfo.getBooleanExtra("My program", false);
        ProgramTV.setText(trainingProgram.getName());
        position = 0;
        exNum = 0;
        DataChange = false;
        rated = false;

        if (trainingProgram != null && position != -1) {
            updateExerciseDetails(); // Update the UI with the first exercise details

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    if (trainingProgram.getProgram().get(position).getUrl().isEmpty() && error < 1) {
                        normalShowAlertDialog("Invalid video ID for this exercise.");
                        youTubePlayerView.setVisibility(View.INVISIBLE);
                        error++;
                    }
                    activeYouTubePlayer = youTubePlayer; // Save the YouTubePlayer instance
                    loadCurrentVideo(); // Load the first video
                }

                @Override
                public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {
                    if (error < 1) {
                        normalShowAlertDialog("Invalid video ID for this exercise.");
                        youTubePlayerView.setVisibility(View.INVISIBLE);
                        error++;
                    }
                }
            });
        }
    }

    public void nextExercise(View view) {
        exNum++;
        if (exNum == trainingProgram.getProgram().size()-1) {
            nextEx.setText("Finish Program");

        }
        if (nextEx.getText().toString().equals("Submit Rating")) {
            float rating = programRatingBar.getRating();
            trainingProgram.getRatings().add(rating);

            float sum = 0f;
            for (float r : trainingProgram.getRatings()) {
                sum += r;
            }
            float average = sum / (trainingProgram.getRatings().size() - 1);

            trainingProgram.setRating(average);
            DatabaseReference pRef = FirebaseDatabase.getInstance().getReference("TrainingPrograms").child(trainingProgram.getName());
            pRef.child("rating").setValue(average);
            pRef.child("ratings").setValue(trainingProgram.getRatings());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference UserRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(Uid)
                    .child("programs");

            UserRef.push().setValue(trainingProgram.getName())
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            showAlertDialog("Failed to add program to your list.");
                        }
                    });
            builder.setMessage("Thank you for rating " + trainingProgram.getName() + " program\n\nRate: " + rating + "/5")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            Intent BuiltIn = new Intent(Show_Program.this, Built_In_Programs.class);
                            startActivity(BuiltIn);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        } else if (position + 1 < trainingProgram.getProgram().size()) {
            position++;
            updateExerciseDetails();
            loadCurrentVideo(); // Load the video for the new exercise
        }  else {
            showAlertDialog("Training Program Ended");
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
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(rated) {
                            Intent BuiltIn = new Intent(Show_Program.this, Built_In_Programs.class);
                            startActivity(BuiltIn);
                            rated = false;
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setNeutralButton("Restart Program", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        position = 0;
                        updateExerciseDetails();
                        loadCurrentVideo();
                    }
                })
                .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(MyProgram)
                        {
                            Intent MyPrograms = new Intent(Show_Program.this, My_Programs.class);
                            startActivity(MyPrograms);
                        }
                        else {
                            Intent PersonalTraining = new Intent(Show_Program.this, Built_In_Programs.class);
                            startActivity(PersonalTraining);
                        }
                    }
                })
                .setPositiveButton("Submit Rating", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if(MyProgram)
                        {
                            normalShowAlertDialog("Can't rate yourself");
                            return;
                        }
                        else if(trainingProgram.getCreator().equals(currentUid))
                        {
                            normalShowAlertDialog("Can't rate yourself");
                            return;
                        }
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

                        userRef.child(currentUid).child("programs").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 if(!DataChange) {
                                     DataChange = true;
                                     for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                         if (snapshot.getValue().equals(trainingProgram.getName())) {
                                             rated = true;
                                             normalShowAlertDialog("Already rated this program");


                                         }
                                     }
                                 }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        ExerciseDetailsTV.setText("Rate the program using the RatingBar below:");
                        nextEx.setText("Submit Rating");
                        youTubePlayerView.setVisibility(View.INVISIBLE);
                        programRatingBar.setVisibility(View.VISIBLE);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void go_back(View view) {
        if(MyProgram)
        {
            Intent MyPrograms = new Intent(Show_Program.this, My_Programs.class);
            startActivity(MyPrograms);
        }
        else {
            Intent BuiltIn = new Intent(Show_Program.this, Built_In_Programs.class);
            startActivity(BuiltIn);
        }
    }
}
