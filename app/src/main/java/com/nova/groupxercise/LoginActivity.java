package com.nova.groupxercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {
    private Button mLoginBtn;
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set content view
        setContentView(R.layout.activity_login);

        // Initialise components
        mLoginBtn = findViewById(R.id.btn_login);
        mEmailEt = findViewById(R.id.et_email);
        mPasswordEt = findViewById(R.id.et_password);

        // Set event listeners
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = mEmailEt.getText().toString();
                String password = mPasswordEt.getText().toString();

                signInUser(email,password);
            }
        });
    }

    private void signInUser(String email, String password) {
        if(email == null || email.compareTo("") == 0) {
            Toast.makeText(LoginActivity.this, "Enter an email address",
                    Toast.LENGTH_SHORT).show();
        } else if(password == null || password.compareTo("") == 0) {
            Toast.makeText(LoginActivity.this, "Enter a password",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If the current user is null, go to the login screen
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                String errorMsg = "Authentication failed";
                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                    errorMsg = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }
}
