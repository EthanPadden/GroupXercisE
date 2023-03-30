package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nova.groupxercise.Adapters.ActivityItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.R;

import java.util.ArrayList;
import java.util.Collections;

public class ActivitiesFragment extends Fragment {
    private ArrayList< ExerciseActivity > mActivitesList;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayAdapter< String > mItemsAdapter;
    protected ArrayList< DBListener > mDBListeners;
    private boolean backButtonPressed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set back button behaviour
        backButtonPressed = false;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!backButtonPressed) {
                    Toast.makeText( getActivity(), "Press back button again to exit", Toast.LENGTH_SHORT ).show();
                    backButtonPressed = true;
                } else {
                    // Back button pressed twice - exit appp
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    backButtonPressed = false;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>(  );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_activities, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mListView = view.findViewById( R.id.activities_list );
        mLoadingText = view.findViewById( R.id.text_loading_activities );

        // Create arraylist to store activities retrieved from the database and create an adapter to display them
        mActivitesList = new ArrayList<>();
        mItemsAdapter = new ActivityItemsAdapter( getActivity(), mActivitesList );
        DBListener activitiesListener = new DBListener() {

            public void onRetrievalFinished() {
                if ( mActivitesList.isEmpty() ) {
                    mLoadingText.setText( "No activities" );
                } else {
                    // Sort activities by time
                    Collections.sort( mActivitesList );
                    // Update UI
                    mLoadingText.setVisibility( View.GONE );
                    mListView.setAdapter( mItemsAdapter );
                }
                mDBListeners.remove( this );
            }


        };
        mDBListeners.add( activitiesListener );
        ExerciseActivity.retrieveActivities( mActivitesList,  activitiesListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Deactivate any active listeners
        for(DBListener dbListener : mDBListeners) {
            dbListener.setActive( false );
        }
    }
}
