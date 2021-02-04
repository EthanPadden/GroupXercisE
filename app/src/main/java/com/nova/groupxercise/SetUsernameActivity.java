package com.nova.groupxercise;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                checkIfUsernameIsValid( username );
            }
        } );
    }

    private void checkIfUsernameIsValid(String username) {
        if(username == null || username.compareTo( "" ) == 0) {
            Toast.makeText( SetUsernameActivity.this, "Invalid username", Toast.LENGTH_SHORT ).show();
        } else {
            checkIfUsernameIsAvailable( username );
        }
    }
    private void checkIfUsernameIsAvailable(String username) {

    }
}
