package com.nova.groupxercise.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.DBObjects.UserDetailsDBObject;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.Calendar;

public class EditUserDetailsActivity extends AppCompatActivity {
    private TextView mDobText;
    private EditText mWeightEt;
    private Spinner mSexSpinner;
    private FloatingActionButton mUpdateBtn;
    private User.Sex mSelectedSex;
    private Calendar mSelectedDob;
    private DrawerLayout mDrawerContainer;
    private NavigationView mDrawer;
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_user_details );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If it is the case that the current user is null, go to the login screen
        if ( mAuth.getCurrentUser() == null ) {
            // If the current user is null, go to the registration screen
            Intent intent = new Intent( EditUserDetailsActivity.this, RegistrationActivity.class );
            startActivity( intent );
        }

        // Initialise components
        mDobText = findViewById( R.id.text_dob );
        mWeightEt = findViewById( R.id.et_weight );
        mSexSpinner = findViewById( R.id.spinner_sex );
        mUpdateBtn = findViewById( R.id.btn_update );
        Toolbar toolbar = findViewById( R.id.toolbar );

        // Sets the Toolbar to act as the ActionBar for this ExerciseActivity window.
        toolbar.setTitleTextColor( Color.WHITE );
        setSupportActionBar( toolbar );

        // Initialise and set up navigation drawer
        mDrawerContainer = findViewById( R.id.drawer_container );
        mDrawer = findViewById( R.id.drawer );
        setupDrawerContent();

        // Set default selected values
        mSelectedSex = User.Sex.MALE;
        mSelectedDob = Calendar.getInstance();

        // Create an array adapter for the spinner component and apply
        ArrayAdapter< CharSequence > adapter = ArrayAdapter.createFromResource( this,
                R.array.sex_array, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mSexSpinner.setAdapter( adapter );

        // Set event listeners
        mUpdateBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                validateEnteredDetails();
            }
        } );
        mSexSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            public void onItemSelected( AdapterView< ? > parent, View view, int pos, long id ) {
                Object selectedOption = parent.getItemAtPosition( pos );
                updateSelectedSex( selectedOption );
            }

            public void onNothingSelected( AdapterView< ? > parent ) {
                updateSelectedSex( null );
            }
        } );
        mDobText.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                DialogFragment newFragment = new DatePickerFragment( mSelectedDob, mDobText );
                newFragment.show( getSupportFragmentManager(), "datePicker" );
            }
        } );

        // Set back button behaviour
        Toast detailsNotSavedToast = Toast.makeText( this, "Details not saved - press save button to save details!" , Toast.LENGTH_SHORT);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to Profile activity and show toast
                Intent intent = new Intent( EditUserDetailsActivity.this, ProfileActivity.class );
                detailsNotSavedToast.show();
                startActivity( intent );
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Sets the event listeners for the navigation drawer
     */
    private void setupDrawerContent() {
        mDrawer.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
                if ( item.getItemId() == R.id.drawer_home ) {
                    // Go to edit details screen
                    Intent intent = new Intent( EditUserDetailsActivity.this, HomeScreenActivity.class );
                    startActivity( intent );
                } else if ( item.getItemId() == R.id.drawer_logout ) {
                    // Sign out the user and navigate to hte Login activity
                    User.getInstance().signOutUser();
                    Intent intent = new Intent( EditUserDetailsActivity.this, LoginActivity.class );
                    startActivity( intent );
                }
                mDrawerContainer.closeDrawers();
                return true;
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.toolbar_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == R.id.toolbar_btn_open_drawer ) {
            mDrawerContainer.openDrawer( GravityCompat.START );
        }
        return super.onOptionsItemSelected( item );
    }


    /**
     * Updates the member variable mSelectedSex with the option in the parameters
     *
     * @param selectedOption The object(String) corresponding to the selected option
     */
    private void updateSelectedSex( Object selectedOption ) {
        String selectedOptionStr = selectedOption.toString();

        if ( selectedOptionStr.compareTo( getResources().getString( R.string.sex_male ) ) == 0 ) {
            mSelectedSex = User.Sex.MALE;
        } else if ( selectedOptionStr.compareTo( getResources().getString( R.string.sex_female ) ) == 0 ) {
            mSelectedSex = User.Sex.FEMALE;
        } else {
            Toast.makeText( EditUserDetailsActivity.this, R.string.error_invalid_user_details,
                    Toast.LENGTH_SHORT ).show();
            mSelectedSex = null;
        }
    }

    /**
     * Inner class for the DOB date picker fragment
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        // Instance variables
        private Calendar mSelectedDate;
        private TextView mDobText;

        // Constructors
        public DatePickerFragment( Calendar mSelectedDate, TextView mDobText ) {
            this.mSelectedDate = mSelectedDate;
            this.mDobText = mDobText;
        }

        // Override methods
        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState ) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get( Calendar.YEAR );
            int month = c.get( Calendar.MONTH );
            int day = c.get( Calendar.DAY_OF_MONTH );

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog( getActivity(), this, year, month, day );
        }

        /**
         * Method called when the user selects the date (hits OK button)
         *
         * @param view  The datepicker component
         * @param year  The selected year
         * @param month The selected month
         * @param day   The selected day
         */
        public void onDateSet( DatePicker view, int year, int month, int day ) {
            // Set the selected date value (months are 0 indexed in both Calendar and the datepicker element)
            mSelectedDate.set( year, month, day );

            // Update UI element
            mDobText.setText( String.format( "%d/%d/%d", day, month + 1, year ) );
        }
    }

    /**
     * Checks that the details entered by the user are valid
     * If so, calls method to save them to the DB
     */
    private void validateEnteredDetails() {
        boolean detailsCanBeSet = true;

        // Get DOB
        DateTime dob = new DateTime( mSelectedDob.getTime() );

        // Get weight
        String weightStr = mWeightEt.getText().toString();
        float weight = 50;
        if ( weightStr == null || weightStr.compareTo( "" ) == 0 ) detailsCanBeSet = false;
        else {
            weight = Float.parseFloat( mWeightEt.getText().toString() );
            if ( mSelectedSex == User.Sex.MALE && ( weight < 50 || weight > 140 ) )
                detailsCanBeSet = false;
                // If female, weight should be in range 40-120
            else if ( mSelectedSex == User.Sex.FEMALE && ( weight < 40 || weight > 120 ) )
                detailsCanBeSet = false;
        }

        if ( detailsCanBeSet ) {
            // To avoid a difference between the locally stored details
            // and the details stored in the DB, save to the DB first and update
            // the locally stored details if successful
            saveUserDetailsToDB( dob, weight );
        } else {
            Toast.makeText( EditUserDetailsActivity.this, R.string.error_invalid_user_details,
                    Toast.LENGTH_SHORT ).show();
        }


        // Go to profile screen
        Intent intent = new Intent( EditUserDetailsActivity.this, ProfileActivity.class );
        startActivity( intent );
    }

    /**
     * Saves the details in the arguments to the database
     * If no details are set for the user, it creates a set
     * If details exist, it updates them
     * If successful, it sets the local user details to be the values
     *
     * @param dob    date of birth of user
     * @param weight weight of user
     */
    private void saveUserDetailsToDB( final DateTime dob, final float weight ) {
        // Path to the users details
        final String userID = mAuth.getCurrentUser().getUid();
        String path = "user_details";

        // Get the DB reference
        final DatabaseReference childRef = mRootRef.child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                // Create user DB object
                Instant dobInstant = dob.toInstant();
                long dobTimeStamp = dobInstant.getMillis();
                UserDetailsDBObject userDetailsDBObject = new UserDetailsDBObject( dobTimeStamp, weight, mSelectedSex.toString() );

                // Check if details already exists for the user
                DataSnapshot userDetailsSnapshot = dataSnapshot.child( userID );
                if ( userDetailsSnapshot.exists() ) {
                    // If so, the operation is an update
                    Toast.makeText( EditUserDetailsActivity.this, R.string.info_updating_details, Toast.LENGTH_SHORT ).show();

                } else {
                    // If not, the operation is a create
                    Toast.makeText( EditUserDetailsActivity.this, R.string.info_setting_details, Toast.LENGTH_SHORT ).show();
                }

                // If no child exists, this will create a new one
                // If one does, this will update it
                childRef.child( userID ).setValue( userDetailsDBObject );

                setLocalUserDetails( dob, weight );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );

    }

    /**
     * Sets the singleton instance of the user details locally
     *
     * @param dob    date of birth of user
     * @param weight weight of user
     */
    private void setLocalUserDetails( DateTime dob, float weight ) {
        User localUser = User.getInstance();
        localUser.setDob( dob );
        localUser.setWeight( weight );
        localUser.setSex( mSelectedSex );

        localUser.setUserDetailsAreSet( true );
        // Go to profile screen
        Intent intent = new Intent( EditUserDetailsActivity.this, ProfileActivity.class );
        startActivity( intent );
    }
}
