package com.nova.groupxercise;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetailsDBObject {
    public long dob;
    public float weight;
    public String sex;

    public UserDetailsDBObject() {
    }

    public UserDetailsDBObject( long dob, float weight, String sex ) {
        this.dob = dob;
        this.weight = weight;
        this.sex = sex;
    }
}
