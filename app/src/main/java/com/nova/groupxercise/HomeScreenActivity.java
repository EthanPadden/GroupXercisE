package com.nova.groupxercise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeScreenActivity extends AppCompatActivity implements ExerciseListItemFragment.OnFragmentInteractionListener{
    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected( @NonNull MenuItem item ) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            switch ( item.getItemId() ) {
                case R.id.navigation_home:
                    mTextMessage.setText( R.string.title_home );
                    HomeFragment homeFragment = new HomeFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, homeFragment );
                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText( R.string.title_discoveries );
                    DiscoveriesFragment discoveriesFragment = new DiscoveriesFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, discoveriesFragment );
                    ft.commit();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText( R.string.title_goals );
                    GoalsFragment goalsFragment = new GoalsFragment();
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, goalsFragment );
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
        mTextMessage = findViewById( R.id.message );

        // Set event listeners
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        // Create the home fragment
        HomeFragment homeFragment = new HomeFragment();

        // Set the fragment to be displayed in the frame view
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace( R.id.frame_home_screen_fragment_placeholder, homeFragment );
        ft.commit();
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
     * Required for implementing OnFragmentInteractionListener
     */
    @Override
    public void onFragmentInteraction( Uri uri ) {

    }
}
