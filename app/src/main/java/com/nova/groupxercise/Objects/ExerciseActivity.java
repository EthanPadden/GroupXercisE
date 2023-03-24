package com.nova.groupxercise.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ExerciseActivity implements Comparable<ExerciseActivity>{
    private String mExerciseName;
    private DateTime mTime;
    private float mLevel;

    public ExerciseActivity( String mExerciseName, DateTime mTime, float mLevel ) {
        this.mExerciseName = mExerciseName;
        this.mTime = mTime;
        this.mLevel = mLevel;
    }

    public static void retrieveActivities( final ArrayList< ExerciseActivity> activities,final DBListener listener) {
        // Path to the users goals
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "activities/" + userId;

        // Get the DB reference
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Get the exercise name
                    String exerciseName = exerciseDataSnapshot.getKey();

                    for ( DataSnapshot activityDataSnapshot : exerciseDataSnapshot.getChildren() ) {
                        String timestampStr = activityDataSnapshot.getKey();
                        long timestamp = Long.parseLong( timestampStr );
                        DateTime time = new DateTime( timestamp );

                        Object levelObj = activityDataSnapshot.getValue();
                        float level;
                        if ( levelObj instanceof Long ) {
                            level = ( ( Long ) levelObj ).floatValue();
                        } else {
                            level = ( ( Float ) levelObj ).floatValue();
                        }


                        ExerciseActivity activity = new ExerciseActivity( exerciseName, time, level );
                        activities.add( activity );
                    }
                }
                if(listener != null && listener.isActive()) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public static void retrieveExerciseList(final ArrayList<String> exerciseNames, final DBListener listener){
        String category = "Strength";
        String path = "exercise_list/" + category;
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Add the exercise names to the list
                    String exerciseName = exerciseDataSnapshot.getKey();
                    exerciseNames.add( exerciseName );
                }
                if(listener != null && listener.isActive()) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    @Override
    public String toString() {
        return String.format( "%s\n%s\n%f", mExerciseName, mTime, mLevel );
    }

    @Override
    public int compareTo( ExerciseActivity activity ) {
        return activity.getmTime().compareTo( this.mTime );
    }

    public String getmExerciseName() {
        return mExerciseName;
    }

    public void setmExerciseName( String mExerciseName ) {
        this.mExerciseName = mExerciseName;
    }

    public DateTime getmTime() {
        return mTime;
    }

    public void setmTime( DateTime mTime ) {
        this.mTime = mTime;
    }

    public float getmLevel() {
        return mLevel;
    }

    public void setmLevel( float mLevel ) {
        this.mLevel = mLevel;
    }
}
