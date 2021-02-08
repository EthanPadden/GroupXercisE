package com.nova.groupxercise.Activities;

import org.joda.time.DateTime;

public class ExerciseActivity implements Comparable<ExerciseActivity>{
    private String mExerciseName;
    private DateTime mTime;
    private float mLevel;

    public ExerciseActivity( String mExerciseName, DateTime mTime, float mLevel ) {
        this.mExerciseName = mExerciseName;
        this.mTime = mTime;
        this.mLevel = mLevel;
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
