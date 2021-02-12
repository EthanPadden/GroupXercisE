package com.nova.groupxercise.Objects;

public abstract class DBListener {
    private boolean active;
    void onRetrievalFinished(Object retrievedData){};
    void onRetrievalFinished(){};

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