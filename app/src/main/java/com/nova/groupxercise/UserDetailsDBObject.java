package com.nova.groupxercise;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetailsDBObject {
    public String name;
    public long dob;
    public float weight;
    public String sex;

    public UserDetailsDBObject() {
    }

    public UserDetailsDBObject( String name, long dob, float weight, String sex ) {
        this.name = name;
        this.dob = dob;
        this.weight = weight;
        this.sex = sex;
    }
}
