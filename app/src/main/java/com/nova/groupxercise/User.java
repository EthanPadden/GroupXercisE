package com.nova.groupxercise;

import org.joda.time.DateTime;

import java.util.Calendar;

public class User {
    // Instance variables
    private String name;
    private DateTime dob;
    private float weight;

    public enum Sex {MALE, FEMALE}

    private Sex sex;

    // Singleton class
    private static final User user = new User();

    // Constructors
    private User() {
    }

    public static User getInstance() {
        return user;
    }

    // Accessor/Mutator methods
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public DateTime getDob() {
        return dob;
    }

    public void setDob( DateTime dob ) {
        this.dob = dob;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight( float weight ) {
        this.weight = weight;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex( Sex sex ) {
        this.sex = sex;
    }
}
