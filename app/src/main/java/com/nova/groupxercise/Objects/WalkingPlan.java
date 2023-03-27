package com.nova.groupxercise.Objects;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.DBObjects.WalkingPlanGoalDBObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;

public class WalkingPlan {
    private int mStartingPoint;
    private int mIncrement;
    private int mGoal;
    private String mWalkingPlanName;
    private long mStartTime;
    private int mProgress;
    private int mTodaysStepGoal;
    private long mLastTimeStepGoalWasReset;
    private long mLastWalkTime;

    public WalkingPlan( String mWalkingPlanName ) {
        setmWalkingPlanName( mWalkingPlanName );
    }

    public void retrieveWalkingPlanDetails( final DBListener listener ) {
        final String path = "exercise_list/Cardio/Walking/" + mWalkingPlanName;
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
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
                WalkingPlanGoalDBObject walkingPlanGoalDBObject = new WalkingPlanGoalDBObject( mWalkingPlanName, mStartingPoint, mIncrement );
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

        /**
         * Prob = last walk time went to 0 - FIXED??, last time step goal reset updated for no reason?, progress went to 0
         * With act logged ^^
         * */

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                if (dataSnapshot.exists()) {
                    // Get plan name
                    String planName = dataSnapshot.child( "plan" ).getValue().toString();

                    // Get progress
                    Integer progress = null;
                    Object progressObj = dataSnapshot.child( "progress" ).getValue();

                    if ( dataSnapshot.exists() ) {
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

                    if ( dataSnapshot.exists() ) {
                        if ( todaysStepGoalObj instanceof Long ) {
                            todaysStepGoal = ( ( Long ) todaysStepGoalObj ).intValue();
                        } else {
                            todaysStepGoal = ( ( Integer ) todaysStepGoalObj ).intValue();
                        }
                    }

                    // Get last time step goal was reset
                    Object lastTimeGoalWasResetObj = dataSnapshot.child( "last_time_step_goal_reset" ).getValue();
                    long lastTimeGoalWasReset = ( ( Long ) lastTimeGoalWasResetObj ).longValue();

                    // Get last walk time so that it is not overwritten
                    Object lastWalkTimeObj = dataSnapshot.child( "last_walk_time" ).getValue();
                    long lastWalkTime = ( ( Long ) lastWalkTimeObj ).longValue();

                    // Get increment
                    Integer increment = null;
                    Object incrementObj = dataSnapshot.child( "increment" ).getValue();

                    if ( dataSnapshot.exists() ) {
                        if ( incrementObj instanceof Long ) {
                            increment = ( ( Long ) incrementObj ).intValue();
                        } else {
                            increment = ( ( Integer ) incrementObj ).intValue();
                        }
                    }

                    // Create an object to be stored locally
                    WalkingPlan walkingPlan = new WalkingPlan( planName );
                    walkingPlan.setmProgress( progress );
                    walkingPlan.setmStartTime( startTime );
                    walkingPlan.setmTodaysStepGoal( todaysStepGoal );
                    walkingPlan.setmLastTimeStepGoalWasReset( lastTimeGoalWasReset );
                    walkingPlan.setmLastWalkTime( lastWalkTime );
                    walkingPlan.setmIncrement( increment );

                    // Create a DB object to replace the one in the database
                    WalkingPlanGoalDBObject walkingPlanGoalDBObject = new WalkingPlanGoalDBObject(
                            walkingPlan.mWalkingPlanName,
                            walkingPlan.getmTodaysStepGoal(),
                            walkingPlan.getmIncrement()
                    );
                    // These will not be changed here
                    walkingPlanGoalDBObject.last_walk_time = lastWalkTime;
                    walkingPlanGoalDBObject.start_time = startTime;
                    walkingPlanGoalDBObject.last_time_step_goal_reset = lastTimeGoalWasReset;
                    walkingPlanGoalDBObject.progress = progress;

                    /** RESET WALKING PLAN */
                    long todayTS = DateTime.now().getMillis();
                    if ( walkingPlan.getmLastTimeStepGoalWasReset() == 0) {
                        // Set it as today - basically the first day of the plan
                        walkingPlan.setmLastTimeStepGoalWasReset( todayTS );
                        walkingPlanGoalDBObject.last_time_step_goal_reset = todayTS;
                    } else {
                        // LocalDate represents a date without time
                        DateTime dayLastStepGoalResetDT = new DateTime(lastTimeGoalWasReset);
                        LocalDate dayLastStepGoalResetDate = dayLastStepGoalResetDT.toLocalDate();
                        DateTime todayDT = DateTime.now();
                        LocalDate todayDate = todayDT.toLocalDate();

                        if (!dayLastStepGoalResetDate.equals( todayDate )) {
                            /**    NEED TO GET INCREMENT FROM DB    */
                            // Reset the progress for a new day
                            walkingPlan.setmProgress( 0 );
                            walkingPlanGoalDBObject.progress = 0;

                            // Did we meet our step goal yesterday? - IGNORE IF THE WALKING PLAN IS CUSTOM STEPS
                            if (walkingPlan.getmWalkingPlanName().compareTo( "Custom" ) != 0) {
                                if ( walkingPlan.getmProgress() >= walkingPlan.getmTodaysStepGoal() ) {
                                    // Increase todays step goal by the increment
                                    int yesterdaysStepGoal = walkingPlan.getmTodaysStepGoal();
                                    int newTodayStepGoal = yesterdaysStepGoal + walkingPlan.getmIncrement();
                                    walkingPlan.setmTodaysStepGoal( newTodayStepGoal );
                                    walkingPlanGoalDBObject.todays_step_goal = newTodayStepGoal;
                                }
                            }

                            // Update the last time the step goal was reset
                            walkingPlan.setmLastTimeStepGoalWasReset( todayTS );
                            walkingPlanGoalDBObject.last_time_step_goal_reset = todayTS;
                        }
                        // Otherwise - do nothing
                    }

                    // Update database
                    childRef.setValue( walkingPlanGoalDBObject );
                    listener.onRetrievalFinished( walkingPlan );
                } else {
                    listener.onRetrievalFinished();
                }
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

    public long getmLastTimeStepGoalWasReset() {
        return mLastTimeStepGoalWasReset;
    }

    public void setmLastTimeStepGoalWasReset( long mLastTimeStepGoalWasReset ) {
        this.mLastTimeStepGoalWasReset = mLastTimeStepGoalWasReset;
    }

    public long getmLastWalkTime() {
        return mLastWalkTime;
    }

    public void setmLastWalkTime( long mLastWalkTime ) {
        this.mLastWalkTime = mLastWalkTime;
    }
}
