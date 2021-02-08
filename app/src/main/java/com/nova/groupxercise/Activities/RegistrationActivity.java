package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nova.groupxercise.DBObjects.GoalDBObject;
import com.nova.groupxercise.R;
import com.nova.groupxercise.Objects.User;

public class RegistrationActivity extends AppCompatActivity {
    private Button mRegistrationBtn;
    private Button mLoginBtn;
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set screen layout
        setContentView( R.layout.activity_registration );

        // Initialise components
        mRegistrationBtn = findViewById( R.id.btn_register );
        mLoginBtn = findViewById( R.id.btn_login_link );
        mEmailEt = findViewById( R.id.et_email );
        mPasswordEt = findViewById( R.id.et_password );

        // Set event listeners
        mRegistrationBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                String email = mEmailEt.getText().toString();
                String password = mPasswordEt.getText().toString();

                boolean fieldsAreValid = validateFields( email, password );
                if ( fieldsAreValid ) registerUser( email, password );
            }
        } );
        mLoginBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                Intent intent = new Intent( RegistrationActivity.this, LoginActivity.class );
                startActivity( intent );
            }
        } );
    }

    /**
     * Checks if the email and password strings are valid
     * Checks: not null, not empty, email is in the form x@y.z
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return true if both strings are valid
     */
    private boolean validateFields( String email, String password ) {
        boolean fieldsAreValid = false;

        if ( email == null || email.compareTo( "" ) == 0 ) {
            // If the email is null or empty
            Toast.makeText( RegistrationActivity.this, R.string.error_no_email_entered,
                    Toast.LENGTH_SHORT ).show();
        } else if ( password == null || password.compareTo( "" ) == 0 ) {
            // If the password is null or empty
            Toast.makeText( RegistrationActivity.this, R.string.error_no_psw_entered,
                    Toast.LENGTH_SHORT ).show();
        } else if ( !Patterns.EMAIL_ADDRESS.matcher( email ).matches() ) {
            // If the email is not in the form x@y.z
            Toast.makeText( RegistrationActivity.this, R.string.error_invalid_email, Toast.LENGTH_SHORT ).show();
        } else {
            fieldsAreValid = true;
        }
        return fieldsAreValid;
    }

    /**
     * Registers the user using Firebase method
     *
     * @param email    the email of the user
     * @param password the password of the user
     */
    private void registerUser( String email, String password ) {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener< AuthResult >() {
                    @Override
                    public void onComplete( @NonNull Task< AuthResult > task ) {
                        if ( task.isSuccessful() ) {
                            createEmptyDBGoalset( mAuth.getCurrentUser().getUid() );
                            User.getInstance().setUserDetailsAreSet( false );
                            Intent intent = new Intent( RegistrationActivity.this, SetUsernameActivity.class );
                            startActivity( intent );
                        } else {
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText( RegistrationActivity.this, errorMsg,
                                    Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
    }

    /**
     * Creates a child node in the DB to store the user goals
     *
     * @param userId the Firebase user ID
     */
    private void createEmptyDBGoalset( String userId ) {
        String path = "user_goals/";
        DatabaseReference childRef = mRootRef.child( path );
        childRef.child( userId ).child( getResources().getString( R.string.sample_goal_name ) ).setValue( new GoalDBObject( 20f, 50f ) );

    }
}
