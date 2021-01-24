package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ExerciseListActivity extends AppCompatActivity {
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_exercise_list );

        ArrayList<String> tempLongList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            tempLongList.add( "Item " + i );
        }
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempLongList);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
    }

//    public void retrieveExerciseList( String exerciseName) {
//
//        String path = "strength_standards/" + exerciseName + "/" + testUser.getSex().toString() + "/" + weightClass;
//        DatabaseReference childRef = mRootRef.child( path );
//        final Goal goal = new Goal( exerciseName );
//        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange( DataSnapshot dataSnapshot) {
//                int[] standards = new int[5];
//                Long[] standardLongValues = new Long[5];
//                standardLongValues[0] = (Long) dataSnapshot.child( "Beginner" ).getValue();
//                standardLongValues[1] = (Long) dataSnapshot.child( "Novice" ).getValue();
//                standardLongValues[2] = (Long) dataSnapshot.child( "Intermediate" ).getValue();
//                standardLongValues[3] = (Long) dataSnapshot.child( "Advanced" ).getValue();
//                standardLongValues[4] = (Long) dataSnapshot.child( "Elite" ).getValue();
//                for( int i = 0; i < standardLongValues.length; i++ ) {
//                    standards[i] = standardLongValues[i] == null ? null : Math.toIntExact(standardLongValues[i]);
//                }
//
//                goal.setmStandards( standards );
//                mGoalText.setText( goal.toString() );
//            }
//
//            @Override
//            public void onCancelled( DatabaseError databaseError) {}
//        });
//    }
}
