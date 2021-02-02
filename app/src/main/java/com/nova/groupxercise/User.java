package com.nova.groupxercise;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class User {
    // Instance variables
    private String name;
    private DateTime dob;
    private float weight;

    public enum Sex {MALE, FEMALE}

    private Sex sex;
    private UserDetailsDBObject mUserDetailsDBObject;

    // Singleton class
    private static final User user = new User();

    // Constructors
    public User() {

    }

    public static User getInstance() {
        return user;
    }

    // Override methods
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern( "MM/dd/yyyy HH:mm:ss" );

        return String.format(
                "Name: " + name
                        + "\nDOB: " + dtf.print( dob )
                        + "\nWeight: " + weight
                        + "\nSex: " + sex
        );
    }

    // Other methods

    /**
     * Verifies user details are in the range expected
     *
     * @return true if details are valid
     */
    public boolean detailsAreValid() {
        boolean validDetails = true;

        // Validate name
        if ( name == null || name.compareTo( "" ) == 0 ) validDetails = false;

        // Validate DOB
        Period period = new Period( dob, DateTime.now() );
        int age = period.getYears();
        if ( age < 14 || age > 89 ) validDetails = false;

        // If male, weight should be in range 50-140
        if ( sex == Sex.MALE && ( weight < 50 || weight > 140 ) )
            validDetails = false;
            // If female, weight should be in range 40-120
        else if ( sex == Sex.FEMALE && ( weight < 40 || weight > 120 ) )
            validDetails = false;

        return validDetails;
    }

    public void setUserDetails(String name, DateTime dob, float weight, Sex sex){
        Instant dobInstant = dob.toInstant();
        long dobTimeStamp = dobInstant.getMillis();
        setName( name );
        setDob( dob );
        setWeight( weight );
        setSex( sex );
        mUserDetailsDBObject = new UserDetailsDBObject(
                name,
                dobTimeStamp,
                weight,
                sex.toString()
        );
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

    public UserDetailsDBObject getmUserDetailsDBObject() {
        return mUserDetailsDBObject;
    }

    public void setmUserDetailsDBObject( UserDetailsDBObject mUserDetailsDBObject ) {
        this.mUserDetailsDBObject = mUserDetailsDBObject;
    }
}
