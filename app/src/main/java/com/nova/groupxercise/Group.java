package com.nova.groupxercise;

public class Group {
    private String mGroupName;
    private String mGroupId;

    public Group( String mGroupName, String mGroupId ) {
        this.mGroupName = mGroupName;
        this.mGroupId = mGroupId;
    }

    public String getmGroupName() {
        return mGroupName;
    }

    public void setmGroupName( String mGroupName ) {
        this.mGroupName = mGroupName;
    }

    public String getmGroupId() {
        return mGroupId;
    }

    public void setmGroupId( String mGroupId ) {
        this.mGroupId = mGroupId;
    }
}
