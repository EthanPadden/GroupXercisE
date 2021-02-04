package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerContainer;
    private NavigationView mDrawer;
    private Button mEditBtn;
    private TextView mInfoText;
    private TextView mDobText;
    private TextView mWeightText;
    private TextView mSexText;
    private TableLayout mUserDetailsTable;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If it is the case that the current user is null, go to the login screen
        if ( mAuth.getCurrentUser() == null ) {
            // If the current user is null, go to the registration screen
            Intent intent = new Intent( ProfileActivity.this, RegistrationActivity.class );
            startActivity( intent );
        }
        // Set content view
        setContentView( R.layout.activity_profile );

        // Initialise components
        mToolbar = findViewById( R.id.toolbar );
        mEditBtn = findViewById( R.id.btn_edit );
        mInfoText = findViewById( R.id.text_info );
        mDobText = findViewById( R.id.text_dob );
        mWeightText = findViewById( R.id.text_weight );
        mSexText = findViewById( R.id.text_sex );
        mUserDetailsTable = findViewById( R.id.table_user_details );

        // Set event listeners
        mEditBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                // Go to edit details screen
                Intent intent = new Intent( ProfileActivity.this, EditUserDetailsActivity.class );
                startActivity( intent );
            }
        } );

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar( mToolbar );

        // Initialise and set up navigation drawer
        mDrawerContainer = findViewById( R.id.drawer_container );
        mDrawer = findViewById( R.id.drawer );
        setupDrawerContent();

        displayUserDetails();
    }

    /**
     * Updates the UI with the details from the locally stored user object
     */
    private void displayUserDetails() {
        User currentUser = User.getInstance();
        if ( currentUser.isUserDetailsAreSet() ) {
            mInfoText.setVisibility( View.GONE );

            int dobDay = currentUser.getDob().getDayOfMonth();
            int dobMonth = currentUser.getDob().getMonthOfYear();
            int dobYear = currentUser.getDob().getYear();

            mDobText.setText( String.format( "%d/%d/%d", dobDay, dobMonth + 1, dobYear ) );

            mWeightText.setText( Float.toString( currentUser.getWeight() ) );
            mSexText.setText( currentUser.getSex().toString() );

            mUserDetailsTable.setVisibility( View.VISIBLE );
        } else {
            mInfoText.setText( R.string.error_no_details_found );
        }
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
                    Intent intent = new Intent( ProfileActivity.this, HomeScreenActivity.class );
                    startActivity( intent );
                } else if ( item.getItemId() == R.id.drawer_logout ) {
                    signOutUser();
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
     * Sign out the user that is currently logged in using Firebase method
     * Toast with error message if no user is currently logged in
     */
    protected void signOutUser() {
        // Check if there is a user currently logged in
        if ( mAuth.getCurrentUser() != null ) {
            mAuth.signOut();
        } else {
            Toast.makeText( ProfileActivity.this, R.string.error_user_not_logged_in,
                    Toast.LENGTH_SHORT ).show();
        }

        // Regardless, go to login screen
        Intent intent = new Intent( ProfileActivity.this, LoginActivity.class );
        startActivity( intent );
    }
}
