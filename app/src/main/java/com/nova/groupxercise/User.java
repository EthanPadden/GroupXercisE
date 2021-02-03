package com.nova.groupxercise;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private FirebaseAuth mAuth;


    public enum Sex {MALE, FEMALE}

    private Sex sex;
    private UserDetailsDBObject mUserDetailsDBObject;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private boolean userDetailsAreSet = false;

    // Singleton class
    private static final User user = new User();

    // Constructors
    public User() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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

    public void retreiveUserDetails() {

            // Path to the user details
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String path = "user_details/" + firebaseUser.getUid();

            // Get the DB reference
            DatabaseReference childRef = mRootRef.child( path );

            childRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    if(dataSnapshot.exists()) {
                        // There are user details in the DB for this user
                        String dbName = null;
                        DateTime dbDob = null;
                        float dbWeight = -1f;
                        Sex dbSex = null;

                        // Get the user name
                        DataSnapshot nameDataSnapshot = dataSnapshot.child( "name" );
                        if ( nameDataSnapshot.exists() ) {
                            dbName = nameDataSnapshot.getValue().toString();
                        }

                        // Get the user dob
                        DataSnapshot dobDataSnapshot = dataSnapshot.child( "dob" );
                        long dbDobTimestamp = -1;
                        if ( dobDataSnapshot.exists() ) {
                            dbDobTimestamp = (( Long ) dobDataSnapshot.getValue()).longValue();
                            dbDob = new DateTime( dbDobTimestamp );
                        }

                        // Get the user weight
                        DataSnapshot weightDataSnapshot = dataSnapshot.child( "weight" );
                        if ( weightDataSnapshot.exists() ) {
                            dbWeight = ( (Long ) weightDataSnapshot.getValue()).floatValue();
                        }

                        // Get the user sex
                        DataSnapshot sexDataSnapshot = dataSnapshot.child( "sex" );
                        if ( sexDataSnapshot.exists() ) {
                            String dbSexStr = sexDataSnapshot.getValue().toString();
                            for(Sex sex : Sex.values()) {
                                if(dbSexStr.compareTo( sex.toString() ) == 0) dbSex = sex;
                            }
                        }

                        if(dbName != null && dbDob != null && dbDobTimestamp != -1 && dbWeight != -1 && dbSex != null) {
                            userDetailsAreSet = true;
                            setName( dbName );
                            setDob( dbDob );
                            setWeight( dbWeight );
                            setSex( dbSex );
                            mUserDetailsDBObject = new UserDetailsDBObject(
                                    dbName,
                                    dbDobTimestamp,
                                    dbWeight,
                                    dbSex.toString()
                            );
                        }

                    }

                    // Otherwise, do nothing

                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );


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

    public DatabaseReference getmRootRef() {
        return mRootRef;
    }

    public void setmRootRef( DatabaseReference mRootRef ) {
        this.mRootRef = mRootRef;
    }

    public boolean isUserDetailsAreSet() {
        return userDetailsAreSet;
    }

    public void setUserDetailsAreSet( boolean userDetailsAreSet ) {
        this.userDetailsAreSet = userDetailsAreSet;
    }
}
