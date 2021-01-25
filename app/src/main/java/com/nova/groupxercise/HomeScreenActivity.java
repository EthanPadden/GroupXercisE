package com.nova.groupxercise;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivity extends AppCompatActivity {
    private TextView mTextMessage;

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
        setContentView( R.layout.activity_home_screen );
        BottomNavigationView navView = findViewById( R.id.nav_view );
        mTextMessage = findViewById( R.id.message );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        // Create the home fragment
        HomeFragment homeFragment = new HomeFragment();

        // Set the fragment to be displayed in the frame view
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace( R.id.frame_home_screen_fragment_placeholder, homeFragment );
        ft.commit();
    }
}
