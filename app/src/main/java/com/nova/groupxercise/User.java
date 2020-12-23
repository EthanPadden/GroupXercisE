package com.nova.groupxercise;

import java.util.Date;

public class User {
    // Instance variables
    private String email;
    private String name;
    private Date dob;
    private float weight;

    public enum Sex {MALE, FEMALE}

    private Sex sex;

    // Singleton class
    private static final User user = new User();

    // Constructors
    public User() {
    }

    // Accessor/Mutator methods
    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob( Date dob ) {
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
