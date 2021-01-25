package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class HomeFragment extends Fragment {
    private TextView mEmailText;
    private Button mLogoutBtn;
    private Button mEditDetailsBtn;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_home, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mEmailText = view.findViewById( R.id.email_text );
        mLogoutBtn = view.findViewById( R.id.btn_logout );
        mEditDetailsBtn = view.findViewById( R.id.btn_edit_details_link );

        // Set event listeners
        mLogoutBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                HomeScreenActivity homeScreenActivity = (HomeScreenActivity)getActivity();
                homeScreenActivity.signOutUser();
            }
        } );
        mEditDetailsBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Go to edit details screen
                Intent intent = new Intent( getActivity(), EditUserDetailsActivity.class );
                startActivity( intent );
            }
        } );

        // Display the email of the current user
        HomeScreenActivity homeScreenActivity = (HomeScreenActivity ) getActivity();
        mEmailText.setText( "User logged in: " + homeScreenActivity.getmAuth().getCurrentUser().getEmail() );
    }
}
