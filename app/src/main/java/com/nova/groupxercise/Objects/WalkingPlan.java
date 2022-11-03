package com.nova.groupxercise.Objects;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.DBObjects.WalkingPlanGoalDBObject;

import java.util.ArrayList;

public class WalkingPlan {
    private int mStartingPoint;
    private int mIncrement;
    private int mGoal;
    private String mWalkingPlanName;
    private long mStartTime;
    private int mProgress;
    private int mTodaysStepGoal;

    public WalkingPlan( String mWalkingPlanName ) {
        setmWalkingPlanName( mWalkingPlanName );
    }

    public void retrieveWalkingPlanDetails( final DBListener listener ) {
        final String path = "exercise_list/Cardio/Walking/" + mWalkingPlanName;
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                // TODO: Check theres no mix up between starting point, goal and increment - check does the debugger match the DB
                Object startingPointObj = dataSnapshot.child( "starting_point" ).getValue();
                int startingPoint;
                if (dataSnapshot.exists()) {
                    if (startingPointObj instanceof Long ){
                        startingPoint = ( ( Long ) startingPointObj ).intValue();
                    } else {
                        startingPoint = ( ( Integer ) startingPointObj ).intValue();
                    }
                    setmStartingPoint( startingPoint );
                }

                Object incrementObj = dataSnapshot.child( "increment" ).getValue();
                int increment;
                if (dataSnapshot.exists()) {
                    if (incrementObj instanceof Long ){
                        increment = ( ( Long ) incrementObj ).intValue();
                    } else {
                        increment = ( ( Integer ) incrementObj ).intValue();
                    }
                    setmIncrement( increment );
                }

                Object goalObj = dataSnapshot.child( "goal" ).getValue();
                int goal;
                if (dataSnapshot.exists()) {
                    if (goalObj instanceof Long ){
                        goal = ( ( Long ) goalObj ).intValue();
                    } else {
                        goal = ( ( Integer ) goalObj ).intValue();
                    }
                    setmGoal( goal );
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );
    }


    public void saveWalkingPlanAsPersonalGoal( final DBListener listener ){
        // Hitting the save button WILL RESET THE USER'S PROGRESS towards it
        // Need to disregard any activities before the walking plan was set
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String path = "personal_goals/" + userId;
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                WalkingPlanGoalDBObject walkingPlanGoalDBObject = new WalkingPlanGoalDBObject( mWalkingPlanName, mStartingPoint );
                childRef.child( "Walking" ).setValue( walkingPlanGoalDBObject );
                listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public static void retrievePersonalWalkingPlanGoal( final DBListener listener ) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String path = "personal_goals/" + userId + "/Walking";
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                // Get plan name
                String planName = dataSnapshot.child( "plan" ).getValue().toString();

                // Get progress
                Integer progress = null;
                Object progressObj = dataSnapshot.child( "progress" ).getValue();

                if (dataSnapshot.exists()){
                    if ( progressObj instanceof Long ) {
                        progress = ( ( Long ) progressObj ).intValue();
                    } else {
                        progress = ( ( Integer ) progressObj ).intValue();
                    }
                }

                // Get start time
                Object startTimeObj = dataSnapshot.child( "start_time" ).getValue();
                Long startTime = ( ( Long ) startTimeObj ).longValue();

                // Get todays step goal
                Integer todaysStepGoal = null;
                Object todaysStepGoalObj = dataSnapshot.child( "todays_step_goal" ).getValue();

                if (dataSnapshot.exists()){
                    if ( todaysStepGoalObj instanceof Long ) {
                        todaysStepGoal = ( ( Long ) todaysStepGoalObj ).intValue();
                    } else {
                        todaysStepGoal = ( ( Integer ) todaysStepGoalObj ).intValue();
                    }
                }

                WalkingPlan walkingPlan = new WalkingPlan( planName );
                walkingPlan.setmProgress( progress );
                walkingPlan.setmStartTime( startTime );
                walkingPlan.setmTodaysStepGoal( todaysStepGoal );

                listener.onRetrievalFinished( walkingPlan );
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );
    }
    public static void retrieveWalkingPlanList( final ArrayList<String> walkingPlanNames, final DBListener listener){
        String path = "exercise_list/Cardio/Walking";
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot walkingPlanDataShot : dataSnapshot.getChildren() ) {
                    // Add the exercise names to the list
                    String walkingPlanName = walkingPlanDataShot.getKey();
                    walkingPlanNames.add( walkingPlanName );
                }
                if(listener != null && listener.isActive()) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public int getmStartingPoint() {
        return mStartingPoint;
    }

    public void setmStartingPoint( int mStartingPoint ) {
        this.mStartingPoint = mStartingPoint;
    }

    public int getmIncrement() {
        return mIncrement;
    }

    public void setmIncrement( int mIncrement ) {
        this.mIncrement = mIncrement;
    }

    public int getmGoal() {
        return mGoal;
    }

    public void setmGoal( int mGoal ) {
        this.mGoal = mGoal;
    }

    public String getmWalkingPlanName() {
        return mWalkingPlanName;
    }

    public void setmWalkingPlanName( String mWalkingPlanName ) {
        this.mWalkingPlanName = mWalkingPlanName;
    }

    public long getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime( long mStartTime ) {
        this.mStartTime = mStartTime;
    }

    public int getmProgress() {
        return mProgress;
    }

    public void setmProgress( int mProgress ) {
        this.mProgress = mProgress;
    }

    public int getmTodaysStepGoal() {
        return mTodaysStepGoal;
    }

    public void setmTodaysStepGoal( int mTodaysStepGoal ) {
        this.mTodaysStepGoal = mTodaysStepGoal;
    }
}
