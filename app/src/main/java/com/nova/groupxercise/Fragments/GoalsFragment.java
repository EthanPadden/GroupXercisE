package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nova.groupxercise.Adapters.GoalItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.WalkingPlan;
import com.nova.groupxercise.R;

import java.util.ArrayList;


public class GoalsFragment extends Fragment {
    private ArrayList< Goal > mPersonalGoalsList;
    private GoalItemsAdapter mItemsAdapter;
    private ListView mPersonalGoalsLListView;
    private TextView mLoadingPersonalGoalsText;
    private TextView mLoadingGroupGoalsText;
    private ArrayList< Group > mGroups;
    private LinearLayout mGroupGoalsLayout;
    protected ArrayList< DBListener > mDBListeners;
    private RelativeLayout mWalkingPlanPlaceholder;
    private TextView mLoadingWalkingPlanText;
    private boolean backButtonPressed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set back button behaviour
        backButtonPressed = false;
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(!backButtonPressed) {
                    Toast.makeText( getActivity(), "Press back button again to exit", Toast.LENGTH_SHORT ).show();
                    backButtonPressed = true;
                } else {
                    // Back button pressed twice - exit app
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
        mDBListeners = new ArrayList<>();
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
        mPersonalGoalsLListView = view.findViewById( R.id.goals_fgt_list_personal_goals );
        mLoadingPersonalGoalsText = view.findViewById( R.id.goals_fgt_text_loading_personal_goals );
        mLoadingGroupGoalsText = view.findViewById( R.id.goals_fgt_text_loading_group_goals );
        mGroupGoalsLayout = view.findViewById( R.id.layout_group_goals );
        mWalkingPlanPlaceholder = view.findViewById( R.id.walking_plan_goal_placeholder );
        mLoadingWalkingPlanText = view.findViewById( R.id.goals_fgt_text_loading_walking_plan_goal );

        // Set loading texts
        mLoadingPersonalGoalsText.setText( "Loading personal goals..." );
        mLoadingGroupGoalsText.setText( "Loading group goals..." );

        // Initialise personal goals list and set the list as the list for the items adapter
        mPersonalGoalsList = new ArrayList<>();
        mItemsAdapter = new GoalItemsAdapter( getActivity(), mPersonalGoalsList );

        // Retrieve personal goals
        DBListener pesonalGoalsListener = new DBListener() {
            @Override
            public void onRetrievalFinished( Object retrievedData ) {
                mPersonalGoalsList = (ArrayList< Goal> ) retrievedData;
                if (mPersonalGoalsList.size() == 0) {
                    mLoadingPersonalGoalsText.setText( "You have no personal goals" );
                } else {
                    mLoadingPersonalGoalsText.setVisibility( View.GONE );
                    mPersonalGoalsLListView.setAdapter( new GoalItemsAdapter( getActivity(), mPersonalGoalsList ) );
                }
                mDBListeners.remove( this );

                // Use recursive algorithm where base case is when the number of items updated is the same as the goals list size?
                updateUIWithPersonalGoalProgress();
                addGoalClickListeners( mPersonalGoalsLListView );
            }
            //


        };
        mDBListeners.add( pesonalGoalsListener );
        Goal.retrievePersonalStrengthGoals( pesonalGoalsListener );

        // Retrieve groups
        mGroups = new ArrayList<>();
        DBListener groupIdsListener = new DBListener() {
            public void onRetrievalFinished( Object retrievedData ) {
                ArrayList< String > retrievedGroupIds = ( ArrayList< String > ) retrievedData;
                DBListener groupNamesListener = new DBListener() {
                    public void onRetrievalFinished() {
                        if ( mGroups.size() == 0 ) {
                            mLoadingGroupGoalsText.setText( "You have no groups" );
                        } else {
                            mLoadingGroupGoalsText.setVisibility( View.GONE );
                            for ( final Group group : mGroups ) {
                                DBListener groupGoalsListener = new DBListener() {
                                    public void onRetrievalFinished() {
                                        addGroupGoalsToUI( group );
                                        mDBListeners.remove( this );
                                    }
                                };
                                mDBListeners.add( groupGoalsListener );
                                // This retrieves group goals and places on group object
                                group.retrieveGroupGoals( groupGoalsListener );
                            }
                        }
                        mDBListeners.remove( this );
                    }

                };
                mDBListeners.add( groupNamesListener );
                mDBListeners.remove( this );
                Group.retrieveGroupNames( retrievedGroupIds, mGroups, groupNamesListener );
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( groupIdsListener );
        Group.retrieveGroupIds( groupIdsListener );

        DBListener walkingPlanPersonalGoalListener = new DBListener() {
            public void onRetrievalFinished( Object retrievedData ) {
                // In this case, there is a walking plan set
                WalkingPlan walkingPlan = ( WalkingPlan ) retrievedData;
                updateUIWithWalkingPlan( walkingPlan );
                mDBListeners.remove( this );
            }

            @Override
            public void onRetrievalFinished() {
                // In this case, there is no walking goal set
                mLoadingWalkingPlanText.setText( "No walking plan set" );
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( walkingPlanPersonalGoalListener );
        WalkingPlan.retrievePersonalWalkingPlanGoal( walkingPlanPersonalGoalListener );
    }

    /**
     * This method is only called if there is a walking plan set
     * i.e. if there was a walking plan retrieved from the database
     * @param walkingPlan
     */
    private void updateUIWithWalkingPlan( WalkingPlan walkingPlan ){
        View walkingPlanView = (View) getLayoutInflater().inflate( R.layout.layout_goal_list_item, null );

        TextView exerciseNameText = walkingPlanView.findViewById( R.id.goal_exercise_name );
        TextView progressText = walkingPlanView.findViewById( R.id.goal_progress );
        TextView dividerText = walkingPlanView.findViewById( R.id.goal_divider );
        TextView targetText = walkingPlanView.findViewById( R.id.goal_target );
        TextView unitText = walkingPlanView.findViewById( R.id.goal_unit );

        String walkingPlanText = walkingPlan.getmWalkingPlanName() + " walking plan";
        exerciseNameText.setText( walkingPlanText );
        progressText.setText( Integer.toString( walkingPlan.getmProgress() ) );
        targetText.setText( Integer.toString( walkingPlan.getmTodaysStepGoal() ) );
        unitText.setText( "steps" );

        mWalkingPlanPlaceholder.addView( walkingPlanView );
        mLoadingWalkingPlanText.setVisibility( View.GONE );

        // Set text to green if the daily goal is complete
        if ( walkingPlan.getmProgress() >= walkingPlan.getmTodaysStepGoal() ) {
            exerciseNameText.setTextColor( Color.GREEN );
            progressText.setTextColor( Color.GREEN );
            dividerText.setTextColor( Color.GREEN );
            targetText.setTextColor( Color.GREEN );
            unitText.setTextColor( Color.GREEN );
        }

        // Set on click listener for walking plan
        walkingPlanView.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Create a fragment and pass in the exercise name
                LogWalkFragment logWalkFragment = LogWalkFragment.newInstance();

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, logWalkFragment );
                ft.commit();
            }
        } );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }

