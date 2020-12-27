package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView mEmailText;
    private Button mLogoutBtn;
    private Button mEditDetailsBtn;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If it is the case that the current user is null, go to the login screen
        if ( mAuth.getCurrentUser() == null ) {
            // If the current user is null, go to the registration screen
            Intent intent = new Intent( HomeActivity.this, RegistrationActivity.class );
            startActivity( intent );
        } else {
            mCurrentUser = mAuth.getCurrentUser();
        }

        // Set the screen layout
        setContentView( R.layout.activity_home );

        // Initialise components
        mEmailText = findViewById( R.id.email_text );
        mLogoutBtn = findViewById( R.id.btn_logout );
        mEditDetailsBtn = findViewById( R.id.btn_edit_details_link );

        // Set event listeners
        mLogoutBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                signOutUser();
            }
        } );
        mEditDetailsBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Go to edit details screen
                Intent intent = new Intent( HomeActivity.this, EditUserDetailsActivity.class );
                startActivity( intent );
            }
        } );

        // Display the email of the current user
        mEmailText.setText( "User logged in: " + mCurrentUser.getEmail() );
    }

    /**
     * Sign out the user that is currently logged in using Firebase method
     * Toast with error message if no user is currently logged in
     */
    private void signOutUser() {
        // Check if there is a user currently logged in
        if ( mCurrentUser != null ) {
            mAuth.signOut();
        } else {
            Toast.makeText( HomeActivity.this, R.string.error_user_not_logged_in,
                    Toast.LENGTH_SHORT ).show();
        }

        // Regardless, go to login screen
        Intent intent = new Intent( HomeActivity.this, LoginActivity.class );
        startActivity( intent );
    }

}
