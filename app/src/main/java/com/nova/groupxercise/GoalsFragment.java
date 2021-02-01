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

        // TODO: Populate the array here - check discoveries fragment
        // Temporary:
        mGoalsList = new ArrayList<>();
        mGoalsList.add( new Goal( "Bench Press", 30, 60 ) );
        mGoalsList.add( new Goal( "Deadlift", 45, 75 ) );
        mGoalsList.add( new Goal( "Squat", 20, 50 ) );

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mGoalsList );

        mLoadingText.setVisibility( View.INVISIBLE );
        mListView.setAdapter( mItemsAdapter );
        retrieveGoals();
    }

    public void retrieveGoals() {
        // TODO: use the current user name
        String tempUserName = "john_doe";

        // Path to the users goals
        String path = "user_goals/" + tempUserName;

        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        // Create an empty list for the exercise names
        mGoalsList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mGoalsList );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    String exerciseName = exerciseDataSnapshot.getKey();

                    DataSnapshot currentStatusDataSnapshot = exerciseDataSnapshot.child( "current_status" );
                    float currentStatus = 0.0f;
                    if ( currentStatusDataSnapshot.exists() ) {
                        Long currentStatusLong = ( Long ) currentStatusDataSnapshot.getValue();
                        currentStatus = currentStatusLong.floatValue();
                    }

                    DataSnapshot targetStatusDataSnapshot = exerciseDataSnapshot.child( "target" );
                    float target = 0.0f;
                    if ( targetStatusDataSnapshot.exists() ) {
                        Long targetLong = ( Long ) targetStatusDataSnapshot.getValue();
                        currentStatus = targetLong.floatValue();
                    }

                    // Add the goal to the list
                    mGoalsList.add( new Goal( exerciseName,currentStatus,target) );
                }
                mLoadingText.setVisibility( View.INVISIBLE );
                mListView.setAdapter( mItemsAdapter );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }
}
