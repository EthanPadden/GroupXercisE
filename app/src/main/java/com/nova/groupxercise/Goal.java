package com.nova.groupxercise;

import com.google.firebase.database.IgnoreExtraProperties;

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