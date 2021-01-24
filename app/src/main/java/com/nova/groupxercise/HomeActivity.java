package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView mEmailText;
    private TextView mGoalText;
    private Button mLogoutBtn;
    private Button mEditDetailsBtn;
    private Button mCalculateGoalBtn;
    private Button mExerciseListBtn;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If it is the case that the current user is null, go to the login screen
        if ( mAuth.getCurrentUser() == null ) {
            // If the current user is null, go to the registration screen
            Intent intent = new Intent( HomeActivity.this, RegistrationActivity.class );
            startActivity( intent );
        } else {
            mCurrentUser = mAuth.getCurrentUser();
        }

        // Set the screen layout
        setContentView( R.layout.activity_home );

        // Initialise components
        mEmailText = findViewById( R.id.email_text );
        mGoalText = findViewById( R.id.text_goal_info );
        mLogoutBtn = findViewById( R.id.btn_logout );
        mEditDetailsBtn = findViewById( R.id.btn_edit_details_link );
        mCalculateGoalBtn = findViewById( R.id.btn_calculate_goal );
        mExerciseListBtn = findViewById( R.id.btn_exercise_list );

        // Set event listeners
        mLogoutBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                signOutUser();
            }
        } );
        mEditDetailsBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Go to edit details screen
                Intent intent = new Intent( HomeActivity.this, EditUserDetailsActivity.class );
                startActivity( intent );
            }
        } );
        mExerciseListBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Go to edit details screen
                Intent intent = new Intent( HomeActivity.this, ExerciseListActivity.class );
                startActivity( intent );
            }
        } );
        mCalculateGoalBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Temporary
                String exerciseName = "Bench Press";

                calculateStrengthGoal( exerciseName );
            }
        } );

        // Display the email of the current user
        mEmailText.setText( "User logged in: " + mCurrentUser.getEmail() );
    }

    /**
     * Sign out the user that is currently logged in using Firebase method
     * Toast with error message if no user is currently logged in
     */
    private void signOutUser() {
        // Check if there is a user currently logged in
        if ( mCurrentUser != null ) {
            mAuth.signOut();
        } else {
            Toast.makeText( HomeActivity.this, R.string.error_user_not_logged_in,
                    Toast.LENGTH_SHORT ).show();
        }

        // Regardless, go to login screen
        Intent intent = new Intent( HomeActivity.this, LoginActivity.class );
        startActivity( intent );
    }

    public void calculateStrengthGoal( String exerciseName) {
        User user = User.getInstance();
        mGoalText.setText( "Loading..." );
        // Check if all user details are set correctly
        User testUser = new User();

        if ( !user.detailsAreValid() ) {
            // TODO: replace with returning null with toast message
            testUser.setName( "John Doe" );
            testUser.setSex( User.Sex.MALE );
            testUser.setWeight( 68 );
            testUser.setDob( new DateTime( 1990, 9,1,0,0 ) );
        }

        int weightClass = getWeightClass( testUser.getWeight() );
        String path = "strength_standards/" + exerciseName + "/" + testUser.getSex().toString() + "/" + weightClass;
        DatabaseReference childRef = mRootRef.child( path );
        final Goal goal = new Goal( exerciseName );
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                int[] standards = new int[5];
                Long[] standardLongValues = new Long[5];
                standardLongValues[0] = (Long) dataSnapshot.child( "Beginner" ).getValue();
                standardLongValues[1] = (Long) dataSnapshot.child( "Novice" ).getValue();
                standardLongValues[2] = (Long) dataSnapshot.child( "Intermediate" ).getValue();
                standardLongValues[3] = (Long) dataSnapshot.child( "Advanced" ).getValue();
                standardLongValues[4] = (Long) dataSnapshot.child( "Elite" ).getValue();
                for( int i = 0; i < standardLongValues.length; i++ ) {
                    standards[i] = standardLongValues[i] == null ? null : Math.toIntExact(standardLongValues[i]);
                }

                goal.setmStandards( standards );
                mGoalText.setText( goal.toString() );
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {}
        });
    }

    private int getWeightClass(float weight) {
        return (int)(Math.floor( weight/5 )*5);
    }
}
