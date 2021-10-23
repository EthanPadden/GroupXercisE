package com.nova.groupxercise.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Goal {
    private String mExerciseName;
    private float mTarget;

    public Goal( String mExerciseName ) {
        setmExerciseName( mExerciseName );
    }

    public Goal( String mExerciseName, float mTarget ) {
        this( mExerciseName );
        setmTarget( mTarget );
    }

    public static void retrievePersonalGoals( final DBListener listener ) {
        // Path to the users goals
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "personal_goals/" + userId;

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                // Create list of goals from subtree
                ArrayList<Goal> personalGoalsList = new ArrayList<Goal>();

                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Get the exercise name
                    String exerciseName = exerciseDataSnapshot.getKey();

                    // Get the target as a float value
                    Long target = (Long) exerciseDataSnapshot.getValue();

                    // Add the goal to the list
                    personalGoalsList.add( new Goal( exerciseName, target ) );
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished( personalGoalsList );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public static void retrieveStrengthStandards( final String exerciseName, final DBListener listener ) {

        User currentUser = User.getInstance();

        // Build the path and retrieve the strength standards
        int weightClass = getWeightClass( currentUser.getWeight() );
        String path = "strength_standards/" + exerciseName + "/" + currentUser.getSex().toString() + "/" + weightClass;

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                // Store the standards in memory so that they do not have to be retrieved again
                if ( listener != null && listener.isActive() )  listener.onRetrievalFinished( dataSnapshot );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Return the weight class based on the user details for retrieving strength standards
     *
     * @param weight the user weight
     * @return the weight class as listed in the DB
     */
    private static int getWeightClass( float weight ) {
        return ( int ) ( Math.floor( weight / 5 ) * 5 );
    }
    @Override
    public String toString() {
        return String.format( "%s\nCurrent: %s\nTarget: %s", mExerciseName, mTarget );
    }

    public void setmExerciseName( String mExerciseName ) {
        this.mExerciseName = mExerciseName;
    }

    public String getmExerciseName() {
        return mExerciseName;
    }

    public float getmTarget() {
        return mTarget;
    }

    public void setmTarget( float mTarget ) {
        this.mTarget = mTarget;
    }
}