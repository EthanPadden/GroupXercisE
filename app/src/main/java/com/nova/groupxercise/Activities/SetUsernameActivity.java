package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                    User.getInstance().setUsernameInDatabase( username, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete( @Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference ) {
                            if(databaseError != null) {
                                Toast.makeText( SetUsernameActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT ).show();
                            } else {
                                Toast.makeText( SetUsernameActivity.this, "Username set!", Toast.LENGTH_SHORT ).show();
                                // Go to the tutorial screen
                                Intent intent = new Intent( SetUsernameActivity.this, TutorialActivity.class );
                                startActivity( intent );
                            }
                        }
                    } );
                } else {
                    Toast.makeText( SetUsernameActivity.this, "Invalid username", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SetUsernameActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }





}
