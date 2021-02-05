package com.nova.groupxercise;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GroupDBObject {
    public String name;
    public String creator;

    public GroupDBObject() {
    }

    public GroupDBObject( String name, String creator ) {
        this.name = name;
        this.creator = creator;
    }
}