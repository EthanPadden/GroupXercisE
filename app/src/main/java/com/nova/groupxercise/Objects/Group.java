package com.nova.groupxercise.Objects;

import java.util.ArrayList;

public class Group {
    private String mGroupName;
    private String mGroupId;
    private String mGroupCreator;
    private ArrayList<String> members;
    private ArrayList<Goal> goals;

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

    public ArrayList< String > getMembers() {
        return members;
    }

    public void setMembers( ArrayList< String > members ) {
        this.members = members;
    }

    public String getmGroupCreator() {
        return mGroupCreator;
    }

    public void setmGroupCreator( String mGroupCreator ) {
        this.mGroupCreator = mGroupCreator;
    }

    public ArrayList< Goal > getGoals() {
        return goals;
    }

    public void setGoals( ArrayList< Goal > goals ) {
        this.goals = goals;
    }
}
