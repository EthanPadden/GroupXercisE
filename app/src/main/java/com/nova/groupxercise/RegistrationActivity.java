package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {
    private Button m_registrationBtn;
    private EditText m_emailEt;
    private EditText m_passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                validateFields(email, password);
            }
        });
    }

    private void validateFields(String email, String password) {
        if (email.equals("") || password.equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegistrationActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegistrationActivity.this, "Registering...", Toast.LENGTH_SHORT).show();
        }
    }
}
