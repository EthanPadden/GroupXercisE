package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

public class SetUsernameActivity extends AppCompatActivity {
    private EditText mUsernameEt;
    private Button mSetUsernameBtn;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_username );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialise components
        mUsernameEt = findViewById( R.id.et_username );
        mSetUsernameBtn = findViewById( R.id.btn_set_username );

        // Set event listeners
        mSetUsernameBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                String username = mUsernameEt.getText().toString();
                if(User.checkIfUsernameIsValid( username )){
                    DBListener checkForUsernameListener = new DBListener() {
                        public void onRetrievalFinished(Object retrievedData){
                            boolean isAvailable = (Boolean) retrievedData;
                            if(isAvailable) {
                                Toast.makeText( SetUsernameActivity.this, "Username set!", Toast.LENGTH_SHORT ).show();

                                // Go to the home screen
                                Intent intent = new Intent( SetUsernameActivity.this, HomeScreenActivity.class );
                                startActivity( intent );
                            } else {
                                Toast.makeText( SetUsernameActivity.this, "Username unavailable", Toast.LENGTH_SHORT ).show();
                            }
                        };
                    };
                    User.getInstance().setUsernameInDatabase( username, checkForUsernameListener );
                } else {
                    Toast.makeText( SetUsernameActivity.this, "Invalid username", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }





}
