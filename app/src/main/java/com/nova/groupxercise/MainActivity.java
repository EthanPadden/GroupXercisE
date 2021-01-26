package com.nova.groupxercise;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        // This method executes before the screen UI elements are rendered
        super.onStart();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if there is a user currently logged in
        if ( mAuth.getCurrentUser() == null ) {
            // If the current user is null, go to the login screen
            Intent intent = new Intent( MainActivity.this, LoginActivity.class );
            startActivity( intent );
        } else {
            // If there is a user logged in, go to the home screen
            Intent intent = new Intent( MainActivity.this, HomeScreenActivity.class );
            startActivity( intent );
        }
    }
}
