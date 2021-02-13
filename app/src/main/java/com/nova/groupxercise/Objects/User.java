package com.nova.groupxercise.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class User {
    // Instance variables
    private String username = null;
    private DateTime dob = null;
    private float weight = -1f;

    public enum Sex {MALE, FEMALE}

    private Sex sex;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private boolean userDetailsAreSet = false;

    // Singleton class
    private static final User user = new User();

    private FirebaseAuth mAuth;

    // Constructors
    public User() {
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
                "Username: " + username
                        + "DOB: " + dtf.print( dob )
                        + "\nWeight: " + weight
                        + "\nSex: " + sex
        );
    }

    // Other methods

    /**
     * Verifies user details are in the range expected (excluding username)
     *
     * @return true if details are valid
     */
    public boolean detailsAreValid() {
        // Validate DOB
        // TODO: Age not used yet
//        Period period = new Period( dob, DateTime.now() );
//        int age = period.getYears();
//        if ( age < 14 || age > 89 ) validDetails = false;

        if ( sex == null ) return false;
        // If male, weight should be in range 50-140
        if ( sex == Sex.MALE && ( weight < 50 || weight > 140 ) )
            return false;
            // If female, weight should be in range 40-120
        else return sex != Sex.FEMALE || ( !( weight < 40 ) && !( weight > 120 ) );

    }

    /**
     * Retrieves the username for the user currently logged in from the DB
     * If there is no username saved in the DB, it does nothing
     * If there is, it sets the value of the object username to match the username in the DB
     */
    public void retrieveUsername() {
        // Path to the username child
        String path = "usernames/";

        final DatabaseReference childRef = mRootRef.child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usernameDataSnapshot : dataSnapshot.getChildren() ) {
                    String dbUserId = usernameDataSnapshot.getValue().toString();
                    String thisUserId = FirebaseAuth.getInstance().getUid();
                    if ( dbUserId.compareTo( thisUserId ) == 0 ) {
                        String usernameFound = usernameDataSnapshot.getKey();
                        setUsername( usernameFound );
                    }
                }

                // If no username is found, this will do nothing
                // Checking if getUsername() is null is the check whether the username was found for the user
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );

    }

    /**
     * Sign out the user that is currently logged in using Firebase method
     */
    public void signOutUser() {
        // Check if there is a user currently logged in
        if ( mAuth.getCurrentUser() != null ) {
            // Reset the local user instance
            setUserDetailsAreSet( false );
            setUsername( null );
            mAuth.signOut();
        }
    }

    public static void checkIfUserExists(String username, final DBListener listener) {
        // Path to the username child
        String path = "usernames/" + username;

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    String userId = dataSnapshot.getValue().toString();
                    if ( listener != null && listener.isActive() ) listener.onRetrievalFinished(userId);
                } else {
                    if ( listener != null && listener.isActive() ) listener.onRetrievalFinished(null);
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public static boolean checkIfUsernameIsValid( String username ) {
        return username != null && username.compareTo( "" ) != 0;
    }

    /**
     * Retrieves the user details from the DB
     * If there are no details saved in the DB, it does nothing
     * If there are, it sets the values of the object to match those in the DB
     */
    public void retreiveUserDetails() {
        // Path to the user details
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String path = "user_details/" + firebaseUser.getUid();

        // Get the DB reference
        DatabaseReference childRef = mRootRef.child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    // There are user details in the DB for this user
                    DateTime dbDob = null;
                    float dbWeight = -1f;
                    Sex dbSex = null;

                    // Get the user dob
                    DataSnapshot dobDataSnapshot = dataSnapshot.child( "dob" );
                    long dbDobTimestamp = -1;
                    if ( dobDataSnapshot.exists() ) {
                        dbDobTimestamp = ( ( Long ) dobDataSnapshot.getValue() ).longValue();
                        dbDob = new DateTime( dbDobTimestamp );
                    }

                    // Get the user weight
                    DataSnapshot weightDataSnapshot = dataSnapshot.child( "weight" );
                    if ( weightDataSnapshot.exists() ) {
                        dbWeight = ( ( Long ) weightDataSnapshot.getValue() ).floatValue();
                    }

                    // Get the user sex
                    DataSnapshot sexDataSnapshot = dataSnapshot.child( "sex" );
                    if ( sexDataSnapshot.exists() ) {
                        String dbSexStr = sexDataSnapshot.getValue().toString();
                        for ( Sex sex : Sex.values() ) {
                            if ( dbSexStr.compareTo( sex.toString() ) == 0 ) dbSex = sex;
                        }
                    }

                    if ( dbDob != null && dbDobTimestamp != -1 && dbWeight != -1 && dbSex != null ) {
                        userDetailsAreSet = true;
                        setDob( dbDob );
                        setWeight( dbWeight );
                        setSex( dbSex );
                    }
                } else {
                    user.setUserDetailsAreSet( false );
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );


    }

    // Accessor/Mutator methods


    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
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

    public boolean isUserDetailsAreSet() {
        return userDetailsAreSet;
    }

    public void setUserDetailsAreSet( boolean userDetailsAreSet ) {
        this.userDetailsAreSet = userDetailsAreSet;
    }
}
