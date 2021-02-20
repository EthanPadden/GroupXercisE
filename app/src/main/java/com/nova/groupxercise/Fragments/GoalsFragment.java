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
    private ArrayList< Goal > mPersonalGoalsList;
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
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mPersonalGoalsList );

        mGroups = new ArrayList<>(  );
        mLoadingText.setVisibility( View.INVISIBLE );
//        retrieveGoals();




        mPersonalGoalsList = new ArrayList<>(  );

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mPersonalGoalsList );
        DBListener pesonalGoalsListener =   new DBListener() {
            public void onRetrievalFinished() {
                if( mPersonalGoalsList.size() > 0){
                    mLoadingText.setVisibility( View.GONE );
                    mListView.setAdapter( mItemsAdapter );
                }
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( pesonalGoalsListener );
        Goal.retrievePersonalGoals( mPersonalGoalsList, pesonalGoalsListener);



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
                                        mDBListeners.remove( this );

                                    }
                                };
                                mDBListeners.add( groupGoalsListener );
                                group.retrieveGroupGoals( groupGoalsListener );
                            }
                        }
                        mDBListeners.remove( this );

                    }

                };
                mDBListeners.add( groupNamesListener );
                mDBListeners.remove( this );
                Group.retrieveGroupNames( groupIds, mGroups, groupNamesListener );
                mDBListeners.remove( this );

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
    private void addGroupGoalsToUI( final Group group ) {
        // Group title
        View groupTitleView = getLayoutInflater().inflate( R.layout.text_group_subtitle, null );
        TextView groupTitleText = groupTitleView.findViewById( R.id.goal_group_name );
        groupTitleText.setText( group.getmGroupName() );
        final LinearLayout groupLayout = new LinearLayout( getActivity() );
        groupLayout.setOrientation( LinearLayout.VERTICAL );
        groupLayout.setId( group.getmGroupId().hashCode() );
        groupLayout.addView( groupTitleView );
        mGroupGoalsLayout.addView( groupLayout );



        if ( group.getGoals() != null ) {

            final ListView groupListView = createGroupGoalsListView( group );
            groupLayout.addView( groupListView );

            for(Goal groupGoal:group.getGoals()) {
                final DBListener memberProgressListener = new DBListener() {
                    public void onRetrievalFinished() {
                        ((GoalItemsAdapter)groupListView.getAdapter()).notifyDataSetChanged();
                        mDBListeners.remove( this );
                    }
                };
                mDBListeners.add( memberProgressListener );
                group.retrieveThisMembersProgress( memberProgressListener, groupGoal );
            }
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
