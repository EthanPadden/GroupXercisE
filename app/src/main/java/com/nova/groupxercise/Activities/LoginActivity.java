package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nova.groupxercise.R;
import com.nova.groupxercise.Objects.User;

public class LoginActivity extends AppCompatActivity {
    private Button mLoginBtn;
    private Button mRegistrationBtn;
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private FirebaseAuth mAuth;
    private boolean backButtonPressed;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set content view
        setContentView( R.layout.activity_login );

        // Initialise components
        mLoginBtn = findViewById( R.id.btn_login );
        mRegistrationBtn = findViewById( R.id.btn_register_link );
        mEmailEt = findViewById( R.id.et_email );
        mPasswordEt = findViewById( R.id.et_password );

        // Set event listeners
        mLoginBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                String email = mEmailEt.getText().toString();
                String password = mPasswordEt.getText().toString();

                signInUser( email, password );
            }
        } );
        mRegistrationBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                Intent intent = new Intent( LoginActivity.this, RegistrationActivity.class );
                startActivity( intent );
            }
        } );

        // Set back button behaviour
        backButtonPressed = false;
        Toast backBtnToast = Toast.makeText( this, "Press back button again to exit", Toast.LENGTH_SHORT );
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // If back button is pressed twice - exit app
                if(!backButtonPressed) {
                    backBtnToast.show();
                    backButtonPressed = true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    backButtonPressed = false;
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Sign in the user using Firebase method
     * Checks if the email/password is null or empty
     *
     * @param email    the email of the user
     * @param password the password of the user
     */
    private void signInUser( String email, String password ) {
        if ( email == null || email.compareTo( "" ) == 0 ) {
            Toast.makeText( LoginActivity.this, R.string.error_no_email_entered,
                    Toast.LENGTH_SHORT ).show();
        } else if ( password == null || password.compareTo( "" ) == 0 ) {
            Toast.makeText( LoginActivity.this, R.string.error_no_psw_entered,
                    Toast.LENGTH_SHORT ).show();
        } else {
            mAuth.signInWithEmailAndPassword( email, password )
                    .addOnCompleteListener( this, new OnCompleteListener< AuthResult >() {
                        @Override
                        public void onComplete( @NonNull Task< AuthResult > task ) {
                            if ( task.isSuccessful() ) {
                                // Retrieve user details
                                User.getInstance().retreiveUserDetails();

                                // Go to the home screen
                                Intent intent = new Intent( LoginActivity.this, HomeScreenActivity.class );
                                startActivity( intent );
                            } else {
                                String errorMsg = task.getException().getMessage();
                                Toast.makeText( LoginActivity.this, errorMsg,
                                        Toast.LENGTH_SHORT ).show();
                            }
                        }
            } );
        }
    }
}
