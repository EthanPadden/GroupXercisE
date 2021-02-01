package com.nova.groupxercise;

public class Goal {
    private String mExerciseName;
    private float mCurrentStatus;
    private float mTarget;

    public Goal( String mExerciseName ) {
        setmExerciseName( mExerciseName );
    }

    public Goal( String mExerciseName, float mCurrentStatus, float mTarget ) {
        this( mExerciseName );
        setmCurrentStatus( mCurrentStatus );
        setmTarget( mTarget );
    }

    @Override
    public String toString() {
        return String.format( "%s\nCurrent: %s\nTarget: %s", mExerciseName,mCurrentStatus,mTarget );
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
}