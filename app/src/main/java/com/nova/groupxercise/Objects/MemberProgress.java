package com.nova.groupxercise.Objects;

public class MemberProgress {
    private String username;
    private float currentStatus;

    public MemberProgress( String username, float currentStatus ) {
        this.username = username;
        this.currentStatus = currentStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public float getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus( float currentStatus ) {
        this.currentStatus = currentStatus;
    }
}
