package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                checkIfUsernameIsValid( username );
            }
        } );
    }

    private void checkIfUsernameIsValid( String username ) {
        if ( username == null || username.compareTo( "" ) == 0 ) {
            Toast.makeText( SetUsernameActivity.this, "Invalid username", Toast.LENGTH_SHORT ).show();
        } else {
            checkIfUsernameIsAvailable( username );
        }
    }

    private void checkIfUsernameIsAvailable( final String username ) {
        // Path to the username child
        String path = "usernames/";

        final DatabaseReference childRef = mRootRef.child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                DataSnapshot thisUsernameDataSnapshot = dataSnapshot.child( username );
                if ( thisUsernameDataSnapshot.exists() ) {
                    // If the username exists, display an error message
                    Toast.makeText( SetUsernameActivity.this, "Username unavailable", Toast.LENGTH_SHORT ).show();
                } else {
                    // If not, set the username
                    String userId = mAuth.getCurrentUser().getUid();
                    childRef.child( username ).setValue( userId );

                    // Set the username on the local user object
                    User currentUser = User.getInstance();
                    currentUser.setUsername( username );

                    // Go to the home screen
                    Intent intent = new Intent( SetUsernameActivity.this, HomeScreenActivity.class );
                    startActivity( intent );
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }


}
