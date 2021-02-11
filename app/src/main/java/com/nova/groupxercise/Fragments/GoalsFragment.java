package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Adapters.GoalItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.R;

import java.util.ArrayList;


public class GoalsFragment extends Fragment {
    private ArrayList< Goal > mGoalsList;
    private GoalItemsAdapter mItemsAdapter;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayList< Group > mGroups;
    private LinearLayout mGroupGoalsLayout;

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
        mGroupGoalsLayout = view.findViewById( R.id.layout_group_goals );

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mGoalsList );

        mGroups = new ArrayList<>(  );
        mLoadingText.setVisibility( View.INVISIBLE );
        retrieveGoals();
        final ArrayList<String> groupIds = new ArrayList<>(  );
        Group.retrieveGroupIds( groupIds, new DBListener() {
            @Override
            public void onRetrievalFinished() {
                Group.retrieveGroupNames( groupIds, mGroups, new DBListener() {
                    @Override
                    public void onRetrievalFinished() {
                        if(mGroups.size() == 0) {
                            mLoadingText.setText( "You have no groups" );
                        } else {
                            for( final Group group: mGroups) {
                                group.retrieveGroupGoals( new DBListener() {
                                    @Override
                                    public void onRetrievalFinished() {
                                        addGroupGoalsToUI(group);
                                    }
                                } );
                            }
                        }
                    }
                } );
            }
        } );
    }


    /**
     * Builds a list for every group to display the group goals
     */
    private void addGroupGoalsToUI(Group group) {
        // Group title
        TextView groupTitleText = new TextView( getActivity() );
        groupTitleText.setText( group.getmGroupName().toUpperCase() );
        mGroupGoalsLayout.addView( groupTitleText );

        if(group.getGoals() != null) {
            ListView groupListView = createGroupGoalsListView( group );

            // Append to layout
            mGroupGoalsLayout.addView( groupListView );

        } else {
            // Display that the group has no goals
            TextView noGoalsText = new TextView( getActivity() );
            noGoalsText.setText( "No goals" );
            mGroupGoalsLayout.addView( noGoalsText );
        }
    }

    private ListView createGroupGoalsListView(Group group) {
        ListView listView = new ListView( getActivity() );
        GoalItemsAdapter itemsAdapter = new GoalItemsAdapter( getActivity(), group.getGoals() );
        listView.setAdapter( itemsAdapter );
        return listView;
    }

    /**
     * Gets the list of goals from the DB and makes the UI list visible when retrieved
     */
    public void retrieveGoals() {
        // Path to the users goals
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "user_goals/" + userId;

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
                mLoadingText.setVisibility( View.GONE );
                mListView.setAdapter( mItemsAdapter );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    private void retreiveGroupGoals() {

    }
}
