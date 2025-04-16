package com.example.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearproject.fragments.HomePage;

public class InstructionGame2 extends AppCompatActivity {

    private Button btnN2;
    private Button btnBack2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_game2);

        // Initialize the buttons
        btnN2 = findViewById(R.id.btnNGame2);
        btnBack2 = findViewById(R.id.btnBack2);

        // Set click listener to start the game
        btnN2.setOnClickListener(v -> {
            Intent i = new Intent(InstructionGame2.this, MyGame2.class);
            startActivity(i);  // Starts MyGame2 activity
        });

        // Set click listener to go back to the HomePage
        btnBack2.setOnClickListener(v -> {
            Intent i = new Intent(InstructionGame2.this, HomePage.class);
            startActivity(i);
        });
    }
}
