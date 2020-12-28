package com.nova.groupxercise;

public class StrengthGoal {
    private String mExerciseName;
    private String[] mStandards;

    public StrengthGoal( String mExerciseName ) {
        setmExerciseName( mExerciseName );
    }

    public String getmExerciseName() {
        return mExerciseName;
    }

    public void setmExerciseName( String mExerciseName ) {
        this.mExerciseName = mExerciseName;
    }

    public String[] getmStandards() {
        return mStandards;
    }

    public void setmStandards( String[] mStandards ) {
        this.mStandards = mStandards;
    }
}