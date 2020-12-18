package com.nova.groupxercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    private Button m_registrationBtn;
    private EditText m_emailEt;
    private EditText m_passwordEt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration);

        // Initialise components
        m_registrationBtn = findViewById(R.id.btn_register);
        m_emailEt = findViewById(R.id.et_email);
        m_passwordEt = findViewById(R.id.et_password);

        // Set event listeners
        m_registrationBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = m_emailEt.getText().toString();
                String password = m_passwordEt.getText().toString();

                boolean fieldsAreValid = validateFields(email, password);
                if(fieldsAreValid) registerUser(email, password);
            }
        });
    }

    private boolean validateFields(String email, String password) {
        boolean fieldsAreValid = false;
        if (email.equals("") || password.equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegistrationActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        } else {
            fieldsAreValid = true;
        }
        return fieldsAreValid;
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String successMsg = "Success";
                            String dn = user.getDisplayName();
                            Toast.makeText(RegistrationActivity.this, successMsg,
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            String errorMsg = "Authentication failed";
                            if(task.getException() instanceof FirebaseAuthWeakPasswordException)
                                errorMsg = task.getException().getMessage();

                            Toast.makeText(RegistrationActivity.this, errorMsg,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
