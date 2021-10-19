package com.nova.groupxercise.Objects;

import java.util.ArrayList;

public class MemberProgress {
    private String username;
    private ArrayList<Goal> memberProgresses;

    public MemberProgress( String username ) {
        this.username = username;
        memberProgresses = new ArrayList<>(  );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public ArrayList< Goal > getMemberProgresses() {
        return memberProgresses;
    }
}
