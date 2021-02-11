package com.nova.groupxercise.DBObjects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GoalDBObject {
    public float current_status;
    public float target;

    public GoalDBObject() {
    }

    public GoalDBObject( float current_status, float target ) {
        this.current_status = current_status;
        this.target = target;
    }
}