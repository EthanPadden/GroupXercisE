package com.nova.groupxercise.Objects;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WalkingPlan {
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
}
