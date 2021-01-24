package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ExerciseListActivity extends AppCompatActivity {
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
}
