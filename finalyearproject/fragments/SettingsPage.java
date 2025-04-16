package com.example.finalyearproject.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalyearproject.R;

import java.io.IOException;

public class SettingsPage extends Fragment {

    private EditText usernameEdit, bioEdit, emailEdit, phoneEdit;
    private Button editButton;
    private ImageView profileImage;
    private boolean isEditing = false;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_page, container, false);

        // Bind views
        usernameEdit = view.findViewById(R.id.username);
        bioEdit = view.findViewById(R.id.bio);
        emailEdit = view.findViewById(R.id.email);
        phoneEdit = view.findViewById(R.id.phone);
        editButton = view.findViewById(R.id.edit_button);
        profileImage = view.findViewById(R.id.profile_image);

        // Disable editing initially
        toggleFields(false);

        // Toggle edit/save button logic
        editButton.setOnClickListener(v -> {
            if (!isEditing) {
                toggleFields(true);
                editButton.setText("Save");
                isEditing = true;
            } else {
                // Save profile details
                if (saveProfile()) {
                    toggleFields(false);
                    editButton.setText("Edit Profile");
                    isEditing = false;
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Register image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.Source source = ImageDecoder.createSource(
                                        requireActivity().getContentResolver(), imageUri);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().getContentResolver(), imageUri);
                            }
                            profileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Profile picture click to change image
        profileImage.setOnClickListener(v -> {
            if (isEditing) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });

        return view;
    }

    private void toggleFields(boolean enable) {
        usernameEdit.setEnabled(enable);
        bioEdit.setEnabled(enable);
        emailEdit.setEnabled(enable);
        phoneEdit.setEnabled(enable);
    }

    // Now returns boolean: true if valid and saved, false if failed
    private boolean saveProfile() {
        // Get the user input
        String username = usernameEdit.getText().toString().trim();
        String bio = bioEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email (e.g., user@example.com)", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate phone number (digits only and length between 10-15)
        if (phone.length() < 10 || phone.length() > 11 || !phone.matches("\\d+")) {
            Toast.makeText(requireContext(), "Please enter a valid phone number (10-11 digits only)", Toast.LENGTH_LONG).show();
            return false;
        }

        // If valid, proceed to "save" (currently just print)
        System.out.println("Username: " + username);
        System.out.println("Bio: " + bio);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);

        return true;
    }
}
