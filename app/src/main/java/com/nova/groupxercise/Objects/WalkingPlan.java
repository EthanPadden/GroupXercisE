package com.nova.groupxercise.Objects;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WalkingPlan {
    private int mStartingPoint;
    private int mIncrement;
    private int mGoal;
    private String mWalkingPlanName;

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

    public void setWalkingPlan() {

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
}
