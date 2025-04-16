package com.example.finalyearproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finalyearproject.InstructionGame1;
import com.example.finalyearproject.InstructionGame2;
import com.example.finalyearproject.R;

public class HomePage extends Fragment {

    private Button btnGame1, btnGame2;
    private TextView scoreTextGame1, scoreTextGame2;

    // MediaPlayer for background music
    private MediaPlayer backgroundMusic;

    // Animation for the buttons
    private Animation scaleAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout once
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize buttons and TextViews for both games
        btnGame1 = view.findViewById(R.id.btnGame1);
        btnGame2 = view.findViewById(R.id.btnNGame2);
        scoreTextGame1 = view.findViewById(R.id.scoreTextView); // Ensure this ID exists in your XML
        scoreTextGame2 = view.findViewById(R.id.scoreTextView2); // Make sure you have this ID in your XML

        // Retrieve score for MyGame1 from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MyGamePrefs", Context.MODE_PRIVATE);
        int lastScoreGame1 = prefs.getInt("last_score", -1); // Default value: -1

        // Display the score for Game 1
        if (lastScoreGame1 != -1) {
            scoreTextGame1.setText("Last Score: " + lastScoreGame1);
        } else {
            scoreTextGame1.setText("Gain the highest score!");
        }

        // Retrieve score for MyGame2 from SharedPreferences
        SharedPreferences prefs2 = requireContext().getSharedPreferences("MyGamePrefs2", Context.MODE_PRIVATE);
        int lastScoreGame2 = prefs2.getInt("last_score2", -1); // Default value: -1

        // Display the score for Game 2
        if (lastScoreGame2 != -1) {
            scoreTextGame2.setText("Last Score: " + lastScoreGame2);
        }

        // Initialize background music
        backgroundMusic = MediaPlayer.create(requireContext(), R.raw.home_page_background_music); // Replace with your music file
        backgroundMusic.setLooping(true);  // Loop the music
        backgroundMusic.start(); // Start playing background music

        // Load the scale animation
        scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_animation);

        // Set up listeners for buttons with animation
        btnGame1.setOnClickListener(v -> {
            // Apply animation on click
            btnGame1.startAnimation(scaleAnimation);

            // Start MyGame1 when btnGame1 is clicked
            Intent i = new Intent(requireContext(), InstructionGame1.class);
            startActivity(i);
        });

        btnGame2.setOnClickListener(v -> {
            // Apply animation on click
            btnGame2.startAnimation(scaleAnimation);

            // Start MyGame2 when btnGame2 is clicked
            Intent i = new Intent(requireContext(), InstructionGame2.class);
            startActivity(i);
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop the music when the fragment is paused
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause(); // Pause the music
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resume the music when the fragment is resumed
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start(); // Resume the music if it was paused
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Release the media player when the fragment is destroyed
        if (backgroundMusic != null) {
            backgroundMusic.release(); // Release the MediaPlayer resources
            backgroundMusic = null; // Set it to null
        }
    }
}
