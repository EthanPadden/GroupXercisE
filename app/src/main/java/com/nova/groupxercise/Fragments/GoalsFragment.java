package com.nova.groupxercise.Fragments;

import android.content.Context;
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
    protected ArrayList< DBListener > mDBListeners;

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>(  );
    }

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
//        retrieveGoals();




        mGoalsList = new ArrayList<>(  );

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mGoalsList );
        DBListener pesonalGoalsListener =   new DBListener() {
            public void onRetrievalFinished() {
                if(mGoalsList.size() > 0){
                    mLoadingText.setVisibility( View.GONE );
                    mListView.setAdapter( mItemsAdapter );
                }
            }
        };
        mDBListeners.add( pesonalGoalsListener );
        Goal.retrievePersonalGoals( mGoalsList, pesonalGoalsListener);



        final ArrayList<String> groupIds = new ArrayList<>(  );
        DBListener groupIdsListener = new DBListener() {
            public void onRetrievalFinished() {
                DBListener groupNamesListener = new DBListener() {
                    public void onRetrievalFinished() {
                        if(mGroups.size() == 0) {
                            mLoadingText.setText( "You have no groups" );
                        } else {
                            for( final Group group: mGroups) {
                                DBListener groupGoalsListener = new DBListener() {
                                    public void onRetrievalFinished() {
                                        addGroupGoalsToUI(group);
                                    }
                                };
                                mDBListeners.add( groupGoalsListener );
                                group.retrieveGroupGoals( groupGoalsListener );
                            }
                        }
                    }
                };
                mDBListeners.add( groupNamesListener );
                Group.retrieveGroupNames( groupIds, mGroups, groupNamesListener );
            }
        };
        mDBListeners.add( groupIdsListener );
        Group.retrieveGroupIds( groupIds, groupIdsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(DBListener dbListener : mDBListeners) {
            dbListener.setActive( false );
        }
    }

    /**
     * Builds a list for every group to display the group goals
     */
    private void addGroupGoalsToUI( Group group ) {
        // Group title
        TextView groupTitleText = new TextView( getActivity() );
        groupTitleText.setText( group.getmGroupName().toUpperCase() );
        mGroupGoalsLayout.addView( groupTitleText );

        if ( group.getGoals() != null ) {
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



    private void retreiveGroupGoals() {

    }
}
