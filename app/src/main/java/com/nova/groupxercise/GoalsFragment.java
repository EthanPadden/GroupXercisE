package com.nova.groupxercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GoalsFragment extends Fragment {
    private ArrayList< Goal > mGoalsList;
    private GoalItemsAdapter mItemsAdapter;
    private ListView mListView;
    private TextView mLoadingText;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_goals, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mListView = view.findViewById( R.id.goal_list );
        mLoadingText = view.findViewById( R.id.text_loading_exercise_list );

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mGoalsList );

        mLoadingText.setVisibility( View.INVISIBLE );
        retrieveGoals();
    }

    /**
     * Gets the list of goals from the DB and makes the UI list visible when retrieved
     */
    public void retrieveGoals() {
        // TODO: use the current user name
        String tempUserName = "john_doe";

        // Path to the users goals
        String path = "user_goals/" + tempUserName;

        // Get the DBr reference
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        // Create an empty list for the goals
        mGoalsList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mGoalsList );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Get the exercise name
                    String exerciseName = exerciseDataSnapshot.getKey();

                    // Get the current status as a float value
                    DataSnapshot currentStatusDataSnapshot = exerciseDataSnapshot.child( "current_status" );
                    float currentStatus = 0.0f;
                    if ( currentStatusDataSnapshot.exists() ) {
                        Long currentStatusLong = ( Long ) currentStatusDataSnapshot.getValue();
                        currentStatus = currentStatusLong.floatValue();
                    }

                    // Get the target as a float value
                    DataSnapshot targetStatusDataSnapshot = exerciseDataSnapshot.child( "target" );
                    float target = 0.0f;
                    if ( targetStatusDataSnapshot.exists() ) {
                        Long targetLong = ( Long ) targetStatusDataSnapshot.getValue();
                        target = targetLong.floatValue();
                    }

                    // Add the goal to the list
                    mGoalsList.add( new Goal( exerciseName, currentStatus, target ) );
                }

                // Update UI
                mLoadingText.setVisibility( View.INVISIBLE );
                mListView.setAdapter( mItemsAdapter );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }
}