    /**
     * Updates the progress using the user_progress subtree
     */
    private void updateUIWithPersonalGoalProgress() {
        GoalItemsAdapter goalItemsAdapter = new GoalItemsAdapter( getActivity(), mPersonalGoalsList );
        mPersonalGoalsLListView.setAdapter( goalItemsAdapter );

        for (Goal goalToUpdate : mPersonalGoalsList ) {
            DBListener progressListener = new DBListener() {
                public void onRetrievalFinished( Object retrievedData ) {
                    // If retrievedData == null,
                    // there is no progress saved for this exercise yet
                    // so 0 is the default value
                    float progress = 0;

                    if ( retrievedData != null ) {
                        progress = ( ( Float ) retrievedData ).floatValue();
                    }
                    goalToUpdate.setmProgress( progress );

                    goalItemsAdapter.notifyDataSetChanged();
                    mDBListeners.remove( this );
                }

            };
            mDBListeners.add( progressListener );
            goalToUpdate.retrieveUserProgress( progressListener );
        }
    }
    /**
     * Builds a list for every group to display the group goals
     */
    private void addGroupGoalsToUI( final Group group ) {
        // Inflate a group goals view
        LinearLayout groupGoalsView = (LinearLayout) getLayoutInflater().inflate( R.layout.layout_group_goals, null );
        mGroupGoalsLayout.addView( groupGoalsView );

        // Set group title
        LinearLayout groupTitleLayout = groupGoalsView.findViewById( R.id.text_group_goal_title );
        TextView groupTitleText = groupTitleLayout.findViewById( R.id.group_title );
        groupTitleText.setText( group.getmName() );

        // Set adapter for listview
        ListView groupGoalsList = groupGoalsView.findViewById( R.id.list_group_goals );
        if ( group.getmGoals() != null ) {
            if ( group.getmGoals().size() == 0 ) {
                // Display that the group has no goals
                TextView noGoalsText = new TextView( getActivity() );
                noGoalsText.setText( "No goals" );
                groupGoalsView.addView( noGoalsText );
                groupGoalsList.setVisibility( View.GONE );
            } else {
                groupGoalsList.setAdapter( new GoalItemsAdapter( getActivity(), group.getmGoals() ) );
                addGoalClickListeners( groupGoalsList );
            }
        }

        updateUIWithGroupGoalProgress( group, groupGoalsList );
    }

    /**
     * Updates the progress using the user_progress subtree
     */
    private void updateUIWithGroupGoalProgress( Group group, ListView groupGoalsList ) {
        for (Goal goalToUpdate : group.getmGoals() ) {
            DBListener progressListener = new DBListener() {
                public void onRetrievalFinished( Object retrievedData ) {
                    // If retrievedData == null,
                    // there is no progress saved for this exercise yet
                    // so 0 is the default value
                    float progress = 0;

                    if ( retrievedData != null ) {
                        progress = ( ( Float ) retrievedData ).floatValue();
                    }
                    goalToUpdate.setmProgress( progress );
                    groupGoalsList.setAdapter( new GoalItemsAdapter( getActivity(), group.getmGoals() ) );
                    mDBListeners.remove( this );
                }

            };
            mDBListeners.add( progressListener );
            goalToUpdate.retrieveUserProgress( progressListener );
        }
    }

    private void addGoalClickListeners( ListView listView ) {
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                // Get the exercise name
                Goal selectedGoal = (Goal) listView.getItemAtPosition( i );
                String exerciseName = selectedGoal.getmExerciseName();

                // Create a fragment and pass in the exercise name
                LogActivityFragment exerciseListItemFragment = LogActivityFragment.newInstance( exerciseName );

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, exerciseListItemFragment );
                ft.commit();
            }
        } );
    }
}
