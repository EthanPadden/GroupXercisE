package com.nova.groupxercise;

public class GoalCalculator {
    private String mExerciseName;

    public GoalCalculator( String mExerciseName ) {
        setmExerciseName( mExerciseName );
    }

    public StrengthGoal calculateStrengthGoal() {
        return null;
    }

    public String getmExerciseName() {
        return mExerciseName;
    }

    public void setmExerciseName( String mExerciseName ) {
        this.mExerciseName = mExerciseName;
    }
}
