package com.nova.groupxercise.Objects;

import java.util.ArrayList;

public class Member {
    private String mUsername;
    private ArrayList< Goal > mProgress;

    public Member( String mUsername ) {
        setmUsername( mUsername );
        mProgress = new ArrayList<>();
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername( String mUsername ) {
        this.mUsername = mUsername;
    }

    public ArrayList< Goal > getmProgress() {
        return mProgress;
    }

    public void setmProgress( ArrayList< Goal > mProgress ) {
        this.mProgress = mProgress;
    }
}
