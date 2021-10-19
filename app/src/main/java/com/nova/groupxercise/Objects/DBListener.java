package com.nova.groupxercise.Objects;

public abstract class DBListener {
    private boolean active;
    public void onRetrievalFinished(Object retrievedData){};
    public void onRetrievalFinished(){};

    public DBListener(  ) {
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive( boolean active ) {
        this.active = active;
    }
}