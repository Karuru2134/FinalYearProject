package com.example.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearproject.fragments.HomePage;

public class InstructionGame1 extends AppCompatActivity {

    private Button btnN;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_game1);

        // Initialize the buttons
        btnN = findViewById(R.id.btnNGame);
        btnBack = findViewById(R.id.btnBack);

        // Set click listener to start the game
        btnN.setOnClickListener(v -> {
            Intent i = new Intent(InstructionGame1.this, MyGame1.class);
            startActivity(i);
        });

        // Set click listener to go back to the HomePage
        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(InstructionGame1.this, HomePage.class);
            startActivity(i);
        });
    }
}
