package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView mEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If it is the case that the current user is null, go to the login screen
        if(mAuth.getCurrentUser() == null) {
            // If the current user is null, go to the registration screen
            Intent intent = new Intent(HomeActivity.this, RegistrationActivity.class);
            startActivity(intent);
        } else {
            mCurrentUser = mAuth.getCurrentUser();
        }

        // Set the screen layout
        setContentView(R.layout.activity_home);

        // Initialise components
        mEmailText = findViewById(R.id.email_text);

        // Display the email of the current user
        mEmailText.setText("User logged in: " + mCurrentUser.getEmail());
    }
}
