package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerContainer;
    private NavigationView mDrawer;
    private FloatingActionButton mEditBtn;
    private TextView mInfoText;
    private TextView mDobText;
    private TextView mWeightText;
    private TextView mSexText;
    private LinearLayout mUserDetailsTable;
    private TextView mUsernameText;

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
        mUserDetailsTable = findViewById( R.id.layout_user_details );
        mUsernameText = findViewById( R.id.text_username );
        Toolbar toolbar = findViewById( R.id.toolbar );

        // Sets the Toolbar to act as the ActionBar for this ExerciseActivity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitleTextColor( Color.WHITE );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( "Profile" );

        // Set event listeners
        mEditBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                // Go to edit details screen
                Intent intent = new Intent( ProfileActivity.this, EditUserDetailsActivity.class );
                startActivity( intent );
            }
        } );

        // Sets the Toolbar to act as the ActionBar for this ExerciseActivity window.
        // Make sure the toolbar exists in the activity and is not null
        mToolbar.setTitleTextColor( Color.WHITE );
        setSupportActionBar( mToolbar );

        // Initialise and set up navigation drawer
        mDrawerContainer = findViewById( R.id.drawer_container );
        mDrawer = findViewById( R.id.drawer );
        setupDrawerContent();

        // Call methods to update UI
        displayUserDetails();
        displayUsername();

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to HomeScreenActivity
                Intent intent = new Intent( ProfileActivity.this, HomeScreenActivity.class );
                startActivity( intent );
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Updates the UI with the username from the locally stored user object
     */
    private void displayUsername() {
        User currentUser = User.getInstance();
        mUsernameText.setText( currentUser.getUsername() );
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

            mWeightText.setText( Float.toString( currentUser.getWeight() ) + " kg" );
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
                    User.getInstance().signOutUser();
                    Intent intent = new Intent( ProfileActivity.this, LoginActivity.class );
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
        int id = item.getItemId();
        if ( id == R.id.toolbar_btn_open_drawer ) {
            mDrawerContainer.openDrawer( GravityCompat.START );
        }
        return super.onOptionsItemSelected( item );
    }
}