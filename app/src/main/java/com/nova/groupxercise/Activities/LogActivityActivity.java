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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Adapters.GoalItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
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


//        retrieveGoals();
        mGoalsList = new ArrayList<>(  );

        // Set the list as the list for the items adapter
        mItemsAdapter = new GoalItemsAdapter( this, mGoalsList );
        Goal.retrievePersonalGoals( mGoalsList, new DBListener() {
            @Override
            public void onRetrievalFinished() {
                if(mGoalsList.size() > 0){
                    setupGoalsList();
                }
            }
        } );
//        retrieveGroupIds();
        final ArrayList<String> groupIds = new ArrayList<>(  );
        Group.retrieveGroupIds( groupIds, new DBListener() {
            @Override
            public void onRetrievalFinished() {
                // Create an empty list for the group names
                mGroups = new ArrayList<>();

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
                                        for ( Goal goal : group.getGoals() ) {
                                            mGoalsList.add( goal );
                                            setupGoalsList();

                                        }
                                    }
                                } );
                            }
                        }
                    }
                } );

            }
        } );
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

    public void updateGroupGoals( final ExerciseActivity exerciseActivity){
        for(Group group : mGroups) {
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
                    if(dataSnapshot.exists()) {
                        // This means there is a goal for this exercise
                        // Update the value here
                        Object progressObj = dataSnapshot.getValue();
                        float progress;
                        if ( progressObj instanceof Long ) {
                            progress = ( ( Long ) progressObj ).floatValue();
                        } else {
                            progress = ( ( Float ) progressObj ).floatValue();
                        }

                        if(exerciseActivity.getmLevel() > progress) {
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
                    if(activity.getmLevel() > currentStatus) {
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
