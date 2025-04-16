package com.example.finalyearproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.media.MediaPlayer;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearproject.fragments.HomePage;

import java.util.Random;

public class MyGame2 extends AppCompatActivity {

    private ImageView[] holes;
    private TextView scoreText;
    private TextView timerText;
    private int score;
    private int timeLeft;
    private Random random;
    private Handler handler;

    private boolean isPaused = false;
    private Button pauseButton;

    private final Runnable spawnRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPaused) return;

            int holeIndex = random.nextInt(holes.length);
            ImageView hole = holes[holeIndex];

            int event = random.nextInt(4);
            switch (event) {
                case 0:
                    hole.setImageResource(R.drawable.red_bot);
                    break;
                case 1:
                    hole.setImageResource(R.drawable.blue_bot);
                    break;
                case 2:
                    hole.setImageResource(R.drawable.hamster);
                    break;
                default:
                    hole.setImageResource(R.drawable.hole);
                    break;
            }

            handler.postDelayed(() -> hole.setImageResource(R.drawable.hole), 500);
            handler.postDelayed(this, 1000);
        }
    };


    //timer logic

        private final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPaused) return;

                if (timeLeft > 0) {
                    timeLeft--;
                    timerText.setText("Time: " + timeLeft);
                    handler.postDelayed(this, 1000);
                } else {
                    endGame();
                }
            }
        };

    // MediaPlayer variables
    private MediaPlayer backgroundMusic;
    private MediaPlayer hamsterHitSound;
    private MediaPlayer robotHitSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_game2);

        initializeViews();
        initializeGameParameters();
        initializeSounds();
        startGame();
    }

    //generate holes for whack a moles

        private void initializeViews() {
            scoreText = findViewById(R.id.scoreText);
            timerText = findViewById(R.id.timerText);
            pauseButton = findViewById(R.id.pauseButton);

            pauseButton.setOnClickListener(v -> showPauseDialog());

            holes = new ImageView[9];
            holes[0] = findViewById(R.id.hole1);
            holes[1] = findViewById(R.id.hole2);
            holes[2] = findViewById(R.id.hole3);
            holes[3] = findViewById(R.id.hole4);
            holes[4] = findViewById(R.id.hole5);
            holes[5] = findViewById(R.id.hole6);
            holes[6] = findViewById(R.id.hole7);
            holes[7] = findViewById(R.id.hole8);
            holes[8] = findViewById(R.id.hole9);
        }

    private void initializeGameParameters() {
        score = 0;
        timeLeft = 30;
        random = new Random();
        handler = new Handler();
        updateScore();
        timerText.setText("Time: " + timeLeft);
    }

    private void initializeSounds() {
        // Initialize the sounds
        backgroundMusic = MediaPlayer.create(this, R.raw.game2_background_music);
        hamsterHitSound = MediaPlayer.create(this, R.raw.hamster_hit);
        robotHitSound = MediaPlayer.create(this, R.raw.robot_hit);

        // Loop the background music
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
    }

    private void startGame() {
        isPaused = false;
        handler.post(timerRunnable);
        handler.post(spawnRunnable);
    }

    public void onHoleClicked(View view) {
        if (isPaused) return;

        ImageView clickedHole = (ImageView) view;
        handleHoleClick(clickedHole);
        clickedHole.setImageResource(R.drawable.hole);
        updateScore();
    }

    //spawn hit sound and score point

    private void handleHoleClick(ImageView hole) {
        if (hole.getDrawable() == null) return;

        if (hole.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.red_bot).getConstantState()) {
            score += 1;
            robotHitSound.start();
        } else if (hole.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.blue_bot).getConstantState()) {
            score -= 1;
        } else if (hole.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.hamster).getConstantState()) {
            score += 5;
            hamsterHitSound.start();
        }
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
    }

    private void endGame() {
        timerText.setText("Game Over!");
        handler.removeCallbacks(timerRunnable);
        handler.removeCallbacks(spawnRunnable);
        showEndGameDialog();
    }

    private void showPauseDialog() {
        isPaused = true;

        // Pause the background music
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_pause, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button resumeButton = dialogView.findViewById(R.id.button_resume);
        Button backHomeButton = dialogView.findViewById(R.id.button_back_home);

        resumeButton.setOnClickListener(v -> {
            isPaused = false;
            dialog.dismiss();
            handler.post(timerRunnable);
            handler.post(spawnRunnable);

            // Resume background music
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
        });

        //bring the score to the homepage

        backHomeButton.setOnClickListener(v -> {
            // Save score to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("MyGamePrefs2", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("last_score2", score);
            editor.apply();

            dialog.dismiss();
            Intent intent = new Intent(MyGame2.this, HomePage.class);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    //Pause Menu

    private void showEndGameDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_time_up, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button backButton = dialogView.findViewById(R.id.button_back_home);
        Button restartButton = dialogView.findViewById(R.id.button_restart_game);

        backButton.setOnClickListener(v -> {
            // Save score to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("MyGamePrefs2", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("last_score2", score);
            editor.apply();

            dialog.dismiss();
            Intent intent = new Intent(MyGame2.this, MainActivity.class); // Back to homepage
            startActivity(intent);
            finish();
        });

        restartButton.setOnClickListener(v -> {
            dialog.dismiss();
            initializeGameParameters();
            startGame();
        });

        dialog.show();
    }


    //hit sound

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

        // Release the MediaPlayer resources
        if (backgroundMusic != null) {
            backgroundMusic.release();
        }
        if (hamsterHitSound != null) {
            hamsterHitSound.release();
        }
        if (robotHitSound != null) {
            robotHitSound.release();
        }
    }
}
