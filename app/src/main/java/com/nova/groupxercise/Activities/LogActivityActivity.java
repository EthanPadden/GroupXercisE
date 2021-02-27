package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.User;
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
    private ArrayList< String > mGoalsNameList;
    private ArrayAdapter mItemsAdapter;
    private TextView mLoadingText;
    private ListView mListView;
    private ArrayList< Group > mGroups;
    private ArrayList< DBListener > mDBListeners;


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


//        retrieveGoals();
        mGoalsList = new ArrayList<>();

        mDBListeners = new ArrayList<>();


        // Set the list as the list for the items adapter]
        mGoalsNameList = new ArrayList<>();
        mItemsAdapter = new ArrayAdapter< String >( this, android.R.layout.simple_list_item_1, mGoalsNameList );
        DBListener personalGoalsListener = new DBListener() {
            public void onRetrievalFinished() {
                if ( mGoalsList.size() > 0 ) {
                    setupGoalsList();
                }
                mDBListeners.remove( this );

            }
        };
        mDBListeners.add( personalGoalsListener );
        Goal.retrievePersonalGoals( mGoalsList, personalGoalsListener );
        
        DBListener groupIdsListener = new DBListener() {
            public void onRetrievalFinished(Object retrievedData) {
                ArrayList<String> retrievedGroupIds = (ArrayList< String>)  retrievedData;
                // Create an empty list for the group names
                mGroups = new ArrayList<>();

                DBListener groupNamesListener = new DBListener() {
                    public void onRetrievalFinished() {
                        mDBListeners.remove( this );

                        if ( mGroups.size() == 0 ) {
                            mLoadingText.setText( "You have no groups" );
                        } else {
                            for ( final Group group : mGroups ) {
                                DBListener groupGoalsListener = new DBListener() {
                                    public void onRetrievalFinished() {
                                        for ( Goal goal : group.getmGoals() ) {
                                            mGoalsList.add( goal );
                                            setupGoalsList();
                                        }
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
                Group.retrieveGroupNames( retrievedGroupIds, mGroups, groupNamesListener );
                mDBListeners.remove( this );

            }
        };
        mDBListeners.add( groupIdsListener );
        Group.retrieveGroupIds( groupIdsListener );
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }

    private void validateEnteredActivity() {
        String levelStr = mLevelEt.getText().toString();
        if ( levelStr != null && levelStr.compareTo( "" ) != 0 ) {
            float level = Float.parseFloat( levelStr );
            if ( mSelectedGoal != null ) {
                ExerciseActivity exerciseActivity = new ExerciseActivity( mSelectedGoal.getmExerciseName(), DateTime.now(), level );
                logActivity( exerciseActivity );
                // Update both seperately (should be the same at this point)
                updatePersonalGoal( exerciseActivity );
                updateGroupGoals( exerciseActivity );
            } else {
                Toast.makeText( LogActivityActivity.this, "Choose a goal", Toast.LENGTH_SHORT ).show();
            }
        } else {
            Toast.makeText( LogActivityActivity.this, "Enter level", Toast.LENGTH_SHORT ).show();
        }
    }

    public void updateGroupGoals( final ExerciseActivity exerciseActivity ) {
        for ( Group group : mGroups ) {
            // Update the progress only if there is a goal for that group:
            // Check is there a goal for the exercise in the group (using the progress)
            // If so, update the progress with the value
            String progressPath = "groups/"
                    + group.getmGroupId()
                    + "/members/"
                    + User.getInstance().getUsername()
                    + "/progress/"
                    + exerciseActivity.getmExerciseName();

            final DatabaseReference progressRef = mRootRef.child( progressPath );
            progressRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    if ( dataSnapshot.exists() ) {
                        // This means there is a goal for this exercise
                        // Update the value here
                        Object progressObj = dataSnapshot.getValue();
                        float progress;
                        if ( progressObj instanceof Long ) {
                            progress = ( ( Long ) progressObj ).floatValue();
                        } else {
                            progress = ( ( Float ) progressObj ).floatValue();
                        }

                        if ( exerciseActivity.getmLevel() > progress ) {
                            progressRef.setValue( exerciseActivity.getmLevel() );
                        }
                    }
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {

                }
            } );
        }
    }

    private void logActivity( ExerciseActivity exerciseActivity ) {

        // Path to the subtree
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        String userActivitiesPath = "activities/" + currentUserId + "/" + exerciseActivity.getmExerciseName();
        final DatabaseReference childRef = mRootRef.child( userActivitiesPath );

        Instant activityInstant = exerciseActivity.getmTime().toInstant();
        long activityTimeStamp = activityInstant.getMillis();
        String activityTimeStampStr = Long.toString( activityTimeStamp );

        childRef.child( activityTimeStampStr ).setValue( exerciseActivity.getmLevel() );

        Toast.makeText( LogActivityActivity.this, "Activity logged", Toast.LENGTH_SHORT ).show();

        Intent intent = new Intent( LogActivityActivity.this, HomeScreenActivity.class );
        intent.putExtra( "FRAGMENT_ID", R.id.navigation_activities );
        startActivity( intent );
    }

    private void setupGoalsList() {
        mLoadingText.setVisibility( View.GONE );
        for ( Goal goal : mGoalsList ) {
            String goalName = goal.getmExerciseName();
            if ( !mGoalsNameList.contains( goalName ) )
                mGoalsNameList.add( goalName );
        }
        mListView.setAdapter( mItemsAdapter );

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                String selectedGoalName = ( String ) mListView.getItemAtPosition( i );
                for ( Goal goal : mGoalsList ) {
                    if ( goal.getmExerciseName().compareTo( selectedGoalName ) == 0 ) {
                        mSelectedGoal = goal;
                    }
                }

                for ( int j = 0; j < mListView.getCount(); j++ ) {
                    mListView.getChildAt( j ).setBackgroundColor( Color.WHITE );
                }
                view.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );


            }
        } );
    }


    private void updatePersonalGoal( final ExerciseActivity activity ) {
        // Get the current user ID
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                    if ( activity.getmLevel() > currentStatus ) {
                        goalRef.setValue( activity.getmLevel() );
                    }

                } else {
                    Toast.makeText( LogActivityActivity.this, "There is no personal goal for this exercise", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }


}
