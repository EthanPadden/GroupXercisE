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

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_exercise_list );
        mListView = findViewById( R.id.list );
        mLoadingText = findViewById( R.id.text_loading_exercise_list );

        retrieveExerciseList();
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                String exerciseName = mListView.getItemAtPosition( i ).toString();
                ExerciseListItemFragment exerciseListItemFragment = ExerciseListItemFragment.newInstance( exerciseName );
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace( R.id.frame_set_goal_fragment_placeholder, exerciseListItemFragment );
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
        } );
    }

    private void retrieveExerciseList() {
        // TODO: change to differentiate between strength and cardio
        String category = "strength";
        String path = "exercise_list/" + category;
        DatabaseReference childRef = mRootRef.child( path );
        mExerciseNameList = new ArrayList<>();
        mItemsAdapter = new ArrayAdapter< String >( this, android.R.layout.simple_list_item_1, mExerciseNameList );


        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
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

    @Override
    public void onFragmentInteraction( Uri uri ) {

    }
}
