package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Adapters.GoalItemsAdapter;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.ArrayList;

public class LogActivityActivity extends AppCompatActivity {
    private EditText mLevelEt;
    private Button mLogActivityBtn;
    private Goal mSelectedGoal;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList< Goal > mGoalsList;
    private GoalItemsAdapter mItemsAdapter;
    private TextView mLoadingText;
    private ListView mListView;
    private ArrayList< Group > mGroups;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log_activity );

        // Initialise components
        mLevelEt = findViewById( R.id.et_level );
        mLogActivityBtn = findViewById( R.id.btn_log_activity );
        mLoadingText = findViewById( R.id.text_loading_exercise_list );
        mListView = findViewById( R.id.exercise_list );

        // Set event listeners
        mLogActivityBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                validateEnteredActivity();
            }
        } );


        retrieveGoals();
        retrieveGroupIds();
    }

    private void validateEnteredActivity() {
        String levelStr = mLevelEt.getText().toString();
        if ( levelStr != null && levelStr.compareTo( "" ) != 0 ) {
            float level = Float.parseFloat( levelStr );
            if ( mSelectedGoal != null ) {
                ExerciseActivity exerciseActivity = new ExerciseActivity( mSelectedGoal.getmExerciseName(), DateTime.now(), level );
                logActivity( exerciseActivity );
                updatePersonalGoal( exerciseActivity );
            } else {
                Toast.makeText( LogActivityActivity.this, "Choose a goal", Toast.LENGTH_SHORT ).show();
            }
        } else {
            Toast.makeText( LogActivityActivity.this, "Enter level", Toast.LENGTH_SHORT ).show();
        }
    }

    private void logActivity( ExerciseActivity exerciseActivity ) {
        Toast.makeText( LogActivityActivity.this, exerciseActivity.toString(), Toast.LENGTH_SHORT ).show();

        // Path to the subtree
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        String userActivitiesPath = "activities/" + currentUserId + "/" + exerciseActivity.getmExerciseName();
        final DatabaseReference childRef = mRootRef.child( userActivitiesPath );

        Instant activityInstant = exerciseActivity.getmTime().toInstant();
        long activityTimeStamp = activityInstant.getMillis();
        String activityTimeStampStr = Long.toString( activityTimeStamp );

        childRef.child( activityTimeStampStr ).setValue( exerciseActivity.getmLevel() );

        Intent intent = new Intent( LogActivityActivity.this, HomeScreenActivity.class );
        startActivity( intent );
    }

    private void setupGoalsList() {
        mLoadingText.setVisibility( View.GONE );
        mListView.setAdapter( mItemsAdapter );

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                Goal selectedGoal = ( Goal ) mListView.getItemAtPosition( i );
                mSelectedGoal = selectedGoal;
            }
        } );
    }

    /**
     * Retrieves the group IDs of the groups that the user is a member of from the DB
     */
    private void retrieveGroupIds() {
        // Create empty list for the group IDs
        final ArrayList< String > groupIds = new ArrayList<>();

        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the reference
        String usersGroupPath = "user_groups/" + currentUserId;
        // Get the DBr reference
        DatabaseReference childRef = mRootRef.child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    groupIds.add( usersGroupsDataSnapshot.getKey() );
                }

                retrieveGroupGoals( groupIds );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    private void updatePersonalGoal( final ExerciseActivity activity ) {
        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the current status of this goal
        String goalPath = "user_goals/" + currentUserId + "/" + activity.getmExerciseName() + "/current_status";

        final DatabaseReference goalRef = mRootRef.child( goalPath );

        goalRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    // We have a goal for this exercise
                    Object currentStatusObj = dataSnapshot.getValue();
                    float currentStatus;
                    if ( currentStatusObj instanceof Long ) {
                        currentStatus = ( ( Long ) currentStatusObj ).floatValue();
                    } else {
                        currentStatus = ( ( Float ) currentStatusObj ).floatValue();
                    }
                    if(activity.getmLevel() > currentStatus) {
                        goalRef.setValue( activity.getmLevel() );
                    }
                } else {
                    Toast.makeText( LogActivityActivity.this, "There is no goal for this exercise", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Given a list of group IDs, retrieve the group goals from the DB
     *
     * @param groupIds the list of group IDs
     */
    private void retrieveGroupGoals( ArrayList< String > groupIds ) {
        // Create an empty list for the groups
        mGroups = new ArrayList<>();

        // The UI is updated when all of the group names have been added
        // Necessary because of the async call within the for loop
        final int expectedSize = groupIds.size();

        for ( final String groupId : groupIds ) {
            String groupPath = "groups/" + groupId;
            // Get the DBr reference
            DatabaseReference groupRef = mRootRef.child( groupPath );

            groupRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    String groupName = dataSnapshot.child( "name" ).getValue().toString();

                    Group group = new Group( groupName, groupId );
                    DataSnapshot groupGoalsDataSnapshot = dataSnapshot.child( "goals" );

                    // If the group has goals
                    if ( groupGoalsDataSnapshot.exists() ) {
                        ArrayList< Goal > groupGoals = new ArrayList<>();

                        for ( DataSnapshot groupGoalDataSnapshot : groupGoalsDataSnapshot.getChildren() ) {
                            String exerciseName = groupGoalDataSnapshot.getKey();
                            String targetStr = groupGoalDataSnapshot.getValue().toString();
                            float target = Float.parseFloat( targetStr );
                            Goal goal = new Goal( exerciseName, 0, target );
                            groupGoals.add( goal );
                        }

                        group.setGoals( groupGoals );
                    }

                    mGroups.add( group );


                    if ( mGroups.size() == expectedSize ) {
                        displayGroupGoals();
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );
        }
        if ( mGroups.size() == 0 ) {
            mLoadingText.setText( "You have no groups" );
        }

    }

    /**
     * Builds a list for every group to display the group goals
     */
    private void displayGroupGoals() {
        for ( Group group : mGroups ) {

            if ( group.getGoals() != null ) {
                for ( Goal goal : group.getGoals() ) {
                    mGoalsList.add( goal );
                }
            }
        }

        setupGoalsList();
    }

    /**
     * Gets the list of goals from the DB and makes the UI list visible when retrieved
     */
    public void retrieveGoals() {
        // Path to the users goals
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "user_goals/" + userId;

        // Get the DB reference
        DatabaseReference childRef = mRootRef.child( path );

        // Create an empty list for the goals
        mGoalsList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( this, mGoalsList );

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
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

}
