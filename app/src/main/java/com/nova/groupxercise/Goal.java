package com.nova.groupxercise;

public class Goal {
    private String mExerciseName;
    private int[] mStandards;
    private String mLevel;
    private int mSets;
    private int mReps;
    private double mWeight;

    public Goal( String mExerciseName ) {
        setmExerciseName( mExerciseName );
    }

    @Override
    public String toString() {
        String info = mExerciseName;
        for ( int standard : mStandards ) info += "\n" + standard;
        return info;

    }

    public String getmExerciseName() {
        return mExerciseName;
    }

    public void setmExerciseName( String mExerciseName ) {
        this.mExerciseName = mExerciseName;
    }

    public int[] getmStandards() {
        return mStandards;
    }

    public void setmStandards( int[] mStandards ) {
        this.mStandards = mStandards;
    }

}