package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ExerciseListActivity extends AppCompatActivity implements ExerciseListItemFragment.OnFragmentInteractionListener {
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList< String > mExerciseNameList;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayAdapter< String > mItemsAdapter;
    private ArrayAdapter< CharSequence > mLevelSpinnerAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set content view
        setContentView( R.layout.activity_exercise_list );

        // Initialise components
        mListView = findViewById( R.id.list );
        mLoadingText = findViewById( R.id.text_loading_exercise_list );

        mLevelSpinnerAdapter = ArrayAdapter.createFromResource( this,
                R.array.level_array, android.R.layout.simple_spinner_item );
        // Specify the layout to use when the list of choices appears
        mLevelSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        // Get the list of exercises from the database
        retrieveExerciseList();

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                // Get the exercise name
                String exerciseName = mListView.getItemAtPosition( i ).toString();

                // Create a fragment and set the spinner adapter on the fragment
                ExerciseListItemFragment exerciseListItemFragment = ExerciseListItemFragment.newInstance( exerciseName );
                exerciseListItemFragment.setmLevelSpinnerAdapter( mLevelSpinnerAdapter );

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_set_goal_fragment_placeholder, exerciseListItemFragment );
                ft.commit();
            }
        } );


    }

    /**
     * Gets the list of exercises from the DB and makes the UI list visible when retriieved
     */
    private void retrieveExerciseList() {
        // TODO: change to differentiate between strength and cardio
        String category = "strength";
        String path = "exercise_list/" + category;
        DatabaseReference childRef = mRootRef.child( path );

        // Create an empty list for the exercise names
        mExerciseNameList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new ArrayAdapter< String >( this, android.R.layout.simple_list_item_1, mExerciseNameList );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Add the exercise names to the list
                    String exerciseName = exerciseDataSnapshot.getKey();
                    mExerciseNameList.add( exerciseName );
                }
                mLoadingText.setVisibility( View.INVISIBLE );
                mListView.setAdapter( mItemsAdapter );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Required for implementing OnFragmentInteractionListener
     */
    @Override
    public void onFragmentInteraction( Uri uri ) {

    }
}
