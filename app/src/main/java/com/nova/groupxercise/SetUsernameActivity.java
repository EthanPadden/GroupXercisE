package com.nova.groupxercise;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SetUsernameActivity extends AppCompatActivity {
    private EditText mUsernameEt;
    private Button mSetUsernameBtn;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_username );

        // Initialise components
        mUsernameEt = findViewById( R.id.et_username );
        mSetUsernameBtn = findViewById( R.id.btn_set_username );

        // Set event listeners
        mSetUsernameBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                String username = mUsernameEt.getText().toString();

                // Check if the username exists
            }
        } );
    }
}
