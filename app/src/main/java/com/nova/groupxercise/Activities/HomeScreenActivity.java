package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nova.groupxercise.Fragments.ActivitiesFragment;
import com.nova.groupxercise.Fragments.DiscoveriesFragment;
import com.nova.groupxercise.Fragments.ExerciseListItemFragment;
import com.nova.groupxercise.Fragments.GoalsFragment;
import com.nova.groupxercise.Fragments.MyGroupsFragment;
import com.nova.groupxercise.R;
import com.nova.groupxercise.Objects.User;

public class HomeScreenActivity extends AppCompatActivity implements ExerciseListItemFragment.OnFragmentInteractionListener {
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerContainer;
    private NavigationView mDrawer;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected( @NonNull MenuItem item ) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            switch ( item.getItemId() ) {
                case R.id.navigation_discoveries:
                    getSupportActionBar().setTitle( R.string.title_discoveries );
                    DiscoveriesFragment discoveriesFragment = new DiscoveriesFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, discoveriesFragment );
                    ft.commit();
                    return true;
                case R.id.navigation_goals:
                    getSupportActionBar().setTitle( R.string.title_goals );
                    GoalsFragment goalsFragment = new GoalsFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, goalsFragment );
                    ft.commit();
                    return true;
                case R.id.navigation_groups:
                    getSupportActionBar().setTitle( "Groups" );
                    MyGroupsFragment groupsFragment = new MyGroupsFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, groupsFragment );
                    ft.commit();
                    return true;
                case R.id.navigation_activities:
                    getSupportActionBar().setTitle( "Activities" );
                    ActivitiesFragment activitiesFragment = new ActivitiesFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, activitiesFragment );
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If it is the case that the current user is null, go to the login screen
        if ( mAuth.getCurrentUser() == null ) {
            // If the current user is null, go to the registration screen
            Intent intent = new Intent( HomeScreenActivity.this, RegistrationActivity.class );
            startActivity( intent );
        }
        // Set the screen layout
        setContentView( R.layout.activity_home_screen );

        // Initialise components
        BottomNavigationView navView = findViewById( R.id.nav_view );
        Toolbar toolbar = findViewById( R.id.toolbar );

        // Sets the Toolbar to act as the ActionBar for this ExerciseActivity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar( toolbar );

        // Initialise and set up navigation drawer
        mDrawerContainer = findViewById( R.id.drawer_container );
        mDrawer = findViewById( R.id.drawer );
        setupDrawerContent();

        // Set event listeners
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        // Create Discoveries Fragment
        DiscoveriesFragment discoveriesFragment = new DiscoveriesFragment();

        // Set the default toolbar title
        getSupportActionBar().setTitle( R.string.title_discoveries );

        // Set the fragment to be displayed in the frame view
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace( R.id.frame_home_screen_fragment_placeholder, discoveriesFragment );
        ft.commit();

        // If the user details are not set locally, retrieve them from the database
        User currentUser = User.getInstance();
        if ( !currentUser.isUserDetailsAreSet() ) {
            currentUser.retreiveUserDetails();
        }

        // If the username is not set locally, retrieve it from the database
        if(currentUser.getUsername() == null) {
            currentUser.retrieveUsername();
        }
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
            // Reset the local user instance
            User.getInstance().setUserDetailsAreSet( false );
            User.getInstance().setUsername( null );
            mAuth.signOut();
        } else {
            Toast.makeText( HomeScreenActivity.this, R.string.error_user_not_logged_in,
                    Toast.LENGTH_SHORT ).show();
        }

        // Regardless, go to login screen
        Intent intent = new Intent( HomeScreenActivity.this, LoginActivity.class );
        startActivity( intent );
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public DatabaseReference getmRootRef() {
        return mRootRef;
    }

    /**
     * Sets the event listeners for the navigation drawer
     */
    private void setupDrawerContent() {
        mDrawer.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
                if ( item.getItemId() == R.id.drawer_profile ) {
                    // Go to edit details screen
                    Intent intent = new Intent( HomeScreenActivity.this, ProfileActivity.class );
                    startActivity( intent );
                } else if ( item.getItemId() == R.id.drawer_logout ) {
                    signOutUser();
                }
                mDrawerContainer.closeDrawers();
                return true;
            }
        } );
    }

    /**
     * Required for implementing OnFragmentInteractionListener
     */
    @Override
    public void onFragmentInteraction( Uri uri ) {

    }
}
