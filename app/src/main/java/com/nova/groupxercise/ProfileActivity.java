package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar( mToolbar );

        // Initialise and set up navigation drawer
        mDrawerContainer = findViewById( R.id.drawer_container );
        mDrawer = findViewById( R.id.drawer );
        setupDrawerContent();
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
