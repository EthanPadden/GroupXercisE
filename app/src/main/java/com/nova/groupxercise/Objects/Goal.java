package com.nova.groupxercise.Objects;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.DBObjects.GoalDBObject;

public class Goal {
    private String mExerciseName;
    private float mCurrentStatus;
    private float mTarget;
    private GoalDBObject mGoalDBObject;

    public Goal( String mExerciseName ) {
        setmExerciseName( mExerciseName );
    }

    public Goal( String mExerciseName, float mCurrentStatus, float mTarget ) {
        this( mExerciseName );
        setmCurrentStatus( mCurrentStatus );
        setmTarget( mTarget );
        setmGoalDBObject( new GoalDBObject( mCurrentStatus, mTarget ) );
    }

    public void matchUserProgressToGroup( String userId, User user, final Group group) {
        String personalGoalProgressPath = "user_goals/" + userId + "/" + mExerciseName + "/current_status";
        final String groupGoalProgressPath = "groups/" + group.getmGroupId() + "/members/" + user.getUsername() + "/progress/" + mExerciseName;

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference personalGoalProgressRef = rootRef.child( personalGoalProgressPath );
        final DatabaseReference groupGoalProgressRef = rootRef.child( groupGoalProgressPath );

        personalGoalProgressRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull final DataSnapshot personalGoalDataSnapshot ) {
                groupGoalProgressRef.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot groupGoalProgressDataSnapshot ) {
                        if(personalGoalDataSnapshot.exists() && groupGoalProgressDataSnapshot.exists()) {
                            Object personalProgressObj = personalGoalDataSnapshot.getValue();
                            Object groupGoalProgressObj = groupGoalProgressDataSnapshot.getValue();
                            float personalProgress;
                            float groupGoalProgress;

                            if ( personalProgressObj instanceof Long ) {
                                personalProgress = ( ( Long ) personalProgressObj ).floatValue();
                            } else {
                                personalProgress = ( ( Float ) personalProgressObj ).floatValue();
                            }

                            if ( groupGoalProgressObj instanceof Long ) {
                                groupGoalProgress = ( ( Long ) groupGoalProgressObj ).floatValue();
                            } else {
                                groupGoalProgress = ( ( Float ) groupGoalProgressObj ).floatValue();
                            }

                            // Compare values
                            if(personalProgress > groupGoalProgress) {
                                groupGoalProgressRef.setValue( personalProgress );
                            }

                        } else if(personalGoalDataSnapshot.exists()) {
                            Object personalProgressObj = personalGoalDataSnapshot.getValue();

                            float personalProgress;
                            if ( personalProgressObj instanceof Long ) {
                                personalProgress = ( ( Long ) personalProgressObj ).floatValue();
                            } else {
                                personalProgress = ( ( Float ) personalProgressObj ).floatValue();
                            }
                            groupGoalProgressRef.setValue( personalProgress );


                        }else {
                            groupGoalProgressRef.setValue( 0.0f );
                        }
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError databaseError ) {

                    }
                } );

            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );
    }

    public void matchGroupProgressToUser( String userId, User user, final Group group) {
        String personalGoalProgressPath = "user_goals/" + userId + "/" + mExerciseName + "/current_status";
        final String groupGoalProgressPath = "groups/" + group.getmGroupId() + "/members/" + user.getUsername() + "/progress/" + mExerciseName;

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference personalGoalProgressRef = rootRef.child( personalGoalProgressPath );
        final DatabaseReference groupGoalProgressRef = rootRef.child( groupGoalProgressPath );

        personalGoalProgressRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull final DataSnapshot personalGoalDataSnapshot ) {
                groupGoalProgressRef.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot groupGoalProgressDataSnapshot ) {
                        if(personalGoalDataSnapshot.exists() && groupGoalProgressDataSnapshot.exists()) {
                            Object personalProgressObj = personalGoalDataSnapshot.getValue();
                            Object groupGoalProgressObj = groupGoalProgressDataSnapshot.getValue();
                            float personalProgress;
                            float groupGoalProgress;

                            if ( personalProgressObj instanceof Long ) {
                                personalProgress = ( ( Long ) personalProgressObj ).floatValue();
                            } else {
                                personalProgress = ( ( Float ) personalProgressObj ).floatValue();
                            }

                            if ( groupGoalProgressObj instanceof Long ) {
                                groupGoalProgress = ( ( Long ) groupGoalProgressObj ).floatValue();
                            } else {
                                groupGoalProgress = ( ( Float ) groupGoalProgressObj ).floatValue();
                            }

                            // Compare values
                            if(groupGoalProgress > personalProgress) {
                                personalGoalProgressRef.setValue( groupGoalProgress );
                            }

                        } else  {
                            personalGoalProgressRef.setValue( 0.0f );
                        }
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError databaseError ) {

                    }
                } );

            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );
    }
    @Override
    public String toString() {
        return String.format( "%s\nCurrent: %s\nTarget: %s", mExerciseName, mCurrentStatus, mTarget );
    }

    public void setmExerciseName( String mExerciseName ) {
        this.mExerciseName = mExerciseName;
    }

    public String getmExerciseName() {
        return mExerciseName;
    }

    public float getmCurrentStatus() {
        return mCurrentStatus;
    }

    public void setmCurrentStatus( float mCurrentStatus ) {
        this.mCurrentStatus = mCurrentStatus;
    }

    public float getmTarget() {
        return mTarget;
    }

    public void setmTarget( float mTarget ) {
        this.mTarget = mTarget;
    }

    public GoalDBObject getmGoalDBObject() {
        return mGoalDBObject;
    }

    public void setmGoalDBObject( GoalDBObject mGoalDBObject ) {
        this.mGoalDBObject = mGoalDBObject;
    }
}