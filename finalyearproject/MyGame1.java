package com.example.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.media.MediaPlayer;

public class MyGame1 extends AppCompatActivity {

    private TextView questionTextView, scoreTextView, timerTextView;
    private EditText answerEditText;
    private Button submitButton;
    private ProgressBar timerProgressBar;

    private int correctAnswer;
    private int score = 0;
    private int baseTime = 15; // base time for normal questions
    private CountDownTimer countDownTimer;
    private int currentTimeLimit;

    // MediaPlayer
    private MediaPlayer backgroundMusic;
    private MediaPlayer correctAnswerSound;
    private MediaPlayer wrongAnswerSound;
    private MediaPlayer timerTickingSound;
    private MediaPlayer timeUpSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_game1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind
        questionTextView = findViewById(R.id.questionTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitButton = findViewById(R.id.submitButton);

        // Sound
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        correctAnswerSound = MediaPlayer.create(this, R.raw.correct_answer_sound);
        wrongAnswerSound = MediaPlayer.create(this, R.raw.wrong_answer_sound);
        timerTickingSound = MediaPlayer.create(this, R.raw.timer_ticking_sound);
        timeUpSound = MediaPlayer.create(this, R.raw.time_up_sound);

        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        generateQuestion();
        startTimer();

        Button Button = findViewById(R.id.menuButton);
        Button.setOnClickListener(v -> {
            pauseGame();
            showPauseDialog();
        });

        submitButton.setOnClickListener(v -> checkAnswer());
    }

    private void generateQuestion() {
        int difficulty = (int) (Math.random() * 4);
        int num1, num2, num3;

        if (difficulty == 3) {
            // Hard question
            num1 = (int) (Math.random() * 50 + 10);
            num2 = (int) (Math.random() * 30 + 5);
            num3 = (int) (Math.random() * 10 + 1);
            correctAnswer = (num1 + num2) * num3;
            questionTextView.setText("(" + num1 + " + " + num2 + ") × " + num3 + " = ?");
            currentTimeLimit = 25;
        } else {
            // Easy questions
            currentTimeLimit = baseTime;
            switch (difficulty) {
                case 0:
                    num1 = (int) (Math.random() * 50 + 1);
                    num2 = (int) (Math.random() * 50 + 1);
                    correctAnswer = num1 + num2;
                    questionTextView.setText(num1 + " + " + num2 + " = ?");
                    break;
                case 1:
                    num1 = (int) (Math.random() * 50 + 1);
                    num2 = (int) (Math.random() * 50 + 1);
                    if (num2 > num1) { int temp = num1; num1 = num2; num2 = temp; }
                    correctAnswer = num1 - num2;
                    questionTextView.setText(num1 + " - " + num2 + " = ?");
                    break;
                case 2:
                    num1 = (int) (Math.random() * 12 + 1);
                    num2 = (int) (Math.random() * 12 + 1);
                    correctAnswer = num1 * num2;
                    questionTextView.setText(num1 + " × " + num2 + " = ?");
                    break;
            }
        }

        answerEditText.setTag(difficulty);
        answerEditText.setText("");
    }

    private void checkAnswer() {
        String userAnswer = answerEditText.getText().toString();

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswerInt = Integer.parseInt(userAnswer);
        int difficulty = (int) answerEditText.getTag();

        if (userAnswerInt == correctAnswer) {
            int points = (difficulty == 3) ? 8 : 5;
            score += points;
            Toast.makeText(this, "Correct! +" + points + " points", Toast.LENGTH_SHORT).show();
            correctAnswerSound.start();

            // Reset the timer after a correct answer
            startTimer();  // This will cancel the current timer and start a new one

        } else {
            Toast.makeText(this, "Wrong! Answer: " + correctAnswer, Toast.LENGTH_SHORT).show();
            wrongAnswerSound.start();
        }

        // Update score
        scoreTextView.setText("Score: " + score);

        // Generate new question
        generateQuestion();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();

        timerProgressBar.setMax(currentTimeLimit);
        timerProgressBar.setProgress(currentTimeLimit);
        timerTextView.setText("Time: " + currentTimeLimit + "s");

        countDownTimer = new CountDownTimer(currentTimeLimit * 1000L, 1000) {
            int timeLeft = currentTimeLimit;

            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000);
                timerTextView.setText("Time: " + timeLeft + "s");
                timerProgressBar.setProgress(timeLeft);
                if (!timerTickingSound.isPlaying()) timerTickingSound.start();
            }

            public void onFinish() {
                timeUpSound.start();
                showTimeUpDialog();
            }
        }.start();
    }

    private void showTimeUpDialog() {
        View customView = getLayoutInflater().inflate(R.layout.dialog_time_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyGame1.this)
                .setView(customView)
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        Button backHomeButton = customView.findViewById(R.id.button_back_home);
        Button restartGameButton = customView.findViewById(R.id.button_restart_game);

        backHomeButton.setOnClickListener(v -> {
            getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("last_score", score)
                    .apply();
            startActivity(new Intent(MyGame1.this, MainActivity.class));
            finish();
        });

        restartGameButton.setOnClickListener(v -> {
            score = 0; // Reset score
            scoreTextView.setText("Score: " + score); // Update the score display
            generateQuestion(); // Generate a new question
            startTimer(); // Reset and start the timer
            dialog.dismiss(); // Dismiss the dialog
        });

        dialog.show();
    }

    private void showPauseDialog() {
        View customView = getLayoutInflater().inflate(R.layout.dialog_pause, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyGame1.this)
                .setView(customView)
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        Button resumeButton = customView.findViewById(R.id.button_resume);
        Button backHomeButton = customView.findViewById(R.id.button_back_home);

        resumeButton.setOnClickListener(v -> {
            resumeGame();
            dialog.dismiss();
        });

        backHomeButton.setOnClickListener(v -> {
            getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("last_score", score)
                    .apply();
            startActivity(new Intent(MyGame1.this, MainActivity.class));
            finish();
        });

        dialog.show();
    }

    private void resumeGame() {
        startTimer();
        if (!backgroundMusic.isPlaying()) backgroundMusic.start();
    }

    private void pauseGame() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (backgroundMusic.isPlaying()) backgroundMusic.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic.isPlaying()) backgroundMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!backgroundMusic.isPlaying()) backgroundMusic.start();
    }
}
