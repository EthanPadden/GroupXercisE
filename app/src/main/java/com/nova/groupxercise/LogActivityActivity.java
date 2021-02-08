package com.nova.groupxercise;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LogActivityActivity extends AppCompatActivity {
    private EditText mLevelEt;
    private Button mLogActivityBtn;
    private Goal mSelectedGoal;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Goal> mGoalsList;
    private GoalItemsAdapter mItemsAdapter;
    private TextView mLoadingText;
    private ListView mListView;


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
                //validateEnteredActivity();
            }
        } );

        retrieveGoals();
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

                // Update UI
                mLoadingText.setVisibility( View.GONE );
                mListView.setAdapter( mItemsAdapter );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

}
